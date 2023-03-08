import json

with open(r"words.txt", "r", encoding="utf-8") as f:
    lines = map(str.split, f.readlines())
    lines = [x for x in lines if int(x[1]) > 10 and all(" " <= c <= "~" for c in x[0])]
    short = [x[0] for x in lines if len(x[0]) < 6][:20000]
    long = [x[0] for x in lines if len(x[0]) > 5][:20000]
    print("short length:" + str(len(short)))
    print("long length:" + str(len(long)))

# import dictionary

with open("ShortDictionary.txt", "w", encoding="utf-8") as out:
    out.write("\n".join(short))

with open("LongDictionary.txt", "w", encoding="utf-8") as out:
    out.write("\n".join(long))

with open("dictionary.js", "w", encoding="utf-8") as out:
    out.write("const dictionary={short:%s.split('|'),long:%s.split('|')}" % (json.dumps("|".join(short)), json.dumps("|".join(long))))
