package com.moneymanager.activities.accounts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.repo.TAccounts;

import static com.moneymanager.Common.setupToolbar;

class AAddAccount extends AppCompatActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_account);

		setupToolbar(this, R.id.add_account_toolbar, "Add New Account");

	}

	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_account_menu, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	public void onInsertAccountClick(View view) {

		final EditText acc_name = (EditText) findViewById(R.id.new_account_name);
		final EditText acc_bal = (EditText) findViewById(R.id.new_account_bal);
		final Switch acc_ex = (Switch) findViewById(R.id.new_account_exclude);

		final String new_acc_name = acc_name.getText().toString();
		final String new_acc_bal = acc_bal.getText().toString();
		final boolean new_acc_ex = acc_ex.isChecked();

		final TAccounts accountTable = new TAccounts(this);

		if (new_acc_name.isEmpty()) {
			acc_name.setError("Name cannot be empty");
		} else if (new_acc_bal.isEmpty()) {
			acc_bal.setError("Enter starting balance");

		} else {
			if (accountTable.insertNewAccount(new Account(-1, new_acc_name, Double.valueOf(new_acc_bal), new_acc_ex)) >= 1) {
				Toast.makeText(this, "New Account " + new_acc_name + " created!", Toast.LENGTH_LONG).show();
				AAccounts.noAccounts = false;
			} else {
				Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_LONG).show();
			}
			finish();
		}


	}

}
