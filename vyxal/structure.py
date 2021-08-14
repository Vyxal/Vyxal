from typing import Union

Branch = Union[str, list["Structure"], list["Token"]]


class Structure:
    def __init__(self, *branches: Branch):
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

    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class IfStatement(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.truthy = branches[0]
        self.falsey = []
        self.inbetween = []
        if len(branches) > 1:
            self.falsey = branches[-1]
            if len(branches) > 2:
                self.inbetween = branches[1:-1]


class ForLoop(Structure):
    def __init__(self, names: list[str], body: Branch):
        super().__init__(*names, body)
        self.names = names
        self.body = body


class WhileLoop(Structure):
    """Represents either a while or an infinite loop."""

    def __init__(self, condition: Branch, body: Branch):
        super().__init__(condition, body)
        self.condition = condition
        self.body = body


class FunctionCall(Structure):
    def __init__(self, name: str):
        super().__init__(name)
        self.name = name


class FunctionDef(Structure):
    def __init__(
        self, name: str, parameters: list["Token"], body: list[Structure]
    ):
        super().__init__(name, parameters, body)
        self.name = name
        self.parameters = parameters
        self.body = body


class Lambda(Structure):
    def __init__(self, arity: int, body3: list[Structure]):
        super().__init__(str(arity), body3)
        self.arity = arity
        self.body = body3


class LambdaMap(Lambda):
    def __init__(self, body: list[Structure]):
        print("Here")
        super().__init__(1, body)


class LambdaFilter(Lambda):
    def __init__(self, body: list[Structure]):
        super().__init__(1, body)


class LambdaSort(Lambda):
    def __init__(self, body: list[Structure]):
        super().__init__(1, body)


class FunctionReference(Structure):
    def __init__(self, name: str):
        super().__init__(name)
        self.name = name


class ListLiteral(Structure):
    def __init__(self, *items: Branch):
        super().__init__(*items)
        self.items = items


class MonadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class DyadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class TriadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
