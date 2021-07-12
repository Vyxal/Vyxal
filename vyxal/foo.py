from vyxal import vy_globals
from vyxal.array_builtins import first_n
from vyxal.builtins import pop, halve, const_divisibility, para_apply, multiply, vy_map
vy_globals.stack.append(100)
def _lambda_f87f176f4fb4adb959a97bacb6d3b06c(parameter_stack, arity=-1, self=None):
    vy_globals.context_level += 1
    vy_globals.input_level += 1
    this_function = _lambda_f87f176f4fb4adb959a97bacb6d3b06c
    stored = False
    if 'stored_arity' in dir(self): stored = self.stored_arity;
    if arity != 1 and arity >= 0: parameters = pop(parameter_stack, arity, True); vy_globals.stack = parameters[::]
    elif stored: parameters = pop(parameter_stack, stored, True); vy_globals.stack = parameters[::]
    else: parameters = pop(parameter_stack); vy_globals.stack = [parameters]
    vy_globals.context_values.append(parameters)
    vy_globals.input_values[vy_globals.input_level] = [vy_globals.stack[::], 0]
    def _lambda_484e16aab491694b85ab57fc50f19212(parameter_stack, arity=-1, self=None):
        vy_globals.context_level += 1
        vy_globals.input_level += 1
        this_function = _lambda_484e16aab491694b85ab57fc50f19212
        stored = False
        if 'stored_arity' in dir(self): stored = self.stored_arity;
        if arity != 1 and arity >= 0: parameters = pop(parameter_stack, arity, True); vy_globals.stack = parameters[::]
        elif stored: parameters = pop(parameter_stack, stored, True); vy_globals.stack = parameters[::]
        else: parameters = pop(parameter_stack); vy_globals.stack = [parameters]
        vy_globals.context_values.append(parameters)
        vy_globals.input_values[vy_globals.input_level] = [vy_globals.stack[::], 0]
        vy_globals.stack.append(const_divisibility(pop(vy_globals.stack), 3, lambda item: len(item) == 1))

        ret = [pop(vy_globals.stack)]
        vy_globals.context_level -= 1; vy_globals.context_values.pop()
        vy_globals.input_level -= 1
        return ret
    _lambda_484e16aab491694b85ab57fc50f19212.stored_arity = 1
    vy_globals.stack.append(_lambda_484e16aab491694b85ab57fc50f19212)

    def _lambda_4ccf53ce88bb7f9c8a3f2ed38f72de27(parameter_stack, arity=-1, self=None):
        vy_globals.context_level += 1
        vy_globals.input_level += 1
        this_function = _lambda_4ccf53ce88bb7f9c8a3f2ed38f72de27
        stored = False
        if 'stored_arity' in dir(self): stored = self.stored_arity;
        if arity != 1 and arity >= 0: parameters = pop(parameter_stack, arity, True); vy_globals.stack = parameters[::]
        elif stored: parameters = pop(parameter_stack, stored, True); vy_globals.stack = parameters[::]
        else: parameters = pop(parameter_stack); vy_globals.stack = [parameters]
        vy_globals.context_values.append(parameters)
        vy_globals.input_values[vy_globals.input_level] = [vy_globals.stack[::], 0]
        top = pop(vy_globals.stack); res = const_divisibility(top, 5, lambda item: (top, len(item)))
        if type(res) is tuple: vy_globals.stack += list(res)
        else: vy_globals.stack.append(res)

        ret = [pop(vy_globals.stack)]
        vy_globals.context_level -= 1; vy_globals.context_values.pop()
        vy_globals.input_level -= 1
        return ret
    _lambda_4ccf53ce88bb7f9c8a3f2ed38f72de27.stored_arity = 1
    vy_globals.stack.append(_lambda_4ccf53ce88bb7f9c8a3f2ed38f72de27)

    function_B = pop(vy_globals.stack); function_A = pop(vy_globals.stack)
    para_apply(function_A, function_B, vy_globals.stack); rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append([lhs, rhs])

    vy_globals.stack.append('FizzBuzz')
    vy_globals.stack.append(halve(pop(vy_globals.stack)))
    rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(multiply(lhs, rhs))
    vy_globals.stack.append(first_n(pop(vy_globals.stack)))
    rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(rhs or lhs)

    ret = [pop(vy_globals.stack)]
    vy_globals.context_level -= 1; vy_globals.context_values.pop()
    vy_globals.input_level -= 1
    return ret
_lambda_f87f176f4fb4adb959a97bacb6d3b06c.stored_arity = 1
vy_globals.stack.append(_lambda_f87f176f4fb4adb959a97bacb6d3b06c)
fn, vector = pop(vy_globals.stack, 2);

m=vy_map(fn, vector)

vy_globals.stack.append(m)

print(vy_globals.stack)