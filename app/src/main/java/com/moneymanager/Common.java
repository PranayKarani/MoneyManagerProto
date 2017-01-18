// Created by PranayKarani on 13-12-2016.

package com.moneymanager;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Common {

	public static final String mylog = "mylog";
	public static final String DATE_FORMAT = "yyyy-MM-DD";

	public static final int INCOME = 0;
	public static final int EXPENSE = 1;

	// SharedPreferences Constants
	public static final String spFILE_NAME = "mm_sp_file";
	public static final String spCURRENT_ACCOUNT_ID = "account_id";
	public static int ALL_ACCOUNT_ID = -9837; // Used to get info of all accounts
	public static int CURRENT_ACCOUNT_ID = -1;
	public static String CURRENT_ACCOUNT_NAME;

	public static void setupToolbar(AppCompatActivity activity, int id, String title) {

		final Toolbar toolbar = (Toolbar) activity.findViewById(id);
		toolbar.setTitle(title);
		activity.setSupportActionBar(toolbar);

	}

	public static int getMyColor(Context context, int color) {
		return ContextCompat.getColor(context, color);
	}
}
