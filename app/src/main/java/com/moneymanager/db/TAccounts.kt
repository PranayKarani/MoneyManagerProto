package com.moneymanager.db

import android.content.ContentValues
import android.content.Context
import com.moneymanager.entities.Account
import com.moneymanager.exceptions.NoAccountsException

// Created by PranayKarani on 10-12-2016.
class TAccounts(context: Context) : AbstractTable() {

    companion object {
        val TABLE_NAME = "Account"
        val ID = "_ID"
        val NAME = "acc_name"
        val BALANCE = "acc_bal"
        val EXCLUDE = "acc_ex"

        val q_CREATE_TABLE =
                "CREATE TABLE $TABLE_NAME ( " +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$NAME TEXT," +
                        "$BALANCE DOUBLE," +
                        "$EXCLUDE INTEGER" +
                        ");"

        fun q_SELECT_ALL_ACCOUNTS(column: String?, order: String?): String {
            return "SELECT * FROM $TABLE_NAME ORDER BY ${column ?: NAME} ${order ?: "ASC"};"
        }

    }

    val dbHelper = DBHelper(context)

    fun insertNewAccount(acc: Account): Long {

        val cv = ContentValues()
        cv.put(NAME, acc.name)
        cv.put(BALANCE, acc.bal)
        cv.put(EXCLUDE, acc.exclude)

        return dbHelper.insert(TABLE_NAME, cv)

    }

    fun getAllAccounts(column: String?, order: String?): Array<Account?> {

        val c = dbHelper.select(q_SELECT_ALL_ACCOUNTS(column, order), null)

        if (c.count == 0) throw NoAccountsException()

        val accounts = kotlin.arrayOfNulls<Account>(c.count)

        while (c.moveToNext()) {

            val id = c.getInt(c.getColumnIndex(ID))
            val name = c.getString(c.getColumnIndex(NAME))
            val balance = c.getDouble(c.getColumnIndex(BALANCE))
            val exclude = c.getInt(c.getColumnIndex(NAME)) == 1

            val account = Account(id, name, balance, exclude)

            accounts[c.position] = account

        }

        return accounts

    }

    fun removeAccount(id: Int) {
        dbHelper.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
    }

}
