from flask import Flask, render_template, request, url_for, flash, redirect
from flask_cors import CORS 
import multiprocessing, secrets
import Vyxal
app = Flask(__name__)
CORS(app)

import os, sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

os.system("rm -rf sessions")
os.system("mkdir sessions")

sessions = {}
terminated = set()

@app.route('/', methods=('POST','GET'))
def index():
    session = secrets.token_hex(64)
    sessions[session] = None
    return render_template('main.html', session=session, codepage_info=descriptions)

@app.route("/execute", methods=('POST',))
def execute():
    flags = request.form['flags']
    code = request.form['code'].replace("\r", "")
    input_list = request.form["inputs"].replace("\r", "")
    header = request.form["header"].replace("\r", "")
    footer = request.form["footer"].replace("\r", "")
    session = request.form["session"]

    if session not in sessions:
      return {"stdout": "", "stderr": "The session was invalid! You may need to reload your tab."}

    os.system(f"rm -rf sessions/{session}")
    os.system(f"mkdir sessions/{session}")

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
                time = 10
            elif "b" in flags:
                time = 15
            elif "B" in flags:
                time = 30
            else:
                time = 60
            ret[1] = ""
            ret[2] = ""
            fcode = header + code + footer
            sessions[session] = multiprocessing.Process(target=Vyxal.execute, args=(fcode, flags, input_list, ret))
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
    os.system(f"rm -rf sessions/{session}")
    return val


@app.route("/kill", methods=("POST",))
def kill():
  session = request.form["session"]
  if sessions.get(session) is None: return ""
  sessions[session].kill()
  terminated.add(session)
  return ""

def parse_file():
    import os
    ret = []
    keys = {}
    THIS_FOLDER = os.path.dirname(os.path.abspath(__file__))
    file = os.path.join(THIS_FOLDER, 'docs/elements.txt')
    with open(file, "r", encoding="utf8") as txt:
        for line in txt:
            if line == "\n": #Finished
                break
            else:
                is_digraph = line[0] in "k∆øÞ¨" and line[1] != " "
                if is_digraph:
                    letter = line[1]
                else:
                    letter = line[0]
                
                if letter in keys and letter != " ":
                    index = keys[letter]
                else:
                    index = -1
                    keys[letter] = len(ret)
                
                if line[0] == " ":
                    if is_digraph:
                        print(letter, line)
                        ret[index] += "\n" + line[0] + ": " + line[3:-1]
                    else:
                        ret[index] += "\n" + line[1:-1]
                else:
                    if is_digraph:
                        ret[index] += "\n" + line[0] + ": " + line[3:-1]
                    else:
                        ret.append("\n" + line[1:-1])
    return ret

descriptions = parse_file()
