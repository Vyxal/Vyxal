def gen(shortlen, longlen):
    import json
    import os

    import sys

    with open(
        os.path.dirname(os.path.abspath(__file__)) + r"/words.txt",
        "r",
        encoding="utf-8",
    ) as f:
        lines = f.readlines()
        lines = map(lambda x: x.split(), lines)
        lines = list(filter(lambda x: int(x[1]) > 10, lines))
        lines = list(
            filter(lambda x: all(" " <= c <= "~" for c in x[0]), lines)
        )
        short = list(filter(lambda x: len(x) < 6, map(lambda x: x[0], lines)))[
            :shortlen
        ]
        long = list(filter(lambda x: len(x) > 5, map(lambda x: x[0], lines)))[
            :longlen
        ]
        # print("short length:" + str(len(short)))
        # print("long length:" + str(len(long)))

    # import dictionary
    # turn off while testing

    with open("ShortDictionary.txt", "w", encoding="utf-8") as out:
        out.write("\n".join(short))

    with open("LongDictionary.txt", "w", encoding="utf-8") as out:
        out.write("\n".join(long))

    with open("dictionary.js", "w", encoding="utf-8") as out:
        out.write("const dictionary = ")
        out.write(json.dumps({"short": short, "long": long}))

    return {"short": short, "long": long}


if __name__ == "__main__":
    gen(10000, 50000)
