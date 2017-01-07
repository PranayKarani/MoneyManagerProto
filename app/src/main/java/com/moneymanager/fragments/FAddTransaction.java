package com.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.moneymanager.R;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Category;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TCategories;

import java.util.ArrayList;
import java.util.Calendar;

public class FAddTransaction extends Fragment {

	/** views */
	// income, expense toggle
	ToggleButton toggle;// isChecked - expense

	ArrayList<String> cat_name_list;
	ArrayList<Integer> cat_id_list;
	ArrayList<String> acc_name_list;
	ArrayList<Integer> acc_id_list;

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

		final TextView cat_text = (TextView) rootView.findViewById(R.id.add_trans_cat);
		cat_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// setting up listview
				final ListView listView = new ListView(getContext());
				final ArrayAdapter<String> arr_adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cat_name_list);
				listView.setAdapter(arr_adp);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Log.i("tag", "selected category id: " + cat_id_list.get(position));
					}
				});

				final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setView(listView);
				builder.setCancelable(true);
				builder.create().show();

			}
		});

		final TextView acc_text = (TextView) rootView.findViewById(R.id.add_trans_acc);
		// TODO set current account selected by default here
		acc_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// setting up listview
				final ListView listView = new ListView(getContext());
				final ArrayAdapter<String> arr_adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, acc_name_list);
				listView.setAdapter(arr_adp);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Log.i("tag", "selected account id: " + acc_id_list.get(position));
					}
				});

				final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setView(listView);
				builder.setCancelable(true);
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

        }

	}

}
