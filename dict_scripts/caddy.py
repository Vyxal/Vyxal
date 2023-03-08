import sort_script
import sss_compress
import importlib
import corpus

LENGTHS = [
    5000,
    10000,
    15000,
    20000,
    20453,
    22000,
    25000,
    50000,
    69420,
    100000,
    150000,
    200000,
    220000,
    227845,
]

CARTESIAN_PRODUCT = [(x, y) for x in LENGTHS for y in LENGTHS]

for short, long in CARTESIAN_PRODUCT:
    dicth = sort_script.gen(short, long)
    ## Reload the sss_compress module to get the new dictionary
    e = sort_script.gen(short, long)
    ## Run the tests
    lengths = [(len(sss_compress.g(x, e))) for (x, _) in corpus.STRINGS]
    print(f"short: {short}, long: {long}, avg: {sum(lengths) / len(lengths)}")

    differences_in_lengths = [
        corpus.STRINGS[i][1] - lengths[i] for i in range(len(corpus.STRINGS))
    ]

    print(
        f"greatest difference: {max(differences_in_lengths)}, smallest difference: {min(differences_in_lengths)}, avg: {sum(differences_in_lengths) / len(differences_in_lengths)}"
    )

    print(
        "string shortest than jelly: "
        + corpus.STRINGS[
            differences_in_lengths.index(max(differences_in_lengths))
        ][0][:30].replace("\n", "\\n")
    )

    print(
        "string least shortest than jelly: "
        + corpus.STRINGS[
            differences_in_lengths.index(min(differences_in_lengths))
        ][0][:30].replace("\n", "\\n")
    )
