package com.mancode.financetracker.ui.transactions;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.pojos.TransactionFull;

import org.threeten.bp.LocalDate;

public class FilterQuery {

    private static final int TOKENS_COUNT = 3;

    private static final int TOKEN_TYPE = 0;
    private static final int TOKEN_FROM_DATE = 1;
    private static final int TOKEN_TO_DATE = 2;

    static final int TYPE_ALL = 0;
    static final int TYPE_INCOME = 1;
    static final int TYPE_OUTCOME = -1;

    private String mQuery;
    private String[] mTokens;
    private static final String SEPARATOR = ",";

    FilterQuery(String query) {
        this.mQuery = query;
        tokenize();
    }

    FilterQuery(Integer type, LocalDate from, LocalDate to) {
        mQuery = String.valueOf(type) +
                SEPARATOR +
                DateConverter.INSTANCE.toString(from) +
                SEPARATOR +
                DateConverter.INSTANCE.toString(to);
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

    LocalDate getFromDate() {
        return DateConverter.INSTANCE.toDate(mTokens[TOKEN_FROM_DATE]);
    }

    LocalDate getToDate() {
        return DateConverter.INSTANCE.toDate(mTokens[TOKEN_TO_DATE]);
    }

    boolean isMatch(TransactionFull transaction) {
        int type = getType();
        LocalDate fromDate = getFromDate();
        LocalDate toDate = getToDate();

        return (type == TYPE_ALL || type == transaction.transaction.type) &&
                (fromDate == null || !transaction.transaction.date.isBefore(fromDate)) &&
                (toDate == null || !transaction.transaction.date.isAfter(toDate));
    }
}
