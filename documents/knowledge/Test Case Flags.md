# Test Case Flags

If you've ever wanted to run multiple test cases at once from a code golf challenge, you will have made a custom header/footer to parse the input and present it in a nice looking
format. Luckily, you don't have to do this if you use the `A` and `~` flags. These CLI flags allow for multiple test cases to be given as input without any extra handling apart from
potentially rewriting the input format a bit.

## The `A` Flag

The `A` flag is for running programs over multiple sets of inputs. You might use it as so:

```
Code:

+++

Input:

4,5,6
8,9,2
"abc","def",5
```

This would output:

```
4, 5, 6 => 19
8, 9, 2 => 27
'abc', 'def', 5 => abc5defabc
```

Each line represents a different test case to run the input on. Multiple inputs are separated by commas. Lists are given as python list format. Functions cannot be given using
the `A` flag.

## The `~` Flag

The `~` flag is for running programs over multiple sets of inputs and comparing the results to expected outputs. You might use it as so:

```
Code:

+++

Input:

4,5,6 -> 19
8,9,2 -> 26
"abc","def",5 -> abc5defabc
```

This would output:

```
(4,5,6 -> 19) ==> PASS
(8,9,2 -> 26) ==> FAIL... got 27 instead
("abc","def",5 -> abc5defabc) ==> PASS
```

This is different to the output of the `A` flag, as it actually compares against an expected output.

By default, test cases are tested if they match the regex `(.+) ?(?:=>|-+>) ?(.+)`. However, a custom regex can be provided if the first test case starts with `!`. For example:

```
!f\((.+)\) -> (.+)
f(4,4,5) -> 2
f(1,2) -> 6
```

Extracts the test case text from the relevant parts. Note that the regex only extracts the text. It doesn't ensure it's valid data.
