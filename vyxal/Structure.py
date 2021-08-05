class Structure:
    def __init__(self, branches: list[list["Structure"]]):
        # Don't do anything with the arguments
        self.branches = branches

    def transpile(self) -> str:
        """
        Return the transpiled version of the structure
        """

        return "Yesn't"


class If_Statement(Structure):
    def __init__(self, branches: list[list[Structure]]):
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


class For_Loop(Structure):
    def __init__(self, branches: list[list[Structure]]):
        self.name = ""
        self.body = []

        if len(branches) == 1:
            self.body = branches[0]
        else:
            self.name = branches[0]
            self.body = branches[1]

    def transpile(self) -> str:
        return ""


class While_Loop(Structure):
    def __init__(self, branches: list[list[Structure]]):
        self.condition = [Structure(["1"])]
        self.body = []

        if len(branches) >= 2:
            self.condition = branches[0]
        self.body = branches[-1]

    def transpile(self) -> str:
        return ""


class Function_Call(Structure):
    def __init__(self, branches: list[list[Structure]]):
        self.parameters = branches[0]
        self.body = None
        if len(branches) >= 2:
            self.body = branches[-1]

    def transpile(self) -> str:
        return ""


class Lambda(Structure):
    def __init__(self, branches: list[list[Structure]]):
        self.body = branches[-1]
        self.arity = 1

        if len(branches) > 2:
            self.arity = branches[0]

    def transpile(self) -> str:
        return ""


class Function_Reference(Structure):
    def __init__(self, branches: list[list[Structure]]):
        self.name = branches[0]

    def transpile(self) -> str:
        return ""


class List_Literal(Structure):
    def __init__(self, branches: list[list["Structure"]]):
        self.items = branches

    def transpile(self) -> str:
        return ""
