codepage = "λƛ¬∧⟑∨⟇÷×«\n»°•¿⋄"
codepage += "μξπρςσφχψωɓƈɗƒɠɦ"
codepage += " !\"#$%&'()*+,-./"
codepage += "0123456789:;<=>?"
codepage += "@ABCDEFGHIJKLMNO"
codepage += "PQRSTUVWXYZ[\]`^"
codepage += "_abcdefghijklmno"
codepage += "pqrstuvwxyz{|}~½"
codepage += "ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠ"
codepage += "ṪẆẊẎŻȧḃċḋėḟġḣŀṁṅ"
codepage += "ȯṗṙṡṫẇẋẏż≤≥≠⁼©®∞"
codepage += "₀₁₂₃₄₅₆₇₈₉⁰¹²³⁴⁵"
codepage += "∑¦⌈⌊¯±↳↲⋏⋎꘍ꜝ”‡⇧⇩"
codepage += "∆øÞ¨‘“ð□↑↓∴∵›‹βτ"
codepage += "¶§εʀʁɾɽΠ⁽↕⁾⁺ƁƇƊƑ"
codepage += "ƓƘⱮƝƤƬƲȤδ⟨⟩→←∪∩⊍"

assert len(codepage) == 256
assert len(set(codepage)) == 256
 
command_dict = {
    "¬": ("lhs = pop(stack); stack.append(int(not lhs))", 1),
    "∧": ("rhs, lhs = pop(stack, 2); stack.append(lhs and rhs)", 2),
    "⟑": ("rhs, lhs = pop(stack, 2); stack.append(vectorise(lambda x, y: x and y, lhs, rhs))", 2),
    "∨": ("rhs, lhs = pop(stack, 2); stack.append(lhs or rhs)", 2),
    "⟇": ("rhs, lhs = pop(stack, 2); stack.append(vectorise(lambda x, y: x or y, lhs, rhs))", 2),
    "÷": ("rhs, lhs = pop(stack, 2); stack.append(divide(lhs, rhs))", 2),
    "×": ("rhs, lhs = pop(stack, 2); stack.append(multiply(lhs, rhs))", 2),
    "•": ("rhs, lhs = pop(stack, 2); stack.append(log(lhs, rhs))", 2),
    "⋄": ("fn = pop(stack); stack += function_call(fn, stack)", 1),
    "!": ("stack.append(factorial(pop(stack)))", 1),
    '"': ("rhs, lhs = pop(stack, 2); stack.append([lhs, rhs])", 2),
    "$": ("top, over = pop(stack, 2); stack.append(top); stack.append(over)", 2),
    "%": ("rhs, lhs = pop(stack, 2); stack.append(modulo(lhs, rhs))", 2),
    "*": ("rhs, lhs = pop(stack, 2); stack.append(exponate(lhs, rhs))", 2),
    "+": ("rhs, lhs = pop(stack, 2); stack.append(add(lhs, rhs))", 2),
    ",": ("VY_print(pop(stack))", 1),
    "-": ("rhs, lhs = pop(stack, 2); stack.append(subtract(lhs, rhs))", 2),
    ":": ("temp = pop(stack); stack.append(temp); stack.append(deref(temp))", 1),
    "<": ("rhs, lhs = pop(stack, 2); stack.append(lt(lhs, rhs))", 2),
    ">": ("rhs, lhs = pop(stack, 2); stack.append(gt(lhs, rhs))", 2),
    "=": ("rhs, lhs = pop(stack, 2); stack.append(eq(lhs, rhs))", 2),
    "?": ("stack.append(get_input(0))", 0),
    "A": ("stack.append(int(all(iterable(pop(stack)))))", 1),
    "B": ("stack.append(VY_bin(pop(stack), 2))", 1),
    "C": ("stack.append(chrord(pop(stack)))", 1),
    "D": ("lhs = pop(stack); stack.append(lhs); stack.append(deref(lhs)), stack.append(deref(deref(lhs)))", 1),
    "E": ("lhs = pop(stack); stack.append(VY_eval(lhs))", 1),
    "F": ("rhs, lhs = pop(stack, 2); stack.append(VY_filter(lhs, rhs))", 2),
    "G": ("stack.append(VY_max(pop(stack)))", 1),
    "H": ("stack.append(iterable(pop(stack))[0])", 1)
}

transformers = { # the {} is where the t_lambda goes
    "¿": "cond = stack.pop()\nif cond: stack += function_call(function_A, stack)",
    "μ": "stack.append(function_A)",
    "ς": "stack.append(inner_product(function_A, pop(stack))",
    "σ": "stack.append(VY_sorted(pop(stack), function=function_A))",
    "ɓ": "stack.append(without_popping(function_A, stack))",
    "ƈ": "stack.append(collect_while_unique(function_a, pop(stack)))",
    "ɗ": "stack.append(max_by_function(function_A, pop(stack)))",
    "ƒ": "stack.append(VY_filter(function_A, pop(stack)))",
    "ɠ": "stack.append(min_by_function(function_A, pop(stack)))",
    "ɦ": "stack.append(VY_map(function_A, prefixes(pop(stack))))",
    "&": "stack.append(apply_to_register(function_A, stack))",
    "/": "stack.append(VY_reduce(function_A, pop(stack)))",
    "\\": "stack.append(cumulative_reduce(function_A, pop(stack)))",
    "v": "stack.append(vectorise(function_A, stack))",
    "Ƈ": "stack.append(foldr(function_A, pop(stack)))",
    "Ƒ": "stack.append(VY_map(function_A, pairs(pop(stack))))",
    "Ƙ": "stack.append(VY_map(function_A, suffixes(pop(stack))))",
    "Ƭ": "stack.append(first_n_where(function_A, pop(stack)))",
    "Ʋ": "stack.append(stack_map(function_A, pop(stack)))",
    "Ȥ": "stack.append(zipwith(function_A, pop(stack)))",
    "ψ": "stack.append(para_apply(function_A, function_B, stack))",
    "ω": "stack.append(para_apply(function_A, function_B, stack)); rhs, lhs = pop(stack, 2); stack.append([lhs, rhs])",
    "Ɓ": "stack.append(cyclical_reduce(function_A, function_B, pop(stack)))",
    "Ɗ": "stack.append(cyclical_map(function_A, function_B, pop(stack)))",
    "Ɠ": "if pop(stack): stack += function_call(function_A, stack)\nelse: stack += function(function_B, stack)",
    "Ɱ": "rhs, lhs = pop(stack, 2); stack.append(VY_reduce(function_B, VY_map(function_A, [lhs, rhs])))",
    "Ɲ": "top = pop(stack); stack.append(VY_reduce(function_A, VY_map(function_B)))",
}
