package com.moneymanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moneymanager.R;

public class FAddDebt extends Fragment {

	public FAddDebt() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.f_add_debt, container, false);
	}

}
