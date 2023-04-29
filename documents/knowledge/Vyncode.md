# A Summary of Vyncode

Ever seen other languages have decimals in their byte counts and thought "man, I wish I could do that too"? Ever been beaten by half a byte by some fancy schmancy half-byte esolang and thought "gosh dang it I can't believe it be like this"? Or have you ever just wanted to explore novel compression algorithms?

Well ask these questions and feel those ways no more, because everyone's favourite golfing language just got fractional!

"Hang on a second lyxal... Vyxal is SBCS, meaning commands need the whole byte. How you gonna make things not take an entire byte to store?"

Well the madlads (well, madlad singular really - @AndrovT ftw) in the vyxal chatroom informed me that they cracked the code on how to make programs a string of bits that can be converted to bytes.

By using a [range coder](https://en.wikipedia.org/wiki/Range_coding) trained on the 1400+ vyxal answers that exist on the Code golf stack exchange, weekgolf.net and emkc, programs can be compressed into a string of bits that can later be decoded. If you want to know more about it, head over to the [Vyncode repository](https://github.com/Vyxal/Vyncode).

## Cool but how do I use it?

Glad you asked. And even if you didn't, I asked at one point, so I'll tell you.

To compress programs using Vyncode, use the `=` flag. This will run your program as normal, but will also print the bitstring representation of your program to stdout.

To decompress and execute programs that have been run through Vyncode, use the `!` flag. Make sure your program file contains only the bitstring produced by the encoding process.

Different versions of vyxal may use different versions of Vyncode, as the corpus and prediction algorithm used may change over time. The offline interpreter will always use the latest version, but (soon) you will be able to specify which version to use.

The online interpreter also uses the latest version of Vyncode, but you can change it in the Vyncode version box that appears just under the flag box when using the `=` or `!` flag. 
