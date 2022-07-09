import pdb
import linecache
import tempfile
import re
import sys

from vyxal.transpile import COLUMN_REGEX

class Vdb(pdb.Pdb):
    def __init__(self, code, source, env):
        super().__init__()
        self.fp = tempfile.NamedTemporaryFile()
        self.fp.write(bytes(code, "UTF-8"))
        self.code = compile(code, self.fp.name, "exec")
        self.source = source
        self.env = env
        self.col_to_line = self._parse_file()
        self.line_to_col = {v: k for k, v in self.col_to_line.items()}
        self.enabled_bps = [False] * (len(self.col_to_line) + 1)

    def _parse_file(self):
        result = {}
        for i, line in enumerate(linecache.getlines(self.fp.name)):
            if m := re.match(COLUMN_REGEX, line):
                column = int(m.groups()[0])
                result[column] = i + 2
                self.set_break(self.fp.name, i + 2)
        return result

    def _disable_all_bps(self):
        for i in range(len(self.col_to_line)):
            self.get_bpbynumber(i + 1).disable()

    def _enable_all_bps(self):
        for i in range(len(self.col_to_line)):
            self.get_bpbynumber(i + 1).enable()

    def run(self):
        self._wait_for_mainpyfile = True
        self.mainpyfile = self.fp.name
        self._user_requested_quit = False
        self.reset()
        sys.settrace(self.trace_dispatch)
        exec(self.code, self.env)

    def print_stack_entry(self, frame_lineno):
        _, lineno = frame_lineno
        try:
            col = self.line_to_col[lineno]
            self.message("\n---")
            self.do_p("stack")
            self.message(f"> {self.source.strip()}\n> {' '*(col - 1) + '^'}")
        except:
            # this should never happen
            super().print_stack_entry(frame_lineno)

    def do_enable(self, arg):
        args = map(int, arg.split())
        for i in args:
            self.enabled_bps[i] = True

    def do_disable(self, arg):
        args = map(int, arg.split())
        for i in args:
            self.enabled_bps[i] = False

    def do_step(self, arg):
        self._enable_all_bps()
        return super().do_continue(arg)

    do_s = do_step

    def do_continue(self, arg):
        self._disable_all_bps()
        for i, enabled in enumerate(self.enabled_bps):
            if enabled:
                self.get_bpbynumber(i).enable()
        return super().do_continue(arg)

    do_c = do_continue

"""
the idea is to write a temporary file of the transpiled code, which has been
marked with the columns, and to add breakpoints at the lines corresponding
to those columns. we overwrite pdb.cmdloop so we don't accept input from the
user. then we can enable and disable breakpoints as desired, to simulate
stepping, continuing until breakpoint, etc. etc.

import pdb
import tempfile
fp = tempfile.NamedTemporaryFile()
fp.write(bytes(code, "UTF-8"))
fp.flush()
fp.seek(0)
db = pdb.Pdb()
db._wait_for_mainpyfile = True
db.mainpyfile = fp.name
db._user_requested_quit = False
stmt = compile(code, db.mainpyfile, "exec")
db.run(stmt, locals() | globals())
"""
