package com.moneymanager.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.moneymanager.R;

public class FHomePage extends Fragment {

	private int position;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt("pos");
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle bundle) {

		View root = inflater.inflate(R.layout.f_home_page, container, false);
		final TextView text = (TextView) root.findViewById(R.id.crap);
		text.setText(position + "");

		return root;
	}

}
