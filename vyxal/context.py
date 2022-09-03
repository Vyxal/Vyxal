"""This is for context-related stuff."""

from vyxal.Canvas import Canvas
import sympy

# Like a ctx but for transpilation
# This has to be here because *fricking circular inputs*
class TranspilationOptions:
    def __init__(self):
        self.dict_compress = True
        self.utf8strings = False
        self.variables_as_digraphs = False


class Context:
    """Context objects hold flags and semi-global variables to pass around."""

    def __init__(self):
        self.context_values = [0]
        self.empty_input_is_zero = True
        self.inputs_as_strings = False
        self.default_arity = 1
        self.ghost_variable = sympy.nsimplify(0)
        self.function_stack = []
        self.inputs = [[[], 0]]  # [[[inputs], index], [[inputs], index]]
        # inputs[0] = [[inputs], index]
        # inputs[0][0] = [inputs]
        # inputs[0][0][n] = input_n
        # inputs[1] = index
        self.number_as_range = False
        self.online = False
        self.online_output = ""
        self.print_decimals = False
        self.printed = False
        self.range_start = 1  # Where do auto ranges start?
        self.range_end = 1  # How much to add to the end
        self.register = sympy.nsimplify(0)
        self.repl_mode = False
        self.retain_popped = False
        self.reverse_flag = False
        self.array_inputs = False  # a flag
        self.stacks = []
        self.vectorise_boolify = False
        self.last_popped = []
        self.use_top_input = False
        self.double_zip_vectorize = False
        self.vyxal_lists = True
        self.global_array = []
        self.canvas = Canvas()
        self.utf8strings = False
        self.transpilation_options = TranspilationOptions()

    def copy(self, number_as_range=None, range_start=None):
        """Copy itself so a modified version can be passed elsewhere."""
        ctx = Context()

        ctx.context_values = self.context_values
        ctx.inputs = self.inputs
        ctx.number_as_range = (
            self.number_as_range if number_as_range is None else number_as_range
        )
        ctx.online = self.online
        ctx.online_output = self.online_output
        ctx.printed = self.printed
        ctx.range_start = (
            self.range_start if range_start is None else range_start
        )
        ctx.range_end = self.range_end
        ctx.repl_mode = self.repl_mode
        ctx.retain_popped = self.retain_popped
        ctx.reverse_flag = self.reverse_flag
        ctx.stacks = self.stacks
        ctx.last_popped = self.last_popped
        ctx.use_top_input = self.use_top_input
        ctx.global_array = self.global_array
        ctx.utf8strings = self.utf8strings
        ctx.transpilation_options = self.transpilation_options

        return ctx


DEFAULT_CTX = Context()
