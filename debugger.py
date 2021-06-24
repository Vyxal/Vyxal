from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import *
import Vyxal


Val = Union[int, str, Vyxal.Generator, list]

@dataclass
class Position:
    row: int
    col: int

class Command:
    def __init__(self, py_code: str, breakpoint: bool = False):
        self.py_code = py_code
        self.breakpoint = breakpoint

    def execute(self):
        exec(self.py_code)


class Scope(ABC):
    @abstractmethod
    def get_cmds(self) -> list[Command]:
        pass


class ScopedCommand(ABC, Command):
    def __init__(self, cmds: list[Command], py_code: str, breakpoint: bool):
        super(Command, self).__init__(py_code, breakpoint)
        self.cmds = cmds

    def get_cmds(self):
        return self.cmds


class CommandGroup(ScopedCommand):
    def __init__(self, cmds: list[Command], py_code: str):
        super(Command, self).__init__(py_code, breakpoint=False)


class WhileLoop(ScopedCommand):
    def __init__(self, cmds: list[Command], py_code: str, breakpoint=false):
        super(WhileLoop, self).__init__(cmds, py_code, breakpoint)


class ForLoop(ScopedCommand):
    def __init__(self, cmds: list[Command], py_code: str, breakpoint=false):
        super(ForLoop, self).__init__(cmds, py_code, breakpoint)


class MapOrFilterLambda(ScopedCommand):
    def __init__(self, cmds: list[Command], py_code: str, breakpoint=false):
        super(MapOrFilterLambda, self).__init__(cmds, py_code, breakpoint)


def patch_function(fn: Vyxal.Function, cmds: list[Command], orig_start: Position, orig_end: Position) -> Scope:
    """
    fn: The runnable Python function object
    cmds: The Vyxal commands that comprise this function

    """
    fn.get_cmds = lambda: cmds
    fn.orig_start = orig_start
    fn.orig_end = orig_end
    return fn


class Frame:
    def __init__(self, cmds: list[Command], py_code: str, vars: dict[str, Val], breakpoint: bool):
        self.ind = 0
        self.cmds = cmds
        self.py_code = py_code
        self.vars = vars.copy()
        self.breakpoint = breakpoint


class Debugger:
    def __init__(self, cmds: list[Command]):
        self.ind = 0
        self.call_stack: list[Frame] = []
        self.cmds = cmds

    def step(self):
        """Run the next command"""
        self.curr_cmd().execute()
        self.next_cmd()

    def step_in(self):
        """Step into a steppable command or run it if it's a single command"""
        if isinstance(self.curr_cmd(), ScopedCommand):
            pass
        else:
            self.curr_cmd().execute()
            self.next_cmd()

    def step_out(self):
        """Finish running this iteration of this steppable command"""
        last_frame = self.call_stack[-1]
        

    def step_back(self):
        """Go back a frame"""
        self.call_stack.pop()
        pass

    def resume(self):
        """Continue on until the next breakpoint"""
        pass

    def continue_to_end(self):
        while self.ind < len(self.cmds):
            self.step()

    def curr_cmd(self) -> Command:
        return self.cmds[self.ind]

    def next_cmd(self):
        self.ind += 1

    @staticmethod
    def make_frame(cmd: ScopedCommand, cmd_start: int, cmd_end) -> Frame:
        return Frame(cmd.cmds, cmd.py_code, breakpoint=cmd.breakpoint)
