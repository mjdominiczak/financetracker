package com.mancode.financetracker.ui.transactions

import com.mancode.financetracker.database.converter.DateConverter.toDate
import com.mancode.financetracker.database.converter.DateConverter.toString
import com.mancode.financetracker.database.pojos.TransactionFull
import org.threeten.bp.LocalDate

class FilterQuery {
    var query: String
        private set
    private lateinit var mTokens: Array<String>

    internal constructor(query: String) {
        this.query = query
        tokenize()
    }

    internal constructor(type: Int, from: LocalDate?, to: LocalDate?, timespan: Int, bookmark: Boolean) {
        query = type.toString() +
                SEPARATOR +
                toString(from) +
                SEPARATOR +
                toString(to) +
                SEPARATOR +
                timespan +
                SEPARATOR +
                bookmark
        tokenize()
    }

    private fun tokenize() {
        mTokens = query.split(SEPARATOR).toTypedArray()
    }

    val type: Int
        get() {
            val token = mTokens[TOKEN_TYPE]
            return if (token.isNotEmpty()) token.toInt() else TYPE_ALL
        }

    private val fromDate: LocalDate?
        get() = toDate(mTokens[TOKEN_FROM_DATE])

    private val toDate: LocalDate?
        get() = toDate(mTokens[TOKEN_TO_DATE])

    val timespan: Int
        get() {
            val token = mTokens[TOKEN_TIMESPAN]
            return if (token.isNotEmpty()) token.toInt() else UNCONSTRAINED
        }

    fun bookmarked(): Boolean {
        val token = mTokens[TOKEN_BOOKMARK]
        return token.isNotEmpty() && java.lang.Boolean.parseBoolean(token)
    }

    fun isMatch(transaction: TransactionFull): Boolean {
        val type = type
        val fromDate = fromDate
        val toDate = toDate
        return (type == TYPE_ALL || type == transaction.transaction.type) &&
                (fromDate == null || !transaction.transaction.date.isBefore(fromDate)) &&
                (toDate == null || !transaction.transaction.date.isAfter(toDate)) &&
                (!bookmarked() || transaction.transaction.flags == 1)
    }

    companion object {
        private const val TOKENS_COUNT = 5
        private const val TOKEN_TYPE = 0
        private const val TOKEN_FROM_DATE = 1
        private const val TOKEN_TO_DATE = 2
        private const val TOKEN_TIMESPAN = 3
        private const val TOKEN_BOOKMARK = 4

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

        val emptyQuery: String
            get() {
                val sb = StringBuilder()
                for (i in 0 until TOKENS_COUNT - 1) {
                    sb.append(SEPARATOR)
                }
                return sb.toString()
            }
    }
}