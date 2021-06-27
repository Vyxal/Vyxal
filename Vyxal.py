from operator import mul

from sympy.utilities.iterables import multiset_partitions
from VyParse import *
from commands import *
import encoding
import utilities

import base64
import secrets
import string
import sympy
import types

newline = "\n"
number = "number"
class LazyList():
    def __call__(self, *args, **kwargs):
        return self
    def __contains__(self, item):
        if self.infinite:
            if len(self.generated):
                last = self.generated[-1]
            else:
                last = 0 
        
            while last <= item:
                last = next(self)
                if last == item:
                    return 1
            return 0
        else:
            for temp in self:
                if temp == item: return 1
            return 0
    def __getitem__(self, position):
        if isinstance(position, slice):
            start, stop, step = position.start or 0, position.stop, position.step or 1
            if stop is None:
                @LazyList
                def infinite_index():
                    if len(self.generated):
                        for item in self.generated[position::step]: yield item
                        temp = next(self)
                        while temp:
                            yield temp; temp = next(self)
                return infinite_index()
            else:
                ret = []
                for i in range(start, stop, step):
                    ret.append(self.__getitem__(i))
                return ret
        else:
            if position < 0: self.generated += list(self); return self.generated[position]
            elif position < len(self.generated): return self.generated[position]
            else:
                while len(self.generated) < position + 1:
                    try: self.__next__()
                    except: break
                return self.generated[position % len(self.generated)]
    def __init__(self, source, isinf=False):
        self.raw_object = source
        if isinstance(self.raw_object, types.FunctionType): self.raw_object = self.raw_object()
        self.generated = []
        self.infinite = isinf
    def __iter__(self):
        return self
    def __len__(self):
        return len(self.listify())
    def __next__(self):
        item = next(self.raw_object)
        self.generated.append(item)
        return item
    def listify(self):
        temp = self.generated + list(self.raw_object)
        self.raw_object = iter(temp[::])
        self.generated = []
        return temp
    def output(self):
        print("⟨", end="")
        for item in self.generated[:-1]:
            print(item, end="| ")
        if len(self.generated): print(self.generated[-1], end="")

        try:
            item = self.__next__()
            if len(self.generated) > 1: print("| ", end="")
            while True:
                print(item, end="| ")
                item = self.__next__()
        except:
            print("⟩")
def apply(function, *args):
    if function.__name__.startswith("_lambda"):
        ret = function(list(args), len(args), function)
        if len(ret): return ret[-1]
        else: return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args))[-1]
        if len(ret): return ret[-1]
        else: return []
    return function(*args)
def divide(lhs, rhs):
    return {
        (number, number): lambda: realify(sympy.Rational(lhs, rhs)),
        (number, str): lambda: rhs * int(lhs),
        (str, number): lambda: lhs * int(rhs),
        (str, str): lambda: lhs.split(rhs, maxsplit=1),
    }.get(VY_type(lhs, rhs), lambda: vectorise(divide, lhs, rhs))()
def factorial(lhs):
    return {
        number: lambda: realify(sympy.gamma(lhs)),
        str: lambda: sentence_case(lhs)
    }.get(VY_type(lhs), lambda: vectorise(factorial, lhs))()
def function_call(fn, vector):
    if type(fn) is types.FunctionType:
        return fn(vector, self=fn)
    else:
        return [{
            number: lambda: len(prime_factors(fn)),
            str: lambda: exec(VY_compile(fn)) or []
        }.get(VY_type(fn), lambda: vectorised_not(fn))()]
def log(lhs, rhs):
    ts = (VY_type(lhs), VY_type(rhs))
    if ts == (str, str):
        ret = ""
        for i in range(min(len(lhs), len(rhs))):
            if rhs[i].isupper():
                ret += lhs[i].upper()
            elif rhs[i].islower():
                ret += lhs[i].lower()
            else:
                ret += lhs[i]

        if len(lhs) > len(rhs):
            ret += lhs[i + 1:]

        return ret

    return {
        (number, number): lambda: Sympy.Rational(str(math.log(lhs, rhs))),
        (str, number): lambda: "".join([c * rhs for c in lhs]),
        (number, str): lambda: "".join([c * lhs for c in rhs]),
        (list, list): lambda: mold(lhs, rhs),
        (list, LazyList): lambda: mold(lhs, list(rhs)),
        (LazyList, list): lambda: mold(list(lhs), rhs),
        (LazyList, LazyList): lambda: mold(list(lhs), list(rhs)) #There's a chance molding raw generators won't work
    }.get(ts, lambda: vectorise(log, lhs, rhs))()
def multiply(lhs, rhs):
    ts = VY_type(lhs, rhs)
    if ts == (types.FunctionType, number):
        lhs.stored_arity = int(rhs)
        return lhs
    elif ts == (number, types.FunctionType):
        rhs.stored_arity = int(lhs)
        return rhs
    return {
        (number, number): lambda: lhs * rhs,
        (number, str): lambda: int(lhs) * rhs,
        (str, number): lambda: lhs * rhs,
        (str, str): lambda: [x + rhs for x in lhs]
    }.get(ts, lambda: vectorise(multiply, lhs, rhs))
def realify(lhs):
    if isinstance(lhs, sympy.core.numbers.ComplexInfinity) or isinstance(lhs, sympy.core.numbers.NaN):
        return 0
    else: return lhs
def sentence_case(lhs):
    ret = ""
    capitalise = True
    for char in lhs:
        ret += (lambda: char.lower(), lambda: char.upper())[capitalise]()
        if capitalise and char != " ": capitalise = False
        capitalise = capitalise or char in "!?."
    return ret
