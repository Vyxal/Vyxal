"""This is for context-related stuff."""



class Context:
    """Context objects hold flags and semi-global variables to pass around."""

    def __init__(self):
        self.context_values = [0]
        self.inputs = [[[], 0]]  # [[[inputs], index], [[inputs], index]]
        self.number_as_range = False
        self.online = False
        self.online_output = ""
        self.printed = False
        self.range_start = 1  # Where do auto ranges start?
        self.range_end = 1  # How much to add to the end
        self.repl_mode = False
        self.retain_popped = False
        self.reverse_flag = False
        self.stack = []
        self.last_popped = []
        self.use_top_input = False


DEFAULT_CTX = Context()
