# Advanced Vyxal Features

This document is a collection of advanced features of Vyxal. These advanced features are not useful for golfing, but are provided for people who want to use Vyxal for more than just golfing.

## Table of Contents

1. [Custom Elements and Modifiers](#custom-elements-and-modifiers)

## Custom Elements and Modifiers

Vyxal has a pretty extensive set of built-in elements and modifiers - over 200 of them - but what if you want to add your own? Or what if you want to be able to overwrite the behavior of existing built-ins to do something else? The define structure has you covered.

The general format of the define structure is as follows:

```
#:: @<elementName> | <parameters> | <implementation> }
#:: *<modifierName> | <element parameters> | <implemenation parameters> | <implementation> }
```

### The Name Branch

The first branch of the define structure is the name branch. The name provided in the branch will be the name of the element or modifier that is being redefined. A `@` before the name indicates that an element is being defined, and a `*` before the name indicates that a modifier is being defined. Any valid variable name is valid.

#### Single Character Names

In addition to valid variable names, any single character in the vyxal codepage that is not a structure character can be used. This overrides the built-in behaviour of the character. This will be demonstrated in the examples section.

### The Parameters Branches

For elements, there is only a single parameters branch. This branch is similar to
lambda parameters, in that parameters will be popped from the stack and passed to the element. Anything valid in a lambda parameters branch is valid here.

For modifiers, there are two parameters branches. The first branch names the
elements the modifier will be applied to. That is, this branch is where the physical functions are declared. Only names and numbers are valid in this branch. No varargs (yet - this _might_ be considered in the future.) and no operating on the stack.

The second branch is the arguments that will be
passed to the implementation. This is pretty much the same as the parameters branch for elements.

### The Implementation Branch

The implementation branch is where the actual code for the element or modifier is written. When called, the custom definition will pop all arguments from the stack
and execute the implementation as if it were a lambda. The implementation branch can be any valid Vyxal code.

### Using Custom Elements and Modifiers

To use a custom element:

```
#:@elementName
```

To use a custom modifier:

```
#:`modifierName <element(s)>

### Examples

```
#:: @incrementAndHalf | x | #$x 1+ 2÷}
5 #:@incrementAndHalf
```

prints `3`.

```
## Say for some obscure reason you want to redefine the `+` operator to be subtraction instead of addition.
#:: @+ | lhs, rhs | #$lhs #$rhs -}
4 6 + ## -2
1 1 + ## 0
```

```
#:: *ReduceRange | f | 1 | ɾ #$f R }
5 #:`ReduceRange + ## 15

#:: *RevRow | f | arr | #$arr V #$f M V } 
12ʀ4Ẇ #:`RevRow 1İ ## [[0,1,2], [4,5,6], [8,9,10]]

#:: *p | f, g | ! | #$f Ḃ #=temp #$g Ė #$temp } ## Poor man's parallel apply
4 5 p+- ; ## [9, -1]
```

### Retrieving Original Behaviour

If you've overwritten a built-in element or modifier and want to get the original behaviour back, you can use the `#:~` prefix. This will retrieve the original behaviour of the element or modifier. This is useful if you want to use the original behaviour in your custom definition.

E.g.

```
#:: @+ | lhs, rhs | #[#$lhs|#$rhs#] #[2|2#] ₌ [5|#$lhs #$rhs #:~+}}
```

Uses the original behaviour of `+` to add the two numbers if both arguments aren't 2, and returns `5` otherwise.

### Literate Mode

It is not reccomended to do this with literate mode because keyword lexing can
get a little funky. For now, only use this with SBCS.
