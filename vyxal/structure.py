from typing import List


class Structure:
    def __init__(self, branches: List[List["Structure"]]):
        # Don't do anything with the arguments
        self.branches = branches

    def transpile(self) -> str:
        """
        Return the transpiled version of the structure
        """

        return "{}"

    def __repr__(self):
        return f"{type(self).__name__}({repr(self.branches)})"


class GenericStatement(Structure):
    """
    Elements and so on
    """

    def __init__(self, branches: List[List["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class IfStatement(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.truthy = branches[0]
        self.falsey = []
        self.inbetween = []
        if len(branches) > 1:
            self.falsey = branches[-1]
            if len(branches) > 2:
                self.inbetween = branches[1:-1]

    def transpile(self) -> str:
        """
        Returns the if statement template
        """

        return """
condition = pop(stack, ctx=ctx)
context_values.append(condition)
if boolify(condition):
    {}
else:
    {}
context.values.pop()
"""


class ForLoop(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.name = ""
        self.body = []

        if len(branches) == 1:
            self.body = branches[0]
        else:
            self.name = branches[0]
            self.body = branches[1]

    def transpile(self) -> str:
        return """
for {} in iterable(pop(stack)):
    context_values.append({})
    {}
    context_values.pop()
"""


class WhileLoop(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.condition = [Structure(["1"])]
        self.body = []

        if len(branches) >= 2:
            self.condition = branches[0]
        self.body = branches[-1]

    def transpile(self) -> str:
        return """
{}
condition = pop(stack)
while boolify(condition):
    context_values.append(condition)
    {}
    context_values.pop()
    {}
    condition = pop(stack)
    """


class FunctionCall(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.parameters = branches[0]
        self.body = None
        if len(branches) >= 2:
            self.body = branches[-1]

    def transpile(self) -> str:
        return """
def FN_{}(parameters, *, ctx):
    this = FN_{}
    context_values.append(parameters[-{}:])
    input_level += 1
    stack = []
    {}
    input_values[input_level] = [stack[::], 0]
    {}
    context_values.pop()
    input_level -= 1
    return stack
"""


class Lambda(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.body = branches[-1]
        self.arity = 1

        if len(branches) > 2:
            self.arity = branches[0]

    def transpile(self) -> str:
        return """
def _lambda_{}(parameters, arity, self, *, ctx):
    this = _lambda_{}
    overloaded_arity = False

    if "arity_overload" in dir(self): overloaded_arity = self.arity_overload

    if arity and arity != {}: stack = pop(parameters, arity)
    elif overloaded_arity: stack = pop(parameters, arity)
    else: stack = pop(parameters, {})

    context_values.append(stack[::])
    input_level += 1
    input_values[input_level] = [stack[::], 0]

    {}
    ret = pop(stack)
    context_values.pop()
    input_level -= 1

    return ret
stack.append(_lambda_{})
"""


class LambdaMap(Lambda):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile() + "\n<MAPPING COMMAND>"


class LambdaFilter(Lambda):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile() + "\n<FILTERING COMMAND>"


class LambdaSort(Lambda):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile() + "\nSORTING COMMAND"


class FunctionReference(Structure):
    def __init__(self, branches: List[List[Structure]]):
        super().__init__(branches)
        self.name = branches[0]

    def transpile(self) -> str:
        return "stack.append(FN_{})"


class ListLiteral(Structure):
    def __init__(self, branches: List[List["Structure"]]):
        super().__init__(branches)
        self.items = branches

    def transpile(self) -> str:
        # We have to manually build this because we don't know how
        # many List items there will be.

        temp = "temporary_List = []"
        temp += (
            "\ndef List_item(s, ctx):\n    stack = s[::]\n    "
            "{}\n    return pop(stack, ctx=ctx)\n"
            "temp_List.append(List_item(stack))\n"
        ) * len(self.items)
        temp += "stack.append(temp_List[::])"
        return temp


class MonadicModifier(Structure):
    def __init__(self, branches: List[List["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class DyadicModifier(Structure):
    def __init__(self, branches: List[List["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class TriadicModifier(Structure):
    def __init__(self, branches: List[List["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()
