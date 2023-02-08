importScripts("https://raw.githubusercontent.com/Vyxal/Vyxal/v3-online-interpreter/pages/worker.js")
self.addEventListener('message', function (e) {
    var data = e.data;
    Vyxal.execute(data.code, data.inputs, data.flags)
})
