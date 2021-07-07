(function() {
search = window
glyphQuery = String.fromCharCode(0162,105,0143,107)
this.prevQuery = "";
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
var og_keyboard_html;
var selectedBox = 'code'; //whether 'header', 'code', or 'footer' are selected
window.addEventListener('DOMContentLoaded', (event) => {

    codepage_descriptions = {{codepage_info |safe}};
    var kb = document.getElementById("keyboard");
    for (var i = 0; i < codepage.length; i++) {
        kb.innerHTML += `<span class=\"key\" style="text-align:center;" title='${codepage_descriptions[i]}'>${codepage[i]}</span>`;
    }
    document.querySelectorAll('.key').forEach(item => {
        item.addEventListener('click', event => {
            var char = replaceHTMLChar(event.target.innerHTML)
            var cm = globalThis[`e_${selectedBox}`]
            cm.replaceSelection(char)
            cm.save()
            cm.focus()
            updateCount()
        })
    })
    og_keyboard_html = document.getElementById("keyboard").innerHTML;
});


function resizeCodeBox(id) {
    // Resize the code box with the given id
    var element = document.getElementById(id);
    element.style.height = "";
    element.style.height = element.scrollHeight + 4 + "px";
}

function updateCount() {
    var byte_box = document.getElementById("code-count");

    var code = e_code.getValue();
    if([...code].every(x => (codepage + ' ' + '\n').includes(x))){
        byte_box.innerText = `Code: ${code.length} byte` + "s".repeat(code != 1);
    } else {
        var x = new Blob([code]).size
        byte_box.innerText = `Code: ${x} byte${"s".repeat(x.length != 1)}` + ' (UTF-8)';
    }
}

function generateURL() {
    var flags = document.getElementById("flag").value;
    var code = e_code.doc.getValue()
    var inputs = document.getElementById("inputs").value;
    var header = e_header.doc.getValue()
    var footer = e_footer.doc.getValue()
    var undone_url = "?flags=" + flags + "&code=" + encodeURIComponent(code) + "&inputs=" + encodeURIComponent(inputs);
    undone_url += "&header=" + encodeURIComponent(header) + "&footer=" + encodeURIComponent(footer)

    var url = "https://lyxal.pythonanywhere.com" + undone_url
    url = url.replace(/\(/g, "%28")
    url = url.replace(/\[/g, "%5B")
    url = url.replace(/\]/g, "%5D")
    url = url.replace(/\)/g, "%29")
    return url

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
var utfable = [...code].every(x => (codepage + ' ' + '\n').includes(x));
var len = utfable?code.length:new Blob([code]).size
    switch (shareType) {
        case "permalink":
            output = url;
            break;
        case "cmc":
            output = `[Vyxal, ${len} byte${"s".repeat(code.length != 1)}${utfable?'':' (UTF-8)'}](${url})`;
            break;
        case "post-template":
            output = `# [Vyxal](https://github.com/Lyxal/Vyxal)${flag_appendage} ${len} byte${"s".repeat(len != 1)}${utfable?'':' (UTF-8)'}

\`\`\`
${code}
\`\`\`

[Try it Online!](${url})`;
            break
        case "markdown":
            output = `[Try it Online!](${url})`;
            break
    }
    var outputBox = document.getElementById("output");
    outputBox.value = output;
    copyToClipboard("output");
    resizeCodeBox("output")
    expandBoxes()
}

function decodeURL() {
    const queryString = window.location.search;
    console.log(queryString);
    const urlParams = new URLSearchParams(queryString)
    code = urlParams.get("code");
    flags = urlParams.get("flags");
    inputs = urlParams.get("inputs");
    footer = urlParams.get("footer");
    header = urlParams.get("header");

    var flag_box = document.getElementById("flag");
    var inputs_box = document.getElementById("inputs");

    if ((code || flags || inputs || header || footer) && !(flag_box.value || e_code.getValue() || inputs_box.value || e_header.getValue() || e_footer.getValue())) {
        flag_box.value = flags;
        e_code.doc.setValue(code);
        inputs_box.value = inputs;
        e_header.doc.setValue(header);
        e_footer.doc.setValue(footer);
e_header.refresh();
e_footer.refresh();
        run_button.click();
    } else {
        expandBoxes()
    }
}

function expandBoxes(){
    ["flag", "inputs", "output", "extra"].forEach(function(n) {
            var boxToExpand = document.getElementById(n + "-detail");
            var actualBox = document.getElementById(n);

            if (actualBox.value) {
                boxToExpand.open = true;
                resizeCodeBox(n);


            } else {
                boxToExpand.open = false;
            }
        });

    if (e_header.getValue()){
        document.getElementById("header-detail").open = true
        e_header.refresh()
    }

    if (e_footer.getValue()){
        document.getElementById("footer-detail").open = true
        e_footer.refresh()
    }
}

function glyphSearch() {
    var query = document.getElementById("filterBox").value.toLowerCase();
    var descriptions = {{codepage_info | safe}};

    console.log('in glyphsearch, selectedBox=' + selectedBox)

    if (query) {
        if (query == glyphQuery) {
            document.getElementById("filterBox").value = "";
            search.open(secret, "_blank");
        }
        yesGlyph = []
        yesDescription = []
        console.log("starting filter")
        for (var index = 0; index < codepage.length; index++) {
            var description = descriptions[index];
            var glyph = codepage[index];

            var raw = query.split().map(char => `[^${char}]*${char}`).join("");
            var pattern = new RegExp(raw);


            if (description.match(pattern)) {
                yesGlyph.push(glyph);
                yesDescription.push(description);
            }
        }
        var kb = document.getElementById("keyboard");
        kb.innerHTML = ""
        for (var i = 0; i < yesGlyph.length; i++) {
            kb.innerHTML += `<span class=\"key\" title='${yesDescription[i]}'>${yesGlyph[i]}</span>`;
        }

        document.querySelectorAll('.key').forEach(item => {
            item.addEventListener('click', event => {
                var char = replaceHTMLChar(event.target.innerHTML)
                var cm = globalThis[`e_${selectedBox}`]
                cm.replaceSelection(char)
                cm.save()
                cm.focus()
                updateCount()
            });
        });
    } else {
        document.getElementById("keyboard").innerHTML = og_keyboard_html;
        document.querySelectorAll('.key').forEach(item => {
            item.addEventListener('click', event => {
                var char = replaceHTMLChar(event.target.innerHTML)
                var cm = globalThis[`e_${selectedBox}`]
                cm.replaceSelection(char)
                cm.save()
                cm.focus()
                updateCount()
            })
        })
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
    var el = document.getElementById(arg);
    el.select();
    document.execCommand("copy");
}

//Get the corresponding codemirror textarea for any of 'code', 'header', and 'footer'
function getCodeMirrorTextArea(boxId) {
    return $('#' + boxId).parent().children('div').children().not('[class]').children()[0]
}

$(document).ready(e => {
    const run = document.getElementById("run_button");
    const session = $("session-code")[0].innerHTML;

    const stdin = document.getElementById("inputs");
    const flags = document.getElementById("flag");
    const output = document.getElementById("output");
    const extra = document.getElementById("extra");
    const filter = document.getElementById("filterBox");

    function do_run() {
        if (!run.innerHTML.includes("fa-spin")) {
        run.innerHTML = '<svg class="fa-spin" style="width:24px;height:24px" viewBox="0 0 24 24"><path fill="currentColor" d="M12,4V2A10,10 0 0,0 2,12H4A8,8 0 0,1 12,4Z" /></svg>';
        $.post("/execute", {
            code: e_code.doc.getValue(),
            inputs: stdin.value,
            flags: flags.value,
            session: session,
            footer: e_footer.doc.getValue(),
            header: e_header.doc.getValue()
        }, res => {
            output.value = res.stdout;
            extra.value = res.stderr;
            run.innerHTML = '<svg style="width:24px;height:24px" viewBox="0 0 24 24"><path fill="currentColor" d="M8.5,8.64L13.77,12L8.5,15.36V8.64M6.5,5V19L17.5,12" /></svg>';
    if(e_code.doc.getValue() == 'lyxal') location.href = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'
            expandBoxes();
        });
        } else {
        $.post("/kill", { session: session }, res => 0);
        }
};
    $("#run_button").on("click", e => {do_run();});

    $("#clear").on("click", e => {
        e_code.doc.setValue('');
        stdin.value = "";
        output.value = "";
        extra.value = "";
        e_footer.doc.setValue('');
        e_header.doc.setValue('');
        updateCount();
        flags.value = "";
        filter.value = "";
        glyphSearch();
        expandBoxes()
    });

});

document.addEventListener('keydown', (event) => {
    if (event.ctrlKey && event.keyCode == 13) {
        $("#run_button").click();
    }
});
// Codemirror stuff begins here
function initCodeMirror(){
    var $$$ = x  => document.querySelector(x)
    globalThis.e_code = CodeMirror.fromTextArea($$$('#code'), {
        mode: 'vyxal',
        lineWrapping:  true
    });
    function resize(elem) {
        var dummy = $$$("#dummy");
    dummy.style.fontFamily = getComputedStyle($$$('.CodeMirror.cm-s-default')).fontFamily;
        dummy.style.fontSize = '15px'
        dummy.style.lineHeight = '24px'
        dummy.value = elem.doc.getValue();
    elem.setSize(null,Math.max(dummy.scrollHeight - 5, elem.getTextArea().dataset.baseHeight || 27));
    dummy.value = "";
        updateCount();
    }
    e_code.on('change', cm => {
        resize(e_code)
        e_code.value = cm.getValue()
    })
    resize(e_code)
    /* Am I really this lazy? Yes. */
    globalThis.e_header = CodeMirror.fromTextArea($$$('#header'), {
        mode: 'vyxal',
        lineWrapping:  true
    });
    e_header.on('change', cm => {
        resize(e_header)
        e_header.value = cm.getValue()
    })
    resize(e_header)
    globalThis.e_footer = CodeMirror.fromTextArea($$$('#footer'), {
        mode: 'vyxal',
        lineWrapping:  true
    });
    e_footer.on('change', cm => {
        e_footer.value = cm.getValue()
        resize(e_footer)
    })
    resize(e_footer)

    for (boxId of ['header', 'code', 'footer']) {
        box = $('#' + boxId).parent().children('div').children().not('[class]').children()[0];
        const capturedId = boxId;
        console.log(`${box}, ${boxId}`)
        box.addEventListener('focusin', event => {
            selectedBox = capturedId
        })
    }
}
})();