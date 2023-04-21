import csv

from codepage import codepage

programs = []

with open("vycoder/QueryResults.csv", newline="", encoding="utf-8") as f:
    for row in csv.reader(f):
        if row[0] == "Post Link":
            continue
        code = row[1]
        if "<pre><code>" not in code:
            continue

        # Extract the first bit of code
        vyxal = (
            code.partition("<pre><code>")[2]
            .partition("</code></pre>")[0]
            .strip()
        )
        vyxal = vyxal.replace("&quot;", '"')
        vyxal = vyxal.replace("&gt;", ">").replace("&lt;", "<")
        vyxal = vyxal.replace("&amp;", "&")

        if any(vyxal.count(c) >= 10 for c in vyxal):
            continue
        if len(vyxal) > 100:
            continue
        if any(c not in codepage for c in vyxal):
            continue
        programs.append(vyxal)

with open("vycoder/Data.csv", "w", newline="") as f:
    writer = csv.writer(f)
    for vyxal in programs:
        writer.writerow([codepage.index(c) for c in vyxal])