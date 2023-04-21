import csv
from . import config

frequencies = [0 for _ in range(256)]
pairs = [[0 for _ in range(256)] for __ in range(257)]

data_path = "vycoder/TrainingData.csv" if config.training else "vycoder/Data.csv"

with open(data_path, newline="") as f:
    for row in csv.reader(f):
        y = 256
        for x in row:
            frequencies[int(x)] += 1
            pairs[y][int(x)] += 1
            y = int(x)


def uniform(x):
    return [1]*256


def frequency(x):
    return frequencies


def frequency_plus_uniform(alpha):
    return lambda x: [x+alpha*sum(frequencies)/256 for x in frequencies]


def pair_frequency(alpha, beta):
    lookup = [[pairs[x][y]*sum(frequencies) + alpha*frequencies[y]*sum(pairs[x]) + sum(frequencies)*sum(pairs[x])*beta for y in range(256)] for x in range(257)]
    assert all(max(row)/sum(row) < 0.5 for row in lookup)
    def f(lst):
        if len(lst):
            x = lst[-1]
        else:
            x = 256
        return lookup[x]
    return f


def pair_frequency2(alpha, beta):
    lookup = [[pairs[x][y]*(sum(frequencies)+256*alpha) + beta*(frequencies[y] + alpha) for y in range(256)] for x in range(257)]
    assert all(max(row)/sum(row) < 0.5 for row in lookup)
    def f(lst):
        if len(lst):
            x = lst[-1]
        else:
            x = 256
        return lookup[x]
    return f