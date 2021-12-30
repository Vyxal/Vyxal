"""defines structure classes to represent the syntactic components of Vyxal

See https://github.com/Vyxal/Vyxal/blob/fresh-beginnings/documents/specs/Structures.md
"""

from typing import Union

from vyxal.lexer import Token

Branch = Union[str, list["Structure"], list["Token"]]


class Structure:
    def __init__(self, *branches: Branch):
        # Don't do anything with the arguments
        self.branches = list(branches)

    def transpile(self) -> str:
        """Return the transpiled version of the structure"""
        return "{}"

    def __repr__(self):
        return f"{type(self).__name__}({repr(self.branches)})"


class GenericStatement(Structure):
    """Generic statements are elements and so on"""

    def __init__(self, *branches: Branch):
        super().__init__(*branches)


class BreakStatement(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.parent_structure = branches[0]


class RecurseStatement(Structure):
    def __init__(self, *branches: Branch):
        super().__init__(*branches)
        self.parent_structure = branches[0]


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
    def __init__(self, arity: int, body: list[Structure]):
        super().__init__(str(arity), body)
        self.arity = arity
        self.body = body
        self.force_eval = can_print(body)


class LambdaMap(Lambda):
    def __init__(self, body: list[Structure]):
        super().__init__(1, body)


class LambdaFilter(Lambda):
    def __init__(self, body: list[Structure]):
        super().__init__(1, body)


class LambdaSort(Lambda):
    def __init__(self, body: list[Structure]):
        super().__init__(1, body)


class ListLiteral(Structure):
    def __init__(self, *items: Branch):
        super().__init__(*items)
        self.items = items


class MonadicModifier(Structure):
    def __init__(self, modifier: str, *branches: Branch):
        super().__init__(*branches)
        self.modifier = modifier
        self.function_A = branches[0]

    def __repr__(self):
        return (
            f"{type(self).__name__}({repr(self.branches)}, {self.modifier!r})"
        )


class DyadicModifier(Structure):
    def __init__(self, modifier: str, *branches: Branch):
        super().__init__(*branches)
        self.modifier = modifier
        self.function_A = branches[0]
        self.function_B = branches[1]

    def __repr__(self):
        return (
            f"{type(self).__name__}({repr(self.branches)}, {self.modifier!r})"
        )


class TriadicModifier(Structure):
    def __init__(self, modifier: str, *branches: Branch):
        super().__init__(*branches)
        self.modifier = modifier
        self.function_A = branches[0]
        self.function_B = branches[1]
        self.function_C = branches[2]

    def __repr__(self):
        return (
            f"{type(self).__name__}({repr(self.branches)}, {self.modifier!r})"
        )


def can_print(structures: list[Structure]) -> bool:
    """Checks if the structures are printing instructions"""
    for struct in structures:
        if isinstance(
            struct,
            (
                BreakStatement,
                RecurseStatement,
                FunctionCall,
            ),
        ):
            continue  # These statements can't possibly print, so it's
            # okay to just continue.
        elif isinstance(struct, GenericStatement):
            if struct.branches[0][0].value in list(",₴…") + ["¨,", "¨…"]:
                return True
        else:
            for branch in struct.branches:
                if isinstance(branch, (Structure)):
                    branch = [branch]
                if isinstance(branch, (list, tuple)):
                    if can_print(branch):
                        return True

    return False
