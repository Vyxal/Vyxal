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

const aliases = {
    "λ": ["la", "`l", "A\\"],
    "ƛ": ["Ax", "`L", "mp"],
    "¬": ["-,"],
    "∧": ["&&"],
    "⟑": ["ap"],
    "∨": ["or"],
    "⟇": ["rm"],
    "÷": [":-", "-:", "//"],
    "×": ["xx", "**", "\\*"],
    "«": ["<<"],
    "»": [">>"],
    "°": ["^o", "o^", "jj"],
    "•": [".."],
    "ß": ["bb", "ss"],
    "†": ["tt"],
    "€": ["eu", "Eu", "C=", "=C", "CE", "EC"],
    "½": ["/2"],
    "∆": ["^_", "_^"],
    "ø": ["o/", "/o"],
    "↔": ["<>", "lr"],
    "¢": ["c|", "|c"],
    "⌐": [",-"],
    "æ": ["ae"],
    "ʀ": ["RR", "_R"],
    "ʁ": ["R_", "R'"],
    "ɾ": ["rr", "_r"],
    "ɽ": [",r", "r,", "rh"],
    "Þ": ["bp", "th", "TH", "BP"],
    "ƈ": ["ch", "c,", ",c"],
    "∞": ["oo"],
    "¨": ["^^", ":^", "^:"],
    "↑": ["up", "^|"],
    "↓": ["dn", "v|", "|v"],
    "∴": [":."],
    "∵": [".:"],
    "›": ["_>", ">_", "+1"],
    "‹": ["_<", "<_", "-1"],
    "∷": ["::"],
    "¤": ["em", "``"],
    "ð": ["dx", "sp"],
    "→": ["->"],
    "←": ["<-"],
    "β": ["BB"],
    "τ": ["-r", "tu"],
    "ȧ": ["a.", ".a"],
    "ḃ": ["b.", ".b"],
    "ċ": ["c.", ".c"],
    "ḋ": ["d.", ".d"],
    "ė": ["e.", ".e"],
    "ḟ": ["f.", ".f"],
    "ġ": ["g.", ".g"],
    "ḣ": ["h.", ".h"],
    "ḭ": ["i.", ".i"],
    "ŀ": ["l.", ".l"],
    "ṁ": ["m.", ".m"],
    "ṅ": ["n.", ".n"],
    "ȯ": ["o.", ".o"],
    "ṗ": ["p.", ".p"],
    "ṙ": ["r.", ".r"],
    "ṡ": ["s.", ".s"],
    "ṫ": ["t.", ".t"],
    "ẇ": ["w.", ".w"],
    "ẋ": ["x.", ".x"],
    "ẏ": ["y.", ".y"],
    "ż": ["z.", ".z"],
    "√": ["sq", "v/"],
    "⟨": ["((", "[["],
    "⟩": ["))", "]]"],
    "‛": ["\\'"],
    "₀": ["_0", "0_"],
    "₁": ["_1", "1_"],
    "₂": ["_2", "2_"],
    "₃": ["_3", "3_"],
    "₄": ["_4", "4_"],
    "₅": ["_5", "5_"],
    "₆": ["_6", "6_"],
    "₇": ["_7", "7_"],
    "₈": ["_8", "8_"],
    "¶": ["\\n", "pl", "*|"],
    "⁋": ["nj", "lp", "|*"],
    "§": ["SS"],
    "ε": ["ee"],
    "¡": ["!!"],
    "∑": ["sm", "EE", "+/"],
    "¦": ["||"],
    "≈": ["~=", "~~"],
    "µ": ["|u", "u|", "mu"],
    "Ȧ": ["A.", ".A"],
    "Ḃ": ["B.", ".B"],
    "Ċ": ["C.", ".C"],
    "Ḋ": ["D.", ".D"],
    "Ė": ["E.", ".E"],
    "Ḟ": ["F.", ".F"],
    "Ġ": ["G.", ".G"],
    "Ḣ": ["H.", ".H"],
    "İ": ["I.", ".I"],
    "Ŀ": ["L.", ".L"],
    "Ṁ": ["M.", ".M"],
    "Ṅ": ["N.", ".N"],
    "Ȯ": ["O.", ".O"],
    "Ṗ": ["P.", ".P"],
    "Ṙ": ["R.", ".R"],
    "Ṡ": ["S.", ".S"],
    "Ṫ": ["T.", ".T"],
    "Ẇ": ["W.", ".W"],
    "Ẋ": ["X.", ".X"],
    "Ẏ": ["Y.", ".Y"],
    "Ż": ["Z.", ".Z"],
    "₌": ["_=", "=_"],
    "₍": ["_(", "(_"],
    "⁰": ["^0", "0^"],
    "¹": ["^1", "1^"],
    "²": ["^2", "2^"],
    "∇": ["_v", "v_"],
    "⌈": ["|^"],
    "⌊": ["|_", "_|"],
    "¯": ["^-"],
    "±": ["+-", "pm"],
    "₴": ["S=", "=S"],
    "…": ["el", "._", "_."],
    "□": ["[]"],
    "↳": ["L>", "v>"],
    "↲": ["<|", "<v"],
    "⋏": ["0&", "&0"],
    "⋎": ["0|", "|0"],
    "꘍": ["0X", "X0", "/\\", "vc"],
    "ꜝ": ["^!", "!^", "0!", "!0"],
    "℅": ["co", "%%"],
    "≤": ["<=", "le"],
    "≥": [">=", "ge"],
    "≠": ["/=", "!=", "ne"],
    "⁼": ["^=", "=="],
    "ƒ": ["ff", "f/", "fh"],
    "ɖ": ["dq"],
    "∪": ["UU"],
    "∩": ["NN"],
    "⊍": ["u.", ".u"],
    "£": ["LE", "po"],
    "¥": ["=Y", "Y=", "ye"],
    "⇧": ["UP"],
    "⇩": ["DN"],
    "Ǎ": ["vA", "Av", "uA", "Au"],
    "ǎ": ["va", "av", "ua", "au"],
    "Ǐ": ["vI", "Iv", "uI", "Iu"],
    "ǐ": ["vi", "iv", "ui", "iu"],
    "Ǒ": ["vO", "Ov", "uO", "Ou"],
    "ǒ": ["vo", "ov", "uo", "ou"],
    "Ǔ": ["vU", "Uv", "uU", "Uu"],
    "ǔ": ["vu", "uv", "uu"],
    "⁽": ["^("],
    "‡": ["|=", "=|", "++"],
    "≬": ["()"],
    "⁺": ["^+"],
    "↵": ["<,", ",<"],
    "⅛": ["/8"],
    "¼": ["/4"],
    "¾": ["3/"],
    "Π": ["pi"],
    "„": [",,"],
    "‟": ["''"]
};


