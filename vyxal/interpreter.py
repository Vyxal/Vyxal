import inspect
import os
import secrets
import sys
from datetime import date
from datetime import datetime as dt

# Pipped modules

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal import encoding, utilities
from vyxal.array_builtins import *
from vyxal.builtins import *
from vyxal.commands import *
from vyxal.parser import *

try:
    import numpy
    import pwn
    import regex
    import sympy
except:
    os.system("pip3 install -r requirements.txt --quiet --disable-pip-version-check")
    import numpy
    import pwn
    import regex
    import sympy


def wrap_in_lambda(tokens):
    if len(tokens) == 1 and tokens[0][0] == Structure.NONE:
        return [
            (
                Structure.LAMBDA,
                {
                    Keys.LAMBDA_BODY: tokens,
                    Keys.LAMBDA_ARGS: str(command_dict.get(tokens[0][1], (0, 0))[1]),
                },
            )
        ]
    elif tokens[0] == Structure.LAMBDA:
        return tokens
    else:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: tokens})]


def vy_compile(program, header=""):
    if not program:
        return (
            header or "pass"
        )  # If the program is empty, we probably just want the header or the shortest do-nothing program
    compiled = ""

    if isinstance(program, str):
        program = Tokenise(
            program
        )  # because if it's a string, we want it to be a token list

    for token in program:
        token_name, token_value = token
        if token_name == Structure.NONE:
            if token_value[0] == Digraphs.CODEPAGE:
                compiled += f"vy_globals.stack.append({codepage.find(str(token_value[1]))} + 101)"
            else:
                compiled += command_dict.get(token[1], "  ")[0]
        elif token_name == Structure.NUMBER:
            value = token[-1]
            end = value.find(".", value.find(".") + 1)

            if end != -1:
                value = value[:end]

            if value.isnumeric():
                compiled += f"vy_globals.stack.append({value})"
            else:
                try:
                    float(value)
                    compiled += f"vy_globals.stack.append(sympy.Rational({value}))"
                except:
                    compiled += f"vy_globals.stack.append(sympy.Rational('0.5'))"
        elif token_name == Structure.STRING:
            string, string_type = token_value[0], token_value[1]
            if string_type == StringDelimiters.NORMAL:
                if vy_globals.raw_strings:
                    value = string.replace("\\", "\\\\").replace('"', '\\"')
                    compiled += f'vy_globals.stack.append("{value}")'
                else:
                    value = string.replace("\\", "\\\\").replace('"', '\\"')
                    compiled += (
                        f'vy_globals.stack.append("{utilities.uncompress(value)}")'
                    )
            elif string_type == StringDelimiters.COM_NUMBER:
                number = utilities.to_ten(string, encoding.codepage_number_compress)
                compiled += f"vy_globals.stack.append({number})"
            elif string_type == StringDelimiters.COM_STRING:
                value = utilities.to_ten(string, encoding.codepage_string_compress)
                value = utilities.from_ten(value, utilities.base27alphabet)
                compiled += f"vy_globals.stack.append('{value}')"
        elif token_name == Structure.CHARACTER:
            compiled += f"vy_globals.stack.append({repr(token[1])})"
        elif token_name == Structure.IF:
            compiled += "temp_value = pop(vy_globals.stack)\n"
            compiled += (
                "if temp_value:\n"
                + tab(vy_compile(token_value[Keys.IF_TRUE]))
                + NEWLINE
            )
            if Keys.IF_FALSE in token_value:
                compiled += "else:\n" + tab(vy_compile(token_value[Keys.IF_FALSE]))
        elif token_name == Structure.FOR:
            loop_variable = "LOOP_" + secrets.token_hex(16)
            if Keys.FOR_VAR in token_value:
                loop_variable = "VAR_" + strip_non_alphabet(token_value[Keys.FOR_VAR])
            compiled += (
                "for "
                + loop_variable
                + " in vy_range(pop(vy_globals.stack)):"
                + NEWLINE
            )
            compiled += tab("vy_globals.context_level += 1") + NEWLINE
            compiled += (
                tab("vy_globals.context_values.append(" + loop_variable + ")") + NEWLINE
            )
            compiled += tab(vy_compile(token_value[Keys.FOR_BODY])) + NEWLINE
            compiled += tab("vy_globals.context_level -= 1") + NEWLINE
            compiled += tab("vy_globals.context_values.pop()")
        elif token_name == Structure.WHILE:
            condition = "vy_globals.stack.append(1)"
            if Keys.WHILE_COND in token_value:
                condition = vy_compile(token_value[Keys.WHILE_COND])

            compiled += condition + NEWLINE
            compiled += "while pop(vy_globals.stack):\n"
            compiled += tab(vy_compile(token_value[Keys.WHILE_BODY])) + NEWLINE
            compiled += tab(condition)
        elif token_name == Structure.FUNCTION:
            # Determine if it's a function call or definition
            if Keys.FUNC_BODY not in token_value:
                # Function call
                compiled += (
                    "vy_globals.stack += FN_"
                    + token_value[Keys.FUNC_NAME]
                    + "(vy_globals.stack)"
                )
            else:
                function_information = token_value[Keys.FUNC_NAME].split(":")
                # This will either be a single name, or name and parameter information

                parameter_count = 0
                function_name = function_information[0]
                parameters = []

                if len(function_information) >= 2:
                    for parameter in function_information[1:]:
                        if parameter == "*":
                            # Variadic parameters
                            parameters.append(-1)
                        elif parameter.isnumeric():
                            # Fixed arity
                            parameters.append(int(parameter))
                            parameter_count += parameters[-1]
                        else:
                            # Named parameter
                            parameters.append(parameter)
                            parameter_count += 1

                compiled += (
                    "def FN_" + function_name + "(parameter_stack, arity=None):\n"
                )
                compiled += tab("vy_globals.context_level += 1") + NEWLINE
                compiled += tab("vy_globals.input_level += 1") + NEWLINE
                compiled += tab(f"this_function = FN_{function_name}") + NEWLINE
                if parameter_count == 1:
                    # There's only one parameter, so instead of pushing it as a list
                    # (which is kinda rather inconvienient), push it as a "scalar"

                    compiled += tab(
                        "vy_globals.context_values.append(parameter_stack[-1])"
                    )
                elif parameter_count != -1:
                    compiled += tab(
                        f"vy_globals.context_values.append(parameter_stack[:-{parameter_count}])"
                    )
                else:
                    compiled += tab("vy_globals.context_values.append(parameter_stack)")

                compiled += NEWLINE

                compiled += tab("parameters = []") + NEWLINE

                for parameter in parameters:
                    if parameter == -1:
                        compiled += tab(
                            """arity = pop(parameter_stack)
if vy_type(arity) == Number:
    parameters += parameter_stack[-int(arity):]
else:
    parameters += [arity]
"""
                        )
                    elif parameter == 1:
                        compiled += tab("parameters.append(pop(parameter_stack))")
                    elif isinstance(parameter, int):
                        compiled += tab(
                            f"parameters += pop(parameter_stack, {parameter})[::-1]"
                        )
                    else:
                        compiled += tab("VAR_" + parameter + " = pop(parameter_stack)")
                    compiled += NEWLINE

                compiled += tab("vy_globals.stack = parameters[::]") + NEWLINE
                compiled += (
                    tab(
                        "vy_globals.input_values[vy_globals.input_level] = [vy_globals.stack[::], 0]"
                    )
                    + NEWLINE
                )
                compiled += tab(vy_compile(token_value[Keys.FUNC_BODY])) + NEWLINE
                compiled += (
                    tab(
                        "vy_globals.context_level -= 1; vy_globals.context_values.pop()"
                    )
                    + NEWLINE
                )
                compiled += tab("vy_globals.input_level -= 1") + NEWLINE
                compiled += tab("return vy_globals.stack")
        elif token_name == Structure.LAMBDA:
            defined_arity = 1
            if Keys.LAMBDA_ARGS in token_value:
                lambda_argument = token_value[Keys.LAMBDA_ARGS]
                if lambda_argument.isnumeric():
                    defined_arity = int(lambda_argument)
            signature = secrets.token_hex(16)
            compiled += (
                f"def _lambda_{signature}(parameter_stack, arity=-1, self=None):"
                + NEWLINE
            )
            compiled += tab("vy_globals.context_level += 1") + NEWLINE
            compiled += tab("vy_globals.input_level += 1") + NEWLINE
            compiled += tab(f"this_function = _lambda_{signature}") + NEWLINE
            compiled += tab("stored = False") + NEWLINE
            compiled += (
                tab("if 'stored_arity' in dir(self): stored = self.stored_arity;")
                + NEWLINE
            )
            compiled += (
                tab(
                    f"if arity != {defined_arity} and arity >= 0: parameters = pop(parameter_stack, arity, True); vy_globals.stack = parameters[::]"
                )
                + NEWLINE
            )
            compiled += (
                tab(
                    "elif stored: parameters = pop(parameter_stack, stored, True); vy_globals.stack = parameters[::]"
                )
                + NEWLINE
            )
            if defined_arity == 1:
                compiled += (
                    tab(
                        "else: parameters = pop(parameter_stack); vy_globals.stack = [parameters]"
                    )
                    + NEWLINE
                )
            else:
                compiled += (
                    tab(
                        f"else: parameters = pop(parameter_stack, {defined_arity}); vy_globals.stack = parameters[::]"
                    )
                    + NEWLINE
                )
            compiled += tab("vy_globals.context_values.append(parameters)") + NEWLINE
            compiled += (
                tab(
                    "vy_globals.input_values[vy_globals.input_level] = [vy_globals.stack[::], 0]"
                )
                + NEWLINE
            )
            compiled += tab(vy_compile(token_value[Keys.LAMBDA_BODY])) + NEWLINE
            compiled += tab("ret = [pop(vy_globals.stack)]") + NEWLINE
            compiled += (
                tab("vy_globals.context_level -= 1; vy_globals.context_values.pop()")
                + NEWLINE
            )
            compiled += tab("vy_globals.input_level -= 1") + NEWLINE
            compiled += tab("return ret") + NEWLINE
            compiled += f"_lambda_{signature}.stored_arity = {defined_arity}" + NEWLINE
            compiled += f"vy_globals.stack.append(_lambda_{signature})"
        elif token_name == Structure.LIST:
            compiled += "temp_list = []" + NEWLINE
            for element in token_value[Keys.LIST_ITEMS]:
                if element:
                    compiled += "def list_lhs(parameter_stack):" + NEWLINE
                    compiled += tab("vy_globals.stack = parameter_stack[::]") + NEWLINE
                    compiled += tab(vy_compile(element)) + NEWLINE
                    compiled += tab("return pop(vy_globals.stack)") + NEWLINE
                    compiled += "temp_list.append(list_lhs(vy_globals.stack))" + NEWLINE
            compiled += "vy_globals.stack.append(temp_list[::])"
        elif token_name == Structure.FUNC_REF:
            compiled += f"vy_globals.stack.append(FN_{token_value[Keys.FUNC_NAME]})"
        elif token_name == Structure.VAR_SET:
            compiled += "VAR_" + token_value + " = pop(vy_globals.stack)"
        elif token_name == Structure.VAR_GET:
            compiled += "vy_globals.stack.append(VAR_" + token_value + ")"
        elif token_name == Structure.MONAD_TRANSFORMER:
            function_A = vy_compile(wrap_in_lambda(token_value[1]))
            compiled += function_A + NEWLINE
            compiled += "function_A = pop(vy_globals.stack)\n"
            compiled += transformers[token_value[0]] + NEWLINE
        elif token_name == Structure.DYAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += (
                    vy_compile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})])
                    + NEWLINE
                )
            else:
                function_A = vy_compile(wrap_in_lambda(token_value[1][0]))
                function_B = vy_compile(wrap_in_lambda(token_value[1][1]))
                compiled += function_A + NEWLINE + function_B + NEWLINE
                compiled += "function_B = pop(vy_globals.stack); function_A = pop(vy_globals.stack)\n"
                compiled += transformers[token_value[0]] + NEWLINE
        elif token_name == Structure.TRIAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += (
                    vy_compile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})])
                    + NEWLINE
                )
            else:
                function_A = vy_compile(wrap_in_lambda(token_value[1][0]))
                function_B = vy_compile(wrap_in_lambda(token_value[1][1]))
                function_C = vy_compile(wrap_in_lambda(token_value[1][2]))
                compiled += (
                    function_A + NEWLINE + function_B + NEWLINE + function_C + NEWLINE
                )
                compiled += "function_C = pop(vy_globals.stack); function_B = pop(vy_globals.stack); function_A = pop(vy_globals.stack)\n"
                compiled += transformers[token_value[0]] + NEWLINE

        compiled += "\n"

    return header + compiled


