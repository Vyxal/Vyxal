# Parser

The parser is responsible for converting tokenised code into a list of structures. 

The following list of structure types should be comprehensive and complete.

    NONE
        The generic structure.

    IF_STMT
        If statement structure.

    FOR_LOOP
        For loop structure.

    WHILE_LOOP
        While loop structure.

    FUNCTION
        Function structure.

    LAMBDA
        Lambda structure. Note that the other lambda types
        (map, filter and sort) are just lambdas followed by the
        appropriate element token. Hence, their attributes won't be
        listed here.

    FUNCTION_REF
        Function reference structure.

    VARIABLE_GET
        Variable retrieval.

    VARIABLE_SET
        Variable assignment.

    LIST
        List literal.

    MONADIC_MODIFIER
        A monadic modifier - takes the next element

    DYADIC_MODIFIER
        A dyadic modifier - takes the next two elements

    TRIADIC_MODIFIER
        A triadic modifier - takes the next three elements
