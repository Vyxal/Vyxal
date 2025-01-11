# Vyxalism - The Design Principles of Vyxal

Hi there hello. You've found yourself in the part of the documentation that
outlines why Vyxal exists and why it is the way it is. Throughout this document,
I will describe some of the core design principles I tried to accomplish with
Vyxal, why I think they are important, and how they might look going forward.

## Why Vyxal at All?

Programming languages are a dime a dozen. Seriously, while some programming
languages stand out for unique breakthroughs (e.g. Rust in terms of 
memory safety), a lot of languages go unnoticed as just another entry in
the ever-growing database of computer instruction sets. So then, why should
there be another programming language? Especially one that falls under many
well-established categories of languages (stack-based, array language, 
golfing language, etc.)?

Because I believe that there is space for something the intersection of all
of these categories miss. Expressiveness without sacrificing convenience.

For example, stack-based languages are great for convenience and brevity, afterall
they allow for a rather natural manner of expressing algroithms. However, they
often lack the expressiveness of paradigms like array languages.

On the other hand, array languages are great for expressing complex operations
in compact and really quite beautiful ways. But they often lack the convenience
of conventional programming languages (stack-based and not), prioritising forcing
users to mould their thinking to the language rather than the other way around.

As such, the combination of programming paradigms in Vyxal entangles the benefits
of each while mitigating the downsides. The stack-based nature of Vyxal captures 
convenience. The array language nature of Vyxal enables the expressiveness lauded 
in languages like APL and J. And the golfing language nature of Vyxal allows for an 
additional layer of elegant terseness.

Now sure, it does so with a few trade-offs - some design decisions are made to
optimise for the golfing aspect at the expense of sensibility. In its original
form, Vyxal was intended to be good at code golf. However, in spite of the
golfing focus, Vyxal encapsulates a fine blend of expressiveness and convenience.

## Why Stack-Based?

Because stack-based languages are the best at code golf. As aforementioned, Vyxal 3
is still fundamentally a golfing language. History has shown that stack-based languages
do the best at code golf, so it made sense to stick with that paradigm.

However, the real real reason for Vyxal being stack-based is that the stack-based
paradigm was my first interaction with esoteric programming languages (esolangs).
\><> (fish) was the first esolang I learned, and its stack operations are what
were so enticing. Pushing items onto the stack, popping them when needed, 
ordering arguments in a way that makes sense to the algorithm. Something about
stack languages just really feels good.

## Why Array-Based?

Would you believe me if I said because it was good for code golf? To be honest, 
that shouldn't come as a surprise at this point :p.

Yet, the array nature of Vyxal was originally more accidental than intentional.
Well, "not intentional" in that I didn't realise that's what I was doing - if you
had asked me if I was making an array language, I would have said "Vyxal isn't 
anything like APL at all". 

When I first started with making Vyxal, I had just come from playing around with 
05AB1E and a smidge of Jelly. These languages served as inspiration through
providing a baseline for what built-ins should be present in a competitive golfing
language. In analysing these languages, and adapting their features to Vyxal, I
ended up unwittingly infusing Vyxal with array language features.

Nowadays, the array nature of Vyxal is much more intentional. Since creating
Vyxal in 2020, I have had experience with the more traditional realm of array
languages (what I refer to as "Big Array"), and I'm now more aware of what makes
something an array language. Rather than unknowingly stumbling into array
language territory, I can unironically claim that Vyxal is an array language.

And that leads into the elephant in the room.

## Why Isn't There a Rectangular Array Model?

If you were to ask someone what they'd expect to see in an array language, chances
are they would say "an array model". I mean, it's in the name, "_array_" language.

However, an array model is not a requirement for an array language. General
consensus seems to be that an array language focuses primarily on operations
that "allow the application of operations to an entire set of values at once" 
([Array programming - Wikipedia](https://en.wikipedia.org/wiki/Array_programming)).
As mentioned earlier, this is a trait that Vyxal does possess.

That justifies calling Vyxal an array language, but it doesn't explain why Vyxal
doesn't have a rectangular array model. The reason for this is simple. Traditional
array lanugages are really good at expressing complex algroithms in elegant ways.
This is a very admirable trait of array languages, and one that I really do respect
about languages like APL, J, BQN, Uiua, etc.

However, in their pursuit of array operations, Big Array languages let array models
get in the way of user convenience. Rectangular array thinking may become second-nature
after a while, but in my opinion, it does not match reality and the way humans
think about data. Very rarely does life fit within a neat box of fixed dimensions.
Rather, it is flexible, jagged, and unstructured. Therefore, I believe that a
programming language should reflect this reality.

The lack of a rectangular array model in Vyxal allows users to think less about
whether their data will play nicely with certain shapes, and more about what they
actually want to do with their data. Sure, there is an aspect of elegance sacrificed
in this decision, as having a rectangular array model makes some operations and
algorithms neater/nicer to work with/mathematically "purer". But at the end of
the day, I believe it's better to have code that is easy to work with regardless
of array expertise than code that covers all the bases but is a pain to write and
comprehend from scratch.

## Strings as Objects, Not Arrays

<TODO>

## Why Have Practical Features in a Golfing Language?

<TODO>

## Why a Golfing Language at All?

<TODO>

## Literate Mode

<TODO>

## Functions as Objects

<TODO>

## Variables? In my Tacit?
_Alternatively: Variables? In my Golfing Language?_

<TODO>

## Other Differences from Big Array

<TODO>

## Conclusion

<TODO>
