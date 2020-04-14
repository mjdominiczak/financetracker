package com.mancode.financetracker.ui.transactions

import com.mancode.financetracker.database.converter.DateConverter.toDate
import com.mancode.financetracker.database.converter.DateConverter.toString
import com.mancode.financetracker.database.pojos.TransactionFull
import org.threeten.bp.LocalDate
import java.util.Locale.ROOT

class FilterQuery {
    init {
        updateQuery()
    }

    // query might be removed
    var query: String = ""
    var description: String = ""
        private set(value) {
            field = value.trim().replace(SEPARATOR, "")
        }
    var type: Int = TYPE_ALL
        private set
    var fromDate: LocalDate? = null
        private set
    var toDate: LocalDate? = null
        private set
    var timespan: Int = UNCONSTRAINED
        private set
    var bookmarked: Boolean = false
        private set

    fun update(
            description: String? = null,
            type: Int? = null,
            from: LocalDate? = null,
            to: LocalDate? = null,
            timespan: Int? = null,
            bookmark: Boolean? = null
    ) {
        if (description != null) this.description = description
        if (type != null) this.type = type
        if (from != null) this.fromDate = from
        if (to != null) this.toDate = to
        if (timespan != null) this.timespan = timespan
        if (bookmark != null) this.bookmarked = bookmark
        updateQuery()
    }

    fun resetExceptDescription() {
        update(
                type = TYPE_ALL,
                timespan = UNCONSTRAINED,
                bookmark = false
        )
    }

    private fun updateQuery() {
        query = description +
                SEPARATOR +
                type +
                SEPARATOR +
                toString(fromDate) +
                SEPARATOR +
                toString(toDate) +
                SEPARATOR +
                timespan +
                SEPARATOR +
                bookmarked
    }

    private fun updateFields() {
        val tokens = query.split(SEPARATOR)
        description = tokens[0]
        type = if (tokens[1].isNotEmpty()) tokens[1].toInt() else TYPE_ALL
        fromDate = toDate(tokens[2])
        toDate = toDate(tokens[3])
        timespan = if (tokens[4].isNotEmpty()) tokens[4].toInt() else UNCONSTRAINED
        bookmarked = tokens[5].isNotEmpty() && tokens[5].toBoolean()
    }

    fun isMatch(transaction: TransactionFull): Boolean {
        return (description.isEmpty() || transaction.transaction.description
                .toLowerCase(ROOT).contains(description.toLowerCase(ROOT))) &&
                (type == TYPE_ALL || type == transaction.transaction.type) &&
                (fromDate == null || !transaction.transaction.date.isBefore(fromDate)) &&
                (toDate == null || !transaction.transaction.date.isAfter(toDate)) &&
                (!bookmarked || transaction.transaction.flags == 1)
    }

    companion object {
        const val TYPE_ALL = 0
        const val TYPE_INCOME = 1
        const val TYPE_OUTCOME = -1

        const val UNCONSTRAINED = 0
        const val LAST_WEEK = 1
        const val LAST_MONTH = 2
        const val THIS_MONTH = 3
        const val PREVIOUS_MONTH = 4
        const val THIS_YEAR = 5
        const val CUSTOM = 6

        private const val SEPARATOR = ","
    }
}