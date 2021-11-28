var codepage = "λƛ¬∧⟑∨⟇÷×«␤»°•ß†€"
codepage += "½∆ø↔¢⌐æʀʁɾɽÞƈ∞¨␠"
codepage += "!\"#$%&'()*+,-./01"
codepage += "23456789:;<=>?@A"
codepage += "BCDEFGHIJKLMNOPQ"
codepage += "RSTUVWXYZ[\\]`^_abc"
codepage += "defghijklmnopqrs"
codepage += "tuvwxyz{|}~↑↓∴∵›"
codepage += "‹∷¤ð→←βτȧḃċḋėḟġḣ"
codepage += "ḭŀṁṅȯṗṙṡṫẇẋẏż√⟨⟩"
codepage += "‛₀₁₂₃₄₅₆₇₈¶⁋§ε¡"
codepage += "∑¦≈µȦḂĊḊĖḞĠḢİĿṀṄ"
codepage += "ȮṖṘṠṪẆẊẎŻ₌₍⁰¹²∇⌈"
codepage += "⌊¯±₴…□↳↲⋏⋎꘍ꜝ℅≤≥"
codepage += "≠⁼ƒɖ∪∩⊍£¥⇧⇩ǍǎǏǐǑ"
codepage += "ǒǓǔ⁽‡≬⁺↵⅛¼¾Π„‟"

search = window
glyphQuery = String.fromCharCode(0162, 105, 0143, 107)
this.prevQuery = ""
secret = "dQw4"
secret += secret[2]
temp = "9WgXc"
secret += temp + secret[1]
temp = "out"
temp += temp[1] + "."
temp += "be"
temp = "y" + temp
temp = codepage[47] + temp
temp = codepage[115] + codepage[58] + temp[0] + temp
secret = "tp" + temp + "/" + secret
secret = "h" + codepage[116] + secret


var og_keyboard_html
var selectedBox = 'code' //whether 'header', 'code', or 'footer' are selected


function resizeCodeBox(id) {
    // Resize the code box with the given id
    var element = document.getElementById(id)
    element.style.height = ""
    element.style.height = element.scrollHeight + 4 + "px"
}

function updateCount() {
    var byte_box = document.getElementById("code-count")

    var code = e_code.getValue()
    if ([...code].every(x => (codepage + ' ' + '\n').includes(x))) {
        byte_box.innerText = `Code: ${code.length} byte` + "s".repeat(code.length != 1)
    } else {
        var x = new Blob([code]).size
        byte_box.innerText = `Code: ${x} byte${"s".repeat(x != 1)}` + ' (UTF-8)'
    }
}

function encode(obj) {
    return btoa(unescape(encodeURIComponent(JSON.stringify(obj))));
}

function decode(str) {
    return JSON.parse(decodeURIComponent(escape(atob(str))));
}

function generateURL() {
    var flags = document.getElementById("flag").value
    var code = e_code.doc.getValue()
    var inputs = document.getElementById("inputs").value
    var header = e_header.doc.getValue()
    var footer = e_footer.doc.getValue()

    var url = [flags, header, code, footer, inputs];
    return "https://vyxal.pythonanywhere.com/#" + encode(url)
}

function shareOptions(shareType) {
    var code = e_code.doc.getValue()
    var url = generateURL()
    var flags = document.getElementById("flag").value
    var flag_appendage = ","
    if (flags) {
        flag_appendage = " `" + flags + "`,"
    }
    var output = ""
    var utfable = [...code].every(x => (codepage + ' ' + '\n').includes(x))
    var len = utfable ? code.length : new Blob([code]).size
    switch (shareType) {
        case "permalink":
            output = url
            break
        case "cmc":
            output = `[Vyxal, ${len} byte${"s".repeat(code.length != 1)}${utfable ? '' : ' (UTF-8)'}](${url})`
            break
        case "post-template":
            output = `# [Vyxal](https://github.com/Vyxal/Vyxal)${flag_appendage} ${len} byte${"s".repeat(len != 1)}${utfable ? '' : ' (UTF-8)'}
\`\`\`
${code}
\`\`\`
[Try it Online!](${url})`;
            break
        case "markdown":
            output = `[Try it Online!](${url})`
            break
    }
    var outputBox = document.getElementById("output")
    outputBox.value = output
    copyToClipboard("output")
    resizeCodeBox("output")
    expandBoxes()
}

function decodeURL() {
    var [flags, header, code, footer, inputs] = decode(window.location.hash.substring(1));

    var flag_box = document.getElementById("flag")
    var inputs_box = document.getElementById("inputs")

    var queryIsNonEmpty = code || flags || inputs || header || footer
    var allBoxesAreEmpty = !(flag_box.value
        || e_header.getValue() || e_code.getValue()
        || e_footer.getValue() || inputs_box.value)

    if (queryIsNonEmpty && allBoxesAreEmpty) {
        flag_box.value = flags
        e_code.doc.setValue(code)
        inputs_box.value = inputs
        e_header.doc.setValue(header)
        e_footer.doc.setValue(footer)
        e_header.refresh()
        e_footer.refresh()
        run_button.click()
    } else {
        expandBoxes()
    }
}

