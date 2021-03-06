package com.baseio.kmm.calendar

import kotlinx.datetime.*

class JetMonth private constructor(
  val startDate: LocalDate,
  val endDate: LocalDate,
  var firstDayOfWeek: DayOfWeek,
) : JetCalendarType() {
  lateinit var monthWeeks: List<JetWeek>

  fun name(): String {
    return startDate.month.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
  }

  fun monthYear(): String {
    return name() + " " + year()
  }

  fun year(): String {
    return startDate.year.toString()
  }

  companion object {
    fun current(
      date: LocalDate,
      firstDayOfWeek: DayOfWeek
    ): JetMonth {
      val startOfMonth = date.firstDayOfMonth()
      val endOfMonth = date.lastDayOfMonth()
      val month = JetMonth(startOfMonth, endOfMonth, firstDayOfWeek = firstDayOfWeek)
      month.monthWeeks = month.weeks(firstDayOfWeek)
      return month
    }
  }

  private fun weeks(firstDayOfWeek: DayOfWeek): List<JetWeek> {
    var date = startDate
    val monthWeeks = mutableListOf<JetWeek>()

    while (true) {
      val firstDateOfWeek = date.dateOfCurrentWeek(firstDayOfWeek, monthWeeks.size == 0)
      date = firstDateOfWeek.plus(DatePeriod(days = 6))
      if (endDate.monthNumber != date.monthNumber) {
        monthWeeks.add(
          JetWeek.current(
            firstDateOfWeek,
            dayOfWeek = this.firstDayOfWeek,
            this.startDate.monthNumber
          )
        )
        break
      } else {
        monthWeeks.add(
          JetWeek.current(
            firstDateOfWeek,
            dayOfWeek = this.firstDayOfWeek,
            this.startDate.monthNumber
          )
        )
      }
      date = date.plus(DatePeriod(days = 1))
    }
    return monthWeeks
  }

}

fun JetMonth.nextMonth(): JetMonth {
  return JetMonth.current(this.endDate.plus(DatePeriod(days = 1)), this.firstDayOfWeek)
}

private fun LocalDate.lastDayOfMonth(): LocalDate {
  // first day of next month minus 1 day is last day of month :P
  val currentMonth = this.month.number
  return if (currentMonth == 12) {
    LocalDate(this.year.plus(1), 1, 1).minus(DatePeriod(days = 1))
  } else {
    LocalDate(this.year, this.month.number + 1, 1).minus(DatePeriod(days = 1))
  }

}

private fun LocalDate.firstDayOfMonth(): LocalDate {
  return LocalDate(this.year, this.month, 1)
}
