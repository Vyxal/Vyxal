enum AST {
  case Number(value: String)
  case Str(value: String)
  case Command(value: String)
  case MonadicModifier(modi: String, elem1: AST)
  case DyadicModifier(modi: String, elem1: AST, elem2: AST)
  case TriadicModifier(modi: String, elem1: AST, elem2: AST, elem3: AST)
  case QuadricModifier(
      modi: String,
      elem1: AST,
      elem2: AST,
      elem3: AST,
      elem4: AST
  )
  case SpecialModifier(modi: String, value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case Structure(open: String, branches: List[List[AST]])
}