function expandBoxes() {
    ["flag", "inputs", "output", "extra"].forEach(function (n) {
        var boxToExpand = document.getElementById(n + "-detail")
        var actualBox = document.getElementById(n)

        if (actualBox.value) {
            boxToExpand.open = true
            resizeCodeBox(n)


        } else {
            boxToExpand.open = false
        }
    })

    if (e_header.getValue()) {
        document.getElementById("header-detail").open = true
        e_header.refresh()
    }

    if (e_footer.getValue()) {
        document.getElementById("footer-detail").open = true
        e_footer.refresh()
    }
}


function replaceHTMLChar(char) {
    return char === "␤" ? "\n" :
        char === "␠" ? " " :
            char === "&lt;" ? "<" :
                char === "&gt;" ? ">" :
                    char === "&amp;" ? "&" : char
}

function copyToClipboard(arg) {
    var el = document.getElementById(arg).value
    navigator.clipboard.writeText(el)
}

$(document).ready(e => {
    const run = document.getElementById("run_button")
    const session = $("session-code")[0].innerHTML

    const stdin = document.getElementById("inputs")
    const flags = document.getElementById("flag")
    const output = document.getElementById("output")
    const extra = document.getElementById("extra")
    const filter = document.getElementById("filterBox")

    function do_run() {
        if (!run.innerHTML.includes("fa-spin")) {
            run.innerHTML =
                `<svg class="fa-spin" style="width:24px;height:24px" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M12,4V2A10,10 0 0,0 2,12H4A8,8 0 0,1 12,4Z"/>
                </svg>`;
            $.post("/execute", {
                code: e_code.doc.getValue(),
                inputs: stdin.value,
                flags: flags.value,
                session: session,
                footer: e_footer.doc.getValue(),
                header: e_header.doc.getValue()
            }, res => {
                output.value = res.stdout
                extra.value = res.stderr
                run.innerHTML =
                    `<i class="fas fa-play-circle"></i>
                    `;
                if (e_code.doc.getValue() == 'lyxal') {
                    location.href = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'
                }
                expandBoxes()
            })
        } else {
            $.post("/kill", { session: session }, res => 0)
        }
    }

    $("#run_button").on("click", do_run)

    $("#clear").on("click", e => {
        e_code.doc.setValue('')
        stdin.value = ""
        output.value = ""
        extra.value = ""
        e_footer.doc.setValue('')
        e_header.doc.setValue('')
        updateCount()
        flags.value = ""
        filter.value = ""
        glyphSearch()
        expandBoxes()
    })

})

document.addEventListener('keydown', (event) => {
    if (event.ctrlKey && event.key == 'Enter') {
        $("#run_button").click()
    }
})

// Codemirror stuff begins here
function initCodeMirror() {
    const $$$ = x => document.querySelector(x)

    //Get the corresponding codemirror textarea for any of 'code', 'header', and 'footer'
    function getCodeMirrorTextArea(boxId) {
        return $('#' + boxId).parent().children('div').children().not('[class]').children()[0]
    }

    function resize(elem) {
        var dummy = $$$("#dummy")
        dummy.style.fontFamily = getComputedStyle($$$('.CodeMirror.cm-s-default')).fontFamily
        dummy.style.fontSize = '1em'
        dummy.style.lineHeight = '1em'

        dummy.value = elem.doc.getValue()
        elem.setSize(
            null,
            Math.max(dummy.scrollHeight - 5, elem.getTextArea().dataset.baseHeight || 27)
        )
        dummy.value = ""

        // Make sure e_code is not null
        if ("e_code" in globalThis) {
            updateCount()
        }
    }

    let mode = {
        mode: 'vyxal',
        lineWrapping: true
    }

    for (const boxId of ['header', 'code', 'footer']) {
        console.log(boxId)
        globalThis['e_' + boxId] = CodeMirror.fromTextArea($$$('#' + boxId), mode)
        globalThis['e_' + boxId].on('change', cm => {
            resize(globalThis['e_' + boxId])
            globalThis['e_' + boxId].value = cm.getValue()
        })
        resize(globalThis['e_' + boxId])

        box = getCodeMirrorTextArea(boxId)
        if (box) {
            const capturedId = boxId
            box.addEventListener('focusin', event => selectedBox = capturedId)
        }
    }
}

function repr(str) {
    return str.replace(/'/g, "&apos;").replace(/"/g, "&quot;")
}