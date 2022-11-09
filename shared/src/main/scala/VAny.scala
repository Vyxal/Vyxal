package vyxal

// todo check if these names or this whole way of structuring need to be changed
type VAny = VAtom | VList
type VAtom = VVal | VFun
type VVal = VNum | String
type VNum = spire.math.Number

enum VFun(arity: Int) {
  case Lam(lam: AST.Lambda, arity: Int, ctx: Context) extends VFun(arity)

  /** A reference to a user-defined function */
  case FnRef(fnDef: AST.FnDef, ctx: Context) extends VFun(fnDef.arity)

  def ctx: Context
}
