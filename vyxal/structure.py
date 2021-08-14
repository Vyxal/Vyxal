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

    def transpile(self) -> str:
        return super().transpile()


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
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.name = None
        self.body = []

        if len(branches) == 1:
            self.body = branches[0]
        else:
            self.name = branches[0]
            self.body = branches[1]


class WhileLoop(Structure):
    """Represents either a while or an infinite loop."""

    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.condition = [Structure("1")]
        self.body = []

        if len(branches) >= 2:
            self.condition = branches[0]
        self.body = branches[-1]


class FunctionCall(Structure):
    def __init__(self, parameters: list[Structure], *branches: Branch):
        super().__init__(*branches)
        self.parameters = parameters
        self.body = None
        if len(branches) >= 1:
            self.body = branches[-1]


class Lambda(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.body = branches[-1]
        self.arity = 1

        if len(branches) > 2:
            self.arity = branches[0]


class LambdaMap(Lambda):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class LambdaFilter(Lambda):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class LambdaSort(Lambda):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class FunctionReference(Structure):
    def __init__(self, name: str):
        super().__init__(name)
        self.name = name


class ListLiteral(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.items = branches


class MonadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class DyadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class TriadicModifier(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
