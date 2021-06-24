from VyParse import *
from commands import *
import encoding
import utilities

import base64
import secrets
import string
import sympy

newline = "\n"

def strip_non_alphabet(name):
    stripped = filter(lambda char: char in string.ascii_letters + "_", name)
    return "".join(stripped)
tab = lambda x: newline.join(["    " + item for item in x.split(newline)]).rstrip("    ")
def wrap_in_lambda(tokens):
    if tokens[0] == Structure.NONE:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: [tokens], Keys.LAMBDA_ARGS: str(command_dict[tokens[1]][1])})]
    else:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: [tokens]})]
def transpile(program, header=""):
    if not program: return header or "pass" # If the program is empty, we probably just want the header or the shortest do-nothing program
    compiled = ""

    if isinstance(program, str):
        program = parse(program)
    for token in program:
        print(token)
        token_name, token_value = token
        if token_name == Structure.NONE:
            compiled += command_dict.get(token[1], "  ")[0]
        elif token_name == Structure.NUMBER:
            value = token[-1]
            end = value.find(".", value.find(".") + 1)

            if end != -1: value = value[:end]

            if value.isnumeric():
                compiled += f"stack.append({value})"
            else:
                try:
                    float(value)
                    compiled += f"stack.append(sympy.Rational({value}))"
                except:
                    compiled += f"stack.append(sympy.Rational('0.5'))"
        elif token_name == Structure.STRING:
            string, string_type = token_value[0], token_value[1]
            if string_type == StringDelimiters.NORMAL:
                value = string.replace('"', "\\\"")
                compiled += f"stack.append(\"{value}\")"
            elif string_type == StringDelimiters.DICTIONARY:
                value = string.replace('"', "\\\"")
                compiled += f"stack.append(\"{utilities.uncompress(value)}\")"
            elif string_type == StringDelimiters.COM_NUMBER:
                number = utilities.to_ten(string, encoding.codepage_number_compress)
                compiled += f"stack.append({number})"
            elif string_type == StringDelimiters.COM_STRING:
                value = utilities.to_ten(string, encoding.codepage_string_compress)
                value = utilities.from_ten(value, utilities.base27alphabet)
                compiled += f"stack.append('{value}')"
        elif token_name == Structure.CHARACTER:
            compiled += f"stack.append({repr(token[1])})"
        elif token_name == Structure.IF:
            compiled += "temp_value = pop(stack)\n"
            compiled += "if temp_value:\n" + tab(transpile(token_value[Keys.IF_TRUE])) + newline
            if Keys.IF_FALSE in token_value: compiled += "else:\n" + tab(transpile(token_value[Keys.IF_FALSE]))
        elif token_name == Structure.FOR:
            loop_variable = "LOOP_" + _mangle(compiled)
            if Keys.FOR_VAR in token_value:
                loop_variable = "VAR_" + strip_non_alphabet(token_value[Keys.FOR_VAR])
            compiled += "for " + loop_variable + " in VY_range(pop(stack)):" + newline
            compiled += tab("context_level += 1") + newline
            compiled += tab("context_values.append(" + loop_variable + ")") + newline
            compiled += tab(transpile(token_value[Keys.FOR_BODY])) + newline
            compiled += tab("context_level -= 1") + newline
            compiled += tab("context_values.pop()")
        elif token_name == Structure.WHILE:
            condition = "stack.append(1)"
            if Keys.WHILE_COND in value:
                condition = transpile(token_value[Keys.WHILE_COND])
            
            compiled += condition + newline
            compiled += "while pop(stack):\n"
            compiled += tab(transpile(token_value[Keys.WHILE_BODY])) + newline
            compiled += tab(condition)
        elif token_name == Structure.FUNCTION:
            # Determine if it's a function call or definition
            if Keys.FUNC_BODY not in token_value:
                # Function call
                compiled += "stack += FN_" + token_value[Keys.FUNC_NAME] + "(stack)"
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

                compiled += "def FN_" + function_name + "(parameter_stack, arity=None):\n"
                compiled += tab("global context_level, context_values, input_level, input_values, retain_items, printed, register") + newline
                compiled += tab("context_level += 1") + newline
                compiled += tab("input_level += 1") + newline
                compiled += tab(f"this_function = FN_{function_name}") + newline
                if parameter_count == 1:
                    # There's only one parameter, so instead of pushing it as a list
                    # (which is kinda rather inconvienient), push it as a "scalar"

                    compiled += tab("context_values.append(parameter_stack[-1])")
                elif parameter_count != -1:
                    compiled += tab(f"context_values.append(parameter_stack[:-{parameter_count}])")
                else:
                    compiled += tab("context_values.append(parameter_stack)")

                compiled += newline

                compiled += tab("parameters = []") + newline

                for parameter in parameters:
                    if parameter == -1:
                        compiled += tab("""arity = pop(parameter_stack)
if VY_type(arity) == Number:
    parameters += parameter_stack[-int(arity):]
else:
    parameters += [arity]
""")
                    elif parameter == 1:
                        compiled += tab("parameters.append(pop(parameter_stack))")
                    elif isinstance(parameter, int):
                        compiled += tab(f"parameters += pop(parameter_stack, {parameter})")
                    else:
                        compiled += tab("VAR_" + parameter + " = pop(parameter_stack)")
                    compiled += newline

                compiled += tab("stack = parameters[::]") + newline
                compiled += tab("input_values[input_level] = [stack[::], 0]") + newline
                compiled += tab(transpile(token_value[Keys.FUNC_BODY])) + newline
                compiled += tab("context_level -= 1; context_values.pop()") + newline
                compiled += tab("input_level -= 1") + newline
                compiled += tab("return stack")
        elif token_name == Structure.LAMBDA:
            defined_arity = 1
            if Keys.LAMBDA_ARGS in token_value:
                lambda_argument = token_value[Keys.LAMBDA_ARGS]
                if lambda_argument.isnumeric():
                    defined_arity = int(lambda_argument)
            signature = secrets.token_hex(16)
            compiled += f"def _lambda_{signature}(parameter_stack, arity=-1, self=None):" + newline
            compiled += tab("global context_level, context_values, input_level, input_values, retain_items, printed, register") + newline
            compiled += tab("context_level += 1") + newline
            compiled += tab("input_level += 1") + newline
            compiled += tab(f"this_function = _lambda_{signature}") + newline
            compiled += tab("stored = False") + newline
            compiled += tab("if 'stored_arity' in dir(self): stored = self.stored_arity;") + newline
            compiled += tab(f"if arity != {defined_arity} and arity >= 0: parameters = pop(parameter_stack, arity); stack = parameters[::]") + newline
            compiled += tab("elif stored: parameters = pop(parameter_stack, stored); stack = parameters[::]") + newline
            if defined_arity == 1:
                compiled += tab(f"else: parameters = pop(parameter_stack); stack = [parameters]") + newline
            else:
                compiled += tab(f"else: parameters = pop(parameter_stack, {defined_arity}); stack = parameters[::]") + newline
            compiled += tab("context_values.append(parameters)") + newline
            compiled += tab("input_values[input_level] = [stack[::], 0]") + newline
            compiled += tab(transpile(token_value[Keys.LAMBDA_BODY])) + newline
            compiled += tab("ret = [pop(stack)]") + newline
            compiled += tab("context_level -= 1; context_values.pop()") + newline
            compiled += tab("input_level -= 1") + newline
            compiled += tab("return ret") + newline
            compiled += f"stack.append(_lambda_{signature})"
        elif token_name == Structure.LIST:
            compiled += "temp_list = []" + newline
            for element in token_value[Keys.LIST_ITEMS]:
                if element:
                    compiled += "def list_item(parameter_stack):" + newline
                    compiled += tab("stack = parameter_stack[::]") + newline
                    compiled += tab(transpile(element)) + newline
                    compiled += tab("return pop(stack)") + newline
                    compiled += "temp_list.append(list_item(stack))" + newline
            compiled += "stack.append(temp_list[::])"
        elif token_name == Structure.FUNC_REF:
            compiled += f"stack.append(FN_{token_value[Keys.FUNC_NAME]})"
        elif token_name == Structure.VAR_SET:
            compiled += "VAR_" + token_value[Keys.VAR_NAME] + " = pop(stack)"
        elif token_name == Structure.VAR_GET:
            compiled += "stack.append(VAR_" + token_value[Keys.VAR_NAME] + ")"
        elif token_name == Structure.MONAD_TRANSFORMER:
            print(token_value)
            grouped = transpile(wrap_in_lambda(token_value[1][0]))
            compiled += grouped + newline
            compiled += "function_A = pop(stack)\n"
            compiled += transformers[token_value[0]] + newline
        compiled += "\n"

    
    return header + compiled

if __name__ == "__main__":
    print(transpile("+&"))
