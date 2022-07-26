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
    "Þ×": [/(all_?)?comb(inations?|os?)(_?with)r(epl(acement)?)?/],
    "«": [/c(ompressed)?_?str(ing)?/],
    "»": [/c(ompressed)?_?(int|num)/],
    "°": ["deg","complex"],
    "•": ["log",/r(epeat)?_?cha?r/,"mold",/c(aps)?_?tr(ans(fer)?)?/],
    "ß": [/cond(ition(al)?)?(_?execute)?/],
    "†": [/call(_?func(tion)?)?/,/len_?p(rime)?_?f(actors)?/,/py(thon)?_?exec|python/,/vec(tori[sz]ed)?_?not/],
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
    "↑": [/max(imum)?_?by_?t(ail)?/],
    //Þ↑ and Þ↓ deprecated
    "↓": [/min(imum)?_?by_?t(ail)?/],
    "Þ↓": [/min(imum)?_?by/],
    "∴": [/d(yad(ic)?)?_?max(imum)?/, /max(imum)?_?by/],
    "Þ∴": [/e(lem)?_?(wi[sz]e)?d(yad(ic)?)?_?max(imum)?/],
    "∵": [/d(yad(ic)?)?_?min(imum)?/, /min(imum)?_?by/],
    "Þ∵": [/e(lem)?_?(wi[sz]e)?d(yad(ic)?)?_?min(imum)?/],
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
    "øṗ": [/pal(indromi[sz]e)?_?cen(ter)?_?new(lines?)?/],
    "ṙ": ["round",/real_?imag/,/quad_?p(alin(dromi[sz]e)?)?/],
    "∆ṙ": [/poly_?roots?/],
    "øṙ": [/regex_?repl(ace(ment)?)?/],
    "ṡ": ["sort",/sort_?by/,/inc(lusive)_?range/,/regex_?split/],
    "Þṡ": [/sort_?(by_?)?len(gth)?/],
    "ṫ": [/t(ail)?_?ext(ract)?/],
    "∆ṫ": ["totient"],
    "ẇ": [/(chunk_?)?wrap/],
    "Þẇ": ["unwrap"],
    "¨ẇ": [/wrap_?n_?stack/],
    "ẋ": ["repeat"],
    "ẏ": [/ex(c(lusive)?)?_?r(ange)?_?len(gth)?/],
    "ż": [/in(c(lusive)?)?_?r(ange)?_?len(gth)/],
    "Þż": ["lift"],
    "√": ["sqrt",/every_?(second|2|2nd|other)_?c(har)?/],
    "⟨": [/o(pen)?_?list/],
    "⟩": [/c(lose)?_?list/],
    "‛": [/(two|2)_?cha?r_s(tr(ing)?)?/,"scc"],
    "₀": ["ten", "10"],
    "₁": ["100","hundred"],
    "₂": ["even",/len(gth)?_?even/],
    "₃": [/(3|three)_?div|(is_?)?div_?(3|three)/,/len_?(1|one)/],
    "₄": ["26",/twenty_?six/],
    "₅": [/(5|five)_?div|(is_?)?div_?(5|five)/,/dup_?len/],
    "₆": ["64",/sixty_?four/],
    "₇": ["128"],
    "₈": ["256"],
    "¶": ["newline"],
    "k¶": ["512"],
    "⁋": [/j(oin)?_?(newline|nl)s?/],
    "k⁋": ["1024"],
    "§": [/vert(ical)?_?join/],
    "ε": [/abs(olute)?_?diff(erence)?/,/str_?(list|lst?)_?repeat/,/regex_?match/],
    "kε": ["32768"],
    "¡": [/fact(orial)?/,/s(entence)?_?case/],
    "k¡": ["16384"],
    "∑": ["sum"],
    "¦": [/cum(ulative)?_?sum/],
    "k¦": ["2048"],
    "≈": [/all_?(equal|same|eq)/],
    "µ": [/sort_?l(ambda)?/],
    "Ȧ": ["assign"],
    "Ḃ": ["bifurcate",/dup(licate)?_?rev(erse)?/],
    "Ċ": ["counts",/count_?each/],
    "∆Ċ": [/poly_?f(rom)?_?coeffs/],
    "øĊ": ["center"],
    "ÞĊ": [/is_?(un|not|nt)sorted/],
    "Ḋ": [/(is_?)?divis(ible)?/,/many_?dup(licate)/,/group_?by_?order/],
    "øḊ": [/d(yad)?_?r(un)?_?l(ength)?_?d(ecode)?/],
    "ÞḊ": [/(mat(rix)?_?)?det(erminant)?/],
    "Ė": ["eval","reciprocal"],
    "∆Ė": [/e_?digits?|digits?_?e/],
    "øĖ": [/sep(arate)_?r(un)?_?l(en(gth)?)?_?e(nc(od(ing|e))?)?/],
    "Ḟ": [/gen(erat(or|e))?/,/every_?n(th)?/,/format_?num(ber)?/,/repl(ace)?_?spaces?/],
    "øḞ": [/repl(ace)?_?f(irst)?_?occur(ence)?/],
    "ÞḞ": [/fill_?rect/],
    "Ġ": [/group_?cons(ecutive)?/],
    "Ḣ": [/head_?(rm|remove)/,"behead"],
    "İ": [/index_?each/,/col(lect)?_?uniq(ue)?/],
    "Þİ": [/first_?n_?last/],
    "Ŀ": [/transl(iterate)?/,"tl"],
    "∆Ŀ": ["lcm"],
    "Ṁ": ["insert",/map_?every_?n(th)?/],
    "øṀ": [/vert(ical)?_?mirror_?refl(ect)?/],
    "ÞṀ": [/mat(rix)?_?mul(tipl(y|ication))?/],
    "Ṅ": [/space_?j(oin)?|j(oin)?_?space/,/int(eger)?_?par(t(itions)?)?/],
    "kṄ": ["4096"],
    "øṄ": [/rep(l(ace)?)?_?n(th)?_?occur([ae]nce)?/],
    "ÞṄ": [/inf_?pos_?int_?set/],
    "Ȯ": ["over"],
    "ÞȮ": [/is_?sorted/],
    "Ṗ": [/perm(utation)?s?/],
    "kṖ": [/nest(ed)_?brac(kets)?/],
    "øṖ": [/part(ition)?s?/],
    "∆Ṗ": [/next_?prime/],
    "Ṙ": [/rev(erse)?/],
    "kṘ": [/roman_?num(eral)?s?/],
    "øṘ": ["roman"],
    "ÞṘ": [/is_?rev(erse)?_?sort(ed)?/],
    "∆Ṙ": [/rand_?float/],
    "Ṡ": [/vec_?sums?/],
    "ÞṠ": [/is_?sorted/],
    "Ṫ": [/tail_?(rm|remove)/,"betail",/truthy?_?under/],
    "ÞṪ": [/trans(pose)?_?fill(er)?/],
    "Ẇ": [/split_?keep(_?delim(iter)?)?/,/apply_?every_?(second|2|2nd|other)/],
    "Ẏ": [/first_?n/,/slice_?(zero|0)_?n/,/take_?while/,/regex_?all/],
    "Ż": [/slice_?(one|1)_?(to_?)?n/,/regex_?groups/],
    "ÞŻ": [/sort_?(every|all)_?(level|dim)/],
    "₌": [/p(ara(llel)?)?_?apply/],
    "₍": [/p(ara(llel)?)?_?a(pply)?wrap/],
    "⁰": [/first_?in(put)?|in(put)?_?1/],
    "k⁰": [/consonants?_?y/],
    "¹": [/second_?in(put)?|in(put)?_?2/],
    "k¹": [/consonants?/],
    "²": ["square"],
    "k²": ["link"],
    "∆²": [/perf(ect)?_?sq(uare)/],
    "∇": ["shift","rot"],
    "⌈": ["ceil","ceiling","imag","imaginary",/split_?spaces?/],
    "⌊": ["floor","floor","real",],
    "¯": [/deltas?/,/(forward_?)?diffs?/],
    "±": ["sign",/is_?num(eric|ber)?/],
    "∆±": ["copysign"],
    "₴": [/print_?(no|sans|without)_?(newline|nl)/],
    "k₴": ["65536"],
    "…": [/print_?(tos|peek|(no|sans|without)_?pop)/],
    "Þ…": [/even(ly)?_?dist(ribute)?/],
    "¨…": [/print_?space_?(tos|peek|(no|sans|without)_?pop)/],
    "□": ["inputs"],
    "k□": [/card(inal)?_?dir(ection)?s?/],
    "Þ□": [/ident(ity)?_?mat(rix)?/,"eye"],
    "¨□": [/arrow_?(to|2)_?(int|num)/],
    "↳": [/shift_?r(ight)?/,/r(ight)?_?pad/,/pad_?r(ight)?/],
    "ø↳": [/c(ustom)?_?pad_?l(eft)?/], // Frick
    "↲": [/shift_?l(eft)?/,/l(eft)?_?pad/,/pad_?l(eft)?/],
    "ø↲": [/c(ustom)?_?pad_?r(ight)?/],
    "⋏": [/bit(wi[sz]e)?_?and/,/pad_?c(enter)?|c(enter)?_?pad/],
    "⋎": [/bit(wi[sz]e)?_?or/,/(rm|remove)_?ind(ex)?/,"merge"],
    "꘍": [/bitwi[sz]e_?xor/,/l(even(shtein)?)?_?dist(ance)?/,/(add|prepend|append)_?spaces?/],
    "ꜝ": [/bitwi[sz]e_?not/,/filter_?fals[ye]/,/any_?upper(case)?/],
    "℅": ["choice"],
    "Þ℅": ["shuffle"],
    "≤": [/le(ss)?_?eq(ual)?/],
    "≥": [/gr(eater)?_?eq(ual)?/],
    "≠": [/not_?eq(ual)?/],
    "⁼": [/exact(ly)?_?eq(ual)?/],
    "ƒ": ["reduce","fold","foldl"],
    "ɖ": ["scan","scanl"],
    "∪": ["union"],
    "k∪": [/l(ow(ercase)?)?_?vowels?_?y/],
    "Þ∪": [/m(ulti)?(set)?_?union/],
    "∩": ["trans","tp","transpose"],
    "k∩": [/vowels?_?y/],
    "Þ∩": [/m(ulti)?(set)?_?intersection/],
    "⊍": [/set_?(diff(erence)?|xor)/],
    "k⊍": [/up(per(case)?)?_?vowels?_?y/],
    "Þ⊍": [/m(ulti)?(set)?_?(diff(erence)?|xor)/],
    "£": [/s(et)?_?reg(ister)?/],
    "¨£": [/star_?map/, /zip_?with/],
    "¥": [/(g(et)?_?)?reg(ister)?/],
    "⇧": [/up(per)?(case)?/,/plus_?2|2_?plus/,/grade_?up/],
    "Þ⇧": [/(is_?)?strict(ly)?_?(inc(reasing)?|asc(ending)?)/],
    "⇩": [/low(er)?(case)?/,/minus_?2|2_?minus/,/grade_?down/],
    "Þ⇩": [/(is_?)?strict(ly)?_?(dec(reasing)?|desc(ending)?)/],
    "Ǎ": [/(remove|rm)_?non_?alpha/,"2pow"],
    "ǎ": [/substr(ing)?s?/,/n(th)?_?prime/],
    "Ǐ": [/dist(inct)?_?prime_?fact(or)?s?/,/append_?(first|head)/],
    "ǐ": [/prime_?fact(or)?s?/,/sent(ence)?_?case/],
    "∆ǐ" : [/prime_?exp(onents?)?/],
    "Ǒ": [/multiplic(ity)?/,/(remove|rm)_?fix(ed)?(_?point)?/,/find_?tru(e|thy)/],
    "ǒ": [/mod_?(3|three)|(3|three)_?mod/,/chunks?_?2|2_?chunks?/],
    "Ǔ": [/rot(ate)?_?r(ight)?/],
    "ÞǓ": [/conn(ected)?_?uniqu(e|ify)?/],
    "ǔ": [/rot(ate)?_?l(eft)?/],
    "Þǔ": ["untruth"],
    "⁽": [/l(am(bda)?)?_?1|1_?((byte|elem)_?)?l(am(bda)?)?/],
    "‡": [/l(am(bda)?)?_?2|2_?((byte|elem)_?)?l(am(bda)?)?/],
    "≬": [/l(am(bda)?)?_?3|3_?((byte|elem)_?)?l(am(bda)?)?/],
    "⁺": [/n(ext)?_?cha?r_?(num(ber)?|int)/],
    "↵": [/split_?(newline|nl)/,/10_?pow|pow_?10/],
    "⅛": [/glob(al)?_?push/],
    "¼": [/glob(al)?_?pop/],
    "¾": [/push_?glob(al)?/],
    "Þ¾": [/clear_?glob(al)?/],
    "Π": [/prod(uct)?/],
    "„": [/(rot(ate)?_?)?stack_?l(eft)?/],
    "‟": [/(rot(ate)?_?)?stack_?r(ight)?/],
    "kF": ["fizzbuzz"],
    "Þ!": [/(all_?)?fact(orial)?s/],
    "∆%": [/mod(ular)?_?exp(onent(iation)?)?/],
    "¨*": [/(all_?)?multi(ple)?s/],
    "¨,": [/print_?space/],
    "ø.": ["surround"],
    "Þ/": [/m(ain)?_?diag(onal)?/],
    "k1": ["1e3","1000"],
    "k2": ["1e4","10000"],
    "k3": ["1e5","100000"],
    "k4": ["1e6","1000000"],
    "¨=": [/eq(ual)?_?under/],
    "Þ<": [/less_inc(reasing)?/],
    "¨<": [/strict_?(l(ess)?_?t(han)?)/],
    "¨>": [/strict_?(g(reater)?_?t(han)?)?/],
    "¨?": [/read_?stdin/],
    "øA": [/letter_?num(ber)?/,/num(ber)?_?letter/],
    "ÞA": [/adj(acency)?_?mat(rix)?/],
    "øB": ["bracketify"],
    "ÞB": [/rand(om)?_?bits/],
    "∆C": [/arc_?cos(ine)?/],
    "øC": [/num_?compress/],
    "∆D": [/to_?deg(rees)?/],
    "øD": [/dict_?comp(ress)?/],
    "ÞD": [/diag(onal)?s?/],
    "∆E": [/(e_?power|power_?e)m1/,"expm1"],
    "øE": [/ends_?with/],
    "∆F": [/fib(onacci)?_?0/],
    "ÞF": [/a(ll)?_?fib(onacci)?/],
    "ÞG": ["longest"],
    "∆I": [/pi_?digits|digits_?pi/],
    "ÞI": [/all_?indices_?multi(dimensional)?/],
    "øJ": ["json"],
    "∆K": [/sum_?div(isors)?|div(isors)?_?sum/],
    "ÞK": [/suffix(es)?/],
    "∆L": [/nat(ural)?_?log|log_?nat(ural)?|log_?e|e_?log/,"ln"],
    "øL": [/strip_?left/],
    "∆M": ["mode"],
    "øM": [/vert(ical)_?flip_?palindrom(is)?e/],
    "ÞM": [/max(imal)?_?ind(ice)?s?/],
    "ÞN": [/alt(ernating)?_?neg(ation)?/],
    "∆P": [/solve_?poly/],
    "øP": [/plurali[sz]e/],
    "∆Q": [/solve_?quad(ratic)?/],
    "∆R": [/to_?rad(ians)?/],
    "øR": [/strip_?right/],
    "ÞR": [/(zero|0)_?cum(ulative)?_?sums?/],
    "∆S": [/arc_?sine?/],
    "øS": [/strip_?both/],
    "ÞS": ["sublists"],
    "∆T": [/arc_?tan(gent)?/],
    "ÞT": [/m(ulti)?dim(ension)?_?tru(thy|e)_?ind(ice)?s?/],
    "ÞU": [/uniq(ue)?_?mask/],
    "¨U": ["get","url"],
    "øV": [/rep(l(ace)?)?_?no_?c(hange)?/],
    "∆W": [/round_?dec(imal)?/],
    "øW": [/group_?words/],
    "∆Z": ["zfill"],
    "¨Z": [/zip_?lam(bda)?/],
    "Þ\\": [/anti_?diag(onal)?/],
    "ø^": [/str(ing)?_?canvas/],
    "¨^": [/dir_?to_?vec/],
    "Þa": [/adj(acency)?_?mat(rix)?_?dir(ected)?/],
    "∆b": [/bin(ary)?_?str(ing)?/],
    "øb": [/parenthesi[zs]e/],
    "∆c": ["cos","cosine"],
    "øc": [/str_?comp(ress)?/],
    "Þc": [/card(inal)?s/],
    "∆d": [/line_?dist(ance)?/],
    "ød": [/r(un)?_?l(ength)?_?dec(ode)?/],
    "Þd": [/dist(ance)?_?mat(rix)?_?dir(ected)?/],
    "∆e": [/e_?power/,"exp"],
    "øe": [/r(un)?_?l(ength)?_?enc(oding)?/],
    "Þe": [/mat(rix)?_?exp(onent(iation)?)?/],
    "∆f": [/n(th)?_?fib(onacci)?/],
    "øf": [/ends?_?(with_?)?set/],
    "Þf": [/flat_?by/],
    "Þg": ["shortest"],
    "∆i": [/n(th)?_?pi_?digit/],
    "Þi": [/m(ulti)?_?dim(ension(al)?)?_?ind(ex)?/],
    "¨i": [/if_?else/],
    "Þj": ["depth"],
    "∆l": [/log_?2/],
    "øl": [/strip_?left_?by/],
    "øm": [/m(ir(ror)?)?_?c(enter)?_?newlines?/],
    "Þm": [/(zero|0)_?mat(rix)?/],
    "Þn": [/a(ll)?_?ints?/],
    "∆o": [/n(th)?_?ord(inal)?/],
    "Þo": [/(all_?)ord(inal)?s/],
    "øp": [/starts?_?with/],
    "Þp": [/(all_?)primes/],
    "¨p": [/over_?pairs/],
    "ør": [/strip_?right_?by/],
    "Þr": [/(remove|rm)_?(last|tail)_?p(repend)?_?(0|zero)/],
    "∆s": [/sine?/],
    "øs": [/starts_?(with_?)?set/],
    "∆t": [/tan(gent)?/],
    "Þu": [/all_?uniq(ue)?/],
    "¨v": [/simp(le)?_?vec(tori[sz]e)/],
    "Þw": [/dist(ance)?_?mat(rix)?/],
    "Þx": [/a(ll)?_?comb(ination)?s?_?(without|no|sans)_?rep(l(acement)?)?/],
    "∆p": [/(near|clos)est_?prime/]
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
    const code = e_code.doc.getValue()
    const url = generateURL()
    const flags = document.getElementById("flag").value
    let flagAppendage = ","
    const flagsThatMatter = flags.replace(/[5bBTAP…]/g, "");
    if (flagsThatMatter) {
        flagAppendage = " `" + flagsThatMatter + "`,"
    }
    let output = ""
    const utfable = [...code].every(x => (codepage + ' ' + '\n').includes(x))
    const len = utfable ? code.length : new Blob([code]).size
    switch (shareType) {
        case "permalink":
            output = url
            break
        case "cmc":
            output = `[Vyxal, ${len} byte${"s".repeat(code.length != 1)}${utfable ? '' : ' (UTF-8)'}](${url})`
            break
        case "post-template":
            output = `# [Vyxal](https://github.com/Vyxal/Vyxal)${flagAppendage} ${len} byte${"s".repeat(len != 1)}${utfable ? '' : ' (UTF-8)'}
\`\`\`
${code}
\`\`\`
[Try it Online!](${url})`;
            break
        case "markdown":
            output = `[Try it Online!](${url})`
            break
    }
    const outputBox = document.getElementById("output")
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
                    cm.replaceRange(t[0], { line: cur.line, ch: cur.ch - 2 }, { line: cur.line, ch: cur.ch });
                    return;
                }
                const num = line.match(/\d+$/)?.[0] || "";
                if (num) {
                    let n = BigInt(num);
                    const c = codepage.replace('»', '').replace('␠', ' ').replace('␤', '\n');
                    let compressed = '';
                    do {
                        compressed = c[Number(n % 255n)] + compressed;
                        n /= 255n;
                    } while (n);
                    compressed = '»' + compressed + '»';
                    if (compressed.length <= num.length) {
                        cm.replaceRange(compressed, { line: cur.line, ch: cur.ch - num.length }, { line: cur.line, ch: cur.ch });
                        return;
                    }
                }
                const str = line.match(/`[a-z ]+`$/)?.[0]?.slice(1, -1);
                if (str) {
                    let r = 0n;
                    for (const c of str)
                        r = 27n * r + BigInt(' abcdefghijklmnopqrstuvwxyz'.indexOf(c));
                    const c = codepage.replace('«', '').replace('␠', ' ').replace('␤', '\n');
                    let compressed = '';
                    do {
                        compressed = c[Number(r % 255n)] + compressed;
                        r /= 255n;
                    } while (r);
                    compressed = '«' + compressed + '«';
                    if (compressed.length <= str.length + 2) {
                        cm.replaceRange(compressed, { line: cur.line, ch: cur.ch - str.length - 2 }, { line: cur.line, ch: cur.ch });
                        return;
                    }
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
