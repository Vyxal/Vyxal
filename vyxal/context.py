"""This is for context-related stuff."""


class Context:
    """Context objects hold flags and semi-global variables to pass around."""

    def __init__(self):
        self.context_values = [0]
        self.inputs = [[[], 0]]  # [[[inputs], index], [[inputs], index]]
        self.online = False
        self.repl_mode = False
        self.retain_popped = False
        self.reverse_flag = False
        self.last_popped = []
        self.use_top_input = False
