package vyxal

type Overload = (Tuple, String)

case class Element(
    symbol: String,
    arity: Option[Int],
    desc: String,
    overloads: List[Overload],
    impl: () => Context ?=> Unit
)

object Elements {
  private object Impls {
    val elements = collection.mutable.Map.empty[String, Element]

    def addNilad(name: String, desc: String, overloads: List[Overload])(
        impl: Context ?=> VAny
    ): Unit = {
      elements += name -> Element(
        name,
        Some(0),
        desc,
        overloads,
        () => ctx ?=> ctx.push(impl(using ctx))
      )
    }

    def addMonad(name: String, desc: String, overloads: List[Overload], vectorises: Boolean = false)(
        impl: Monad
    ): Monad = {
      elements += name -> Element(
        name,
        Some(1),
        desc,
        overloads,
        { () => ctx ?=>
          ctx.push(impl(ctx.pop()))
        }
      )
      impl
    }

    def addMonadVect(name: String, desc: String, overloads: List[Overload])(
        impl: SimpleMonad
    ) =
      addMonad(name, desc, overloads, vectorises = true)(vect1(impl))

    def addDyad(name: String, desc: String, overloads: List[Overload], vectorises: Boolean = false)(
        impl: Dyad
    ): Dyad = {
      elements += name -> Element(
        name,
        Some(2),
        desc,
        overloads,
        { () => ctx ?=>
          val arg2, arg1 = ctx.pop()
          ctx.push(impl(arg1, arg2))
        }
      )
      impl
    }

    def addDyadVect(name: String, desc: String, overloads: List[Overload])(
        impl: SimpleDyad
    ) = addDyad(name, desc, overloads, vectorises = true)(vect2(impl))

    def addTriad(name: String, desc: String, overloads: List[Overload], vectorises: Boolean = false)(
        impl: Triad
    ): Triad = {
      elements += name -> Element(
        name,
        Some(3),
        desc,
        overloads,
        { () => ctx ?=>
          val arg3, arg2, arg1 = ctx.pop()
          ctx.push(
            impl(arg1, arg2, arg3)
          )
        }
      )
      impl
    }

    def addTriadVect(name: String, desc: String, overloads: List[Overload])(
        impl: SimpleTriad
    ) =
      addTriad(name, desc, overloads, vectorises = true)(vect3(impl))

    def addTetrad(name: String, desc: String, overloads: List[Overload], vectorises: Boolean = false)(
        impl: Tetrad
    ): Tetrad = {
      elements += name -> Element(
        name,
        Some(4),
        desc,
        overloads,
        { () => ctx ?=>
          val arg4, arg3, arg2, arg1 = ctx.pop()
          ctx.push(
            impl(arg1, arg2, arg3, arg4)
          )
        }
      )
      impl
    }

    /** Add an element that works directly on the entire stack */
    def addDirect(name: String, desc: String, overloads: List[Overload])(
        impl: Context ?=> Unit
    ): Unit =
      elements += name -> Element(name, None, desc, overloads, () => impl)


    val add = addDyadVect(
      "+",
      "Add stuff",
      List(
        ("num", "num") -> "num",
        ("str", "str") -> "str"
      )
    ) {
      case (a: VNum, b: VNum) => a + b
    }
  }
}
