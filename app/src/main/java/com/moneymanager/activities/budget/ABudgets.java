package com.moneymanager.activities.budget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.moneymanager.R;
import com.moneymanager.entities.Budget;
import com.moneymanager.repo.TBudget;

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


	}

	@Override
	protected void onResume() {
		super.onResume();

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
			View rowView = inf.inflate(R.layout.x_budget_row, null);


			return rowView;
		}
	}

}
