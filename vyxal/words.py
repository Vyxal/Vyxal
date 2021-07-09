from vyxal import dictionary
from vyxal import encoding
from vyxal import utilities


def extract_word(code):
    index_pos = utilities.to_ten(code, encoding.compression)
    return _words[index_pos]


def word_index(word):
    if word in dictionary.lookup:
        ret = utilities.from_ten(dictionary.lookup[word], encoding.compression)
        if len(ret) == 1:
            ret = "λ" + ret
        return ret
    else:
        return -1


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
