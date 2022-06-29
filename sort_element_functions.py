import re

f = open('vyxal/elements.py', 'r', encoding='utf-8')
file = f.read()
f.close()

fns = [x.strip() for x in re.findall("(?:@[a-zA-Z_]+\n)?def +[a-zA-Z_][a-zA-Z_\d]*\s*\([\S\s]*?\)(?: *-> *.+?)?\:(?:\n(?:(?:    |\t).*)?)+", file)[1:]]

file = file.split("\n")

start_line = file.index(fns[0].split("\n")[0])
last_line = file.index(fns[-1].split("\n")[-1])

f = open('vyxal/elements.py', 'w', encoding='utf-8')

f.write("\n".join(file[:start_line]) + "\n" + "\n\n\n".join(sorted(fns, key=lambda x: x[x.index('def '):])) + "\n\n" + "\n".join(file[last_line + 1:]))
f.close()