def strip_non_alphabet(name):
    stripped = filter(lambda char: char in string.ascii_letters + "_", name)
    return "".join(stripped)
tab = lambda x: newline.join(["    " + item for item in x.split(newline)]).rstrip("    ")
def vectorise(fn, left, right=None, third=None, explicit=False):
    if third:
        ts = (VY_type(left), VY_type(right))
        def gen():
            for pair in VY_zip(right, left):
                yield apply(fn, third, *pair)
        def expl(l, r):
            for item in l:
                yield apply(fn, third, r, item)
        def swapped_expl(l, r):
            for item in r:
                yield apply(fn, third, item, l)

        ret =  {
            (ts[0], ts[1]): (lambda: apply(fn, left, right),
                                   lambda: expl(iterable(left), right)),
            (list, ts[1]): (lambda: [apply(fn, x, right) for x in left],
                               lambda: expl(left, right)),
            (ts[0], list): (lambda: [apply(fn, left, x) for x in right],
                               lambda: swapped_expl(left, right)),
            (LazyList, ts[1]): (lambda: expl(left, right),
                                    lambda: expl(left, right)),
            (ts[0], LazyList): (lambda: swapped_expl(left, right),
                                    lambda: swapped_expl(left, right)),
            (list, list): (lambda: gen(),
                           lambda: expl(left, right)),
            (LazyList, LazyList): (lambda: gen(),
                                     lambda: expl(left, right)),
            (list, LazyList): (lambda: gen(),
                                lambda: expl(left, right)),
            (LazyList, list): (lambda: gen(),
                                lambda: expl(left, right))
        }[ts][explicit]()

        if type(ret) is types.GeneratorType: return LazyList(ret)
        else: return ret
    elif right:
        ts = (VY_type(left), VY_type(right))
        def gen():
            for pair in VY_zip(left, right): yield apply(fn, *pair)
        def expl(l, r):
            for item in l: yield apply(fn, r, item)
        def swapped_expl(l, r):
            for item in r:
                yield apply(fn, item, l)
        ret = {
            (ts[0], ts[1]): (lambda: apply(fn, left, right),
                                   lambda: expl(iterable(left), right)),
            (list, ts[1]): (lambda: [apply(fn, x, right) for x in left],
                               lambda: expl(left, right)),
            (ts[0], list): (lambda: [apply(fn, left, x) for x in right],
                               lambda: swapped_expl(left, right)),
            (LazyList, ts[1]): (lambda: expl(left, right),
                                    lambda: expl(left, right)),
            (ts[0], LazyList): (lambda: swapped_expl(left, right),
                                    lambda: swapped_expl(left, right)),
            (list, list): (lambda: gen(),
                           lambda: expl(left, right)),
            (LazyList, LazyList): (lambda: gen(),
                                     lambda: expl(left, right)),
            (list, LazyList): (lambda: gen(),
                                lambda: expl(left, right)),
            (LazyList, list): (lambda: gen(),
                                lambda: expl(left, right))
        }[ts][explicit]()

        if type(ret) is types.GeneratorType: return LazyList(ret)
        else: return ret

    else:
        if VY_type(left) is LazyList:
            def gen():
                for item in left:
                    yield apply(fn, item)
            return LazyList(gen())
        elif VY_type(left) in (str, Number):
            return apply(fn, list(iterable(left)))
        else:
            ret =  [apply(fn, x) for x in left]
            return ret
def wrap_in_lambda(tokens):
    if tokens[0] == Structure.NONE:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: [tokens], Keys.LAMBDA_ARGS: str(command_dict.get(tokens[1], (0,0))[1])})]
    elif tokens[0] == Structure.LAMBDA:
        return tokens
    else:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: [tokens]})]
def VY_type(lhs, rhs=None, simple=False):
    if rhs is not None:
        return (VY_type(lhs), VY_type(rhs))
    elif isinstance(lhs, int) or isinstance(lhs, sympy.core.numbers.Rational):
        return number
    elif isinstance(lhs, LazyList):
        return (LazyList, list)[simple]
    else:
        return type(item)
def VY_zip(lhs, rhs):
    @LazyList
    def f():
        lengths = (len(lhs), len(rhs))
        for i in range(max(lengths)):
            left = lhs[i] if i < lengths[0] else 0
            right = rhs[i] if i < lengths[1] else 0
            yield [left, right]
    return f()
def zipwith(function, lhs, rhs):
    @LazyList
    def f():
        for pair in VY_zip(lhs, rhs):
            yield apply(function, *pair)
    return f()
def transpile(program, header=""):
    if not program: return header or "pass" # If the program is empty, we probably just want the header or the shortest do-nothing program
    compiled = ""

    if isinstance(program, str):
        program = parse(program)
    for token in program:
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
            function_A = transpile(wrap_in_lambda(token_value[1][0]))
            compiled += function_A + newline
            compiled += "function_A = pop(stack)\n"
            compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.DYAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                compiled += function_A + newline + function_B + newline
                compiled += "function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.TRIAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: [token_value[1]]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                function_C = transpile(wrap_in_lambda(token_value[1][2]))
                compiled += function_A + newline + function_B + newline + function_C + newline
                compiled += "function_C = pop(stack); function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.FOUR_ARITY_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                function_C = transpile(wrap_in_lambda(token_value[1][2]))
                function_D = transpile(wrap_in_lambda(token_value[1][3]))
                compiled += function_A + newline + function_B + newline + function_C + newline
                compiled += "function_D = pop(stack); function_C = pop(stack); function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline

        compiled += "\n"

    
    return header + compiled

if __name__ == "__main__":
    # print(transpile("*+-ξψ"))
    print(list(VY_zip([1, 2, 3], [4, 5])))
