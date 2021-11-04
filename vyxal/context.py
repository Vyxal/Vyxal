"""This is for context-related stuff."""


class Context:
    """Context objects hold flags and semi-global variables to pass around."""

    def __init__(self):
        self.context_values = [0]
        self.inputs = [[[], 0]]  # [[[inputs], index], [[inputs], index]]
        # inputs[0] = [[inputs], index]
        # inputs[0][0] = [inputs]
        # inputs[0][0][n] = input_n
        # inputs[1] = index
        self.number_as_range = False
        self.online = False
        self.online_output = ""
        self.printed = False
        self.range_start = 1  # Where do auto ranges start?
        self.range_end = 1  # How much to add to the end
        self.repl_mode = False
        self.retain_popped = False
        self.reverse_flag = False
        self.stacks = []
        self.last_popped = []
        self.use_top_input = False

    def copy(self):
        """Copy itself so a modified version can be passed elsewhere."""

        ctx = Context()

        ctx.context_values = self.context_values
        ctx.inputs = self.inputs
        ctx.number_as_range = self.number_as_range
        ctx.online = self.online
        ctx.online_output = self.online_output
        ctx.printed = self.printed
        ctx.range_start = self.range_start
        ctx.range_end = self.range_end
        ctx.repl_mode = self.repl_mode
        ctx.retain_popped = self.retain_popped
        ctx.reverse_flag = self.reverse_flag
        ctx.stacks = self.stacks
        ctx.last_popped = self.last_popped
        ctx.use_top_input = self.use_top_input

        return ctx

    @property
    def stack(self):
        return self.stacks[-1]

    @stack.setter
    def stack(self, new_stack):
        self.stacks[-1] = new_stack

    @stack.deleter
    def stack(self):
        self.stacks.pop(-1)


DEFAULT_CTX = Context()
