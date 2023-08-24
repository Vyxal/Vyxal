package vyxal

/** Maps ASCII trigraphs to Unicode characters */
object SugarMap:
  val trigraphs: Map[String, String] = Map.from(
    makeTrigraphs("#.", "abcdefghlmnoprstx", "ȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋ") ++
      makeTrigraphs("#.", "ABCDEFGHILMNOPRSTWX", "ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊ") ++
      makeTrigraphs("#,", "BDHILMNORST", "ḄḌḤỊḶṂṆỌṚṢṬ") ++ makeTrigraphs(
        "#^",
        "abcdefgHijklmnopRstuvWXyz+-)!01234_`|<^>;=$(\"'~.:%^",
        "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻ⁺⁻⁾ꜝ⁰¹²³Ч¯ᶿᶲ←↑→↓£¥€“”≈⸠ϩэᵜ",
      ) ++ makeTrigraphs("#,", "0123456789", "₀₁₂₃₄₅₆₇₈₉") ++
      makeTrigraphs("#,", "<>+=o(*:v!/{(~&|\"\n.", "≤≥±₌§∑√∵⊻¬ø₳µɾ∧∨„¶•") ++
      makeTrigraphs(
        "#.",
        "=`|.*:!/5<>\\){[(~@&96ib?;",
        "≠ΘΦ…×∴⌐÷½«»∆ÞλƛΩʀ¤†Ɠɠıð¿¦",
      )
  )

  private def makeTrigraphs(prefix: String, ascii: String, unicode: String) =
    assert(ascii.length == unicode.length)
    ascii.lazyZip(unicode).map { (a, u) => s"$prefix$a" -> u.toString }
end SugarMap
