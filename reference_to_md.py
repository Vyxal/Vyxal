md = open('docs/reference.md', 'w+', encoding='utf8')

md.write('cmd  |  stack   |out/*effect\n---|---|---\n')

with open('docs/reference.txt', 'r', encoding='utf8') as txt:
  txt.readline()
  for line in txt:
    if line[0] in '*<|\\>':
      line = '\\' + line
    space = line.index(' ')
    line = '| ' + line[:space] + ' |' + line[space:]
    md.write(line)