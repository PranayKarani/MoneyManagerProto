package com.moneymanager.db

// Created by PranayKarani on 10-12-2016.
class TTransactions : AbstractTable(){

    companion object {
        val TABLE_NAME = "Transaction"
        val ID = "_ID"
        val AMOUNT = "trans_amount"
        val CATEGORY = "trans_cat"
        val ACCOUNT = "trans_acc"
        val INFO = "trans_info"
        val DATE_TIME = "trans_datetime"
        val EXCLUDE = "trans_ex"


        val createTableQuery = "" +
                "CREATE TABLE $TABLE_NAME (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$AMOUNT DOUBLE," +
                "$CATEGORY INTEGER," +
                "$ACCOUNT INTEGER," +
                "$INFO TEXT," +
                "$DATE_TIME DATETIME," +
                "$EXCLUDE INTEGER," +
                "FOREIGN KEY($CATEGORY) REFERENCES ${TCategories.TABLE_NAME}(${TCategories.ID})," +
                "FOREIGN KEY($ACCOUNT) REFERENCES ${TAccounts.TABLE_NAME}(${TAccounts.ID})" +
                ");"

    }
}
