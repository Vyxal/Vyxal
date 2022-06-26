let pops = [];
function setupKeys() {
    document.querySelectorAll('.key').forEach(item => {
        item.addEventListener('click', event => {
            var char = replaceHTMLChar(event.target.innerHTML);
            var cm = globalThis[`e_${selectedBox}`];
            cm.replaceSelection(char);
            cm.save();
            cm.focus();
            updateCount();
        });
        const tooltip = item.nextSibling;
        const pop = Popper.createPopper(item, tooltip, {
            modifiers: [
                {
                    name: 'offset',
                    options: {
                        offset: [0, 8],
                    },
                },
                {
                    name: 'preventOverflow',
                    options: {
                        padding: 10,
                    },
                },
            ],
        });
        pops.push(pop);
        item.addEventListener('mouseenter', () => {
            tooltip.setAttribute('data-show', '');
            pop.update();
        });
        item.addEventListener('mouseleave', () => {
            tooltip.removeAttribute('data-show');
        });
    });
}


// render keyboard content
window.addEventListener('DOMContentLoaded', (event) => {
    var kb = document.getElementById("keyboard");
    for (var i = 0; i < codepage.length; i++) {
        kb.innerHTML += (`<span class="key" style="text-align:center;" data-title="${repr(codepage_descriptions[i])}">${codepage[i]}</span><div class="tooltip">${repr(codepage_descriptions[i])}<div class="arrow" data-popper-arrow></div></div>`);
    }
    og_keyboard_html = document.getElementById("keyboard").innerHTML;
    setupKeys();
});
