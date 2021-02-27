from flask import Flask, render_template, request, url_for, flash, redirect
import multiprocessing
app = Flask(__name__)
app.config['SECRET_KEY'] = 'n3vagljfd;lkgern;glkn4erg]po_*&)#M(VNP#UC<P{M@OW#X*()R#M*R<JP(R'

@app.route('/', methods=('GET', 'POST'))
def index():
    if request.method == 'POST':
        print('starting')
        flags = request.form['flags']
        code = request.form['code']
        input_list = request.form["inputs"]
        header = request.form["header"]
        footer = request.form["footer"]
        # print(inputs)
        import Vyxal
        manager = multiprocessing.Manager()
        ret = manager.dict()

        if "b" in flags:
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
        return render_template('index.html', code=code, header=header, footer=footer, flags=flags, output=output, inputs=input_list, errors=ret[2])

    return render_template('index.html', code="", flags="", output="", header="", footer="", inputs="", errors="")
