import words

def to_ten(number, alphabet):
    # [str, iter] -> base_alphabet in base 10 (takes a custom base and returns a single decimal integer)

    s = zip(number, range(len(number) - 1, -1, -1))
    out = 0
    for base, exponent in s:
        out += alphabet.find(base) * (len(alphabet) ** exponent)

    return out

def from_ten(n, alphabet):
    import math
    to_base = len(alphabet)
    power = int(math.log(n if n else 1) / math.log(to_base))

    temp = n
    t = type(alphabet)
    if t in [int, float]:
        alphabet = Stack(list(range(0, int(alphabet))))
    result = "" if t is str else Stack()

    while temp > 0:
        val = alphabet[(temp // (to_base ** power))]
        if t is str: result += val
        else: result.push(val)
        temp -= (temp // (to_base ** power)) * (to_base ** power)
        power -= 1

    return result


def uncompress(s):
    final = ""
    current_two = ""
    escaped = False

    import encoding

    for char in s:
        if escaped:
            if char not in encoding.compression:
                final += "\\"
            final += char
            continue

        elif char == "\\":
            escaped = True
            continue

        elif char in encoding.compression:
            current_two += char
            if len(current_two) == 2:
                if to_ten(current_two, encoding.compression)\
                   < len(words._words):

                    final += words.extract_word(current_two)
                else:
                    final += current_two

                current_two = ""
            continue

        else:
            final += char


    return final


base53alphabet = "¡etaoinshrdlcumwfgypbvkjxqz ETAOINSHRDLCUMWFGYPBVKJXQZ"
base27alphabet = "etaoinshrdlcumwfgypbvkjxqz "

if __name__ == "__main__":
    import encoding
    while 1:
        word = input(">>> ")
        if word.isnumeric():
            print("»" + from_ten(int(word), encoding.codepage_number_compress) + "»")

        else:
            try:
                if type(eval(word)) is list:
                    charmap = dict(zip("0123456789", "etaoinshrd"))
                    out = ""
                    for item in map(str, eval(word)):
                        for c in item:
                            out += charmap[c]
                        out += " "

                    converted = to_ten(out, base53alphabet)
                    print("«" + from_ten(converted, encoding.codepage_string_compress) + "«ũ")
                       
                else:
                    print("«" + from_ten(to_ten(word, base53alphabet), encoding.codepage_string_compress) + "«")
            except:
                    print("«" + from_ten(to_ten(word, base53alphabet), encoding.codepage_string_compress) + "«")
