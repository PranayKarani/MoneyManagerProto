package com.moneymanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moneymanager.R;

public class FHomePage extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle bundle) {

		final int pos = bundle.getInt("pos");

		// alter the layout according to the pos

		return inflater.inflate(R.layout.f_home_page, container, false);
	}

}
