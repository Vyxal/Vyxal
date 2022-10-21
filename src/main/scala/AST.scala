enum AST {
  case Number(value: String)
  case Str(value: String)
  case Command(value: String)
  case MonadicModifier(modi: String)
  case DyadicModifier(modi: String)
  case TriadicModifier(modi: String)
  case QuadricModifier(
      modi: String
  )
  case SpecialModifier(modi: String, value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case Structure(open: String, branches: List[List[AST]])
}
