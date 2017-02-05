package com.moneymanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.moneymanager.R;
import com.moneymanager.entities.Debt;
import com.moneymanager.repo.TDebt;
import com.moneymanager.utilities.MyCalendar;

import java.util.Date;

import static com.moneymanager.Common.DEBT;
import static com.moneymanager.Common.LOAN;

/**
 * A simple {@link Fragment} subclass.
 */
public class FDebts extends Fragment {


	Date previousDebtDate;
	Debt[] debtArray;
	boolean debt;

	public FDebts() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		debt = getArguments().getBoolean("debt");

		refreshDebtArray();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.f_debts, container, false);

		ListView list = (ListView) rootView.findViewById(R.id.f_debt_listview);
		list.setAdapter(new DebtListAdapter(getContext(), debtArray));

		return rootView;
	}

	void refreshDebtArray() {

		TDebt tDebt = new TDebt(getContext());
		debtArray = null;
		debtArray = tDebt.getDebts(debt ? DEBT : LOAN);

		previousDebtDate = debtArray.length > 0 ? MyCalendar.dateBeforeDays(debtArray[0].getDate(), 2) : null;

	}

	class DebtListAdapter extends ArrayAdapter<Debt> {


		public DebtListAdapter(Context context, Debt[] objects) {

			super(context, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inf = getActivity().getLayoutInflater();
			View rowView = inf.inflate(R.layout.x_debt_row, null);
			LinearLayout dateLayout = (LinearLayout) rowView.findViewById(R.id.x_debt_date_layout);
			dateLayout.setVisibility(View.INVISIBLE);
//			LinearLayout lineLayout = (LinearLayout) rowView.findViewById(R.id.x_debt_line);
//			lineLayout.setVisibility(View.GONE);


			Debt debt = getItem(position);

			if (!debt.getDate().equals(previousDebtDate)) {

				dateLayout.setVisibility(View.VISIBLE);
//				if (position > 0) {
//					lineLayout.setVisibility(View.VISIBLE);
//				}

				TextView dateText = (TextView) dateLayout.findViewById(R.id.x_debt_date);
				TextView monthYearText = (TextView) dateLayout.findViewById(R.id.x_debt_month_year);

				dateText.setText(MyCalendar.dateToString(debt.getDate()));

				String monthYear = MyCalendar.monthToString(debt.getDate()) + ",\n" + MyCalendar.yearToString(debt.getDate());
				monthYearText.setText(monthYear);

			}

			previousDebtDate = debt.getDate();

			TextView userText = (TextView) rowView.findViewById(R.id.x_debt_user);
			TextView amtText = (TextView) rowView.findViewById(R.id.x_debt_amt);
			TextView accText = (TextView) rowView.findViewById(R.id.x_debt_acc);
			TextView infoText = (TextView) rowView.findViewById(R.id.x_debt_info);

			userText.setText(debt.getUser().getName());
			amtText.setText("Rs " + debt.getAmount());
			accText.setText(debt.getAccount().getName());
			infoText.setText(debt.getShortInfo());

			return rowView;
		}
	}

}
