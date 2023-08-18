importScripts("./vyxal.js")

self.addEventListener('message', function (e) {
    var data = e.data;
    console.log("Worker received: " + data.mode);
    const session = data.session;
    const sendFn = x => {
        this.postMessage({ "val": x, "command": "append", "session": session })
    };
    Vyxal.setShortDict(data.shortDict)
    Vyxal.setLongDict(data.longDict)
    Vyxal.execute(data.code, data.inputs, data.flags, sendFn)
    this.postMessage({ "command": "done", "session": data.session })
})