def execute(code, flags, input_list, output_variable):
    vy_globals.online_version = True
    vy_globals.output = output_variable
    vy_globals.output[1] = ""
    vy_globals.output[2] = ""
    flags = flags

    if input_list:
        eval_function = vy_eval
        if "Ṡ" in flags:
            eval_function = str
        vy_globals.inputs = list(map(eval_function, input_list.split("\n")))

    if "a" in flags:
        vy_globals.inputs = [vy_globals.inputs]

    if flags:
        vy_globals.set_globals(flags)

        if "h" in flags:
            vy_globals.output[
                1
            ] = """
ALL flags should be used as is (no '-' prefix)
\tH\tPreset stack to 100
\tj\tPrint top of stack joined by newlines on end of execution
\tL\tPrint top of stack joined by newlines (Vertically) on end of execution
\ts\tSum/concatenate top of stack on end of execution
\tM\tMake implicit range generation start at 0 instead of 1
\tm\tMake implicit range generation end at n-1 instead of n
\tṀ\tEquivalent to having both m and M flags
\tv\tUse Vyxal encoding for input file
\tc\tOutput compiled code
\tf\tGet input from file instead of arguments
\ta\tTreat newline seperated values as a list
\td\tPrint deep sum of top of stack on end of execution
\tr\tMakes all operations happen with reverse arguments
\tS\tPrint top of stack joined by spaces on end of execution
\tC\tCentre the output and join on newlines on end of execution
\tO\tDisable implicit output
\to\tForce implicit output
\tK\tEnable Keg mode (input as ordinal values and integers as characters when outputting)
\tl\tPrint length of top of stack on end of execution
\tG\tPrint the maximum item of the top of stack on end of execution
\tg\tPrint the minimum item of the top of the stack on end of execution
\tW\tPrint the entire stack on end of execution
\tṠ\tTreat all vy_globals.inputs as strings (usually obtainable by wrapping in quotations)
\tR\tTreat numbers as ranges if ever used as an iterable
\tD\tTreat all strings as raw strings (don't decompress strings)
\tṪ\tPrint the sum of the entire stack
\tṡ\tPrint the entire stack, joined on spaces
\tJ\tPrint the entire stack, separated by newlines.
\tV\tVariables are only one letter long
\t5\tMake the interpreter timeout after 5 seconds
\tb\tMake the interpreter timeout after 15 seconds
\tB\tMake the interpreter timeout after 30 seconds
\tT\tMake the interpreter timeout after 60 seconds
\t…\tTruncate lists at 100 items
"""
            return
    vy_globals.input_values[0] = [vy_globals.inputs, 0]
    code = vy_compile(
        code,
        inspect.cleandoc(
            """
            from vyxal import vy_globals
            from vyxal.array_builtins import *
            from vyxal.builtins import *"""
        )
        + NEWLINE,
    )
    vy_globals.context_level = 0
    if flags and "c" in flags:
        vy_globals.output[2] = code

    try:
        print(code)
        exec(code, globals())
    except SystemExit:
        if "o" not in flags:
            return
    except Exception as e:
        vy_globals.output[2] += "\n" + str(e)
        vy_globals.output[
            2
        ] += f"\nMost recently popped arguments: {[deref(i, limit=10) for i in vy_globals.last_popped]}"
        vy_globals.output[
            2
        ] += f"\nFinal stack: {[deref(i, limit=10) for i in vy_globals.stack]}"
        raise e

    if (not vy_globals.printed and "O" not in flags) or "o" in flags:
        if flags and "s" in flags:
            vy_print(summate(pop(vy_globals.stack)))
        elif flags and "…" in flags:
            top = pop(vy_globals.stack)
            if vy_type(top) in (list, Generator):
                vy_print(top[:100])
            else:
                vy_print(top)
        elif flags and "ṡ" in flags:
            vy_print(" ".join([vy_str(n) for n in vy_globals.stack]))
        elif flags and "d" in flags:
            vy_print(summate(flatten(pop(vy_globals.stack))))
        elif flags and "Ṫ" in flags:
            vy_print(summate(vy_globals.stack))
        elif flags and "S" in flags:
            vy_print(" ".join([vy_str(n) for n in pop(vy_globals.stack)]))
        elif flags and "C" in flags:
            vy_print("\n".join(centre([vy_str(n) for n in pop(vy_globals.stack)])))
        elif flags and "l" in flags:
            vy_print(len(pop(vy_globals.stack)))
        elif flags and "G" in flags:
            vy_print(vy_max(pop(vy_globals.stack)))
        elif flags and "g" in flags:
            vy_print(vy_min(pop(vy_globals.stack)))
        elif flags and "W" in flags:
            vy_print(vy_globals.stack)
        elif vy_globals._vertical_join:
            vy_print(vertical_join(pop(vy_globals.stack)))
        elif vy_globals._join:
            vy_print("\n".join([vy_str(n) for n in pop(vy_globals.stack)]))
        elif flags and "J" in flags:
            vy_print("\n".join([vy_str(n) for n in vy_globals.stack]))
        else:
            vy_print(pop(vy_globals.stack))


