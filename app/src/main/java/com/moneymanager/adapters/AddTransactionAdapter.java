package com.moneymanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.moneymanager.fragments.FAddDebt;
import com.moneymanager.fragments.FAddTransaction;

public class AddTransactionAdapter extends FragmentPagerAdapter {

	public AddTransactionAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int position) {

		if (position == 0) {
			return new FAddTransaction();
		} else {
			return new FAddDebt();
		}


	}

	public int getCount() {
		return 2;
	}

	public CharSequence getPageTitle(int position) {

		if (position == 0) {
			return "Transaction";
		} else {
			return "Debt";
		}

	}
}