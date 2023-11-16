import sys

if __name__ == "__main__":
    files = [
        x for x in sys.argv[1].split("\n") if x.endswith("js") or x.endswith("map")
    ]
    print(" !! ".join(files))
