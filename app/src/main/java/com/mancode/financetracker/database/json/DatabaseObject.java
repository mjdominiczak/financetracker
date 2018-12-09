package com.mancode.financetracker.database.json;

import com.google.gson.annotations.SerializedName;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.CurrencyEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;

public class DatabaseObject {

    public int version;
    @SerializedName("currencies")
    public List<CurrencyEntity> currencyList;
    @SerializedName("categories")
    public List<CategoryEntity> categoryList;
    @SerializedName("accounts")
    public List<AccountEntity> accountList;
    @SerializedName("balances")
    public List<BalanceEntity> balanceList;
    @SerializedName("transactions")
    public List<TransactionEntity> transactionList;

    public DatabaseObject(int version,
                          List<CurrencyEntity> currencyList,
                          List<CategoryEntity> categoryList,
                          List<AccountEntity> accountList,
                          List<BalanceEntity> balanceList,
                          List<TransactionEntity> transactionList) {
        this.version = version;
        this.currencyList = currencyList;
        this.categoryList = categoryList;
        this.accountList = accountList;
        this.balanceList = balanceList;
        this.transactionList = transactionList;
    }
}
