package com.moneymanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.activities.accounts.AAccounts;
import com.moneymanager.activities.transaction.AAddTransaction;
import com.moneymanager.adapters.HomePagerAdapter;
import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.fragments.FHomePage;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.utilities.ShrPref;

import static com.moneymanager.Common.*;

public class AMain extends AppCompatActivity {

	private Account[] accounts;
	private String[] acc_names;
	private int[] acc_ids;

	private ViewPager viewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);

		final Toolbar home_toolbar = (Toolbar) findViewById(R.id.home_toolbar);
		home_toolbar.setTitle("");
		setSupportActionBar(home_toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);


		// set up fab button to add new transaction
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_transaction);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AMain.this, AAddTransaction.class));
			}
		});
		fab.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(AMain.this, "Soon something will happen on long press too :)", Toast.LENGTH_LONG).show();
				return true;
			}
		});

		final HomePagerAdapter hmp = new HomePagerAdapter(getSupportFragmentManager());

		viewPager = (ViewPager) findViewById(R.id.home_viewpager);
		viewPager.setAdapter(hmp);
		viewPager.setCurrentItem(6);


	}

	@Override
	protected void onResume() {
		super.onResume();

		// check if accounts exists else redirect to accounts page
		final TAccounts accTable = new TAccounts(this);
		try {

			// query for account list, throw NoAccountsException if none found
			accounts = accTable.getAllAccounts(TAccounts.NAME, null);

			// get the current account id
			CURRENT_ACCOUNT_ID = ShrPref.readData(this, spCURRENT_ACCOUNT_ID, ALL_ACCOUNT_ID);

			if (CURRENT_ACCOUNT_ID == ALL_ACCOUNT_ID) {
				CURRENT_ACCOUNT_NAME = "All Accounts";
			}

			// setup Account related Arrays
			acc_names = new String[accounts.length + 1];
			acc_names[0] = "All Accounts";
			acc_ids = new int[accounts.length + 1];
			acc_ids[0] = ALL_ACCOUNT_ID;
			for (int i = 1; i < acc_names.length; i++) {
				acc_names[i] = accounts[i - 1].getName();
				acc_ids[i] = accounts[i - 1].getId();
				if (acc_ids[i] == CURRENT_ACCOUNT_ID) {
					CURRENT_ACCOUNT_NAME = acc_names[i];
				}
			}

			refreshToolbar();


		} catch (NoAccountsException e) {
			startActivity(new Intent(this, AAccounts.class));
		}

		if (getSupportFragmentManager().getFragments() != null) {
			for (Fragment f : getSupportFragmentManager().getFragments()) {
				((FHomePage) f).refreshFragmentContent(CURRENT_ACCOUNT_ID);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		// store current account id
		ShrPref.writeData(this, spCURRENT_ACCOUNT_ID, CURRENT_ACCOUNT_ID);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.home_menu_search: {

				startActivity(new Intent(this, ASearch.class));

				return true;
			}

			case R.id.home_menu_add_account: {

				startActivity(new Intent(this, AAccounts.class));

				return true;

			}

			default: {
				return super.onOptionsItemSelected(item);
			}

		}
	}

	private void refreshToolbar() {
		final LinearLayout layout = (LinearLayout) findViewById(R.id.home_toobar_layout);

		final TextView toolbar_text = (TextView) layout.findViewById(R.id.home_toolbar_textview);
		toolbar_text.setText(CURRENT_ACCOUNT_NAME);

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(AMain.this);
				builder.setCancelable(true);
				builder.setTitle("Select an Account");
				builder.setItems(acc_names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Log.i(mylog, "Current account: " + acc_names[i]);
						CURRENT_ACCOUNT_ID = acc_ids[i];
						toolbar_text.setText(acc_names[i]);
						CURRENT_ACCOUNT_NAME = acc_names[i];
						ShrPref.writeData(AMain.this, spCURRENT_ACCOUNT_ID, CURRENT_ACCOUNT_ID);
						dialogInterface.dismiss();

						for (Fragment f : getSupportFragmentManager().getFragments()) {
							((FHomePage) f).refreshFragmentContent(CURRENT_ACCOUNT_ID);
						}

					}
				});
				builder.create().show();
			}
		});
	}

}
