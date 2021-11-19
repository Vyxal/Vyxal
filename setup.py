from distutils.core import setup

setup(
    name="vyxallaguage",
    version="2.6.0p1",
    packages=["vyxal"],
    scripts=["scripts/vyxal"],
    install_requires=["sympy", "numpy"],
)
