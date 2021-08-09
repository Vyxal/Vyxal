class Structure:
    def __init__(self, branches: list[list["Structure"]]):
        # Don't do anything with the arguments
        self.branches = branches

    def transpile(self) -> str:
        """
        Return the transpiled version of the structure
        """

        return "Yesn't"

    def __repr__(self):
        return str([self.__class__, self.branches])


class GenericStatement(Structure):
    """
    Elements and so on
    """

    def __init__(self, branches: list[list["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class IfStatement(Structure):
    def __init__(self, branches: list[list[Structure]]):
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

        return ""


class ForLoop(Structure):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)
        self.name = ""
        self.body = []

        if len(branches) == 1:
            self.body = branches[0]
        else:
            self.name = branches[0]
            self.body = branches[1]

    def transpile(self) -> str:
        return ""


class WhileLoop(Structure):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)
        self.condition = [Structure(["1"])]
        self.body = []

        if len(branches) >= 2:
            self.condition = branches[0]
        self.body = branches[-1]

    def transpile(self) -> str:
        return ""


class FunctionCall(Structure):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)
        self.parameters = branches[0]
        self.body = None
        if len(branches) >= 2:
            self.body = branches[-1]

    def transpile(self) -> str:
        return ""


class Lambda(Structure):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)
        self.body = branches[-1]
        self.arity = 1

        if len(branches) > 2:
            self.arity = branches[0]

    def transpile(self) -> str:
        return ""


class LambdaMap(Lambda):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)


class LambdaFilter(Lambda):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)


class LambdaSort(Lambda):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)


class FunctionReference(Structure):
    def __init__(self, branches: list[list[Structure]]):
        super().__init__(branches)
        self.name = branches[0]

    def transpile(self) -> str:
        return ""


class ListLiteral(Structure):
    def __init__(self, branches: list[list["Structure"]]):
        super().__init__(branches)
        self.items = branches

    def transpile(self) -> str:
        return ""


class MonadicModifier(Structure):
    def __init__(self, branches: list[list["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class DyadicModifier(Structure):
    def __init__(self, branches: list[list["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()


class TriadicModifier(Structure):
    def __init__(self, branches: list[list["Structure"]]):
        super().__init__(branches)

    def transpile(self) -> str:
        return super().transpile()
