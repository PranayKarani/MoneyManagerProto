package com.moneymanager.activities.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;

import static com.moneymanager.Common.setupToolbar;

public class AAccounts extends AppCompatActivity {

	public static boolean noAccounts = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_accounts);

		// setup toolbar
		setupToolbar(this, R.id.accounts_toolbar, "Accounts");

		// setting up fab
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_account);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AAccounts.this, AAddAccount.class));
			}
		});


	}

	public void onResume() {
		super.onResume();
		refreshAccountsList();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.accounts_menu, menu);
		return true;
	}

	public void onBackPressed() {

		// if accounts exists close this activity
		if (!noAccounts) {

			finish();

		}

	}

	private void refreshAccountsList() {
		final TAccounts accTable = new TAccounts(this);

		final ListView accListView = (ListView) findViewById(R.id.account_accounts_list);
		final TextView accTxt = (TextView) findViewById(R.id.accounts_text);

		try {
			final Account[] accounts = accTable.getAllAccounts(TAccounts.ID, null);
			noAccounts = false;

			accListView.setAdapter(new AccListAdapter(this, accounts, accTable));
			accListView.setVisibility(View.VISIBLE);

			accTxt.setVisibility(View.GONE);

		} catch (NoAccountsException e) {

			accTxt.setVisibility(View.VISIBLE);
			accListView.setVisibility(View.GONE);


		}
	}

	class AccListAdapter extends ArrayAdapter<Account> {

		Activity myContext;
		TAccounts accTable;

		AccListAdapter(Activity myContext, Account[] accArr, TAccounts accTable) {
			super(myContext, -1, accArr);
			this.myContext = myContext;
			this.accTable = accTable;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// inflate and setup the layout
			final LayoutInflater inflator = myContext.getLayoutInflater();
			View rowView;
			if (convertView == null) {
				rowView = inflator.inflate(R.layout.x_account_view, parent, false);
			} else {
				rowView = convertView;
			}


			// get the account and it's details
			final Account acc = getItem(position);
			final int id = acc.getId();
			final String name = acc.getName();
			final double bal = acc.getBalance();

			// fill up the layout elements
			final TextView nametv = (TextView) rowView.findViewById(R.id.account_view_name);
			nametv.setText(name + " (" + id + ")");
			final TextView baltv = (TextView) rowView.findViewById(R.id.account_view_bal);
			baltv.setText(String.valueOf(bal));

			// set click listener to edit button in each row
			final ImageButton button = (ImageButton) rowView.findViewById(R.id.account_view_edit_button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Toast.makeText(myContext, "edit account with ID = " + id, Toast.LENGTH_LONG).show();
					accTable.removeAccount(id);

					// refresh account list
					final TAccounts accTable = new TAccounts(myContext);
					final ListView accListView = (ListView) myContext.findViewById(R.id.account_accounts_list);

					try {
						final Account[] accounts = accTable.getAllAccounts(TAccounts.ID, null);
						noAccounts = false;

						accListView.setAdapter(new AccListAdapter(myContext, accounts, accTable));


					} catch (NoAccountsException e) {

						noAccounts = true;
						final TextView accTxt = (TextView) myContext.findViewById(R.id.accounts_text);
						accTxt.setVisibility(View.VISIBLE);
						accListView.setVisibility(View.GONE);


					}

				}
			});

			return rowView;

		}
	}

}
