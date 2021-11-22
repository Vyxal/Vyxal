from distutils.core import setup

setup(
    name="vyxal",
    version="2.6.0p2",
    packages=["vyxal"],
    scripts=["scripts/vyxal"],
    install_requires=["sympy", "num2words", "numpy"],
)
