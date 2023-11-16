import sys

if __name__ == "__main__":
    files = [
        x for x in sys.argv[1].split("\n") if x.endswith("js") or x.endswith("map")
    ]
    files = [x for x in files if not x in ["worker.js", "main.js"]]
    print(" !! ".join(files))