var og_keyboard_html
var selectedBox = 'code' //whether 'header', 'code', or 'footer' are selected


function resizeCodeBox(id) {
    // Resize the code box with the given id
    var element = document.getElementById(id);
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
    if (str){
        return JSON.parse(decodeURIComponent(escape(atob(str))));
    } else {
        return [];
    }
}

function generateURL() {
    var flags = document.getElementById("flag").value
    var code = e_code.doc.getValue()
    var inputs = document.getElementById("inputs").value
    var header = e_header.doc.getValue()
    var footer = e_footer.doc.getValue()

    var url = [flags, header, code, footer, inputs];
    return location.origin + "/#" + encode(url)
}

// onclick event listener for sharing buttons
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

        } else {
            boxToExpand.open = false
        }

        resizeCodeBox(n)

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


// event listener for copy button
function copyToClipboard(arg) {
    var el = document.getElementById(arg)
    // navigator.clipboard.writeText(el)
    el.select()
    document.execCommand("copy")
}

// set up event listeners for execution
window.addEventListener("DOMContentLoaded", e => {
    const run = document.getElementById("run_button")
    const session = document.getElementsByTagName("session-code")[0].innerHTML
    const clear = document.getElementById("clear")

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
            fetch("/execute", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    code: e_code.doc.getValue(),
                    inputs: stdin.value,
                    flags: flags.value,
                    session: session,
                    footer: e_footer.doc.getValue(),
                    header: e_header.doc.getValue()
                })
            })
            .then(res => res.json())
            .then(res => {
                if (flags.value.includes('E') && !flags.value.includes("h")) {
                    alert('Please read and ensure you 100% trust the JavaScript code which is about to be evaluated. The code is (see next alert):')
                    alert(res.stdout)
                    if (confirm('Do you want to execute it? If you are remotely unsure, click Cancel!')) {
                        try {
                            res.stdout = new Function(res.stdout)()
                        } catch (e) {
                            res.stdout = e
                        }
                    }
                }
                output.value = res.stdout
                extra.value = res.stderr
                run.innerHTML =
                    `<i class="fas fa-play-circle"></i>
                    `;
                if (e_code.doc.getValue() == 'lyxal') {
                    location.href = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'
                }
                if (flags.value.includes('Ḣ') && !flags.value.includes("h")) {
                    const container = document.getElementById("html-rendered-output")
                    const iframe = document.createElement("iframe")
                    iframe.srcdoc = res.stdout
                    container.innerHTML = iframe.outerHTML
                    container.hidden = false
                } else {
                    document.getElementById("html-rendered-output").hidden = true
                }
                expandBoxes()
            })
        } else {
            fetch("/kill", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    session: session,
                })
            })
        }
    }

    run.addEventListener('click', do_run)

    clear.addEventListener('click', e => {
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
    if ((event.ctrlKey || event.metaKey) && event.key == 'Enter') {
        document.getElementById("run_button").click()
    }
})

