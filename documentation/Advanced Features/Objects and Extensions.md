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

In the previous example, we created a lambda that takes a map as an argument. We
then had to pass the map to the lambda as an argument. This is not ideal, as it
does not ensure that `Map` objects are used in the correct way.
