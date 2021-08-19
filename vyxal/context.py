"""This is for context-related stuff."""


class Context:
    """Context objects hold flags and semi-global variables to pass around."""

    def __init__(self):
        self.context_values = [0]
        self.inputs = []
