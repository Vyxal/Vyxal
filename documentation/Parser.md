The Vyxal parser may seem like a complex beast at first glance - with all its private def functions and `@unchecked` pattern matching, it can be hard to comprehend what's happening. Rest assured that this documentation is here to help you make sense of the Vyxal parser. It'll break down the various components and explain how they work together to achieve the desired parsing results.

## The Pipeline - A 30k Feet Overview

To start with, it's helpful to know that the parser follows a sort of three-stage pipeline:

1. Token Preprocessing
2. Token Parsing
3. AST Postprocessing

Each of the stages transforms its input into a format that is helpful for the next stage.

## Token Preprocessing

The first stage of the pipeline is token preprocessing. This stage substitutes some tokens for others, so that the next stage can fully focus on token grouping, rather than handling annoying quirks. The tokens that are changed are:

- `VyxalToken.StructureClose(")")` -> 2 x `VyxalToken.StructureClose("}")
- `VyxalToken.SyntaxTrigraph("#:[") {a bunch of tokens and branches} VyxalToken.StructureAllClose` -> `VyxalToken.UnpackVar("...")`
