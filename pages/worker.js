importScripts("../js/lib/vyxal.js")
self.addEventListener('message', function (e) {
    var data = e.data;
    console.log("Worker received: " + data.mode);
    var sendFn = "x => { this.postMessage({ \"val\": x, \"command\": \"append\", \"session\": \"" + data.session + "\" })}"
    Vyxal.execute(data.code, data.inputs, data.flags, sendFn)
    this.postMessage({ "command": "done", "session": data.session })
})
