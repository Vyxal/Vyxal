# Canvas

The canvas is a global ASCII-art canvas based off [05AB1E's canvas](https://codegolf.stackexchange.com/questions/96361/tips-for-golfing-in-05ab1e/175520#175520). This file will serve as both a specification and a guide for how to use the canvas element.

The `Canvas` class is located in the `Canvas.py` file. It exposes several methods and values, but only one should be used: `draw`, which takes a list of directions, a list of lengths and a piece of text to draw. 

The directions can contain:

- Digits from 0-7 which point in certain directions:
  ```
  7 0 1
   \|/
  6-*-2
   /|\
  6 4 3
  ```
  These send the cursor in their corresponding directions
- 8 which brings the cursor back to the start
- single characters in `+x[]^v<>`, which create patterns similar to their shapes

The lengths are simply a list of numbers. The text is repeated as necessary.

Text is rendered to the length of the directions or the length of the lengths, whichever is larger.

With the Vyxal interface to the canvas, there are two commands:

- `ø∧` modifies a global canvas, which is implicitly printed if nothing else has been printed and it has been modified
- `ø^` starts with a blank canvas and outputs a new one as a string onto the stack.

Both commands take input in the same way, and attempt to rearrange their arguments to make them valid.

- If there is a single string as input, it must be the text.
- If there are two strings as input, one must be the directions - If one string contains only valid dirs, it is a list.
- If there is a number in the input, it must be the (universal) length. If there is another, it's directions.
- In all other cases, the lengths are assumed to come before the directions.