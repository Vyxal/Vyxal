from setuptools import setup

setup(
    name="vyxal",
    version="2.7.1",
    description="The Vyxal Programming Language",
    url="https://github.com/Vyxal/Vyxal",
    author="Vyxal Organisation",
    license="MIT",
    packages=["vyxal"],
    install_requires=["numpy", "sympy", "num2word"],
    scripts=["scripts/vyxal"],
)
