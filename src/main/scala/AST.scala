enum AST {
  case Number(value: String)
  case Str(value: String)
  case Command(value: String)
  case MonadicModifier(elem1: AST)
  case DyadicModifier(elem1: AST, elem2: AST)
  case TriadicModifier(elem1: AST, elem2: AST, elem3: AST)
  case QuadricModifier(elem1: AST, elem2: AST, elem3: AST, elem4: AST)
  case SpecialModifier(value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
}
