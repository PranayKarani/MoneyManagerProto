package com.moneymanager.activities.transaction;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.adapters.AddTransactionAdapter;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.entities.Transaction;
import com.moneymanager.exceptions.InsufficientBalanceException;
import com.moneymanager.fragments.FAddTransaction;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TCategories;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import java.util.Date;

import static com.moneymanager.Common.*;

public class AAddTransaction extends AppCompatActivity implements
		FAddTransaction.OnCategorySelectListener,
		FAddTransaction.OnAccountSelectListener,
		FAddTransaction.OnDateSelectListener {

	private ViewPager viewPager;
	private int selectedCategoryId = -1;
	private int selectedAccountId = -1;
	private Date selectedDate = null;
	private double selectedAccountBalance;

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

		String errorMessage;

		// category
		Category cat;
		if (selectedCategoryId <= 0) {
			errorMessage = "Select a Category";
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
			return null;
		} else {
			cat = cat_table.getCategory(selectedCategoryId);
		}

		// amount
		final EditText add_trans_amt = (EditText) findViewById(R.id.add_trans_amt);
		final String amt = add_trans_amt.getText().toString();
		if (amt.equals("")) {
			errorMessage = "Amount cannot be empty";
			add_trans_amt.setError(errorMessage);
			return null;
		} else if (Double.valueOf(amt) > selectedAccountBalance && cat.getType() == EXPENSE) {
			add_trans_amt.setError("Expense should not exceed Account Balance: (Rs " + selectedAccountBalance + ")");
			return null;
		}

		// Account
		Account acc;
		if (selectedAccountId <= 0) {
			errorMessage = "Select an Account first";// this should not happen since the account is set to current by default, but still...
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
			return null;
		} else {
			acc = acc_table.getAccount(selectedAccountId);
		}

		// info
		final String info = ((TextView) findViewById(R.id.add_trans_info)).getText().toString();

		// date
		if (selectedDate == null) {
			selectedDate = MyCalendar.dateToday();
		}

		// exclude
		final boolean ex = ((Switch) findViewById(R.id.add_trans_ex)).isChecked();

		return new Transaction(-1, Double.valueOf(amt), cat, acc, info, selectedDate, ex);

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
				final Transaction newTransaction = getNewTransaction();
				if (newTransaction != null) {
					try {
						trans_table.insertNewTransaction(newTransaction);
					} catch (InsufficientBalanceException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
					}
					Log.i(mylog, newTransaction.toString());
					finish();
					Toast.makeText(this, "New Transaction added successfully in " + newTransaction.getAccount().getName(), Toast.LENGTH_SHORT).show();
				}
				break;
			}

			case 1: {
				Toast.makeText(this, "debt insert coming soon", Toast.LENGTH_SHORT).show();
				break;
			}

		}


		return true;
	}

	@Override
	public void updateCategoryId(int categoryID) {
		this.selectedCategoryId = categoryID;
	}

	@Override
	public void updateAccountId(int accountId) {
		this.selectedAccountId = accountId;
	}

	@Override
	public void updateAccountBalance(double amount) {
		this.selectedAccountBalance = amount;
	}

	@Override
	public void updateDate(Date date) {
		selectedDate = date;
	}
}
