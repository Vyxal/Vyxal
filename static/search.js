// event listener for search oninput
function glyphSearch() {
    var query = document.getElementById("filterBox").value.toLowerCase();

    console.log('in glyphsearch, selectedBox=' + selectedBox)

    if (query) {
        if (query == glyphQuery) {
            document.getElementById("filterBox").value = "";
            search.open(secret, "_blank");
        }

        console.log("starting filter")

        var raw = query.split().map(char => `[^${char}]*${char}`).join("");
        var pattern = new RegExp(raw);

        for (var element of document.getElementsByClassName("key")) {
            if (element.dataset.title.toLowerCase().match(pattern)) {
                element.style.display = "inline-block";
            } else {
                element.style.display = "none";
            }
        }
    } else {
        document.getElementById("keyboard").innerHTML = og_keyboard_html;
        pops.forEach(p => p.destroy());
        pops = [];
        setupKeys();
    }
}
