# Dictionary Compression

TODO(lyxal) document how dictionary compression and decompression actually works

Look in the `dict_scripts` folder for dictionary-related stuff:

- words.txt - The list of words to be used in the dictionary
- sort_script.py - Make sure to run this every time `words.txt` gets updated.
  It's for separating the words into a short and a long dictionary.
  It creates `ShortDictionary.txt` and `LongDictionary.txt` in `shared/src/main/resources`
  so that the JVM and Native code can load the dictionaries, and it creates
  `js/src/main/scala/Dictionary.scala` for the JS code (because Scala.js doesn't
  embed `resources`).
- corpus.py - Used for providing test data when changing word list sizes.
- caddy.py - Used for trying different word list sizes to see which sizes are optimal
- sss_compress.py - The original sss from jelly, extracted into a single file.
