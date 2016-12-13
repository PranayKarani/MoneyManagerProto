package com.moneymanager.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * All queries to executed on the database should be lauched through this class only.
 * Any operation on database should be COMPULSORLY done through this class
 */

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        val DB_VERSION = 1
        val DB_NAME = "moneymanager.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQueries =
                TAccounts.q_CREATE_TABLE +
                        TCategories.q_CREATE_TABLE +
                        TTransactions.q_CREATE_TABLE


        db?.execSQL(createTableQueries)

    }

    /**
     * Use this method to change the database schemas i.e. add new table, drop tables
     *
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {


    }

    /**
     * Provide Table and map of content values to be inserted.
     * Return -1 if failed or ID of the newly inserted row
     */
    fun insert(tableName: String, cv: ContentValues): Long = writableDatabase.insert(tableName, null, cv)

    /**
     * Provide query string and clauses array to replace ?s in where clause.
     * Cursor will be returned
     * e.g.
     * select("SELECT * FROM student WHERE ID = ?", arrayOf("1"))
     */
    fun select(query: String, clauses: Array<String>?): Cursor = readableDatabase.rawQuery(query, clauses)


    /**
     * Provide name of table to be updated, content values, where string and array of clauses
     * e.g.
     *
     * update("Student", {roll->'1'}, "WHERE name = ?", {"Pranay"})
     *
     */

    fun update(tableName: String, cv: ContentValues, where: String?, clauses: Array<String>?) =
            writableDatabase.update(tableName, cv, where, clauses)


    /**
     * Do not use this to INSERT, UPDATE, DELETE rows
     * Use for ALTER statements
     */
    fun execute(query: String) {
        writableDatabase.execSQL(query)
    }

    // TODO add delete method
    fun delete(table: String, where: String, clauses: Array<String>) {
        writableDatabase.delete(table, where, clauses)
    }

}
