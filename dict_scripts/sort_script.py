import json

with open(r"words.txt", "r", encoding="utf-8") as f:
    lines = f.readlines()
    lines = map(lambda x: x.split(), lines)
    lines = list(filter(lambda x: int(x[1]) > 10, lines))
    short = list(filter(lambda x: len(x) < 6, map(lambda x: x[0], lines)))[
        :10000
    ]
    long = list(filter(lambda x: len(x) > 5, map(lambda x: x[0], lines)))[
        :100000
    ]
    print("short length:" + str(len(short)))
    print("long length:" + str(len(long)))

# import dictionary

with open("ShortDictionary.txt", "w", encoding="utf-8") as out:
    out.write("\n".join(short))

with open("LongDictionary.txt", "w", encoding="utf-8") as out:
    out.write("\n".join(long))

with open("dictionary.js", "w", encoding="utf-8") as out:
    out.write("const dictionary = ")
    out.write(json.dumps({"short": short, "long": long}))
