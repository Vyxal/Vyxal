def to_ten(n, alphabet):
    from_base = len(alphabet)
    result = 0
    power = 0
    for char in str(n)[::-1]:
        index = alphabet.index(char)
        result += index * (from_base ** power)
        power += 1
    return result

def from_ten(n, alphabet):
    import math
    to_base = len(alphabet)
    power = int(math.log(n if n else 1) / math.log(to_base))

    temp = n
    result = ""

    while temp > 0:
        result += alphabet[(temp // (to_base ** power))]
        temp -= (temp // (to_base ** power)) * (to_base ** power)
        power -= 1

    return result

