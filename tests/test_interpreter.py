from vyxal.main import execute_vyxal


def get_output_object():
    return {1: ""}


def run_vyxal(code, inputs, flags):
    """Run Vyxal with the given code, inputs and flags"""
    ret = get_output_object()
    execute_vyxal(code, flags + "e", "\n".join(map(str, inputs)), ret, True)
    return ret[1][:-1]


def test_vyxal_object_inputs():
    res = run_vyxal("", ["⟨1|2|3|4|5⟩"], "")
    assert res == "⟨ 1 | 2 | 3 | 4 | 5 ⟩"

    res = run_vyxal("", ["⟨⟨1|2⟩|⟨3|4⟩⟩"], "")
    assert res == "⟨ ⟨ 1 | 2 ⟩ | ⟨ 3 | 4 ⟩ ⟩"

    res = run_vyxal("", ["[`true`|`false`]"], "")
    assert res == "[`true`|`false`]"
