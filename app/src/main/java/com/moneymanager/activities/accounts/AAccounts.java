package com.moneymanager.activities.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.utilities.ShrPref;

import static com.moneymanager.Common.*;

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.accounts_Menu_transfer:

				if (noAccounts) {

					Toast.makeText(this, "No Accounts found", Toast.LENGTH_SHORT).show();

				} else {

					startActivity(new Intent(this, AAccountTransfer.class));

				}

				break;

		}

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
			final TextView infotv = (TextView) rowView.findViewById(R.id.account_view_info);

			TAccounts tAccounts = new TAccounts(myContext);
			String transactionSum, debtSum, loanSum;

			final int tC = tAccounts.countTransactions(id);
			final int cC = tAccounts.countDebt(id);
			final int lC = tAccounts.countLoan(id);

			transactionSum = tC < 0 ? "" : "transactions(" + tC + ")";
			debtSum = cC < 0 ? "" : "debt(" + cC + ")";
			loanSum = lC < 0 ? "" : "loan(" + lC + ")";
			infotv.setText(transactionSum + "   " + debtSum + "   " + loanSum);

			// set click listener to edit button in each row
			final ImageButton button = (ImageButton) rowView.findViewById(R.id.account_view_edit_button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					final Snackbar sb = Snackbar.make(findViewById(R.id.a_accounts_coordinate_layout),
							"Delete Account?",
							Snackbar.LENGTH_SHORT)
							.setAction("Yes", new View.OnClickListener() {
								@Override
								public void onClick(View v) {

									Toast.makeText(myContext, "edit account with ID = " + id, Toast.LENGTH_LONG).show();
									accTable.removeAccount(id);
									CURRENT_ACCOUNT_ID = ALL_ACCOUNT_ID;
									ShrPref.writeData(AAccounts.this, spCURRENT_ACCOUNT_ID, CURRENT_ACCOUNT_ID);

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
					View sbv = sb.getView();
					sbv.setBackgroundColor(getMyColor(AAccounts.this, R.color.colorRed));
					sb.setActionTextColor(getMyColor(AAccounts.this, R.color.colorPrimaryDark));
					sb.show();

				}
			});

			return rowView;

		}
	}

}
