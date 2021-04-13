md = open("docs/elements.md", "w+", encoding="utf8")

md.write(
    "# Functions and constants\n"
    "cmd | inputs | out/effect\n"
    "---|---|---\n"
)

with open("docs/elements.txt", "r", encoding="utf8") as txt:
    for line in txt:
        if line == "\n":  # Reached the last line
            break
        if line[0] in "*<|\\>": # Escape special characters
            line = "\\" + line
        space_ind = line.index(' ')
        # The type starts at `(`
        type_ind = line[space_ind:4].find("(")
        # The index of the equal sign, where the description starts
        effect_ind = line[1:].index("=") + 1
        md.write(
            f"| `{line[:space_ind]}` "
            f"| {' ' if type_ind == -1 else line[type_ind+space_ind:effect_ind]} "
            f"| {line[effect_ind+1:-1]} |\n"
        )

md.close()
