console.log("here")
import { Vyxal } from "./vyxal.js"
console.log("here2")
console.log(Object.getOwnPropertyNames(Vyxal))
//console.err("asdf")
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
