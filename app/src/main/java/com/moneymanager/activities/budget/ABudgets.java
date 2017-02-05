package com.moneymanager.activities.budget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.moneymanager.Common;
import com.moneymanager.R;
import com.moneymanager.activities.transaction.AEditTransaction;
import com.moneymanager.entities.Budget;
import com.moneymanager.entities.Transaction;
import com.moneymanager.repo.TBudget;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import java.util.Date;

import static com.moneymanager.Common.getMyColor;
import static com.moneymanager.Common.setupToolbar;

public class ABudgets extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_budgets);

		setupToolbar(this, R.id.a_budget_toolbar, "Budgets");

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_budget);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ABudgets.this, AAddBudget.class));
			}
		});

		// TODO remove budgets that have past their deadline

	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshBudgetList();


	}

	private void refreshBudgetList() {
		Budget[] budgets = getBudgets();

		ListView budgetList = (ListView) findViewById(R.id.a_budget_list);
		TextView textView = (TextView) findViewById(R.id.a_budget_no_budget_text);

		if (budgets.length > 0) {

			budgetList.setVisibility(View.VISIBLE);
			textView.setVisibility(View.GONE);

			budgetList.setAdapter(new BudgetListAdapter(this, budgets));
		} else {

			budgetList.setVisibility(View.GONE);
			textView.setVisibility(View.VISIBLE);

		}
	}

	private Budget[] getBudgets() {

		TBudget tBudget = new TBudget(this);
		return tBudget.getAllBudgets();
	}

	class BudgetListAdapter extends ArrayAdapter<Budget> {


		public BudgetListAdapter(Context context, Budget[] objects) {

			super(context, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inf = ABudgets.this.getLayoutInflater();
			final View rowView = inf.inflate(R.layout.x_budget_row, null);

			final Budget budget = getItem(position);
			final Transaction[] trans = new TTransactions(ABudgets.this).getBudgetSpecificTransactions(budget);

			final double set = budget.getAmount();
			double spent = 0;
			for (Transaction t : trans) {
				spent += t.getAmount();
			}

			double rem = 0;
			rem = set - spent;

			ImageView delImg = (ImageView) rowView.findViewById(R.id.x_budget_row_del);
			delImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Snackbar sb = Snackbar.make(findViewById(R.id.a_budgets),
							"Delete this Budget?",
							Snackbar.LENGTH_SHORT)
							.setAction("Yes", new View.OnClickListener() {
								@Override
								public void onClick(View v) {

									TBudget tBudget = new TBudget(ABudgets.this);
									tBudget.removeBudget(budget.getId());

									refreshBudgetList();

								}
							});
					View sbv = sb.getView();
					sbv.setBackgroundColor(getMyColor(ABudgets.this, R.color.colorRed));
					sb.setActionTextColor(getMyColor(ABudgets.this, R.color.colorPrimaryDark));
					sb.show();
				}
			});

			final TextView catText = (TextView) rowView.findViewById(R.id.x_budget_row_cat);
			final TextView setAmtText = (TextView) rowView.findViewById(R.id.x_budget_row_amt_set);
			final TextView spentAmtText = (TextView) rowView.findViewById(R.id.x_budget_row_amt_spent);
			final TextView remAmtText = (TextView) rowView.findViewById(R.id.x_budget_row_amt_rem);
			final TextView remText = (TextView) rowView.findViewById(R.id.x_budget_row_rem_text);

			final TextView periodText = (TextView) rowView.findViewById(R.id.x_budget_row_period);
			final Date startDate = budget.getStartDate();
			final Date endDate = MyCalendar.dateAfterDays(startDate, budget.getPeriod());
			final String period = budget.getPeriod() + " days   ( "
					+ MyCalendar.getNiceFormatedCompleteDateString(startDate) + "  ~  " +
					MyCalendar.getNiceFormatedCompleteDateString(endDate) + " )";
			periodText.setText(period);

			final ProgressBar pb = (ProgressBar) rowView.findViewById(R.id.x_budget_row_progressBar);
			pb.setMax((int) set);
			pb.setProgress((int) spent);

			catText.setText(budget.getCategory().getName() + "'s budget for " + budget.getAccount().getName());
			setAmtText.setText("Rs " + set);

			if (spent > set) {
				spentAmtText.setTextColor(Common.getMyColor(ABudgets.this, R.color.colorRed));
				spentAmtText.setText("Rs " + spent);
				remText.setTextColor(Common.getMyColor(ABudgets.this, R.color.colorRed));
				remText.setText("Overspent");
				remAmtText.setTextColor(Common.getMyColor(ABudgets.this, R.color.colorRed));
				remAmtText.setText("by Rs " + Math.abs(rem));
			} else {
				spentAmtText.setText("Rs " + spent);
				remAmtText.setText("Rs " + rem);
			}

			rowView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String[] trnStrs = new String[trans.length];


					int i = 0;
					for (Transaction transaction : trans) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder
								.append(transaction.getAmountString())
								.append(" on ")
								.append(MyCalendar.getNiceFormatedCompleteDateString(transaction.getDateTime()));
						trnStrs[i] = stringBuilder.toString();
						i++;
					}

					final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					builder.setCancelable(true);
					builder.setTitle("Transactions");
					builder.setItems(trnStrs, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent intent = new Intent(getContext(), AEditTransaction.class);
							intent.putExtra("trans_id", trans[i].getId());
							startActivity(intent);

						}

					});
					builder.setPositiveButton("dismiss", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();

				}
			});

			return rowView;
		}
	}

}
