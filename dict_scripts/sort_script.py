import json
import os
import zlib


# Max length of a Java/Scala string literal
MAX_LEN = 65535


def deflate(data):
    compress = zlib.compressobj(level=9)
    deflated = compress.compress(data.encode())
    deflated += compress.flush()
    # Latin-1 will work with every byte value from 0-255 without giving errors
    return deflated.decode("latin-1")


def scala_str(s):
    """Turn one of the dictionary strings into a Scala string literal"""
    return '"""' + repr(s)[1 : len(s) - 1] + '"""'


def chop(s):
    """Chop the dictionary strings into multiple pieces that are each short
    enough to be valid Scala string literals"""
    s = s.encode("utf-8")
    pieces = [s[:MAX_LEN]]
    s = s[MAX_LEN:]
    while len(s) > MAX_LEN:
        pieces.append(s[:MAX_LEN])
        s = s[MAX_LEN:]
    # Have to turn it into Seq("foo","bar",...).mkString instead of simply
    # "foo"+"bar"+... because the Scala compiler tries to be smart and turn
    # it into a single string literal, which is too long
    return "Seq(" + ",".join(scala_str(p.decode("utf-8").encode("latin-1").decode("latin-1")) for p in pieces) + ").mkString"


def gen(shortlen, longlen):

    curr_dir = os.path.dirname(os.path.realpath(__file__))

    with open(
        os.path.join(curr_dir, "words.txt"),
        "r",
        encoding="utf-8",
    ) as f:
        lines = map(str.split, f.readlines())
        lines = [
            x
            for x in lines
            if int(x[1]) > 10 and all(" " <= c <= "~" for c in x[0])
        ]
        short = [x[0] for x in lines if len(x[0]) < 6][:shortlen]
        long = [x[0] for x in lines if len(x[0]) > 5][:longlen]
        print("short length:" + str(len(short)))
        print("long length:" + str(len(long)))

    # import dictionary
    # turn off while testing

    root = os.path.dirname(curr_dir)
    resources = os.path.join(root, "shared", "src", "main", "resources")

    with open(
        os.path.join(resources, "ShortDictionary.txt"), "w", encoding="utf-8"
    ) as out:
        out.write("\n".join(short))
        out.write("\n")

    with open(
        os.path.join(resources, "LongDictionary.txt"), "w", encoding="utf-8"
    ) as out:
        out.write("\n".join(long))
        out.write("\n")

    short_str = "\n".join(short)
    long_str = "\n".join(long)
    short_deflated = deflate(short_str)
    long_deflated = deflate(long_str)

    print(f"Concatenated: {len(short_str)} (short), {len(long_str)} (long)")
    print(f"Deflated: {len(short_deflated)} (short), {len(long_deflated)} (long)")

    with open(
        os.path.join(root, "js", "src", "main", "scala", "Dictionary.scala"),
        "w",
        encoding="latin-1",
    ) as out:
        # Scala has a maximum string literal length (because of Java)
        out.write(
            f"""
            // This is a generated file. DO NOT EDIT!
            // See dict_scripts/{os.path.basename(__file__)}
            package vyxal
            object Dictionary{{
                val shortDictionary=decompress({len(short_str)},{chop(short_deflated)})
                val longDictionary=decompress({len(long_str)},{chop(long_deflated)})
                def decompress(len: Int, s: String): Seq[String] = {{
                    val bytes = s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1)
                    val inflater = new java.util.zip.Inflater()
                    inflater.setInput(bytes, 0, bytes.length)
                    val result = Array.fill[Byte](len)(0)
                    inflater.inflate(result)
                    inflater.end()
                    new String(result, 0, len, "UTF-8").split("\\n").toSeq
                }}
            }}
"""
        )

    return {"short": short, "long": long}


if __name__ == "__main__":
    gen(10000, 50000)
