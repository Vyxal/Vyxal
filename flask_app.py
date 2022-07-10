from hashlib import sha256
from hmac import compare_digest
import multiprocessing
import os
import secrets
import shutil
import sys

from flask import Flask, render_template, request
from flask_cors import CORS

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.main import execute_vyxal

app = Flask(__name__)
CORS(app)

FUNKY_PASSWORD_HASH = (
    "411b514435eaffc4fc36b25b40347761af7cbf644c1e92e1fe190e6ebcf4b2d2"
)

shutil.rmtree("sessions", ignore_errors=True)
os.system("mkdir sessions")

sessions = {}
terminated = set()


@app.route("/", methods=("POST", "GET"))
def index():
    session = secrets.token_hex(64)
    sessions[session] = None
    return render_template("index.html", session=session, codepage_info="No.")


@app.route("/execute", methods=("POST",))
def execute():

    print(sessions, request.json)
    if request.json is None:
        return {
            "stdout": "",
            "stderr": "",
        }

    flags = request.json["flags"] + "e"
    code = request.json["code"].replace("\r", "")
    input_list = request.json["inputs"].replace("\r", "")
    header = request.json["header"].replace("\r", "")
    footer = request.json["footer"].replace("\r", "")
    session = request.json["session"]

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

    with (
        open(f"sessions/{session}/.stdin", "r", encoding="utf-8") as x,
        open(f"sessions/{session}/.stdout", "w", encoding="utf-8") as y,
        open(f"sessions/{session}/.stderr", "w", encoding="utf-8") as z,
    ):
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
        fcode = (
            (header and (header + "\n")) + code + (footer and ("\n" + footer))
        )

        sessions[session] = multiprocessing.Process(
            target=execute_vyxal,
            args=(fcode, flags, input_list, ret, True),
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
        y.write(ret[1])
        z.write(ret[2])
    with open(f"sessions/{session}/.stdout", "r", encoding="utf-8") as x, open(
        f"sessions/{session}/.stderr", "r", encoding="utf-8"
    ) as y:
        val = {"stdout": x.read(), "stderr": y.read()}
    shutil.rmtree(f"sessions/{session}", ignore_errors=True)
    return val


@app.route("/kill", methods=("POST",))
def kill():
    if request.json is None:
        return ""
    session = request.json["session"]
    if sessions.get(session) is None:
        return ""
    sessions[session].kill()
    terminated.add(session)
    return ""


@app.route("/oeis", methods=("GET",))
def oeis():
    return render_template("oeis.html")


@app.route("/update", methods=("POST",))
def update():
    key = request.headers.get("X-funky-password", "")
    if compare_digest(sha256(key.encode()).hexdigest(), FUNKY_PASSWORD_HASH):
        if os.fork() == 0:
            os.system("/home/Vyxal/mysite/funky_upgrade.sh")
            os._exit()
        return "updated successfully", 200
    else:
        return "incorrect or missing X-funky-password header", 403


@app.route("/version", methods=("GET",))
def version():
    import subprocess

    VERSION = (
        subprocess.check_output(
            [
                "git",
                "--git-dir",
                "/home/Vyxal/mysite/.git",
                "--work-tree",
                "/home/Vyxal/mysite",
                "rev-parse",
                "HEAD",
            ]
        )
        .decode()
        .strip()
    )

    return VERSION

app.run()
