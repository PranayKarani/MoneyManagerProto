// Created by pranay on 07/01/17.

package com.moneymanager.repo;

import android.content.Context;
import android.database.Cursor;
import com.moneymanager.db.DBHelper;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.entities.Transaction;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.interfaces.ITransaction;

import java.util.Date;

public class TTransactions implements ITransaction {

	public static final String TABLE_NAME = "Transactions";
	public static final String ID = "_ID";
	public static final String AMOUNT = "trans_amt";
	public static final String CATEGORY = "trans_cat";
	public static final String ACCOUNT = "trans_acc";
	public static final String INFO = "trans_info";
	public static final String DATETIME = "trans_datetime";
	public static final String EXCLUDE = "trans_ex";
	private DBHelper dbHelper;

	public TTransactions(Context context) {
		dbHelper = new DBHelper(context);
	}

	/* Query Strings */
	public static String q_CREATE_TABLE() {
		return "CREATE TABLE " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				AMOUNT + " DOUBLE," +
				CATEGORY + " INTEGER," +
				ACCOUNT + " INTEGER," +
				INFO + " TEXT," +
				DATETIME + " DATETIME," +
				EXCLUDE + " INTEGER," +
				"FOREIGN KEY(" + CATEGORY + ") REFERENCES " + TCategories.TABLE_NAME + "(" + TCategories.ID + ")," +
				"FOREIGN KEY(" + ACCOUNT + ") REFERENCES " + TAccounts.TABLE_NAME + "(" + TAccounts.ID + ")" +
				");";
	}

	private String q_SELECT_ALL_TRANSACTIONS(String column, String order) {
		if (column == null) {
			column = DATETIME;
		}
		if (order == null) {
			order = "DESC";
		}
		return "SELECT * FROM " + TABLE_NAME +
				"  JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.ID +
				" ORDER BY " + column + " " + order;
	}

	@Override
	public Transaction[] getAllTransactions(String column, String order) {

		final Cursor c = dbHelper.select(q_SELECT_ALL_TRANSACTIONS(column, order), null);

		if (c.getCount() == 0) {
			try {
				throw new NoAccountsException();
			} catch (NoAccountsException e) {
				e.printStackTrace();
			}
		}

		final Transaction[] trans = new Transaction[c.getCount()];

		while (c.moveToNext()) {
			trans[c.getPosition()] = extractTransactionFromCursor(c);
		}

		return trans;

	}

	@Override
	public void insertNewTransaction(Transaction transaction) {

	}

	@Override
	public void removeTransaction(int id) {

	}

	private Transaction extractTransactionFromCursor(Cursor c) {

		final int id = c.getInt(c.getColumnIndex(ID));
		final double amount = c.getDouble(c.getColumnIndex(AMOUNT));
		final String info = c.getString(c.getColumnIndex(INFO));
		final Date dateTime = new Date(c.getString(c.getColumnIndex(DATETIME)));// TODO convert properly to datetime
		final boolean ex = c.getInt(c.getColumnIndex(EXCLUDE)) == 1;

		// create categroy object
		final int cat_id = c.getInt(c.getColumnIndex(CATEGORY));
		final String cat_name = c.getString(c.getColumnIndex(TCategories.NAME));
		final int cat_type = c.getInt(c.getColumnIndex(TCategories.TYPE));
		final boolean cat_ex = c.getInt(c.getColumnIndex(TCategories.EXCLUDE)) == 1;
		final Category category = new Category(cat_id, cat_name, cat_type, cat_ex);

		// create account object
		final int acc_id = c.getInt(c.getColumnIndex(ACCOUNT));
		final String acc_name = c.getString(c.getColumnIndex(TAccounts.NAME));
		final double acc_balance = c.getDouble(c.getColumnIndex(TAccounts.BALANCE));
		final boolean acc_exclude = c.getInt(c.getColumnIndex(TAccounts.EXCLUDE)) == 1;
		final Account account = new Account(acc_id, acc_name, acc_balance, acc_exclude);

		return new Transaction(id, amount, category, account, info, dateTime, ex);

	}

}
