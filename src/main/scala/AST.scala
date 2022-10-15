enum AST {
  case Number(value: String)
  case Str(value: String)
  case Command(value: String)
  case MonadicModifier(mod: String, elem1: AST)
  case DyadicModifier(mod: String, elem1: AST, elem2: AST)
  case TriadicModifier(mod: String, elem1: AST, elem2: AST, elem3: AST)
  case QuadricModifier(
      mod: String,
      elem1: AST,
      elem2: AST,
      elem3: AST,
      elem4: AST
  )
  case SpecialModifier(mod: String, value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case Structure(open: String, branches: List[List[AST]])
}
