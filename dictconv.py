orig = open("unfiltered.txt").read().split("\n")
new = open("dictionary.txt", "w")

for word in orig:
    if len(word) < 3:
        continue
    else:
        new.write(word + "\n")
        new.write(word.capitalize() + "\n")

new.close()
