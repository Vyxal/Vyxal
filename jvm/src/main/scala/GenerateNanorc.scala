package vyxal

private[vyxal] object GenerateNanorc {

  val commonHeader = raw"""|syntax "Vyxal" "\.(vy)$"
    |comment "##"

    |## Default
    |color white "^.+$"

    |## Numbers
    |color yellow "\<(((((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)?ı((((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)|_)?)|(((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?))\>"
    |
    |## Strings
    |color green "L?\"[^"„”“\\]*[\"„”“]"
    |
    |## Comments
    |color blue "^\s*##.*$"
    |""".stripMargin

  val vyxalNanorc = "vyxal.nanorc"
  val vyxalLitNanorc = "vyxal-lit.nanorc"

  def generate(): Unit = {

  }
}
