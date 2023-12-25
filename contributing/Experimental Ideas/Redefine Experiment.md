Syntax

```
#~ name@args | implementation ## as element/function
#~ name*args | implementation ## as modifier
```

Examples

```
#~ +@a,b | #.{#$a #$b -}
#~ increment@x | #.{#$x 1 +}
#~ dip`f | #.{!|#=top #$fĖ #$top}
```

Usage

```
#⸠dip +
4 #⸠increment
```
---
Literate

```
~ name@args -> implementation
~ name*args -> implementation
```

Examples

```
~ +@a,b => {$a $b -}
~ increment@x => {$x 1 +}
~ dip*f => {!: :=top `f` $top}
```

Usage

```
$`dip +
4 $`increment
```