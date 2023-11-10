
# Flags

## Input Flags

|||
|:---------|:---------|
|`H`| Preset stack to 100
|`?`| If there is empty input, treat it as 0 instead of empty string
|`v`| Use Vyxal encoding for input file
|`f`| Get input from file instead of arguments
|`a`| Treat newline seperated values as a list
|`Ṡ`| Treat all inputs as strings
|`A`| Run test cases on all inputs
|`~`| Run test cases on all inputs and report whether results match expected outputs
|`!`| Read program file as bitstring
|`e`| Use the file name as the program source (offline interpreter only)|

## Output Flags

|||
|:---------|:---------|
|`j`| Print top of stack joined by newlines on end of execution
|`L`| Print top of stack joined by newlines (Vertically) on end of execution
|`S`| Print top of stack joined by spaces on end of execution
|`C`| Center the output and join on newlines on end of execution
|`d`| Print deep sum of top of stack on end of execution
|`s`| Sum/concatenate top of stack on end of execution
|`Ṫ`| Print the sum of the entire stack
|`l`| Print length of top of stack on end of execution
|`W`| Print the entire stack on end of execution
|`J`| Print the entire stack, separated by newlines
|`ṡ`| Print the entire stack, joined on spaces
|`G`| Print the maximum item of the top of stack on end of execution
|`g`| Print the minimum item of the top of the stack on end of execution
|`O`| Disable implicit output
|`o`| Force implicit output
|`c`| Output compiled code
|`P`| Print lists as their python representation
|`ḋ`| Print rationals in their decimal form
|`…`| Limit list output to the first 100 items of that list
|`=`| Print bitstring of program (online interpreter also updates byte count)
|`E`| Evaluate stdout as JavaScript (online interpreter only)
|`Ḣ`| Render stdout as HTML (online interpreter only)
|`⋎`| Print the current Vyxal version (offline interpreter only)|

## Modification Flags

|||
|:---------|:---------|
|`M`|  Make implicit range generation and while loop counter start at 0 instead of 1
| `m`| Make implicit range generation end at n-1 instead of n
|`Ṁ`| Equivalent to having both m and M flags
|`r`| Makes all operations happen with reverse arguments
|`R`| Treat numbers as ranges if ever used as an iterable
|`D`| Treat all strings as raw strings (Don't decompress strings)
|`U`| Treat all strings as UTF-8 byte sequences (also don't decompress strings)
|`Z`| With four argument vectorization where all arguments are lists, use zip(zip(a, b), zip(c, d)) instead of zip(a, b, c, d)
|`t`| Vectorise boolify on Lists
|`2`| Make the default arity of lambdas 2
|`3`| Make the default arity of lambdas 3
|`V`| Variables are one character long

## Online Interpreter Timeouts

|||
|:---------|:---------|
|  `5`| Make the interpreter timeout after 5 seconds (online interpreter only)
|  `b`| Make the interpreter timeout after 15 seconds (online interpreter only)
|  `B`| Make the interpreter timeout after 30 seconds (online interpreter only)
|  `T`| Make the interpreter timeout after 60 seconds (online interpreter only)