// Codemirror stuff begins here
function initCodeMirror() {
    const $$$ = x => document.querySelector(x)

    //Get the corresponding codemirror textarea for any of 'code', 'header', and 'footer'
    function getCodeMirrorTextArea(boxId) {
        return $$$(`#${boxId} + div > div > textarea`);
    }

    function resize(elem) {
        var dummy = $$$("#dummy")
        dummy.style.fontFamily = getComputedStyle($$$('.CodeMirror.cm-s-default')).fontFamily
        dummy.style.fontSize = '15px'
        dummy.style.lineHeight = '24px'
        dummy.value = elem.doc.getValue()
        elem.setSize(
            null,
            (elem.lineCount() * 22) + 24
        )
        elem.refresh();
        dummy.value = ""

        // Make sure e_code is not null
        if ("e_code" in globalThis) {
            updateCount()
        }
    }

    let mode = {
        mode: 'vyxal',
        lineWrapping: true,
        autofocus: true,
    }

    let codeMode = {
        ...mode,
        extraKeys: {
            Tab: (cm) => {
                const cur = cm.getCursor();
                const lines = cm.getValue().split("\n");
                const k = lines[cur.line].slice(cur.ch - 2, cur.ch);
                const t = Object.entries(aliases).find(x => x[1].includes(k));
                if (t) {
                    const l = [...lines[cur.line]];
                    l.splice(cur.ch - 2, 2, t[0]);
                    lines[cur.line] = l.join('');
                    cm.setValue(lines.join("\n"));
                    cm.setCursor({ ...cur, ch: cur.ch - 1 });
                }
            }
        }
    }

    for (const boxId of ['header', 'code', 'footer']) {
        console.log(boxId)
        globalThis['e_' + boxId] = CodeMirror.fromTextArea($$$('#' + boxId), boxId === 'code' ? codeMode : mode)
        globalThis['e_' + boxId].on('change', cm => {
            resize(globalThis['e_' + boxId])
            globalThis['e_' + boxId].value = cm.getValue()
        })
        resize(globalThis['e_' + boxId])

        var box = getCodeMirrorTextArea(boxId)
        if (box) {
            const capturedId = boxId
            box.addEventListener('focusin', event => selectedBox = capturedId)
        }
    }
}
