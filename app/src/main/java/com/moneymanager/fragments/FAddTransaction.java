package com.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TCategories;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.moneymanager.Common.*;

public class FAddTransaction extends Fragment {

	/**
	 * views
	 */
	// income, expense toggle
	private ToggleButton toggle;// isChecked - expense

	private ArrayList<String> cat_name_list;
	private ArrayList<Integer> cat_id_list;
	private ArrayList<String> acc_name_list;
	private ArrayList<Integer> acc_id_list;

	private int selectedCategoryID = -1;
	private int selectedAccountID = -1;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.f_add_transaction, container, false);

		toggle = (ToggleButton) rootView.findViewById(R.id.add_trans_type);
		toggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateCategoryList();
			}
		});

		selectedAccountID = CURRENT_ACCOUNT_ID;
		((OnAccountSelectListener) getActivity()).updateAccountId(selectedAccountID);

		final TextView cat_text = (TextView) rootView.findViewById(R.id.add_trans_cat);
		cat_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String[] names = new String[cat_name_list.size()];
				for (int i = 0; i < names.length; i++) {
					names[i] = cat_name_list.get(i);
				}

				final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setCancelable(true);
				builder.setTitle("Select a Category");
				builder.setItems(names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						selectedCategoryID = cat_id_list.get(i);
						((OnCategorySelectListener) getActivity()).updateCategoryId(selectedCategoryID);
						cat_text.setText("category: " + names[i]);
						Log.i(mylog, "selected category id: " + selectedCategoryID);
						dialogInterface.dismiss();
					}
				});
				builder.create().show();


			}
		});

		final TextView acc_text = (TextView) rootView.findViewById(R.id.add_trans_acc);
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

				final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setCancelable(true);
				builder.setTitle("Select an Account");
				builder.setItems(names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						selectedAccountID = acc_id_list.get(i);
						((OnAccountSelectListener) getActivity()).updateAccountId(selectedAccountID);
						acc_text.setText("account: " + names[i]);
						Log.i(mylog, "selected account id: " + selectedAccountID);
						dialogInterface.dismiss();
					}
				});
				builder.create().show();


			}
		});

		return rootView;

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	/**
	 * Initialization stuff goes here.
	 * NOTE: No android.view.View init stuff should happen here
	 */
	private void init() {
		cat_name_list = new ArrayList<>();
		cat_id_list = new ArrayList<>();
		acc_name_list = new ArrayList<>();
		acc_id_list = new ArrayList<>();

		updateCategoryList();
		updateAccountsList();
	}

	// update Category list according to 'income' 'expense' selection
	private void updateCategoryList() {

		final boolean income = !toggle.isChecked();

		final TCategories cats = new TCategories(getContext());

		Category[] cat_array;

		if (income) {
			cat_array = cats.getTypeSpecificCategories(TCategories.INCOME);
		} else {
			cat_array = cats.getTypeSpecificCategories(TCategories.EXPENSE);
		}

		cat_name_list.clear();
		cat_id_list.clear();

		for (Category cat : cat_array) {
			cat_name_list.add(cat.getName());
			cat_id_list.add(cat.getId());
		}

	}

	// Account update is onlyed needed once and is done in onActivityCreated
	private void updateAccountsList() {

		final TAccounts acc = new TAccounts(getContext());

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

	public interface OnCategorySelectListener {
		void updateCategoryId(int categoryID);
	}

	public interface OnAccountSelectListener {
		void updateAccountId(int accountId);
	}

	public interface OnDateSelectListener {
		void updateDate(Date date);
	}

	public static class TimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, y, m, d);
		}

		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

			final TextView text = (TextView) getActivity().findViewById(R.id.add_trans_date);
			text.setText("Day: " + dayOfMonth + "\nMonth: " + month + "\nYear: " + year);

			((OnDateSelectListener) getActivity()).updateDate(new Date(year, month, dayOfMonth));

		}

	}

}
