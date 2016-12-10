package com.moneymanager.db

import com.moneymanager.entities.Account
import android.content.ContentValues
import android.content.Context

// Created by PranayKarani on 10-12-2016.
class TAccounts(context: Context) : AbstractTable() {

    companion object {
        val TABLE_NAME = "Account"
        val ID = "_ID"
        val NAME = "acc_name"
        val BALANCE = "acc_bal"
        val EXCLUDE = "acc_ex"

        val createTableQuery =
                "CREATE TABLE $TABLE_NAME ( " +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$NAME TEXT," +
                        "$BALANCE DOUBLE," +
                        "$EXCLUDE INTEGER" +
                        ");"

        val selectAllAccounts = "SELECT * FROM $TABLE_NAME ORDER BY $NAME ASC;"

    }

    val dbHelper = DBHelper(context)

    fun insertNewAccount(acc: Account): Long {

        val cv = ContentValues()
        cv.put(NAME, acc.name)
        cv.put(BALANCE, acc.bal)
        cv.put(EXCLUDE, acc.exclude)

        return dbHelper.insert(TABLE_NAME, cv)

    }

    fun getAllAccounts(): Array<Account?> {


        val c = dbHelper.select(selectAllAccounts, null)

        if (c.moveToFirst()) {

            val accounts = kotlin.arrayOfNulls<Account>(c.count)

            while (c.moveToNext()) {

                val name = c.getString(c.getColumnIndex(NAME))
                val balance = c.getDouble(c.getColumnIndex(BALANCE))
                val exclude = c.getInt(c.getColumnIndex(NAME)) == 1

                val account = Account(name, balance, exclude)

                accounts[c.position] = account

            }

            return accounts

        } else {
            throw Exception("account table should not be empty")
        }


    }

}
