package com.moneymanager.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        val DB_VERSION = 1
        val DB_NAME = "moneymanager.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQueries =
                TAccounts.createTableQuery +
                TCategories.createTableQuery +
                TTransactions.createTableQuery


        db?.execSQL(createTableQueries)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {



    }


    fun insert(tableName: String, cv: ContentValues) = writableDatabase.insert(tableName, null, cv)

    fun select(query: String, clauses: Array<String>?) = readableDatabase.rawQuery(query, clauses)


}
