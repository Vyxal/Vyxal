package vyxal

import java.time.{Duration as JDuration, ZonedDateTime, ZoneId, ZoneOffset}

import vyxal.conversions.given

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
        val expected = java.time.Instant.EPOCH
          .atZone(ZoneId.systemDefault())
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

end DateTimeTests
