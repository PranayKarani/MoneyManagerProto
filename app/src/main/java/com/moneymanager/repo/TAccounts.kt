package com.moneymanager.repo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.moneymanager.db.DBHelper
import com.moneymanager.entities.Account
import com.moneymanager.exceptions.NoAccountsException
import com.moneymanager.repo.interfaces.IAccount

// Created by PranayKarani on 10-12-2016.
class TAccounts(context: Context) : IAccount {

    companion object {
        val TABLE_NAME = "Account"
        val ID = "_ID"
        val NAME = "acc_name"
        val BALANCE = "acc_bal"
        val EXCLUDE = "acc_ex"

        val q_CREATE_TABLE =
				"CREATE TABLE ${TABLE_NAME} ( " +
						"${ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
						"${NAME} TEXT," +
						"${BALANCE} DOUBLE," +
						"${EXCLUDE} INTEGER" +
                        ");"

        fun q_SELECT_ALL_ACCOUNTS(column: String?, order: String?): String {
			return "SELECT * FROM ${TABLE_NAME} ORDER BY ${column ?: NAME} ${order ?: "ASC"};"
		}

		fun q_GET_ACCOUNT(id: Int) = "SELECT * FROM $TABLE_NAME WHERE $ID = $id"

		fun q_SUM_ALL_ACCOUNTS(): String {
			return "SELECT SUM(${BALANCE}) AS bal FROM ${TABLE_NAME}"
        }

    }

    val dbHelper = DBHelper(context)

	override fun insertNewAccount(acc: Account): Long {

        val cv = ContentValues()
        cv.put(NAME, acc.name)
        cv.put(BALANCE, acc.bal)
        cv.put(EXCLUDE, acc.exclude)

        return dbHelper.insert(TABLE_NAME, cv)

    }

	override fun getAllAccounts(column: String?, order: String?): Array<Account?> {

        val c = dbHelper.select(q_SELECT_ALL_ACCOUNTS(column, order), null)

        if (c.count == 0) throw NoAccountsException()

		val accounts = arrayOfNulls<Account>(c.count)

        while (c.moveToNext()) {

			accounts[c.position] = getAccountInstance(c)

		}

		return accounts

	}

	override fun getAccount(id: Int): Account {

		val c = dbHelper.select(q_GET_ACCOUNT(id), null)

		if (c.moveToFirst()) {

			return getAccountInstance(c)

		} else {
			throw Exception("No Account found retrieved")
		}

	}

	override fun removeAccount(id: Int) {
		dbHelper.delete(TABLE_NAME, "${ID} = ?", arrayOf(id.toString()))
	}

	override fun getAllAcountsOverView(): Int {
		val c = dbHelper.select(q_SUM_ALL_ACCOUNTS(), null)
		if (c.moveToNext()) {
			val balance = c.getInt(c.getColumnIndex("bal"))
			return balance
		} else {
			return -1
		}
	}

	private fun getAccountInstance(c: Cursor): Account {
		val id = c.getInt(c.getColumnIndex(ID))
		val name = c.getString(c.getColumnIndex(NAME))
		val balance = c.getDouble(c.getColumnIndex(BALANCE))
		val exclude = c.getInt(c.getColumnIndex(EXCLUDE)) == 1

		return Account(id, name, balance, exclude)
    }

}
