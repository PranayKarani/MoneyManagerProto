// Created by pranay on 03/02/17.

package com.moneymanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.moneymanager.fragments.FDebts;
import com.moneymanager.fragments.FLoans;

public class DebtAdapter extends FragmentPagerAdapter {

	public DebtAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {

		if (position == 0) {
			return new FDebts();
		} else {
			return new FLoans();
		}

	}

	@Override
	public int getCount() {
		return 2;
	}

	public CharSequence getPageTitle(int position) {

		if (position == 0) {
			return "Debts";
		} else {
			return "Loans";
		}

	}

}
