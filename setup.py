from distutils.core import setup

setup(
    name="vyxallanguage",
    version="2.6.0p1",
    packages=["vyxal"],
    scripts=["scripts/vyxal"],
    install_requires=["sympy", "num2words", "numpy"],
)
