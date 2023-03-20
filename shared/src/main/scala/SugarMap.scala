package vyxal
object SugarMap:
  val internalMap: Map[String, String] =
    val out = Map.newBuilder[String, String]

    val lowerUpdot = "abcdefghlmnoprstx" -> "ȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋ"
    val upperUpdot = "ABCDEFGHILMNOPRSTWX" -> "ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊ"
    val upperDowndot = "BDHILMNORST" -> "ḄḌḤỊḶṂṆỌṚṢṬ"
    val subscriptNumbers = "0123456789" -> "₀₁₂₃₄₅₆₇₈₉"
    val commaExtra = "<>+=o(*:v!/{(~&|\"\n." -> "≤≥±₌§∑√∵⊻¬ø₳µɾ∧∨„¶•"
    val dotExtra = "=`|.*:!/5<>\\){[(~@&96ib?" -> "≠ΘΦ…×∴⌐÷½«»∆ÞλƛΩʀ¤†Ɠɠıð¿"

    val caretMapping =
      "abcdefgHijklmnopRstuvWXyz+-)!01234_`|<^>;=$(\"'~.:%^"
        -> "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻ⁺⁻⁾ꜝ⁰¹²³⁴¯ᶿᶲ←↑→↓£¥€“”≈′″‴ᵜ"

    for i <- 0 until lowerUpdot._1.length do
      out += s"#.${lowerUpdot._1(i).toString}" -> lowerUpdot._2(i).toString

    for i <- 0 until upperUpdot._1.length do
      out += s"#.${upperUpdot._1(i).toString}" -> upperUpdot._2(i).toString

    for i <- 0 until upperDowndot._1.length do
      out += s"#,${upperDowndot._1(i).toString}" -> upperDowndot._2(i).toString

    for i <- 0 until caretMapping._1.length do
      out += s"#^${caretMapping._1(i).toString}" -> caretMapping._2(i).toString

    for i <- 0 until subscriptNumbers._1.length do
      out += s"#,${subscriptNumbers._1(i).toString}" -> subscriptNumbers
        ._2(i)
        .toString

    for i <- 0 until commaExtra._1.length do
      out += s"#,${commaExtra._1(i).toString}" -> commaExtra._2(i).toString

    for i <- 0 until dotExtra._1.length do
      out += s"#.${dotExtra._1(i).toString}" -> dotExtra._2(i).toString

    out.result()
  end internalMap

  def apply(trigraph: String): String =
    internalMap.getOrElse(trigraph, trigraph)
end SugarMap
