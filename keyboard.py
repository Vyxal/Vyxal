def parse_file():
    ret = []
    with open("docs/elements.txt", "r", encoding="utf8") as txt:
        for line in txt:
            if line == "\n": #Finished
                break
            else:
                if line[0] == " ":
                    ret[-1] += "\n" + line[line.find("=", 1) + 1:-1]
                else:
                    ret.append(line[line.find("=", 1) + 1:-1])
    print(ret)

parse_file()