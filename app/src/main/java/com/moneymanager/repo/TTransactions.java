// Created by pranay on 07/01/17.

package com.moneymanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.moneymanager.Common;
import com.moneymanager.db.DBHelper;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.entities.Transaction;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.interfaces.ITransaction;
import com.moneymanager.utilities.MyCalendar;

import java.text.ParseException;
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

	private Context context;

	public TTransactions(Context context) {
		dbHelper = new DBHelper(context);
		this.context = context;
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
				" JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.TABLE_NAME + "." + TCategories.ID +
				" JOIN " + TAccounts.TABLE_NAME + " ON " + ACCOUNT + " = " + TAccounts.TABLE_NAME + "." + TAccounts.ID +
				" ORDER BY " + column + " " + order;
	}

	private String q_SELECT_ALL_TRANSACTIONS_FOR_DAY(Date date) {
		// get Date
		final String date_format = MyCalendar.getSimpleDateFormat().format(date);
		return "SELECT * FROM " + TABLE_NAME +
				" JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.TABLE_NAME + "." + TCategories.ID +
				" JOIN " + TAccounts.TABLE_NAME + " ON " + ACCOUNT + " = " + TAccounts.TABLE_NAME + "." + TAccounts.ID +
				" WHERE " + DATETIME + " = '" + date_format + "'" +
				" ORDER BY " + ACCOUNT + " ASC, " + ID + " DESC";
	}

	private String q_SELECT_ACCOUNT_TRANSACTIONS_FOR_DAY(int accId, Date date) {
		// get Date
		final String date_format = MyCalendar.getSimpleDateFormat().format(date);
		return "SELECT * FROM " + TABLE_NAME +
				" JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.TABLE_NAME + "." + TCategories.ID +
				" JOIN " + TAccounts.TABLE_NAME + " ON " + ACCOUNT + " = " + TAccounts.TABLE_NAME + "." + TAccounts.ID +
				" WHERE " + DATETIME + " = '" + date_format + "' AND " + ACCOUNT + " = " + accId;
	}


	private String q_SELECT_SUM_TRANSACTION_FOR_ACCOUNT_FOR_TYPE_ON_DATE(int acc, int type, Date date) {

		final String date_format = MyCalendar.getSimpleDateFormat().format(date);
		return "SELECT SUM(" + AMOUNT + ") AS " + AMOUNT + " FROM " + TABLE_NAME +
				" JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.TABLE_NAME + "." + TCategories.ID +
				" WHERE " + DATETIME + " = '" + date_format + "' AND " +
				TCategories.TYPE + " = " + type + " AND " +
				ACCOUNT + " = " + acc;


	}

	private String q_SELECT_SUM_TRANSACTION_FOR_TYPE_ON_DATE(int type, Date date) {

		final String date_format = MyCalendar.getSimpleDateFormat().format(date);
		return "SELECT SUM(" + AMOUNT + ") AS " + AMOUNT + " FROM " + TABLE_NAME +
				" JOIN " + TCategories.TABLE_NAME + " ON " + CATEGORY + " = " + TCategories.TABLE_NAME + "." + TCategories.ID +
				" WHERE " + DATETIME + " = '" + date_format + "' AND " + TCategories.TYPE + " = " + type;


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
	public double getSumOfTransactionTypeForDay(int type, Date date) {
		final Cursor c = dbHelper.select(q_SELECT_SUM_TRANSACTION_FOR_TYPE_ON_DATE(type, date), null);
		c.moveToFirst();
		return c.getDouble(c.getColumnIndex(AMOUNT));
	}

	@Override
	public double getAccountSpecificSumOfTransactionTypeForDay(int acc, int type, Date date) {

		final Cursor c = dbHelper.select(q_SELECT_SUM_TRANSACTION_FOR_ACCOUNT_FOR_TYPE_ON_DATE(acc, type, date), null);
		c.moveToFirst();
		return c.getDouble(c.getColumnIndex(AMOUNT));

	}

	public Transaction[] getTransactionsForDay(Date date) {

		Cursor c = dbHelper.select(q_SELECT_ALL_TRANSACTIONS_FOR_DAY(date), null);

		final Transaction[] t = new Transaction[c.getCount()];

		while (c.moveToNext()) {
			t[c.getPosition()] = extractTransactionFromCursor(c);
		}

		return t;
	}

	public Transaction[] getAccountSpecificTransactionsForDay(int accId, Date date) {
		Cursor c = dbHelper.select(q_SELECT_ACCOUNT_TRANSACTIONS_FOR_DAY(accId, date), null);

		final Transaction[] t = new Transaction[c.getCount()];

		while (c.moveToNext()) {
			t[c.getPosition()] = extractTransactionFromCursor(c);
		}

		return t;
	}

	@Override
	public void insertNewTransaction(Transaction transaction) {

		final ContentValues cv = new ContentValues();
		cv.put(AMOUNT, transaction.getAmount());
		cv.put(CATEGORY, transaction.getCategory().getId());
		cv.put(ACCOUNT, transaction.getAccount().getId());
		cv.put(INFO, transaction.getInfo());
		cv.put(DATETIME, MyCalendar.getSimpleDateFormat().format(transaction.getDateTime()));
		cv.put(EXCLUDE, transaction.isExclude());
		dbHelper.insert(TABLE_NAME, cv);

		// update account balance
		TAccounts tAccounts = new TAccounts(context);
		tAccounts.updateAccountBalance(transaction.getAccount().getId(), transaction.getAmount(), transaction.getCategory().getType());

	}

	@Override
	public void removeTransaction(int id) {

	}

	@Override
	public void shiftDeletedTransactions(Category cat) {

		// 1 - Other expense, 2 - Other income
		int newCatID = cat.getType() == Common.EXPENSE ? 1 : 2;

		ContentValues cv = new ContentValues();
		cv.put(CATEGORY, newCatID);

		dbHelper.update(TABLE_NAME, cv, CATEGORY + " = ?", new String[]{String.valueOf(cat.getId())});

	}

	private Transaction extractTransactionFromCursor(Cursor c) {

		final int id = c.getInt(c.getColumnIndex(ID));
		final double amount = c.getDouble(c.getColumnIndex(AMOUNT));
		final String info = c.getString(c.getColumnIndex(INFO));
		Date dateTime = null;
		try {
			dateTime = MyCalendar.getSimpleDateFormat().parse(c.getString(c.getColumnIndex(DATETIME)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
