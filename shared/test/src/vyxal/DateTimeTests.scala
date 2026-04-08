package vyxal

import vyxal.conversions.given

import java.time.{Duration as JDuration, ZoneId, ZoneOffset, ZonedDateTime}

class DateTimeTests extends VyxalTests:

  // Use a fixed zone for deterministic test results
  private val testZone = ZoneId.systemDefault()

  describe("VDate") {
    describe("construction") {
      it("should create a date from components") {
        val d = VDate.of(2024, 3, 15)
        assertResult(VNum(2024))(d.year)
        assertResult(VNum(3))(d.month)
        assertResult(VNum(15))(d.day)
        assertResult(VNum(0))(d.hour)
        assertResult(VNum(0))(d.minute)
        assertResult(VNum(0))(d.second)
      }

      it("should create a date with time components") {
        val d = VDate.of(2024, 3, 15, 10, 30, 45)
        assertResult(VNum(10))(d.hour)
        assertResult(VNum(30))(d.minute)
        assertResult(VNum(45))(d.second)
      }

      it("should parse an ISO-8601 date-time string") {
        val d = VDate.parse("2024-03-15T10:30:00")
        assertResult(VNum(2024))(d.year)
        assertResult(VNum(3))(d.month)
        assertResult(VNum(15))(d.day)
        assertResult(VNum(10))(d.hour)
        assertResult(VNum(30))(d.minute)
      }

      it("should parse a date-only string") {
        val d = VDate.parse("2024-03-15")
        assertResult(VNum(2024))(d.year)
        assertResult(VNum(3))(d.month)
        assertResult(VNum(15))(d.day)
        assertResult(VNum(0))(d.hour)
      }

      it("should parse an ISO-8601 string with zone offset") {
        val d = VDate.parse("2024-03-15T10:30:00+05:00")
        assertResult(VNum(2024))(d.year)
        assertResult(VNum(10))(d.hour)
        val z = d.zone.s
        assert(z.contains("+05:00") || z == "+05:00")
      }

      it("should parse an instant string") {
        val d = VDate.parse("1970-01-01T00:00:00Z")
        assertResult(VNum(1970))(d.year)
        assertResult(VNum(1))(d.month)
        assertResult(VNum(1))(d.day)
      }

      it("should create from epoch second in default zone") {
        val d = VDate.fromEpochSecond(0)
        // The exact date/time depends on the system zone, but the point
        // in time should be the Unix epoch
        val expected = java.time.Instant.EPOCH.atZone(ZoneId.systemDefault())
        assertResult(VNum(expected.getYear))(d.year)
        assertResult(VNum(expected.getMonthValue))(d.month)
        assertResult(VNum(expected.getDayOfMonth))(d.day)
      }

      it("should create from epoch second in a specific zone") {
        val d = VDate.fromEpochSecond(0, ZoneOffset.UTC)
        assertResult(VNum(1970))(d.year)
        assertResult(VNum(1))(d.month)
        assertResult(VNum(1))(d.day)
        assertResult(VNum(0))(d.hour)
      }
    }

    describe("zone") {
      it("should carry the system default zone when constructed via of()") {
        val d = VDate.of(2024, 1, 1)
        assertResult(VStr(ZoneId.systemDefault().getId))(d.zone)
      }
    }

    describe("toUnixTime") {
      it("should return epoch second for UTC epoch") {
        val d = VDate.fromEpochSecond(0, ZoneOffset.UTC)
        assertResult(VNum(0))(d.toUnixTime)
      }

      it("should return correct epoch second for a known date") {
        val d = VDate(
          ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        )
        assertResult(VNum(1704067200L))(d.toUnixTime)
      }
    }

    describe("calendar-aware operations") {
      it("should add months correctly across year boundary") {
        val d = VDate.of(2024, 11, 15).plusMonths(3)
        assertResult(VNum(2025))(d.year)
        assertResult(VNum(2))(d.month)
        assertResult(VNum(15))(d.day)
      }

      it("should add years") {
        val d = VDate.of(2024, 3, 15).plusYears(2)
        assertResult(VNum(2026))(d.year)
        assertResult(VNum(3))(d.month)
      }

      it("should add weeks") {
        val d = VDate.of(2024, 1, 1).plusWeeks(2)
        assertResult(VNum(1))(d.month)
        assertResult(VNum(15))(d.day)
      }
    }

    describe("toString") {
      it("should include timezone information") {
        val d = VDate.of(2024, 3, 15, 10, 30, 0)
        val s = d.toString
        // ZonedDateTime.toString includes zone info, e.g.
        // "2024-03-15T10:30+01:00[Europe/Paris]" or "2024-03-15T10:30Z"
        assert(s.contains("2024-03-15T10:30"))
      }
    }

    describe("comparison") {
      it("should compare dates") {
        val d1 = VDate.of(2024, 1, 1)
        val d2 = VDate.of(2024, 12, 31)
        assert(d1 < d2)
        assert(d2 > d1)
      }

      it("should compare equal dates") {
        given ctx: Context = VyxalTests.testContext()
        val d1 = VDate.of(2024, 3, 15)
        val d2 = VDate.of(2024, 3, 15)
        assert(d1 === d2)
      }

      it("should consider same instant in different zones as equal") {
        given ctx: Context = VyxalTests.testContext()
        // 2024-01-01T05:00 UTC  ==  2024-01-01T00:00 UTC-5
        val d1 = VDate(
          ZonedDateTime.of(2024, 1, 1, 5, 0, 0, 0, ZoneOffset.UTC)
        )
        val d2 = VDate(
          ZonedDateTime.of(
            2024,
            1,
            1,
            0,
            0,
            0,
            0,
            ZoneOffset.ofHours(-5),
          )
        )
        assert(d1 === d2)
      }
    }

    describe("toBool") {
      it("should always be true") {
        assert(VDate.of(2024, 1, 1).toBool)
      }
    }
  }

  describe("VDuration") {
    describe("construction") {
      it("should create durations from various units") {
        assertResult(VNum(1))(VDuration.ofDays(1).toDays)
        assertResult(VNum(2))(VDuration.ofHours(2).toHours)
        assertResult(VNum(30))(VDuration.ofMinutes(30).toMinutes)
        assertResult(VNum(60))(VDuration.ofSeconds(60).toSeconds)
      }

      it("should create duration in weeks") {
        assertResult(VNum(14))(VDuration.ofWeeks(2).toDays)
      }

      it("should parse ISO-8601 duration strings") {
        val d = VDuration.parse("PT2H30M")
        assertResult(VNum(150))(d.toMinutes)
      }
    }

    describe("toString") {
      it("should produce ISO-8601 duration format") {
        assertResult("PT2H30M")(VDuration.parse("PT2H30M").toString)
      }
    }

    describe("comparison") {
      it("should compare durations") {
        val d1 = VDuration.ofHours(1)
        val d2 = VDuration.ofHours(2)
        assert(d1 < d2)
      }
    }

    describe("toBool") {
      it("should be true for non-zero") {
        assert(VDuration.ofSeconds(1).toBool)
      }
      it("should be false for zero") {
        assert(!VDuration.Zero.toBool)
      }
    }
  }

  describe("Date arithmetic") {
    describe("add") {
      it("should add a duration to a date") {
        given ctx: Context = VyxalTests.testContext()
        val date = VDate.of(2024, 1, 1)
        val dur = VDuration.ofDays(1)
        val result = MiscHelpers.add(date, dur)
        assertResult(VDate.of(2024, 1, 2))(result)
      }

      it("should add a duration to a date (commutative)") {
        given ctx: Context = VyxalTests.testContext()
        val date = VDate.of(2024, 1, 1)
        val dur = VDuration.ofDays(1)
        val result = MiscHelpers.add(dur, date)
        assertResult(VDate.of(2024, 1, 2))(result)
      }

      it("should add two durations") {
        given ctx: Context = VyxalTests.testContext()
        val d1 = VDuration.ofHours(1)
        val d2 = VDuration.ofHours(2)
        val result = MiscHelpers.add(d1, d2)
        assertResult(VDuration.ofHours(3))(result)
      }

      it("should add a number of days to a date") {
        given ctx: Context = VyxalTests.testContext()
        val date = VDate.of(2024, 1, 1)
        val result = MiscHelpers.add(date, VNum(5))
        assertResult(VDate.of(2024, 1, 6))(result)
      }
    }

    describe("subtract") {
      it("should subtract a duration from a date") {
        given ctx: Context = VyxalTests.testContext()
        val date = VDate.of(2024, 1, 10)
        val dur = VDuration.ofDays(5)
        val result = MiscHelpers.subtract(date, dur)
        assertResult(VDate.of(2024, 1, 5))(result)
      }

      it("should compute duration between two dates") {
        given ctx: Context = VyxalTests.testContext()
        val d1 = VDate.of(2024, 1, 10)
        val d2 = VDate.of(2024, 1, 1)
        val result = MiscHelpers.subtract(d1, d2)
        assertResult(VDuration.ofDays(9))(result)
      }

      it("should subtract two durations") {
        given ctx: Context = VyxalTests.testContext()
        val d1 = VDuration.ofHours(5)
        val d2 = VDuration.ofHours(2)
        val result = MiscHelpers.subtract(d1, d2)
        assertResult(VDuration.ofHours(3))(result)
      }

      it("should subtract a number of days from a date") {
        given ctx: Context = VyxalTests.testContext()
        val date = VDate.of(2024, 1, 10)
        val result = MiscHelpers.subtract(date, VNum(3))
        assertResult(VDate.of(2024, 1, 7))(result)
      }
    }

    describe("multiply") {
      it("should scale a duration by a number") {
        given ctx: Context = VyxalTests.testContext()
        val dur = VDuration.ofHours(2)
        val result = MiscHelpers.multiply(dur, VNum(3))
        assertResult(VDuration.ofHours(6))(result)
      }

      it("should scale a duration by a number (commutative)") {
        given ctx: Context = VyxalTests.testContext()
        val dur = VDuration.ofHours(2)
        val result = MiscHelpers.multiply(VNum(3), dur)
        assertResult(VDuration.ofHours(6))(result)
      }
    }
  }

  describe("Date comparison via MiscHelpers") {
    it("should compare dates correctly") {
      given ctx: Context = VyxalTests.testContext()
      val d1 = VDate.of(2024, 1, 1)
      val d2 = VDate.of(2024, 12, 31)
      assert(MiscHelpers.compare(d1, d2) < 0)
      assert(MiscHelpers.compare(d2, d1) > 0)
      assert(MiscHelpers.compare(d1, d1) == 0)
    }

    it("should compare durations correctly") {
      given ctx: Context = VyxalTests.testContext()
      val d1 = VDuration.ofSeconds(10)
      val d2 = VDuration.ofSeconds(20)
      assert(MiscHelpers.compare(d1, d2) < 0)
      assert(MiscHelpers.compare(d2, d1) > 0)
      assert(MiscHelpers.compare(d1, d1) == 0)
    }
  }

  describe("typesOf") {
    it("should return 'date' for VDate") {
      assertResult(List("date"))(
        MiscHelpers.typesOf(VDate.of(2024, 1, 1))
      )
    }

    it("should return 'dur' for VDuration") {
      assertResult(List("dur"))(
        MiscHelpers.typesOf(VDuration.ofDays(1))
      )
    }
  }

  describe("vyToString") {
    it("should convert VDate to string") {
      given ctx: Context = VyxalTests.testContext()
      val d = VDate.of(2024, 3, 15, 10, 30, 0)
      val s = StringHelpers.vyToString(d)
      assert(s.contains("2024-03-15T10:30"))
    }

    it("should convert VDuration to string") {
      given ctx: Context = VyxalTests.testContext()
      val d = VDuration.ofHours(2)
      assertResult("PT2H")(StringHelpers.vyToString(d))
    }
  }

  describe("Element overloads") {
    val d1 = VDate.of(2024, 1, 15)
    val d2 = VDate.of(2024, 3, 20)
    val dur1h = VDuration.ofHours(1)
    val dur2h = VDuration.ofHours(2)
    val dur1d = VDuration.ofDays(1)

    describe("Comparison (<, >, =)") {
      it("< should compare dates") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(d1, d2))
        Interpreter.execute("<")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("> should compare durations") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur2h, dur1h))
        Interpreter.execute(">")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("= should compare equal dates") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(d1, d1))
        Interpreter.execute("=")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("= should compare unequal durations") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur1h, dur2h))
        Interpreter.execute("=")(using ctx)
        assertResult(VNum(0))(ctx.peek)
      }
    }

    describe("Division (÷)") {
      it("should divide duration by number") {
        given ctx: Context =
          VyxalTests.testContext(inputs = Seq(dur2h, VNum(2)))
        Interpreter.execute("÷")(using ctx)
        assertResult(dur1h)(ctx.peek)
      }
      it("should get ratio of two durations") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur2h, dur1h))
        Interpreter.execute("÷")(using ctx)
        assertResult(VNum(2.0))(ctx.peek)
      }
    }

    describe("Modulo (%)") {
      it("should format date with pattern") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 3, 15), VStr("yyyy-MM-dd"))
          )
        Interpreter.execute("%")(using ctx)
        assertResult(VStr("2024-03-15"))(ctx.peek)
      }
      it("should compute duration modulo") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDuration.ofHours(5), VDuration.ofHours(3))
          )
        Interpreter.execute("%")(using ctx)
        assertResult(VDuration.ofHours(2))(ctx.peek)
      }
    }

    describe("Negate (N)") {
      it("should negate a duration") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur1h))
        Interpreter.execute("N")(using ctx)
        val result = ctx.peek.asInstanceOf[VDuration]
        assertResult(VDuration.ofHours(-1))(result)
      }
    }

    describe("Double (d)") {
      it("should double a duration") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur1h))
        Interpreter.execute("d")(using ctx)
        assertResult(dur2h)(ctx.peek)
      }
    }

    describe("Increment/Decrement (›/‹)") {
      it("› should add 1 day to a date") {
        given ctx: Context =
          VyxalTests.testContext(inputs = Seq(VDate.of(2024, 1, 15)))
        Interpreter.execute("›")(using ctx)
        assertResult(VDate.of(2024, 1, 16))(ctx.peek)
      }
      it("‹ should subtract 1 day from a date") {
        given ctx: Context =
          VyxalTests.testContext(inputs = Seq(VDate.of(2024, 1, 15)))
        Interpreter.execute("‹")(using ctx)
        assertResult(VDate.of(2024, 1, 14))(ctx.peek)
      }
      it("› should add 1 day to a duration") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(dur1h))
        Interpreter.execute("›")(using ctx)
        assertResult(VDuration(dur1h.dur.plus(JDuration.ofDays(1))))(ctx.peek)
      }
    }

    describe("Floor (⌊) - Date to Unix") {
      it("should convert date to unix timestamp") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.fromEpochSecond(1000))
          )
        Interpreter.execute("⌊")(using ctx)
        assertResult(VNum(1000))(ctx.peek)
      }
    }

    describe("Ceil (⌈) - Date Components") {
      it("should extract date components") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 3, 15, 10, 30, 45))
          )
        Interpreter.execute("⌈")(using ctx)
        assertResult(
          VList(
            Seq(VNum(2024), VNum(3), VNum(15), VNum(10), VNum(30), VNum(45))
          )
        )(ctx.peek)
      }
    }

    describe("Make Date (#t) - Date from Components") {
      it("should create date from [year] — Jan 1 midnight") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VNum(2024))))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(1))(result.month)
        assertResult(VNum(1))(result.day)
        assertResult(VNum(0))(result.hour)
        assertResult(VNum(0))(result.minute)
        assertResult(VNum(0))(result.second)
      }
      it("should create date from [year, month]") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VNum(2024), VNum(5))))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(5))(result.month)
        assertResult(VNum(1))(result.day)
        assertResult(VNum(0))(result.hour)
      }
      it("should create date from [year, month, day]") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VNum(2024), VNum(7), VNum(4))))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(7))(result.month)
        assertResult(VNum(4))(result.day)
      }
      it("should create date from [year, month, day, hour, minute, second]") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(
              VList(
                Seq(
                  VNum(2024),
                  VNum(12),
                  VNum(25),
                  VNum(10),
                  VNum(30),
                  VNum(45),
                )
              )
            )
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(12))(result.month)
        assertResult(VNum(25))(result.day)
        assertResult(VNum(10))(result.hour)
        assertResult(VNum(30))(result.minute)
        assertResult(VNum(45))(result.second)
      }
      it("should parse date from string") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VStr("2024-03-15"))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(3))(result.month)
        assertResult(VNum(15))(result.day)
      }
      it("should create date from epoch seconds") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VNum(0))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(0))(result.toUnixTime)
      }
      it("should round-trip with ⌈ date decomposition") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 3, 15, 10, 30, 45))
          )
        Interpreter.execute("⌈#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(3))(result.month)
        assertResult(VNum(15))(result.day)
        assertResult(VNum(10))(result.hour)
        assertResult(VNum(30))(result.minute)
        assertResult(VNum(45))(result.second)
      }
    }

    describe("Make Duration (#U)") {
      it("should parse an ISO-8601 duration string") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VStr("PT2H30M"))
          )
        Interpreter.execute("#U")(using ctx)
        val result = ctx.peek.asInstanceOf[VDuration]
        // 2 hours 30 minutes = 9000 seconds
        assertResult(VNum(9000L))(result.toSeconds)
      }
      it("should parse a day duration string") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VStr("P2D"))
          )
        Interpreter.execute("#U")(using ctx)
        val result = ctx.peek.asInstanceOf[VDuration]
        // 2 days = 172800 seconds
        assertResult(VNum(172800L))(result.toSeconds)
      }
      it("should create a duration from a numeric value") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VNum(0.5))
          )
        Interpreter.execute("#U")(using ctx)
        val result = ctx.peek.asInstanceOf[VDuration]
        // 0.5 days = 12 hours = 43200000 millis
        assertResult(VNum(43200000L))(result.toMillis)
      }
      it("should create zero duration from 0") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VNum(0))
          )
        Interpreter.execute("#U")(using ctx)
        val result = ctx.peek.asInstanceOf[VDuration]
        assertResult(VNum(0L))(result.toMillis)
      }
    }

    describe("E - Date Components") {
      it("should extract date components") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 12, 25, 8, 0, 0))
          )
        Interpreter.execute("E")(using ctx)
        assertResult(
          VList(Seq(VNum(2024), VNum(12), VNum(25), VNum(8), VNum(0), VNum(0)))
        )(ctx.peek)
      }
    }

    describe("Range (R) - Date Range") {
      it("should create exclusive date range") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 1, 1), VDate.of(2024, 1, 4))
          )
        Interpreter.execute("R")(using ctx)
        assertResult(
          VList(
            Seq(
              VDate.of(2024, 1, 1),
              VDate.of(2024, 1, 2),
              VDate.of(2024, 1, 3),
            )
          )
        )(ctx.peek)
      }
    }

    describe("Inclusive Range (↯) - Date Range") {
      it("should create inclusive date range") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 1, 1), VDate.of(2024, 1, 3))
          )
        Interpreter.execute("↯")(using ctx)
        assertResult(
          VList(
            Seq(
              VDate.of(2024, 1, 1),
              VDate.of(2024, 1, 2),
              VDate.of(2024, 1, 3),
            )
          )
        )(ctx.peek)
      }
    }

    describe("Triple (T) - Date Triple") {
      it("should extract year, month, day") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 7, 4))
          )
        Interpreter.execute("T")(using ctx)
        assertResult(
          VList(Seq(VNum(2024), VNum(7), VNum(4)))
        )(ctx.peek)
      }
    }

    describe("Absolute Difference (@)") {
      it("should compute absolute date difference") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 1, 1), VDate.of(2024, 1, 4))
          )
        Interpreter.execute("@")(using ctx)
        assertResult(VDuration.ofDays(3))(ctx.peek)
      }
      it("should compute absolute difference in reverse order") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 1, 4), VDate.of(2024, 1, 1))
          )
        Interpreter.execute("@")(using ctx)
        assertResult(VDuration.ofDays(3))(ctx.peek)
      }
    }

    describe("Length (L) - Duration Seconds") {
      it("should return total seconds of duration") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDuration.ofHours(2))
          )
        Interpreter.execute("L")(using ctx)
        assertResult(VNum(7200))(ctx.peek)
      }
    }

    describe("Is Leap Year (e)") {
      it("should return 1 for leap year") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2024, 6, 15))
          )
        Interpreter.execute("e")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("should return 0 for non-leap year") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2023, 6, 15))
          )
        Interpreter.execute("e")(using ctx)
        assertResult(VNum(0))(ctx.peek)
      }
      it("should handle century leap year rules") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(2000, 1, 1))
          )
        Interpreter.execute("e")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("should handle century non-leap year") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VDate.of(1900, 1, 1))
          )
        Interpreter.execute("e")(using ctx)
        assertResult(VNum(0))(ctx.peek)
      }
    }

    describe("Untruth/Parse Date (Ṫ)") {
      it("should parse date from string") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VStr("2024-03-15"))
          )
        Interpreter.execute("Ṫ")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(3))(result.month)
        assertResult(VNum(15))(result.day)
      }
      it("should create date from epoch seconds") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VNum(0))
          )
        Interpreter.execute("Ṫ")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(0))(result.toUnixTime)
      }
      it("should still do untruth for lists") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VNum(0), VNum(2), VNum(4))))
          )
        Interpreter.execute("Ṫ")(using ctx)
        assertResult(
          VList(Seq(VNum(1), VNum(0), VNum(1), VNum(0), VNum(1)))
        )(ctx.peek)
      }
    }

    describe("Date Between (⎀)") {
      it("should return 1 when date is between") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(
              VDate.of(2024, 6, 15),
              VDate.of(2024, 1, 1),
              VDate.of(2024, 12, 31),
            )
          )
        Interpreter.execute("⎀")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
      it("should return 0 when date is not between") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(
              VDate.of(2025, 1, 1),
              VDate.of(2024, 1, 1),
              VDate.of(2024, 12, 31),
            )
          )
        Interpreter.execute("⎀")(using ctx)
        assertResult(VNum(0))(ctx.peek)
      }
      it("should return 1 when date equals boundary") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(
              VDate.of(2024, 1, 1),
              VDate.of(2024, 1, 1),
              VDate.of(2024, 12, 31),
            )
          )
        Interpreter.execute("⎀")(using ctx)
        assertResult(VNum(1))(ctx.peek)
      }
    }

    describe("Nilad digraphs") {
      it("#n should return current date/time") {
        given ctx: Context = VyxalTests.testContext()
        Interpreter.execute("#n")(using ctx)
        assert(ctx.peek.isInstanceOf[VDate])
      }
      it("#d should return today at midnight") {
        given ctx: Context = VyxalTests.testContext()
        Interpreter.execute("#d")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(0))(result.hour)
        assertResult(VNum(0))(result.minute)
        assertResult(VNum(0))(result.second)
      }
      it("#m should return start of month") {
        given ctx: Context = VyxalTests.testContext()
        Interpreter.execute("#m")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(1))(result.day)
        assertResult(VNum(0))(result.hour)
      }
      it("#y should return start of year") {
        given ctx: Context = VyxalTests.testContext()
        Interpreter.execute("#y")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(1))(result.month)
        assertResult(VNum(1))(result.day)
        assertResult(VNum(0))(result.hour)
      }
      it("#z should return a sorted list of all timezone IDs") {
        given ctx: Context = VyxalTests.testContext()
        Interpreter.execute("#z")(using ctx)
        val result = ctx.peek.asInstanceOf[VList]
        // Should contain well-known zones
        val strs = result.lst.collect { case VStr(s) => s }
        assert(strs.contains("UTC"), "Should contain UTC")
        assert(
          strs.contains("America/New_York"),
          "Should contain America/New_York",
        )
        assert(strs.contains("Europe/London"), "Should contain Europe/London")
        // Should be sorted
        assert(strs == strs.sorted, "Zone IDs should be sorted")
        // All elements should be VStr
        assert(
          result.lst.forall(_.isInstanceOf[VStr]),
          "All elements should be VStr",
        )
      }
    }

    describe("Timezone Conversion (⊢ with VDate)") {
      it("should convert a date to UTC timezone") {
        val date = VDate(
          ZonedDateTime
            .of(2024, 3, 15, 10, 30, 0, 0, ZoneId.of("America/New_York"))
        )
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(date, VStr("UTC"))
          )
        Interpreter.execute("⊢")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        // 10:30 EST = 14:30 UTC (EST is UTC-5 in March, but DST so EDT = UTC-4)
        assertResult(VNum(14))(result.hour)
        assertResult(VNum(30))(result.minute)
        assertResult(VStr("UTC"))(result.zone)
      }
      it("should preserve the same instant") {
        val date = VDate(
          ZonedDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneId.of("UTC"))
        )
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(date, VStr("Asia/Tokyo"))
          )
        Interpreter.execute("⊢")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        // Same instant, different zone
        assertResult(date.toUnixTime)(result.toUnixTime)
        assertResult(VStr("Asia/Tokyo"))(result.zone)
        // UTC 12:00 = Tokyo 21:00 (UTC+9)
        assertResult(VNum(21))(result.hour)
      }
    }

    describe("Set Timezone Same Local (⊣ with VDate)") {
      it("should change timezone without adjusting time") {
        val date = VDate(
          ZonedDateTime
            .of(2025, 1, 31, 1, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        )
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(date, VStr("UTC"))
          )
        Interpreter.execute("⊣")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        // Same local time, different zone
        assertResult(VNum(1))(result.hour)
        assertResult(VNum(0))(result.minute)
        assertResult(VStr("UTC"))(result.zone)
        assertResult(VNum(2025))(result.year)
        assertResult(VNum(1))(result.month)
        assertResult(VNum(31))(result.day)
      }
      it("should differ from ⊢ which adjusts the time") {
        val date = VDate(
          ZonedDateTime
            .of(2024, 6, 1, 12, 0, 0, 0, ZoneId.of("UTC"))
        )
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(date, VStr("Asia/Tokyo"))
          )
        Interpreter.execute("⊣")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        // Same local time, just relabeled
        assertResult(VNum(12))(result.hour)
        assertResult(VStr("Asia/Tokyo"))(result.zone)
      }
    }

    describe("Set Default Timezone (#Z)") {
      it("should set the default timezone") {
        given ctx: Context = VyxalTests.testContext(inputs = Seq(VStr("UTC")))
        Interpreter.execute("#Z")(using ctx)
        assert(ctx.isStackEmpty)
      }
    }

    describe("Flexible Date Parsing (#t)") {
      it("should parse time-only string") {
        val d = VDate.parse("16:45")
        assertResult(VNum(16))(d.hour)
        assertResult(VNum(45))(d.minute)
      }
      it("should parse time-only with seconds") {
        val d = VDate.parse("16:45:30")
        assertResult(VNum(16))(d.hour)
        assertResult(VNum(45))(d.minute)
        assertResult(VNum(30))(d.second)
      }
      it("should parse time with UTC timezone") {
        val d = VDate.parse("16:45 UTC")
        assertResult(VNum(16))(d.hour)
        assertResult(VNum(45))(d.minute)
        assertResult(VStr("UTC"))(d.zone)
      }
      it("should parse time with named timezone") {
        val d = VDate.parse("10:30 America/New_York")
        assertResult(VNum(10))(d.hour)
        assertResult(VNum(30))(d.minute)
        assertResult(VStr("America/New_York"))(d.zone)
      }
      it("should parse list [time, timezone] via #t") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VStr("16:45"), VStr("UTC"))))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(16))(result.hour)
        assertResult(VNum(45))(result.minute)
        assertResult(VStr("UTC"))(result.zone)
      }
      it("should still parse numeric component lists via #t") {
        given ctx: Context =
          VyxalTests.testContext(
            inputs = Seq(VList(Seq(VNum(2024), VNum(3), VNum(15))))
          )
        Interpreter.execute("#t")(using ctx)
        val result = ctx.peek.asInstanceOf[VDate]
        assertResult(VNum(2024))(result.year)
        assertResult(VNum(3))(result.month)
        assertResult(VNum(15))(result.day)
      }
      it("should parse M/d/yyyy format") {
        val d = VDate.parse("3/15/2024")
        assertResult(VNum(2024))(d.year)
        assertResult(VNum(3))(d.month)
        assertResult(VNum(15))(d.day)
      }
    }
  }

end DateTimeTests
