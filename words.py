def extract_word(code):
    import encoding
    import utilities

    index_pos = utilities.to_ten(code, encoding.compression)

    return _words[index_pos]

def word_index(word):
    import utilities
    import encoding
    if word in _words:
        return utilities.from_ten(_words.index(word), encoding.compression)
    else:
        return -1

import os
prepend = os.path.dirname(__file__)
_words = open(prepend + "/dictionary.txt").read().split("\n")

if __name__ == "__main__":
    while 1:
        x = input()
        print(word_index(x))
