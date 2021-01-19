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
        inputs = request.form["inputs"]
        header = request.form["header"]
        footer = request.form["footer"]
        # print(inputs)
        import Vyxal
        manager = multiprocessing.Manager()
        ret = manager.dict()
        ret[1] = ""
        ret[2] = ""
        fcode = header + code + footer
        process = multiprocessing.Process(target=Vyxal.execute, args=(fcode, flags, inputs, ret))
        process.start()
        process.join(60)

        if process.is_alive():
            process.kill()
            if 2 in ret:
                ret[2] += "\n" + "Code timed out after 60 seconds"
        # print(ret)
        output = ret[1]
        # print(code, flags, output)
        return render_template('index.html', code=code, header=header, footer=footer, flags=flags, output=output, inputs=inputs, errors=ret[2])

    return render_template('index.html', code="", flags="", output="", header="", footer="", inputs="", errors="")
