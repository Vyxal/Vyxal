importScripts("../js/lib/scalajs-3.0.0.js")
self.addEventListener('message', function (e) {
    var data = e.data;
    if (data.mode == "stop") {
        this.close();
    }
    this.postMessage(Vyxal.execute(data.code, data.inputs, data.flags))
})
