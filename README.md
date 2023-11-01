# Vyxal 3 - Better than Ever

_If you are looking for version 2 of Vyxal, you can find it [here](insert hyperlink)_


Vyxal is an stack-based esoteric array language that is dedicated to dominating competition in code golf challenges. This means that it strips away all need for boilerplate, long function names and impractical source layouts. However, it also has a more traditional and familiar way of expressing itself if needed. Vyxal 3 is the third major iteration of the language that drives home this key design goal. Here's how

## What's the Same as Version 2?

Vyxal 3 is still a stack based programming language that uses a Single Byte Code Set (SBCS) for scoring well in code golf. It also retains some of the key features of. version 2, such as contexts, modifiers and many, many type overloads.

It also maintains the more prac-lang-centric features such as variables and comments that version 2 has.

That's about it.

## Because here's what's new in version 3

### Literate Mode

Ever wanted to write vyxal without needing an on screen keyboard or editor shortcuts? Well now you can! Literate mode is designed to allow you to write vyxal 3 programs in complete ascii while maintaining a SBCS score. It does this by simply converting the literate code into sbcs code. Wanna see it in action?

```
## A collatz conjecture program
stdin := n ## read the input into n
(. scan-fix: {if even? then halve else increment endif}) := collatz
$n $collatz call
```

_TODO: SBCS equivalent_

```
## The classic fizzbuzz
100 map{
  n [3, 5] divides?
  "FizzBuzz" halve
  dot-product maximum
} join-on-newlines
```

_TODO: SBCS equivalent_

```
## How about something that generates all the fibonacci numbers?
## Like actually all of them

relation add from [1, 1] end

## is it really that simple?!
```

[yes!](online interpreter link)

(as SBCS):

```
Ṇ+|#[1|1#]}
```

Isn't it snazzy? You can finally do well at golf without needing to smash your head on a weird looking keyboard!

### More modifiers

Notice that `scan-fix:` in the collatz example? That's a modifier! "But version 2 already has modifiers, you said so yourself!" you say. Well version 2 had a very limited selection of modifiers, as they were more experimental at the time. Now, there's like 20 modifiers, all waiting to be used. Plus, they've gotten a little upgrade, in the form of

### Arity grouping

Ever wanted to use jelly but realised it's way too hard? (who even sells hard jelly anyway? I thought the whole point was that it was soft and jiggly.) Well forget about using [Ohm](link to paragraph that makes jelly comparison], because vyxal just got its own element grouping based on arity system. Plus it's much easier to understand!

Say you have a nilad followed by a monad (basically a constant followed by something that takes a single thing). Usually this sequence would be treated as two elements. However, it's obvious that the monad is going to operate directly on the nilad, as it's the same as writing `monad(nilad)`. So instead of treating it as 2 things, it treats it as a single thing. This is useful for modifiers because you might have a situation where you can squeeze an extra element into what a modifier modifies where you wouldn't have been able to do so previously.

### Variable Buffs

But that's all esolang specific stuff. If you've never used a golfing language before, you're probably wondering what all of that element stuff means. Well I've got prac-lang material for you that I think you'll love.

To define a variable:

```
value #=name (sbcs)
value := name (literate)
```

And to retrieve its value:

```
#$name (sbcs)
$name (literate)
```

"that's all good and well, but that's the same capabilities of version 2, how is that a buff?"

Because a) augmented variable assignment:

```
function #>name (SBCS)
function :> name (literate)
```

(works with any element or function, not just the regular `+=`, `-=`, `*=` etc you're probably used to seeing)

And b) variable unpacking

```
#:[x|y|z] (SBCS)
:=[x|y|z] (literate)
```

Variable unpacking can support any number of depths (e.g. `[a|b|[[[[c|d]]]|e]|f]`). Think of it like tuple unpacking in python but a little more powerful.

### Whole New Built-in Set

The entire list of elements has been re-worked to remove some unnecessary overloads in version 2, and add some sorely missing elements that would make golfing a lot easier and shorter.

## How do I run Vyxal 3?

There's a few methods:

1. Download one of the release `jar` files, and run using

```
java -jar vyxal-jar <arguments>
```

2. Download one of the executables and run as you would usually run an executable

3. Head over to [the online interpreter](vyxal.github.io)

4. Download the repository source and use one of the [build tool options](https://github.com/Vyxal/Vyxal/blob/version-3/contributing/BuildTools.md) to build and run.

## Links

- [Repository](https://github.com/Vyxal/Vyxal)
- [Online Interpreter](http://vyxal.github.io)
- [Tutorial](https://vyxapedia.hyper-neutrino.xyz/beginners)
- [Main Chat Room (SE Chat)](https://chat.stackexchange.com/rooms/106764/vyxal)
- [Vycord (Discord)](https://discord.gg/hER4Avd6fz)
- [Elements](https://github.com/Vyxal/Vyxal/blob/main/documents/knowledge/elements.md)
- [Vyxapedia](https://vyxapedia.hyper-neutrino.xyz/)
