package com.moneymanager.activities.transaction;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.adapters.AddTransactionAdapter;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.entities.Transaction;
import com.moneymanager.fragments.FAddTransaction;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TCategories;
import com.moneymanager.repo.TTransactions;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.moneymanager.Common.setupToolbar;

public class AAddTransaction extends AppCompatActivity {

	private ViewPager viewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_transaction);

		setupToolbar(this, R.id.transaction_toolbar, "Add a Transaction");

		// setting up ViewPager Stuff
		final AddTransactionAdapter ta = new AddTransactionAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
		viewPager.setAdapter(ta);

	}

	public void OnSetDateClick(View view) {

		final FAddTransaction.TimePickerFragment timePickerFragment = new FAddTransaction.TimePickerFragment();
		timePickerFragment.show(getSupportFragmentManager(), "Pick a Date");

	}

	private Transaction getNewTransaction() {

		final TCategories cat_table = new TCategories(this);
		final TAccounts acc_table = new TAccounts(this);

		// amount
		final String amt = ((TextView) findViewById(R.id.add_trans_amt)).getText().toString();
		// category
		final Category cat = cat_table.getCategory(1);// for testing purpose, all get Category with id = 1
		// Account
		final Account acc = acc_table.getAccount(new Random().nextInt(3) + 1);// for testing purpose, select random account between IDs 1 and 2

		// info
		final String info = ((TextView) findViewById(R.id.add_trans_info)).getText().toString();

		// date
		final Calendar cal = java.util.Calendar.getInstance();
		final Date date = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

		// exclude
		final boolean ex = findViewById(R.id.add_trans_ex).isActivated();

		return new Transaction(-1, Double.valueOf(amt), cat, acc, info, date, ex);

	}

	/**
	 * Toolbar Menu Stuff
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_trans_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// since only one item in menu
		// no need for switch case

		// whether to insert transaction or debt
		switch (viewPager.getCurrentItem()) {
			case 0: {
				// insert new transaction data into database
				final TTransactions trans_table = new TTransactions(this);
				trans_table.insertNewTransaction(getNewTransaction());
				break;
			}

			case 1: {
				Toast.makeText(this, "debt insert coming soon", Toast.LENGTH_SHORT).show();
				break;
			}

		}


		return true;
	}

}
