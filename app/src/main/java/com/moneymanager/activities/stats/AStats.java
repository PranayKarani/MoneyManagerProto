package com.moneymanager.activities.stats;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.moneymanager.Common;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Transaction;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.moneymanager.Common.*;

public class AStats extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

	private Date myDate;
	private SimpleDateFormat sdf;

	private final int DAY = 324;
	private final int WEEK = 244;
	private final int MONTH = 824;
	private final int YEAR = 944;
	private final int CUSTOM = 31;

	private int selectedAccountID = CURRENT_ACCOUNT_ID;
	private int selectedPeriod = DAY;

	private Account[] accounts;
	private String[] acc_names;
	private int[] acc_ids;

	// Views
	private MenuItem period_text;
	private TextView cardIncomeTextView, cardExpenseTextView, cardTotalTextView;
	private CardView calenderCard, piechartCard, bargraphCard;

	private CardView income_trans_container_card, expense_trans_container_card;
	private LinearLayout income_trans_container, expense_trans_container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_stats);

		init();

		setupToolbar(this, R.id.a_stats_toolbar, "");

		refreshToolbar();

		calenderCard = (CardView) findViewById(R.id.a_stats_overview_calender);
		piechartCard = (CardView) findViewById(R.id.a_stats_overview_piechart);
		bargraphCard = (CardView) findViewById(R.id.a_stats_overview_bargraph);

		refreshCardViews();

		income_trans_container_card = (CardView) findViewById(R.id.a_stats_income_trans_list_container_card);
		income_trans_container = (LinearLayout) findViewById(R.id.a_stats_income_trans_list_container);
		expense_trans_container_card = (CardView) findViewById(R.id.a_stats_expense_trans_list_container_card);
		expense_trans_container = (LinearLayout) findViewById(R.id.a_stats_expense_trans_list_container);


		cardIncomeTextView = (TextView) findViewById(R.id.a_stats_overview_card_income_amt);
		cardExpenseTextView = (TextView) findViewById(R.id.a_stats_overview_card_expense_amt);
		cardTotalTextView = (TextView) findViewById(R.id.a_stats_overview_card_total_amt);

		Calendar c = Calendar.getInstance();

		Bundle b = new Bundle();
		b.putString("date", sdf.format(myDate));
		new TransListLoader().execute(b);

	}

	private void init() {

		sdf = MyCalendar.getSimpleDateFormat();
		final Intent intent = getIntent();
		try {
			myDate = sdf.parse(intent.getStringExtra("date"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Toast.makeText(this, "stats for " + MyCalendar.dateToString(myDate), Toast.LENGTH_SHORT).show();

		// setup Account related Arrays
		TAccounts accTable = new TAccounts(this);
		try {
			accounts = accTable.getAllAccounts(TAccounts.NAME, null);
		} catch (NoAccountsException e) {
			e.printStackTrace();
		}
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

	}

	private void refreshCardViews() {

		switch (selectedPeriod) {

			case DAY:
				bargraphCard.setVisibility(View.GONE);
				calenderCard.setVisibility(View.GONE);
				break;
			case WEEK:
				bargraphCard.setVisibility(View.VISIBLE);
				calenderCard.setVisibility(View.GONE);
				break;
			case MONTH:
				bargraphCard.setVisibility(View.VISIBLE);
				calenderCard.setVisibility(View.VISIBLE);
				break;
			case YEAR:
				bargraphCard.setVisibility(View.VISIBLE);
				calenderCard.setVisibility(View.GONE);
				break;
			default:
				bargraphCard.setVisibility(View.GONE);
				calenderCard.setVisibility(View.GONE);
				break;

		}

	}

	/**
	 * Menu stuff
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stats_period_menu, menu);
		period_text = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.stats_menu_period_text:
				// show selector for selected period
				// e.g. if month is selected and this is tapped, show dialog for selecting other month
				break;
			case R.id.stats_menu_period_day:
				period_text.setTitle("Day");

				selectedPeriod = DAY;


				break;
			case R.id.stats_menu_period_week:
				period_text.setTitle("Week");

				selectedPeriod = WEEK;


				break;
			case R.id.stats_menu_period_month:
				period_text.setTitle("Month");

				selectedPeriod = MONTH;

				break;
			case R.id.stats_menu_period_year:
				period_text.setTitle("Year");

				selectedPeriod = YEAR;

				break;
			default: // custom
				period_text.setTitle("Custom");

				selectedPeriod = CUSTOM;

				break;
		}

		refreshCardViews();
		// refresh the stats according to selected account
		Bundle b = new Bundle();
		b.putString("date", sdf.format(myDate));
		new TransListLoader().execute(b);

		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		Log.i(mylog, position + " selected");

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void refreshToolbar() {
		final LinearLayout layout = (LinearLayout) findViewById(R.id.a_stats_toolbar_layout);

		final TextView toolbar_text = (TextView) layout.findViewById(R.id.a_stats_toolbar_account_text);
		toolbar_text.setText(CURRENT_ACCOUNT_NAME);

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(AStats.this);
				builder.setCancelable(true);
				builder.setTitle("Select an Account");
				builder.setItems(acc_names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Log.i(mylog, "selected account: " + acc_names[i]);
						toolbar_text.setText(acc_names[i]);
						dialogInterface.dismiss();
						selectedAccountID = acc_ids[i];

						// refresh the stats according to selected account
						Bundle b = new Bundle();
						b.putString("date", sdf.format(myDate));
						new TransListLoader().execute(b);

					}
				});
				builder.create().show();
			}
		});
	}

	class TransListLoader extends AsyncTask<Bundle, Void, Transaction[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(mylog, "loader started");

		}

		@Override
		protected Transaction[] doInBackground(Bundle... params) {

			Bundle bundle = params[0];

			TTransactions tTransactions = new TTransactions(AStats.this);

			Transaction[] transactions = null;

			switch (selectedPeriod) {

				case DAY:

					try {
						Date date = sdf.parse(bundle.getString("date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForDay(date);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForDay(selectedAccountID, date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

				case WEEK:
					try {
						Date date = sdf.parse(bundle.getString("date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForWeek(date);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForWeek(selectedAccountID, date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

			}


			return transactions;
		}

		@Override
		protected void onPostExecute(Transaction[] transactions) {
			super.onPostExecute(transactions);

			TextView title = (TextView) findViewById(R.id.a_stats_overview_card_title);
			switch (selectedPeriod) {
				case DAY:
					title.setText(MyCalendar.dateToString(myDate) + " " + MyCalendar.monthToFullString(myDate) + "'s Overview");
					break;
				case WEEK:
					Date[] dates = MyCalendar.weekEndandStartDatesforDate(myDate);
					String startDate = MyCalendar.dateToString(dates[0]) + " " + MyCalendar.monthToString(dates[0]);
					String endDate = MyCalendar.dateToString(dates[1]) + " " + MyCalendar.monthToString(dates[1]);
					title.setText(startDate + " ~ " + endDate + " Overview");
					break;

			}

			double income = 0;
			double expense = 0;

			int countIncomeTrans = 0;
			int countExpenseTrans = 0;

			income_trans_container.removeAllViews();
			expense_trans_container.removeAllViews();

			for (Transaction t : transactions) {

				View rowView = getLayoutInflater().inflate(R.layout.x_home_trans_row, null);

				final TextView tCat = (TextView) rowView.findViewById(R.id.x_home_trans_row_cat);
				final TextView tAmt = (TextView) rowView.findViewById(R.id.x_home_trans_row_amt);
				final TextView tAcc = (TextView) rowView.findViewById(R.id.x_home_trans_row_acc);
				final TextView tInfo = (TextView) rowView.findViewById(R.id.x_home_trans_row_info);
				tInfo.setVisibility(View.GONE);

				if (selectedAccountID == ALL_ACCOUNT_ID) {
					tAcc.setVisibility(View.VISIBLE);
					tAcc.setText(t.getAccount().getName());
				} else {
					tAcc.setVisibility(View.GONE);
				}

				tCat.setText(t.getCategory().getName());
				tAmt.setText(t.getAmountString());

				if (t.getCategory().getType() == INCOME) {
					income += t.getAmount();
					income_trans_container.addView(rowView);
					countIncomeTrans++;
				} else {
					expense += t.getAmount();
					expense_trans_container.addView(rowView);
					countExpenseTrans++;
				}


			}


			income_trans_container_card.setVisibility((countIncomeTrans == 0)?View.GONE:View.VISIBLE);
			expense_trans_container_card.setVisibility((countExpenseTrans == 0)?View.GONE:View.VISIBLE);


			cardExpenseTextView.setText("Rs " + expense);
			cardIncomeTextView.setText("Rs " + income);
			cardTotalTextView.setText("Rs " + (income - expense));

			Log.i(mylog, "loader done loading");
		}
	}

}
