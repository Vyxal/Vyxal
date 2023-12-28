# Objects and Extension Methods

Vyxal is already turing complete, and can be used to easily solve many problems.
However, say there was a situation where you needed to model a complex real-world
object, such as a car. You could use two lists to map keys to values, but that
would require making sure that the two lists are always in sync. Objects take
away this hassle, and allow you to model real-world objects in a way that is
convienient and easy to use.

## The OOP Model

Unlike most OOP languages, Vyxal does not have traditional classes. Instead, it
has struct-like objects. These objects contain only data, and no methods. To add
methods to an object, you can use extension methods (covered later).

There is also no inheritance, meaning that composition is the only way to reuse
code. This is not a bad thing, as it makes the implementation of objects much
simpler.

## Defining Objects

To define an object:

```
#:O ObjectName | #$restrictedMember #=privateMember <data> #$restrictedMemberWithValue
<data> #=privateMemberWithValue #!publicMember}
```

* `ObjectName` is the name of the object. This can be any valid variable name. It
is recommended to use PascalCase for object names.

Object names cannot be one of `num`, `str`, `lst` and `fun`, as these are reserved
for the 4 built-in types.

* `restrictedMember` is a member that can be read publicly, but not written to. It
can be updated by the object itself, but not by the user. This essentially creates
an implicit getter for the member.
* `privateMember` is a member that can only be read and written to by the object.
Attempting to access it from outside the object will result in an error.
* `<data>` is any single element. It'll be used as a default value for the
member.

Those keen among us (!) will notice that the member syntax is similar to variable
syntax. This is intentional, as it creates a consistency with existing syntax.
Further, it creates a system of mmemonics for remembering what each member does:

* `#$` was chosen as the restricted member sigil because it indicates "getting" a
variable.
* `#=` was chosen as the private member sigil because it indicates "setting" a
variable, which is something that should be done in a private context.
* `#!` was chosen as the public member sigil because it indicates danger - public
members are typically a bad idea. It doesn't have the same meaning as `#!` for
constants, but the `!` was appropriate to signify the danger of public members.

Members without a default value will be set from the stack when the object is
created.

## Using Objects

To create a new object:

```
#$ObjectName Ė
```

Defining an object creates a variable with the same name as the object. This
pushes a constructor for the object onto the stack. The `Ė` element calls the
constructor and pushes the created object onto the stack. This is analogous to
calling a function, which is intentional - there's no need for a `new` keyword
when you can just call the constructor in a similar way to a function.

To access a member of an object:

```
## Assuming the object is on the stack
"memberName" «
```

This pushes the value of the member onto the stack. If the member is a private
member, this will fail, unless inside an extension method.

To set a member of an object:

```
## Assuming the object is on the stack  
"memberName" <value> ŀ
```

This sets the value of the member to the value on the stack. If the member is a
restriced or private member, this will fail, unless inside an extension method.

## Example

```
#:O Map | 
  #[#] #!keys
  #[#] #!values
}

λ value, key, mp | 
  #$mp "keys" ᵇ« #$key ŀ 
  #$mp "values" ᵇ« #$value ŀ
  #$mp
} #=put

#$Map Ė
"key1" "value1" #$put Ė
```

This example creates an object that acts like a map. It has two public members,
`keys` and `values`, which are lists of the keys and values in the map. It then
creates a lambda that takes a value, a key, and a map, and adds the value and key
to the map. Finally, it creates a new map, and adds a key and value to it.

Note that the lambda is not a member of the object, nor is it linked to the
object in any way. It is simply a lambda that takes a map as an argument.

## Extension Methods

In the previous example, a lambda was used to add a key and value to a `Map` object.
However, the lambda is in no way linked to the `Map` object - it can be called
with any object, and will attempt to retrieve the `keys` and `values` members.
This obviously is not desirable behaviour, even for a language as loose with types
as Vyxal. To solve this, extension methods can be used to create fine-grained
control over the types involved in a function.

To define an extension method:

```
#:>> symbol | argA | typeA | argB | typeB | ... | argN | typeN | implementation }
```

* `symbol` is the symbol that will be used to call the extension method. This can
be any valid symbol allowed in a custom element.
* `arg` is the name of an argument to the extension method.
* `type` is the type of an argument to the extension method. This can be any valid type name, as well as `*`, which indicates an any type.
* `implementation` is the implementation of the extension method. This will always
be the last branch.

To use an extension method, call it as you would any custom element:

```
#:@symbol
```

### Important Notes

* Extension methods require at least one argument. Attempting to define an
extension method with no arguments will result in an error. Use the define
structure instead.
* Extension methods will be scoped globally. Currently, there is no way to define
an extension method that is only available within a certain scope/context.
* The types of the arguments are checked at runtime. If the types do not match,
an error will be thrown.
* Like custom elements, the implementation branch is where the actual code for the element or modifier is written. When called, the method will pop all arguments from the stack
and execute the implementation as if it were a lambda. The implementation branch can be any valid Vyxal code.

### Order of Extension Method Matching

When a symbol is called, first the interpreter will check for an extension method
with the appropriate types of items on the stack. If found, it will execute the
extension. Otherwise, the interpreter will check for a custom element with the
same symbol. If found, it will execute the custom element. Otherwise, it will
finally try a lookup of the built-in symbols. If none of these are found, an
error will be thrown.

```
Highest Priority
- Extension Methods
- Custom Elements
- Built-in Symbols
Lowest Priority
```

## Example

```
#:O Map | 
  #[#] #!keys
  #[#] #!values
}

#:>> put | value | * | key | * | mp | Map | 
  #$mp "keys" ᵇ« #$key & ŀ 
  #$mp "values" ᵇ« #$value & ŀ
  #$mp
}

#$Map Ė
"key1" "value1" #:@put
```

This example is the same as the previous example, except that it uses an extension
method instead of a lambda. Calling `put` with any other value for `mp` that is
not a `Map` object will not execute the extension method.

## Literate Mode

To define an object:

```
object ObjectName =>
  $restrictedMember
  :=privateMember
  :!=publicMember
}
```

To create an object:

```
`ObjectName`
## or
$ObjectName call
```

To access a member of an object:

```
"memberName" @<=
```

To set a member of an object:

```
"memberName" value @=>
```

To define an extension method:

```
extension symbol given
  argA as typeA,
  argB as typeB,
  ...
  argN as typeN
does
    implementation
}
```

_Note that any branch keyword can be used in place of `does`, `as`, `given` and `,`._

To use an extension method:

```
$@symbol
```

## Map In Literate Mode

```
object Map =>
  [] $keys
  [] $values
}

extension put given
  value as *,
  key as *,
  mp as Map
does
  $mp "keys" peek: (@<=) $key append @=>
  $mp "values" peek: (@<=) $value append @=>
  $mp
}

`Map` "key1" "value1" $@put
```