package com.moneymanager.activities.accounts;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.moneymanager.Common;
import com.moneymanager.R;
import com.moneymanager.activities.MyBaseActivity;
import com.moneymanager.entities.Debt;
import com.moneymanager.entities.Transaction;
import com.moneymanager.repo.TDebt;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import static com.moneymanager.Common.EXPENSE;
import static com.moneymanager.Common.setupToolbar;

public class ALedger extends MyBaseActivity {

	private final int ALL = 23;
	private final int TRANS = 24;
	private final int DEBT = 25;
	private final int TRANSFER = 26;

	private MenuItem type_text;
	private int selectedType = ALL;

	private ListView listView;

	private int selectedAccountId = -1;

	private LinearLayout trans_container, debts_container, transfers_container;

	private double netTransIncome = 0;
	private double netTransExpense = 0;
	private double netTrans = 0;

	private double netDebtAmount = 0;
	private double netLoanAmount = 0;
	private double netDebt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_ledger);

		setupToolbar(this, R.id.a_ledger_toolbar, "Ledger");

		selectedAccountId = getIntent().getIntExtra("acc_id", -1);

		trans_container = (LinearLayout) findViewById(R.id.a_ledger_transactions_container);
		debts_container = (LinearLayout) findViewById(R.id.a_ledger_debts_container);
		transfers_container = (LinearLayout) findViewById(R.id.a_ledger_transfers_container);

		trans_container.setVisibility(View.GONE);
		debts_container.setVisibility(View.GONE);
		transfers_container.setVisibility(View.GONE);

		new TransactionsLedgerLoader().execute();
		new DebtLedgerLoader().execute();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ledger_menu, menu);
		type_text = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		trans_container.setVisibility(View.GONE);
		debts_container.setVisibility(View.GONE);
		transfers_container.setVisibility(View.GONE);

		netTrans = 0;
		netTransIncome = 0;
		netTransExpense = 0;

		netDebtAmount = 0;
		netLoanAmount = 0;
		netDebt = 0;

		switch (item.getItemId()) {

			case R.id.ledger_menu_trans:
				type_text.setTitle("Transactions");
				selectedType = TRANS;
				new TransactionsLedgerLoader().execute();
				break;
			case R.id.ledger_menu_debts:
				type_text.setTitle("Debts & Loans");
				selectedType = DEBT;
				new DebtLedgerLoader().execute();
				break;
			case R.id.ledger_menu_transfers:
				type_text.setTitle("Transfers");
				selectedType = TRANSFER;
				break;
			default: // custom
				type_text.setTitle("All");
				selectedType = ALL;
				new TransactionsLedgerLoader().execute();
				new DebtLedgerLoader().execute();
				break;
		}

		return true;
	}

	class TransactionsLedgerLoader extends AsyncTask<Void, Void, Transaction[]> {

		TextView headerText, helperText;
		LinearLayout listLayout;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			trans_container.setVisibility(View.VISIBLE);
			headerText = (TextView) trans_container.findViewById(R.id.a_ledger_transactions_header);
			headerText.setVisibility(View.GONE);
			helperText = (TextView) trans_container.findViewById(R.id.a_ledger_transactions_helper_text);
			helperText.setVisibility(View.VISIBLE);
			listLayout = (LinearLayout) trans_container.findViewById(R.id.a_ledger_transactions_list);
			listLayout.setVisibility(View.GONE);
		}

		@Override
		protected Transaction[] doInBackground(Void... params) {

			TTransactions tTransactions = new TTransactions(ALedger.this);
			return tTransactions.getAccountSpecificTransactions(selectedAccountId);

		}

		@Override
		protected void onPostExecute(Transaction[] transactions) {
			super.onPostExecute(transactions);

			headerText.setVisibility(View.VISIBLE);

			if (transactions.length == 0) {
				headerText.setText("No Transactions found");
				return;
			}

			listLayout.removeAllViews();

			final View row = getLayoutInflater().inflate(R.layout.x_ledger_transaction, null);
			final TextView date_text = (TextView) row.findViewById(R.id.x_ledger_trans_date);
			date_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			final TextView cat_Text = (TextView) row.findViewById(R.id.x_ledger_trans_cat);
			cat_Text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			final TextView cre_text = (TextView) row.findViewById(R.id.x_ledger_trans_credit);
			cre_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			cre_text.setTextColor(Common.getMyColor(ALedger.this, R.color.colorGreen));
			final TextView deb_text = (TextView) row.findViewById(R.id.x_ledger_trans_debit);
			deb_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			deb_text.setTextColor(Common.getMyColor(ALedger.this, R.color.colorRed));
			listLayout.addView(row);

			for (Transaction transaction : transactions) {

				final View transRow = getLayoutInflater().inflate(R.layout.x_ledger_transaction, null);
				final TextView dateText = (TextView) transRow.findViewById(R.id.x_ledger_trans_date);
				final TextView catText = (TextView) transRow.findViewById(R.id.x_ledger_trans_cat);
				final TextView creText = (TextView) transRow.findViewById(R.id.x_ledger_trans_credit);
				final TextView debText = (TextView) transRow.findViewById(R.id.x_ledger_trans_debit);

				dateText.setText(MyCalendar.getShortDateString(transaction.getDateTime()));
				catText.setText(transaction.getCategory().getName());
				if (transaction.getCategory().getType() == EXPENSE) {
					creText.setText("");
					debText.setText(String.valueOf(transaction.getAmount()));
					netTransExpense += transaction.getAmount();
				} else {
					debText.setText("");
					creText.setText(String.valueOf(transaction.getAmount()));
					netTransIncome += transaction.getAmount();
				}
				listLayout.addView(transRow);

			}


			// Show Total
			final View totalRow = getLayoutInflater().inflate(R.layout.x_ledger_transaction, null);
			final TextView dateText = (TextView) totalRow.findViewById(R.id.x_ledger_trans_date);
			dateText.setVisibility(View.INVISIBLE);
			final TextView catText = (TextView) totalRow.findViewById(R.id.x_ledger_trans_cat);
			catText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			catText.setText("Total");
			final TextView creText = (TextView) totalRow.findViewById(R.id.x_ledger_trans_credit);
			creText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			creText.setText(String.valueOf(netTransIncome));
			creText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorGreen));
			final TextView debText = (TextView) totalRow.findViewById(R.id.x_ledger_trans_debit);
			debText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			debText.setText(String.valueOf(netTransExpense));
			debText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorRed));
			listLayout.addView(totalRow);

			// Show Net
			netTrans = netTransIncome - netTransExpense;

			final View netRow = getLayoutInflater().inflate(R.layout.x_ledger_transaction, null);
			final TextView netDateText = (TextView) netRow.findViewById(R.id.x_ledger_trans_date);
			netDateText.setVisibility(View.INVISIBLE);
			final TextView netCatText = (TextView) netRow.findViewById(R.id.x_ledger_trans_cat);
			netCatText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netCatText.setText("Transactions Net");
			netCatText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			final TextView netCreText = (TextView) netRow.findViewById(R.id.x_ledger_trans_credit);
			netCreText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netCreText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			netCreText.setText("=");
			final TextView netDebText = (TextView) netRow.findViewById(R.id.x_ledger_trans_debit);
			netDebText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netDebText.setText(String.valueOf(netTrans));
			netDebText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			listLayout.addView(netRow);

			helperText.setVisibility(View.GONE);
			listLayout.setVisibility(View.VISIBLE);

		}
	}

	class DebtLedgerLoader extends AsyncTask<Void, Void, Debt[]> {

		TextView headerText, helperText;
		LinearLayout listLayout;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			debts_container.setVisibility(View.VISIBLE);
			headerText = (TextView) debts_container.findViewById(R.id.a_ledger_debt_header);
			headerText.setVisibility(View.GONE);
			helperText = (TextView) debts_container.findViewById(R.id.a_ledger_debt_helper_text);
			helperText.setVisibility(View.VISIBLE);
			listLayout = (LinearLayout) debts_container.findViewById(R.id.a_ledger_debt_list);
			listLayout.setVisibility(View.GONE);
		}

		@Override
		protected Debt[] doInBackground(Void... params) {

			TDebt tDebt = new TDebt(ALedger.this);
			return tDebt.getAccountSpecificDebts(selectedAccountId);

		}

		@Override
		protected void onPostExecute(Debt[] debts) {
			super.onPostExecute(debts);

			headerText.setVisibility(View.VISIBLE);

			if (debts.length == 0) {
				headerText.setText("No Debts or Loans found");
				helperText.setVisibility(View.GONE);
				return;
			}

			listLayout.removeAllViews();

			final View row = getLayoutInflater().inflate(R.layout.x_ledger_debt, null);
			final TextView date_text = (TextView) row.findViewById(R.id.x_ledger_debt_date);
			date_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			final TextView user_Text = (TextView) row.findViewById(R.id.x_ledger_debt_user);
			user_Text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			final TextView type_Text = (TextView) row.findViewById(R.id.x_ledger_debt_type);
			type_Text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			final TextView cre_text = (TextView) row.findViewById(R.id.x_ledger_debt_credit);
			cre_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			cre_text.setTextColor(Common.getMyColor(ALedger.this, R.color.colorGreen));
			final TextView deb_text = (TextView) row.findViewById(R.id.x_ledger_debt_debit);
			deb_text.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			deb_text.setTextColor(Common.getMyColor(ALedger.this, R.color.colorRed));
			listLayout.addView(row);

			for (Debt debt : debts) {

				final View transRow = getLayoutInflater().inflate(R.layout.x_ledger_debt, null);
				final TextView dateText = (TextView) transRow.findViewById(R.id.x_ledger_debt_date);
				final TextView userText = (TextView) transRow.findViewById(R.id.x_ledger_debt_user);
				final TextView typeText = (TextView) transRow.findViewById(R.id.x_ledger_debt_type);
				final TextView creText = (TextView) transRow.findViewById(R.id.x_ledger_debt_credit);
				final TextView debText = (TextView) transRow.findViewById(R.id.x_ledger_debt_debit);

				dateText.setText(MyCalendar.getShortDateString(debt.getDate()));
				userText.setText(debt.getUser().getName());

				if (debt.getType() == Common.DEBT) {
					typeText.setText("Debt");
					creText.setText("");
					debText.setText(String.valueOf(debt.getAmount()));
					netDebtAmount += debt.getAmount();
				} else {
					typeText.setText("Loan");
					debText.setText("");
					creText.setText(String.valueOf(debt.getAmount()));
					netLoanAmount += debt.getAmount();
				}
				listLayout.addView(transRow);

			}


			// Show Total
			final View totalRow = getLayoutInflater().inflate(R.layout.x_ledger_debt, null);
			final TextView dateText = (TextView) totalRow.findViewById(R.id.x_ledger_debt_date);
			dateText.setVisibility(View.INVISIBLE);
			final TextView catText = (TextView) totalRow.findViewById(R.id.x_ledger_debt_user);
			catText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			catText.setText("Total");
			final TextView typeText = (TextView) totalRow.findViewById(R.id.x_ledger_debt_type);
			typeText.setVisibility(View.INVISIBLE);
			final TextView creText = (TextView) totalRow.findViewById(R.id.x_ledger_debt_credit);
			creText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			creText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorGreen));
			creText.setText(String.valueOf(netLoanAmount));
			final TextView debText = (TextView) totalRow.findViewById(R.id.x_ledger_debt_debit);
			debText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			debText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorRed));
			debText.setText(String.valueOf(netDebtAmount));
			listLayout.addView(totalRow);

			// Show Net
			netDebt = netLoanAmount - netDebtAmount;

			final View netRow = getLayoutInflater().inflate(R.layout.x_ledger_debt, null);
			final TextView netDateText = (TextView) netRow.findViewById(R.id.x_ledger_debt_date);
			netDateText.setVisibility(View.INVISIBLE);
			final TextView netUserText = (TextView) netRow.findViewById(R.id.x_ledger_debt_user);
			netUserText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netUserText.setText("Net Debts & Loans");
			netUserText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			final TextView netCatText = (TextView) netRow.findViewById(R.id.x_ledger_debt_type);
			netCatText.setVisibility(View.INVISIBLE);
			final TextView netCreText = (TextView) netRow.findViewById(R.id.x_ledger_debt_credit);
			netCreText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netCreText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			netCreText.setText("=");
			final TextView netDebText = (TextView) netRow.findViewById(R.id.x_ledger_debt_debit);
			netDebText.setTypeface(date_text.getTypeface(), Typeface.BOLD);
			netDebText.setText(String.valueOf(netDebt));
			netDebText.setTextColor(Common.getMyColor(ALedger.this, R.color.colorBlue));
			listLayout.addView(netRow);

			helperText.setVisibility(View.GONE);
			listLayout.setVisibility(View.VISIBLE);

		}
	}

}
