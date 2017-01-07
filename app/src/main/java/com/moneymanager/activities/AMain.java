package com.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.moneymanager.R;
import com.moneymanager.activities.accounts.AAccounts;
import com.moneymanager.activities.transaction.AAddTransaction;
import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;

class AMain extends AppCompatActivity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);

		final Toolbar home_toolbar = (Toolbar) findViewById(R.id.home_toolbar);
		setSupportActionBar(home_toolbar);

		// check if accounts exists else redirect to accounts page
		final TAccounts accTable = new TAccounts(this);
		try {

			final Account[] accounts = accTable.getAllAccounts(null, null);

		} catch (NoAccountsException e) {
			startActivity(new Intent(this, AAccounts.class));
		}


		// set up fab button to add new transaction
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_transaction);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AMain.this, AAddTransaction.class));
			}
		});


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

}
