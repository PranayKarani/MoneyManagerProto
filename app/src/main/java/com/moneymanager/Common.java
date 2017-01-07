// Created by PranayKarani on 13-12-2016.

package com.moneymanager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Common {

	public static final String mylog = "mylog";

	public static void setupToolbar(AppCompatActivity activity, int id, String title) {

		final Toolbar toolbar = (Toolbar) activity.findViewById(id);
		toolbar.setTitle(title);
		activity.setSupportActionBar(toolbar);

	}
}
