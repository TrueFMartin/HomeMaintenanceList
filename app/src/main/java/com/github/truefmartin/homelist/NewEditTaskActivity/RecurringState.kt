package com.github.truefmartin.homelist.NewEditTaskActivity

enum class RecurringState (int: Int) {
    ONCE(-1), DAILY(1), WEEKLY(7), MONTHLY(30), YEARLY(365)
}