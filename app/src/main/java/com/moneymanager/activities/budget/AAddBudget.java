package com.moneymanager.activities.budget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.activities.category.ACategories;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Budget;
import com.moneymanager.entities.Category;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TBudget;
import com.moneymanager.repo.TCategories;
import com.moneymanager.utilities.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.moneymanager.Common.*;

public class AAddBudget extends AppCompatActivity {

	private int selectedCategoryID = -1;
	private int selectedAccountID = -1;
	private Date selectedDate;
	private ArrayList<String> cat_name_list;
	private ArrayList<Integer> cat_id_list;
	private ArrayList<String> acc_name_list;
	private ArrayList<Integer> acc_id_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_budget);

		setupToolbar(this, R.id.a_add_budget_toolbar, "Add budget");

		init();

		final TextView cat_text = (TextView) findViewById(R.id.add_budget_cat);
		cat_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String[] names = new String[cat_name_list.size()];
				for (int i = 0; i < names.length; i++) {
					names[i] = cat_name_list.get(i);
				}

				final AlertDialog.Builder builder = new AlertDialog.Builder(AAddBudget.this);
				builder.setCancelable(true);
				builder.setTitle("Select a Category");
				builder.setItems(names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						selectedCategoryID = cat_id_list.get(i);
						cat_text.setText("category: " + names[i]);
						dialogInterface.dismiss();
					}
				});
				builder.setPositiveButton("manage categories", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(AAddBudget.this, ACategories.class);
						intent.putExtra("type", EXPENSE);
						startActivity(intent);
					}
				});
				builder.create().show();


			}
		});

		final TextView acc_text = (TextView) findViewById(R.id.add_budget_acc);
		if (CURRENT_ACCOUNT_ID != ALL_ACCOUNT_ID) {
			acc_text.setText("account: " + CURRENT_ACCOUNT_NAME);
		} else {
			acc_text.setText("Select Account");
		}
		acc_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String[] names = new String[acc_name_list.size()];
				for (int i = 0; i < names.length; i++) {
					names[i] = acc_name_list.get(i);
				}

				final AlertDialog.Builder builder = new AlertDialog.Builder(AAddBudget.this);
				builder.setCancelable(true);
				builder.setTitle("Select an Account");
				builder.setItems(names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						selectedAccountID = acc_id_list.get(i);
						acc_text.setText("account: " + names[i]);
						dialogInterface.dismiss();
					}
				});
				builder.create().show();


			}
		});


		// Set the date to today's date by default
		TextView dateText = (TextView) findViewById(R.id.add_budget_date);
//		dateText.setText(MyCalendar.getNiceFormatedCompleteDateString(MyCalendar.dateToday()));
		selectedDate = MyCalendar.dateToday();


	}

	private void init() {
		cat_name_list = new ArrayList<>();
		cat_id_list = new ArrayList<>();
		acc_name_list = new ArrayList<>();
		acc_id_list = new ArrayList<>();

		updateCategoryList();
		updateAccountsList();

		selectedAccountID = ALL_ACCOUNT_ID;

	}

	public void OnSetDateClick(View view) {


		final DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getSupportFragmentManager(), "Pick a Date");

	}

	// update Category list according to 'income' 'expense' selection
	private void updateCategoryList() {

		final TCategories cats = new TCategories(this);

		final Category[] cat_array = cats.getTypeSpecificCategories(TCategories.EXPENSE);

		cat_name_list.clear();
		cat_id_list.clear();

		for (Category cat : cat_array) {
			cat_name_list.add(cat.getName());
			cat_id_list.add(cat.getId());
		}

	}

	// Account update is only needed once and is done in onActivityCreated
	private void updateAccountsList() {

		final TAccounts acc = new TAccounts(this);

		try {
			final Account[] acc_array = acc.getAllAccounts(null, null);
			acc_name_list.clear();
			acc_id_list.clear();

			for (Account a : acc_array) {
				acc_name_list.add(a.getName());
				acc_id_list.add(a.getId());
			}
		} catch (NoAccountsException e) {
			e.printStackTrace();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_budget_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Budget newBudget = getNewBudget();

		if (newBudget != null) {
			TBudget tBudget = new TBudget(this);

			tBudget.insertBudget(newBudget);
			finish();

			Toast.makeText(this, "New budget added", Toast.LENGTH_SHORT).show();
		}

		return true;
	}

	private Budget getNewBudget() {

		final TCategories cat_table = new TCategories(this);
		final TAccounts acc_table = new TAccounts(this);

		String errorMessage;

		// category
		Category cat;
		if (selectedCategoryID <= 0) {
			errorMessage = "Select a Category";
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
			return null;
		} else {
			cat = cat_table.getCategory(selectedCategoryID);
		}

		// amount
		final EditText add_budget_amt = (EditText) findViewById(R.id.add_budget_amt);
		final String amt = add_budget_amt.getText().toString();
		if (amt.equals("")) {
			errorMessage = "Amount cannot be empty";
			add_budget_amt.setError(errorMessage);
			return null;
		}

		// Account
		Account acc;
		if (selectedAccountID <= 0) {
			errorMessage = "Select an Account first";// this should not happen since the account is set to current by default, but still...
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
			return null;
		} else {
			acc = acc_table.getAccount(selectedAccountID);
		}

		// info
		final String info = ((TextView) findViewById(R.id.add_budget_info)).getText().toString();

		// date
		if (selectedDate == null) {
			selectedDate = MyCalendar.dateToday();
		}

		// period
		final EditText add_budget_period = (EditText) findViewById(R.id.add_budget_period);
		final int period = add_budget_period.getText().toString().equals("") ? -1 : Integer.valueOf(add_budget_period.getText().toString());

		if (period <= 0) {
			errorMessage = "Period too small";
			add_budget_period.setError(errorMessage);
			return null;
		}

		return new Budget(-1, cat, acc, Double.valueOf(amt), info, selectedDate, period);


	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog dp = new DatePickerDialog(getActivity(), this, y, m, d);
			return dp;
		}

		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

			year -= 1900;//
			Date newDate = new Date(year, month, dayOfMonth);

			final TextView text = (TextView) getActivity().findViewById(R.id.add_budget_date);
			text.setText(MyCalendar.getNiceFormatedCompleteDateString(newDate));
		}

	}

}
