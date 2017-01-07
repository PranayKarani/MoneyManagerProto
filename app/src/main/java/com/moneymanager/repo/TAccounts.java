// Created by pranay on 07/01/17.

package com.moneymanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.moneymanager.db.DBHelper;
import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.interfaces.IAccount;

public class TAccounts implements IAccount {

	public static final String TABLE_NAME = "Account";
	public static final String ID = "_ID";
	public static final String NAME = "acc_name";
	public static final String BALANCE = "acc_bal";
	public static final String EXCLUDE = "acc_ex";

	/* Query Strings */
	private DBHelper dbHelper;

	public TAccounts(Context context) {

		dbHelper = new DBHelper(context);

	}

	public static String q_CREATE_TABLE() {
		return
				"CREATE TABLE " + TABLE_NAME + " (" +
						ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						NAME + " TEXT, " +
						BALANCE + " DOUBLE, " +
						EXCLUDE + " INTEGER" +
						")";
	}

	private String q_SELECT_ALL_ACCOUNTS(String column, String order) {
		if (column == null) {
			column = NAME;
		}
		if (order == null) {
			order = "ASC";
		}
		return "SELECT * FROM " + TABLE_NAME + " ORDER BY " + column + " " + order;
	}

	private String q_SELECT_ACCOUNT(int id) {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
	}

	private String q_SUM_BALANCE_FROM_ALL_ACCOUNTS() {
		return "SELECT SUM( " + BALANCE + ") AS " + BALANCE + " FROM " + TABLE_NAME;
	}

	@Override
	public long insertNewAccount(Account account) {

		final ContentValues cv = new ContentValues();
		cv.put(NAME, account.getName());
		cv.put(BALANCE, account.getBalance());
		cv.put(EXCLUDE, account.isExclude());

		return dbHelper.insert(TABLE_NAME, cv);

	}

	@Override
	public Account[] getAllAccounts(String column, String order) throws NoAccountsException {

		final Cursor c = dbHelper.select(q_SELECT_ALL_ACCOUNTS(column, order), null);

		if (c.getCount() == 0) {
			throw new NoAccountsException();
		}

		final Account[] accounts = new Account[c.getCount()];

		while (c.moveToNext()) {

			accounts[c.getPosition()] = extractAccountFromCursor(c);

		}

		return accounts;

	}

	@Override
	public Account getAccount(int id) {
		final Cursor c = dbHelper.select(q_SELECT_ACCOUNT(id), null);

		if (c.moveToFirst()) {

			return extractAccountFromCursor(c);

		} else {
			try {
				throw new Exception("No Account found");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public void removeAccount(int id) {

		dbHelper.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});

	}

	@Override
	public int getSumOfBalanceOfAllAccounts() {
		final Cursor c = dbHelper.select(q_SUM_BALANCE_FROM_ALL_ACCOUNTS(), null);
		if (c.moveToNext()) {
			return c.getInt(c.getColumnIndex(BALANCE));
		} else {
			return -1;
		}
	}

	private Account extractAccountFromCursor(Cursor c) {
		final int id = c.getInt(c.getColumnIndex(ID));
		final String name = c.getString(c.getColumnIndex(NAME));
		final double balance = c.getDouble(c.getColumnIndex(BALANCE));
		final boolean exclude = c.getInt(c.getColumnIndex(EXCLUDE)) == 1;

		return new Account(id, name, balance, exclude);
	}

}
