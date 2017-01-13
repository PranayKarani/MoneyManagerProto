package com.moneymanager.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.moneymanager.R;
import com.moneymanager.entities.Category;
import com.moneymanager.repo.TCategories;

import static com.moneymanager.Common.EXPENSE;
import static com.moneymanager.Common.INCOME;

public class FCategoryList extends Fragment {

	private Category[] myCategories;
	private String[] myCategoriesNameArray;
	private int position;

	private ListView listView;

	public FCategoryList() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt("pos");

		refreshCatArrays();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.f_category_list, container, false);

		listView = (ListView) rootView.findViewById(R.id.f_category_listview);
		ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, myCategoriesNameArray);
		listView.setAdapter(adp);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getContext(), myCategories[position].toString(), Toast.LENGTH_LONG).show();
			}
		});

		return rootView;
	}

	public void refreshCatList() {

		refreshCatArrays();
		listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, myCategoriesNameArray));

	}

	private void refreshCatArrays() {

		TCategories tc = new TCategories(getContext());
		// 0- expense, 1- income
		myCategories = null;
		myCategories = tc.getTypeSpecificCategories(position == 0 ? EXPENSE : INCOME);

		myCategoriesNameArray = null;
		myCategoriesNameArray = Category.extractNameStringArrayFromArray(myCategories);

	}

}
