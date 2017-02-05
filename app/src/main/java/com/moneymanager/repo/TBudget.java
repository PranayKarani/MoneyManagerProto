// Created by pranay on 05/02/17.

package com.moneymanager.repo;

import android.content.Context;
import com.moneymanager.db.DBHelper;
import com.moneymanager.repo.interfaces.IBudget;

public class TBudget implements IBudget {

	public static final String TABLE_NAME = "Budgets";
	public static final String ID = "_ID";
	public static final String AMOUNT = "bud_amt";
	public static final String CATEGORY = "bud_cat";
	public static final String ACCOUNT = "bud_acc";
	public static final String INFO = "bud_info";
	public static final String DATETIME = "bud_datetime";
	public static final String PERIOD = "bud_period";
	public static final String NOTIFY = "bud_notify";
	private DBHelper dbHelper;

	private Context context;
	private String BID_alias = "bID";


	/* Query Strings */
	public static String q_CREATE_TABLE() {
		return "CREATE TABLE " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				AMOUNT + " DOUBLE," +
				CATEGORY + " INTEGER," +
				ACCOUNT + " INTEGER," +
				INFO + " TEXT," +
				DATETIME + " DATETIME," +
				PERIOD + " INTEGER," +
				NOTIFY + " INTEGER," +
				"FOREIGN KEY(" + CATEGORY + ") REFERENCES " + TCategories.TABLE_NAME + "(" + TCategories.ID + ")," +
				"FOREIGN KEY(" + ACCOUNT + ") REFERENCES " + TAccounts.TABLE_NAME + "(" + TAccounts.ID + ")" +
				");";
	}

}
