importScripts("../js/lib/scalajs-3.0.0.js")
self.addEventListener('message', function (e) {
    var data = e.data;
    console.log("Worker received: " + data.mode);
    let res = Vyxal.execute(data.code, data.inputs, data.flags)
    this.postMessage(res)
})