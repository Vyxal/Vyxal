
self.addEventListener('message', function (e) {
    var data = e.data;
    Vyxal.execute(data.code, data.inputs, data.flags)
})
