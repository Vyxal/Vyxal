# `!` - Length of stack

- Arity: 0
- In: NA
- Out: Integer

Pushes the length of the entire stack.

## Usage

```
1 2 3 ! ║ ⟨1|2|3|3⟩
! ! !   ║ ⟨0|1|2⟩
```

## Justification

Finding the length of the whole stack is a core feature of most practical stack based
languages, hence it's inclusion in Vyxal.

## Alternate Representations

- `W:L&÷&`
