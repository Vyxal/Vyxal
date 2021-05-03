from flask import Flask, render_template, request, url_for, flash, redirect
import multiprocessing
app = Flask(__name__)
app.config['SECRET_KEY'] = 'n3vagljfd;lkgern;glkn4erg]po_*&)#M(VNP#UC<P{M@OW#X*()R#M*R<JP(R'

@app.route('/', methods=('GET', 'POST'))
def index():
    descriptions = parse_file()
    if request.method == 'POST':
        print('starting')
        flags = request.form['flags']
        code = request.form['code'].replace("\r", "")
        input_list = request.form["inputs"]
        header = request.form["header"].replace("\r", "")
        footer = request.form["footer"].replace("\r", "")
        # print(inputs)
        import Vyxal
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
        process = multiprocessing.Process(target=Vyxal.execute, args=(fcode, flags, input_list, ret))
        process.start()
        process.join(time)

        if process.is_alive():
            process.kill()
            if 2 in ret:
                ret[2] += "\n" + f"Code timed out after {time} seconds"
        # print(ret)
        output = ret[1]
        # print(code, flags, output)
        return render_template('main.html', code=code, header=header, footer=footer, flags=flags, output=output, inputs=input_list, debug=ret[2],
        codepage_info=descriptions)

    return render_template('main.html', code="", flags="", output="", header="", footer="", inputs="", debug="", codepage_info=descriptions)

@app.route("/ash")
def ash():
    return render_template("ash.html")

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