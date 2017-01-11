package com.moneymanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.moneymanager.Common;
import com.moneymanager.R;
import com.moneymanager.entities.Transaction;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.moneymanager.Common.*;

public class FHomePage extends Fragment {

	private int position;
	private Date myDate; // date for this page
	private SimpleDateFormat sdf;
	private String title;
	private String overviewCardTitle;
	private TTransactions tTransactions;
	private Transaction[] myTransactions; // transactions for this page
	private ListView transListView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sdf = MyCalendar.getSimpleDateFormat();
		position = getArguments().getInt("pos");
		try {
			myDate = sdf.parse(getArguments().getString("date"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		title = getArguments().getString("title");
		overviewCardTitle = MyCalendar.dateToString(myDate) + " " + MyCalendar.monthToFullString(myDate) + "'s Overview";

		tTransactions = new TTransactions(getContext());
		if (CURRENT_ACCOUNT_ID == ALL_ACCOUNT_ID) {
			myTransactions = tTransactions.getTransactionsForDay(myDate);
		} else {
			myTransactions = tTransactions.getAccountSpecificTransactionsForDay(CURRENT_ACCOUNT_ID, myDate);
		}

		Log.i(mylog, "homepage fragment created for position " + position);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle bundle) {


		Log.i(mylog, MyCalendar.getNiceFormatedCompleteDateString(myDate));

		final View root = inflater.inflate(R.layout.f_home_page, container, false);

		// set overview card's title
		final TextView text = (TextView) root.findViewById(R.id.f_home_overview_card_title);
		text.setText(overviewCardTitle);

		// set income text

		// set expense text

		// set total text

		// list view
		transListView = (ListView) root.findViewById(R.id.f_home_trans_list);
		final TransListAdapter tla = new TransListAdapter(getContext(), myTransactions);
		transListView.setAdapter(tla);

		return root;
	}

	public void refreshTransList(int currentAccount) {

		if (currentAccount == ALL_ACCOUNT_ID) {
			myTransactions = tTransactions.getTransactionsForDay(myDate);
		} else {
			myTransactions = tTransactions.getAccountSpecificTransactionsForDay(CURRENT_ACCOUNT_ID, myDate);
		}
		final TransListAdapter tla = new TransListAdapter(getContext(), myTransactions);
		transListView.setAdapter(tla);

	}

	class TransListAdapter extends ArrayAdapter<Transaction> {


		public TransListAdapter(Context context, Transaction[] objects) {
			super(context, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final LayoutInflater inflater = getActivity().getLayoutInflater();

			final View rowView = inflater.inflate(R.layout.x_home_trans_row, parent, false);

			final TextView tCat = (TextView) rowView.findViewById(R.id.x_home_trans_row_cat);
			final TextView tAmt = (TextView) rowView.findViewById(R.id.x_home_trans_row_amt);
			final TextView tInfo = (TextView) rowView.findViewById(R.id.x_home_trans_row_info);

			final Transaction transaction = getItem(position);
			final int catType = transaction.getCategory().getType();// 0 - income, 1- expense

			if (catType == Common.INCOME) {
				tAmt.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
			} else {
				tAmt.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
			}

			tCat.setText(transaction.getCategory().getName());
			tAmt.setText(transaction.getAmountString());
			tInfo.setText(transaction.getShortInfo());

			return rowView;
		}
	}


}
