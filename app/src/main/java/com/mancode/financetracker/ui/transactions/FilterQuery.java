package com.mancode.financetracker.ui.transactions;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.pojos.TransactionFull;

import org.threeten.bp.LocalDate;

public class FilterQuery {

    private static final int TOKENS_COUNT = 5;

    private static final int TOKEN_TYPE = 0;
    private static final int TOKEN_FROM_DATE = 1;
    private static final int TOKEN_TO_DATE = 2;
    private static final int TOKEN_TIMESPAN = 3;
    private static final int TOKEN_BOOKMARK = 4;

    static final int TYPE_ALL = 0;
    static final int TYPE_INCOME = 1;
    static final int TYPE_OUTCOME = -1;

    static final int UNCONSTRAINED = 0;
    static final int LAST_WEEK = 1;
    static final int LAST_MONTH = 2;
    static final int THIS_MONTH = 3;
    static final int PREVIOUS_MONTH = 4;
    static final int THIS_YEAR = 5;
    static final int CUSTOM = 6;

    private String mQuery;
    private String[] mTokens;
    private static final String SEPARATOR = ",";

    FilterQuery(String query) {
        this.mQuery = query;
        tokenize();
    }

    FilterQuery(Integer type, LocalDate from, LocalDate to, Integer timespan, boolean bookmark) {
        mQuery = type +
                SEPARATOR +
                DateConverter.toString(from) +
                SEPARATOR +
                DateConverter.toString(to) +
                SEPARATOR +
                timespan +
                SEPARATOR +
                bookmark;
        tokenize();
    }

    static String getEmptyQuery() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKENS_COUNT - 1; i++) {
            sb.append(SEPARATOR);
        }
        return sb.toString();
    }

    private void tokenize() {
        mTokens = mQuery.split(SEPARATOR, -1);
    }

    public String getQuery() {
        return mQuery;
    }

    public int getType() {
        String token = mTokens[TOKEN_TYPE];
        return !token.isEmpty() ? Integer.parseInt(token) : TYPE_ALL;
    }

    private LocalDate getFromDate() {
        return DateConverter.toDate(mTokens[TOKEN_FROM_DATE]);
    }

    private LocalDate getToDate() {
        return DateConverter.toDate(mTokens[TOKEN_TO_DATE]);
    }

    public int getTimespan() {
        String token = mTokens[TOKEN_TIMESPAN];
        return !token.isEmpty() ? Integer.parseInt(token) : UNCONSTRAINED;
    }

    public boolean bookmarked() {
        String token = mTokens[TOKEN_BOOKMARK];
        return !token.isEmpty() && Boolean.parseBoolean(token);
    }

    boolean isMatch(TransactionFull transaction) {
        int type = getType();
        LocalDate fromDate = getFromDate();
        LocalDate toDate = getToDate();

        return (type == TYPE_ALL || type == transaction.getTransaction().getType()) &&
                (fromDate == null || !transaction.getTransaction().getDate().isBefore(fromDate)) &&
                (toDate == null || !transaction.getTransaction().getDate().isAfter(toDate)) &&
                (!bookmarked() || transaction.getTransaction().getFlags() == 1);
    }
}
