# Fuzzing

## What's Fuzzing?

According to [Wikipedia](https://en.wikipedia.org/wiki/Fuzzing),

> fuzzing or fuzz testing is an automated software testing technique that involves providing invalid, unexpected, or random data as inputs to a computer program

## How to Run it


The `fuzz` task in the JVM module can be run with Mill like so:

```shell
./mill jvm.fuzz <length> <timeout>
```

`<length>` controls the maximum length of the programs generated, while `<timeout>`
controls how many seconds each program is allowed to run before being terminated.

You can find the actual implementation in [`jvm/src/vyxal/fuzz.scala`](/jvm/src/vyxal/fuzz.scala)
