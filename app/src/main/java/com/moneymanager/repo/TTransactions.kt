package com.moneymanager.repo

import android.content.Context
import com.moneymanager.db.DBHelper
import com.moneymanager.entities.Account
import com.moneymanager.entities.Category
import com.moneymanager.entities.Transaction
import com.moneymanager.exceptions.NoAccountsException
import com.moneymanager.repo.interfaces.ITransaction
import java.util.*

// Created by PranayKarani on 10-12-2016.
class TTransactions(context: Context) : ITransaction {

    companion object {
		val TABLE_NAME = "Transactions"
        val ID = "_ID"
        val AMOUNT = "trans_amount"
        val CATEGORY = "trans_cat"
        val ACCOUNT = "trans_acc"
        val INFO = "trans_info"
        val DATE_TIME = "trans_datetime"
        val EXCLUDE = "trans_ex"


        val q_CREATE_TABLE = "" +
				"CREATE TABLE ${TABLE_NAME} (" +
				"${ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
				"${AMOUNT} " +
				"" +
				"DOUBLE," +
				"${CATEGORY} INTEGER," +
				"${ACCOUNT} INTEGER," +
				"${INFO} TEXT," +
				"${DATE_TIME} DATETIME," +
				"${EXCLUDE} INTEGER," +
				"FOREIGN KEY(${CATEGORY}) REFERENCES ${TCategories.TABLE_NAME}(${TCategories.ID})," +
				"FOREIGN KEY(${ACCOUNT}) REFERENCES ${TAccounts.TABLE_NAME}(${TAccounts.ID})" +
                ");"


		fun q_SELECT_ALL_TRANSACTIONS(col: String?, order: String?): String {
			return "SELECT * FROM ${TABLE_NAME} " +
					"JOIN ${TCategories.TABLE_NAME} ON ${CATEGORY} = ${TCategories.ID} " +
					"ORDER BY ${col ?: DATE_TIME} ${order ?: "DESC"}; "
		}

    }

	val dbHelper = DBHelper(context)

	override fun getAllTransactions(column: String?, order: String?): Array<Transaction?> {

		val c = dbHelper.select(q_SELECT_ALL_TRANSACTIONS(column, order), null)

		if (c.count == 0) throw NoAccountsException()

		val trans = arrayOfNulls<Transaction>(c.count)

		while (c.moveToNext()) {


			val id = c.getInt(c.getColumnIndex(ID))
			val amount = c.getDouble(c.getColumnIndex(AMOUNT))
			val info = c.getString(c.getColumnIndex(INFO))
			val dateTime = Date(c.getString(c.getColumnIndex(DATE_TIME)))// TODO convert properly to datetime
			val ex = c.getInt(c.getColumnIndex(EXCLUDE)) == 1

			// create categroy object
			val cat_id = c.getInt(c.getColumnIndex(CATEGORY))
			val cat_name = c.getString(c.getColumnIndex(TCategories.NAME))
			val cat_type = c.getInt(c.getColumnIndex(TCategories.TYPE))
			val cat_ex = c.getInt(c.getColumnIndex(TCategories.EXCLUDE)) == 1
			val category = Category(cat_id, cat_name, cat_type, cat_ex)

			// create account object
			val acc_id = c.getInt(c.getColumnIndex(ACCOUNT))
			val acc_name = c.getString(c.getColumnIndex(TAccounts.NAME))
			val acc_balance = c.getDouble(c.getColumnIndex(TAccounts.BALANCE))
			val acc_exclude = c.getInt(c.getColumnIndex(TAccounts.EXCLUDE)) == 1
			val account = Account(acc_id, acc_name, acc_balance, acc_exclude)

			trans[c.position] = Transaction(id, amount, category, account, info, dateTime, ex)


		}

		return trans

	}

	override fun insertNewTransaction(trans: Transaction) {

	}

	override fun removeTransaction(transID: Int) {

	}

}