if __name__ == "__main__":
    ### Debugging area
    import sys

    file_location = ""
    flags = ""
    vy_globals.inputs = []
    header = (
        inspect.cleandoc(
            """
        from vyxal import vy_globals
        from vyxal.builtins import *
        from vyxal.array_builtins import *
        vy_globals.stack = []
        vy_globals.register = 0
        vy_globals.printed = False"""
        )
        + NEWLINE
    )

    if len(sys.argv) > 1:
        file_location = sys.argv[1]
    if len(sys.argv) > 2:
        flags = sys.argv[2]
        if flags:
            eval_function = vy_eval
            if "Ṡ" in flags:
                eval_function = str
            if "H" in flags:
                vy_globals.stack = [100]
            if "f" in flags:
                vy_globals.inputs = list(
                    map(eval_function, open(sys.argv[3]).readlines())
                )
            else:
                vy_globals.inputs = list(map(eval_function, sys.argv[3:]))

        if "a" in flags:
            vy_globals.inputs = [vy_globals.inputs]

    if not file_location:  # repl mode

        while 1:
            line = input(">>> ")
            vy_globals.context_level = 0
            line = vy_compile(line, header)
            exec(line)
            vy_print(vy_globals.stack)
    elif file_location == "h":
        print(
            "\nUsage: python3 Vyxal.py <file> <flags (single string of flags)> <input(s) (if not from STDIN)>"
        )
        print("ALL flags should be used as is (no '-' prefix)")
        print("\tH\tPreset stack to 100")
        print("\tj\tPrint top of stack joined by NEWLINEs")
        print("\tL\tPrint top of stack joined by NEWLINEs (Vertically)")
        print("\ts\tSum/concatenate top of stack on end of execution")
        print("\tM\tMake implicit range generation start at 0 instead of 1")
        print("\tm\tMake implicit range generation end at n-1 instead of n")
        print("\tv\tUse Vyxal encoding for input file")
        print("\tc\tOutput compiled code")
        print("\tf\tGet input from file instead of arguments")
        print("\ta\tTreat NEWLINE seperated values as a list")
        print("\td\tDeep sum of top of stack")
        print("\tr\tMakes all operations happen with reverse arguments")
        print("\tS\tPrint top of stack joined by spaces")
        print("\tC\tCentre the output and join on NEWLINEs")
        print("\tO\tDisable implicit output")
        print("\tK\tEnable Keg mode")
        print("\tE\tEnable safe evaluation (offline interpreter only)")
        print("\tl\tPrint length of top of stack")
        print("\tG\tPrint the maximum item of the top of stack on end of execution")
        print("\tg\tPrint the minimum item of the top of the stack on end of execution")
        print("\tW\tPrint the entire stack on end of execution")
        print("\tṠ\tTreat all vy_globals.inputs as strings")
        print("\tR\tTreat numbers as ranges if ever used as an iterable")
        print("\tD\tTreat all strings as raw strings (don't decompress strings)")
        print("\tṪ\tPrint the sum of the entire stack")
        print("\tṀ\tEquivalent to having both m and M flags")
        print("\tJ\tPrint stack joined by NEWLINEs")
        print("\to\tForce implicit output, even when something has been outputted.")
        print("\tṡ\tPrint stack joined on spaces")
        print("\t…\tTruncate lists at 100 items")
    else:
        if flags:
            vy_globals.set_globals(flags)

        # Encoding method thanks to Adnan (taken from the old 05AB1E interpreter)
        if vy_globals.use_encoding:
            import vyxal.encoding

            code = open(file_location, "rb").read()
            code = vyxal.encoding.vyxal_to_utf8(code)
        else:
            code = open(file_location, "r", encoding="utf-8").read()
        vy_globals.input_values[0] = [vy_globals.inputs, 0]
        code = vy_compile(code, header)
        vy_globals.context_level = 0
        if flags and "c" in flags:
            print(code)
        exec(code)
        if (not vy_globals.printed and "O" not in flags) or "o" in flags:
            if flags and "s" in flags:
                print(summate(pop(vy_globals.stack)))
            elif flags and "…":
                top = pop(vy_globals.stack)
                if vy_type(top) in (list, Generator):
                    print(top[:100])
                else:
                    print(top)
            elif flags and "ṡ" in flags:
                print(" ".join([vy_str(n) for n in vy_globals.stack]))
            elif flags and "d" in flags:
                print(summate(flatten(pop(vy_globals.stack))))
            elif flags and "Ṫ" in flags:
                vy_print(summate(vy_globals.stack))
            elif flags and "S" in flags:
                print(" ".join([vy_str(n) for n in pop(vy_globals.stack)]))
            elif flags and "C" in flags:
                print("\n".join(centre([vy_str(n) for n in pop(vy_globals.stack)])))
            elif flags and "l" in flags:
                print(len(pop(vy_globals.stack)))
            elif flags and "G" in flags:
                print(vy_max(pop(vy_globals.stack)))
            elif flags and "g" in flags:
                print(vy_min(pop(vy_globals.stack)))
            elif flags and "W" in flags:
                print(vy_str(vy_globals.stack))
            elif vy_globals._vertical_join:
                print(vertical_join(pop(vy_globals.stack)))
            elif vy_globals._join:
                print("\n".join([vy_str(n) for n in pop(vy_globals.stack)]))
            elif flags and "J" in flags:
                print("\n".join([vy_str(n) for n in vy_globals.stack]))
            else:
                vy_print(pop(vy_globals.stack))
