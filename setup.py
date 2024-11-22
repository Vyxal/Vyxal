from cx_Freeze import setup, Executable

# Dependencies are automatically detected, but they might need fine-tuning.
build_exe_options = {
    "excludes": [],
}

setup(
    name="vyxal2",
    version="0.1",
    description="Vyxal 2 executable",
    options={"build_exe": build_exe_options},
    executables=[Executable("vyxal/__main__.py", base="console", target_name="vyxal2")],
)
