from flask import Flask, render_template, request, url_for, flash, redirect
from flask_cors import CORS
import multiprocessing, secrets
import Vyxal
import git

app = Flask(__name__)
CORS(app)

import os, sys, shutil

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

shutil.rmtree("sessions", ignore_errors=True)
os.system("mkdir sessions")

sessions = {}
terminated = set()


@app.route("/", methods=("POST", "GET"))
def index():
    session = secrets.token_hex(64)
    sessions[session] = None
    return render_template("main.html", session=session, codepage_info=descriptions)


@app.route("/execute", methods=("POST",))
def execute():

    print(sessions, request.form)
    flags = request.form["flags"]
    code = request.form["code"].replace("\r", "")
    input_list = request.form["inputs"].replace("\r", "")
    header = request.form["header"].replace("\r", "")
    footer = request.form["footer"].replace("\r", "")
    session = request.form["session"]

    print(code)

    if session not in sessions:
        return {
            "stdout": "",
            "stderr": "The session was invalid! You may need to reload your tab.",
        }

    shutil.rmtree(f"sessions/{session}", ignore_errors=True)
    os.mkdir(f"sessions/{session}")

    with open(f"sessions/{session}/.stdin", "w", encoding="utf-8") as f:
        f.write(input_list)

    with open(f"sessions/{session}/.stdin", "r", encoding="utf-8") as x:
        with open(f"sessions/{session}/.stdout", "w", encoding="utf-8") as y:
            with open(f"sessions/{session}/.stderr", "w", encoding="utf-8") as z:
                manager = multiprocessing.Manager()
                ret = manager.dict()

                if "5" in flags:
                    time = 5
                elif "T" in flags:
                    time = 60
                elif "b" in flags:
                    time = 15
                elif "B" in flags:
                    time = 30
                else:
                    time = 10
                ret[1] = ""
                ret[2] = ""
                fcode = header + "\n" + code + "\n" + footer
                sessions[session] = multiprocessing.Process(
                    target=Vyxal.execute, args=(fcode, flags, input_list, ret)
                )
                sessions[session].start()
                sessions[session].join(time)

                if session in terminated:
                    terminated.remove(session)
                    ret[2] += "\nSession terminated upon user request"

                if sessions[session].is_alive():

                    sessions[session].kill()
                    if 2 in ret:
                        ret[2] += "\n" + f"Code timed out after {time} seconds"
                output = ret[1]
                y.write(ret[1])
                z.write(ret[2])
    with open(f"sessions/{session}/.stdout", "r", encoding="utf-8") as x:
        with open(f"sessions/{session}/.stderr", "r", encoding="utf-8") as y:
            val = {"stdout": x.read(), "stderr": y.read()}
    shutil.rmtree(f"sessions/{session}", ignore_errors=True)
    return val


@app.route("/kill", methods=("POST",))
def kill():
    session = request.form["session"]
    if sessions.get(session) is None:
        return ""
    sessions[session].kill()
    terminated.add(session)
    return ""


@app.route("/oeis", methods=("GET",))
def oeis():
    return render_template("oeis.html")


@app.route("/update", methods=("GET", "POST"))
def update():
    # Updates the server after a commit
    # this comment is to test to see if i did the stuff right ;p
    # It's possible that it is now working.
    if request.method == 'POST':
        repo = git.Repo('/home/Lyxal/mysite')
        origin = repo.remotes.origin
        with repo.config_writer() as git_config:
            git_config.set_value('user', 'email', "36217120+Lyxal@users.noreply.github.com")
            git_config.set_value('user', 'name', "Lyxal")
        origin.pull()
        return 'Updated PythonAnywhere successfully', 200
    else:
        return 'Wrong event type', 400

def parse_file():
    import os

    ret = []
    keys = {}
    THIS_FOLDER = os.path.dirname(os.path.abspath(__file__))
    file = os.path.join(THIS_FOLDER, "docs/elements.txt")
    with open(file, "r", encoding="utf8") as txt:
        LETTER, MODIFIER = "LETTER", "MODIFIER"

        previous = {LETTER: "", MODIFIER: ""}
        for line in txt:
            if line == "\n": break # Reached EOF
            char = line[:2]
            if char != "  ": 
                if char[-1] != " " and char[0] != "<":
                    previous = {LETTER: line[1], MODIFIER: line[0]}
                else:
                    previous[LETTER] = line[0]
                    previous[MODIFIER] = ""
            
            if char[0] == "<" and char[1] in "ns":
                if "<newline>" in line:
                    previous[LETTER] = "␤"
                if "<space>" in line:
                    previous[LETTER] = "␠"
                
                previous[MODIFIER] = ""

            if previous[LETTER] in keys:
                index = keys[previous[LETTER]]
            else:
                index = -1
                keys[previous[LETTER]] = len(ret)
            
            # print(char, index, previous[LETTER])
            if index == -1:
                ret.append("\n" + line[1:-1])
            
            elif previous[MODIFIER]:
                ret[index] += "\n" + previous[MODIFIER] + ": " + line[3:-1]
            
            else:
                ret[index] += "\n" + line[1:-1]
    return ret

descriptions = parse_file()
