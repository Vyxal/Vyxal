import os


# Max length of a Java/Scala string literal
MAX_LEN = 65535


def scala_str(s):
    """Turn one of the dictionary strings into a Scala string literal"""
    return '"""' + repr(s)[1 : len(s) - 1] + '"""'


def chop(s):
    """Chop the dictionary strings into multiple pieces that are each short
    enough to be valid Scala string literals"""
    pieces = [s[:MAX_LEN]]
    s = s[MAX_LEN:]
    while len(s) > MAX_LEN:
        pieces.append(s[:MAX_LEN])
        s = s[MAX_LEN:]
    # Have to turn it into Seq("foo","bar",...).mkString instead of simply
    # "foo"+"bar"+... because the Scala compiler tries to be smart and turn
    # it into a single string literal, which is too long
    return "Seq(" + ",".join(scala_str(p) for p in pieces) + ").mkString"


def gen(shortlen, longlen):
    curr_dir = os.path.dirname(os.path.realpath(__file__))

    with open(
        os.path.join(curr_dir, "words.txt"),
        "r",
        encoding="utf-8",
    ) as f:
        lines = map(str.split, f.readlines())
        lines = [
            x for x in lines if int(x[1]) > 10 and all(" " <= c <= "~" for c in x[0])
        ]
        short = [x[0] for x in lines if len(x[0]) < 6][:shortlen]
        long = [x[0] for x in lines if len(x[0]) > 5][:longlen]
        print("short length:" + str(len(short)))
        print("long length:" + str(len(long)))

    # import dictionary
    # turn off while testing

    root = os.path.dirname(curr_dir)
    resources = os.path.join(root, "shared", "resources")
    pages = os.path.join(root, "pages")

    short_str = "\n".join(short)
    long_str = "\n".join(long)

    def write_dict(dict_str, path):
        with open(path, "w", encoding="utf-8") as out:
            out.write(dict_str + "\n")

    write_dict(short_str, os.path.join(resources, "ShortDictionary.txt"))
    write_dict(short_str, os.path.join(pages, "ShortDictionary.txt"))
    write_dict(long_str, os.path.join(resources, "LongDictionary.txt"))
    write_dict(long_str, os.path.join(pages, "LongDictionary.txt"))

    return {"short": short, "long": long}


if __name__ == "__main__":
    gen(20000, 200000)
