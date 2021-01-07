def extract_word(code):
    import encoding
    import utilities

    index_pos = utilities.to_ten(code, encoding.compression)

    return _words[index_pos]

def word_index(word):
    import utilities
    import encoding
    if word in _words:
        ret = utilities.from_ten(_words.index(word), encoding.compression)
        if len(ret) == 1:
            ret = "λ" + ret
        return ret
    else:
        return -1

import dictionary
_words = dictionary.contents

if __name__ == "__main__":
    while 1:
        x = input()
        if " " in x:
            output = ""
            words = x.split(" ")
            for word in words:
                if word_index(word) != -1:
                    output += word_index(word) + " "
                else:
                    output += word + " "
            print(output)
        else:
            print(word_index(x))
