package com.github.truefmartin.homelist.NewEditTaskActivity

import java.time.LocalDateTime

enum class RecurringState (int: Int) {
    NONE(-1) {
        override fun modifyDate(startDate: LocalDateTime): LocalDateTime {
            return startDate
        }
             },
    DAILY(1) {
        override fun modifyDate(startDate: LocalDateTime): LocalDateTime {
            return startDate.plusDays(1)
        }
    },
    WEEKLY(7) {
        override fun modifyDate(startDate: LocalDateTime): LocalDateTime {
            return startDate.plusWeeks(1)
        }
    }, MONTHLY(30) {
        override fun modifyDate(startDate: LocalDateTime): LocalDateTime {
            return startDate.plusMonths(1)
        }
    }, YEARLY(365) {
        override fun modifyDate(startDate: LocalDateTime): LocalDateTime {
            return startDate.plusYears(1)
        }
    };

    abstract fun modifyDate(startDate: LocalDateTime): LocalDateTime
}