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
