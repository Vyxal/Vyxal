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
    "›": ["_>", ">_", "+1", "1+"],
    "‹": ["_<", "<_", "-1", "1-"],
    "∷": ["::", "2%"],
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

// While the two byte aliases attempt to identify what characters look like,
// these identify what characters / builtins *do*.
// This is gonna take a while to fill in, and this + above should probably be
// moved to a separate file at some point.
const other_aliases = {
    "λ": ["lam","lambda","func"],
    "ƛ": ["map","each"],
    "¬": [/(logic_?)?not/],
    "∧": [/(logic_?)?and/],
    "ø∧": [/g?canvas/],
    "⟑": [/apply(_?map)?/,/force_?eval/],
    "∨": [/(logic_?)?or/],
    "k∨": [/a(ll)?_?vowels?/],
    "Þ∨": [/m(ulti(set)?)?_?diff/],
    "⟇": [/remove_?index/,/rm_?index/,"rmi"],
    "k⟇": ["codepage"],
    "ø⟇": [/(codepage|cp)_?(get|find|help|util)/],
    "÷": [/(i(tem)?_?)split/, /p(ush)?_?ea(ch)?/],
    "Þ÷": [/div(ide)?_?eq(ual)?(_?parts)?/],
    "×": ["asterisk","star"],
    // Constants mostly skipped
    "Þ×": [/(all_?)?comb(inations?|os?)(_?with)r(epl(acement)?)?/],
    "«": [/c(ompressed)?_?str(ing)?/],
    "»": [/c(ompressed)?_?(int|num)/],
    "°": ["deg","complex"],
    "•": ["log",/r(epeat)?_?cha?r/,"mold",/c(aps)?_?tr(ans(fer)?)?/],
    "ß": [/cond(ition(al)?)?(_?execute)?/],
    "†": [/call(_?func(tion)?)?/,/len_?p(rime)?_?f(actors)?/,/py(thon)?_?exec|python/,/vec(torised)?_?not/],
    "€": [/spl?it_?on/,/fill_?(by_?)?coord(inate)?s/],
    "½": ["half","halve"],
    // ∆ø and other digraphs skipped
    "↔": [/fix(ed_?point)?/,/inter(sect(ion)?)?/,/comb(ination|o)s?/],
    "¢": [/inf(inite)?_?repl(ace(ment)?)?/,/apply_?(at_?)ind(ice)?s/],
    "∆¢": ["carmichael"],
    "⌐": [/compl([ei]ment)?/,/comma(_?split)?/],
    "æ": [/(is_?)?prime/,/case(_?of)?/],
    "ʀ": [/inc(lusive)?_?(z(ero)?|0)_?r(ange)?/,/is_?alpha/],
    "ʁ": [/exc(lusive)?_?(z(ero)?|0)_?r(ange)?/],
    "ɾ": [/inc(lusive)?_?(o(ne)?|1)_?r(ange)?/,/vec_?upper/],
    "ɽ": [/exc(lusive)?_?(o(ne)?|1)_?r(ange)?/,/vec_?lower/],
    "øɽ": [/(just(ify)?|align)_?r(ight)?|r(ight)?_?(just(ify)?|align)/],
    "ƈ": [/cho(ice|ose)|bin(omial)?_?coeff(icient)?/,/rand(om)?_?cho(ice|ose)/,/set_?(eq(ual)?|same)/,/drop_?w(hile)?/],
    "∆ƈ": ["npr",/n_?p(ick)?_?r/],
    "∞": [/pal(indrom(e|i[sz]e))?/],
    "Þ∞": ["inf","infinity"],
    // TODO: add more completions
    "↑": [/max(imum)?_?by_?t(ail)?/],
    //Þ↑ and Þ↓ deprecated
    "↓": [/min(imum)?_?by_?t(ail)?/],
    "Þ↓": [/min(imum)?_?by/],
    "∴": [/d(yad(ic)?)?_?max(imum)?/, /max(imum)?_?by/],
    "Þ∴": [/e(lem)?_?(wise)?d(yad(ic)?)?_?max(imum)?/],
    "∵": [/d(yad(ic)?)?_?min(imum)?/, /min(imum)?_?by/],
    "Þ∵": [/e(lem)?_?(wise)?d(yad(ic)?)?_?min(imum)?/],
    // ∆› and ∆‹ deprecated
    "›": [/inc(rement)?/, "succ", /sp(ace)?_?rep(lace)?_?(w(ith)?_?)?(0|z(ero)?)/],
    "‹": [/dec(rement)?/, "pred", /ap(pend)?_?(hyp(hen)?|dash)/],
    "∷": ["odd", "parity", /mod(ul(o|us))?_?(2|two)/],
    "¤": ["empty","nil","null"],
    "ð": ["space"],
    "kð": [/d(ate)?_?dmy/],
    "→": [/var(iable)?_?s(et)?/],
    "←": [/var(iable)?_?g(et)?/],
    "β": [/(to_?)?b(ase)?_?(10|ten)/, /fr(om)?_?(cust(om)?_?)?b(ase)?/],
    "τ": [/(fr(om)?_?)?b(ase)?_?(10|ten)/, /to_?(cust(om)?_?)?b(ase)?/],
    "ȧ": [/abs(olute_?val(ue)?)?/],
    "ḃ": [/bool(ify)?/],
    "kḃ": [/open_?b(rackets?)?/],
    "øḃ": [/curly_?b(racket(|s|ify))?/],
    "ċ": [/not_?(one|1)/],
    "∆ċ": [/n(th)?_?card(inal)?/,/n(um)?_?(to|2)_?w(ords)?/],
    "øċ": [/(semi_?)?o(pt(imal)?)?_?n(um)?_?c(ompress)?/],
    "Þċ": ["cycle"],
    "ḋ": ["divmod",/comb(ination|o)?s?_?(of_?)?len(gth)?/],
    "øḋ": [/to_?d(ecimal)?/],
    "Þḋ": [/(anti|un)_?diag(onal)?s?/],
    "ė": [/enum(erat(e|ion))?/],
    "∆ė": [/e_?digit|digit_?e/],
    "ḟ": ["find","search",/t(ru(thy|e))?_?ind(ices|exes)?_?map/],
    "Þḟ": [/m(ulti)?_?dim(ension(al)?)?_?(find|search)/],
    "ġ": ["gcd",/group_?by/,/long(est)?_?(common_?)?suf(fix)?/],
    "ḣ": [/h(ead)?_?ext(ract)?/],
    "ḭ": [/fold_?r(ight)?/,/f(loor)?div/],
    "ŀ": [/l(eft)?_?just(ify)?/,"clamp",/grid(ify)?/,/c(ollect)?_?w(hile)?_?f(als[ey])?/,/inf(inite)?_?repl(ace(ment)?)?/],
    "øŀ": [/l(eft)?_?align/],
    "ṁ": ["mean","average"],
    "∆ṁ": ["median"],
    "øṁ": [/vert(ical)?_?mirror/],
    "Þṁ": [/mold_?(no|without)_?rep(eat)?/],
    "ṅ": [/join_?(nil|nothing)/,/insig(nificant)?/,/pad_?bytes?/,/bytes?_?pad/,/f(irst)?_?num/],
    "kṅ": ["8192"],
    "ȯ": [/slice(_?end)?/,/f(irst)?_?n_?nums?/,/vert(ical)?_?merge/],
    "ṗ": [/(p(ower)?|sub)sets?/],
    "kṗ": [/brac(ket)?_?pairs?/],
    "∆ṗ": [/prev(ious)?_?prime/],
    // how does shortcut øṗ?
    "ṙ": ["round",/real_?imag/,/quad_?p(alin(dromise)?)?/],
    "∆ṙ": [/poly_?roots?/],
    "øṙ": [/regex_?repl(ace(ment)?)?/],
    "ṡ": ["sort",/sort_?by/,/inc(lusive)_?range/,/regex_?split/],
    "Þṡ": [/sort_?(by_?)?len(gth)?/],
    "ṫ": [/t(ail)?_?ext(ract)?/],
    "∆ṫ": ["totient"],
    "ẇ": [/(chunk_?)?wrap/],
    "Þẇ": ["unwrap"],
    "¨ẇ": [/wrap_?n_?stack/],

    "₁": ["100","hundred"],
    "₃": [/(3|three)_?div|(is_?)?div_?(3|three)/,/len_?(1|one)/],
    "₅": [/(5|five)_?div|(is_?)?div_?(5|five)/,/dup_?len/],
    "₍": [/p(ara(llel)?)?_?a(pply)?wrap/],
    "kF": ["fizzbuzz"],
    "∑": ["sum"],

}


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
                const line = lines[cur.line].slice(0, cur.ch);
                let alpha = line.match(/[a-z_0-9]+$/)?.[0];
                while (alpha?.length >= 3) { // Greedily match as many characters as possible
                    const t = Object.entries(other_aliases).find(x => x[1].some(y => alpha.match(y)?.[0] == alpha));
                    if (t) {
                        cm.replaceRange(t[0], { line: cur.line, ch: cur.ch - alpha.length }, { line: cur.line, ch: cur.ch }); // Suggested by copilot. **works**???
                        return;
                    }
                    alpha = alpha.slice(1); // Lop off the head, if not found
                }
                const k = lines[cur.line].slice(cur.ch - 2, cur.ch);
                const t = Object.entries(aliases).find(x => x[1].includes(k));
                if (t) {
                    // Sorry Steffan, you've been usurped by the robots.
                    cm.replaceRange(t[0], { line: cur.line, ch: cur.ch - 2 }, { line: cur.line, ch: cur.ch });
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
