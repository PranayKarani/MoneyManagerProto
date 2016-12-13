package com.moneymanager.db

// Created by PranayKarani on 10-12-2016.
class TCategories : AbstractTable() {

    companion object {
        val TABLE_NAME = "Category"
        val ID = "_ID"
        val NAME = "cat_name"
        val TYPE = "cat_type" // 0- income, 1- expense
        val EXCLUDE = "cat_ex"

        val q_CREATE_TABLE =
                "CREATE TABLE $TABLE_NAME ( " +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$NAME TEXT," +
                        "$TYPE INTEGER," +
                        "$EXCLUDE INTEGER" +
                        ");"

    }

}
