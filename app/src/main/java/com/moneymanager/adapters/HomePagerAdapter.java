package com.moneymanager.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.moneymanager.fragments.FHomePage;

import java.util.Calendar;
import java.util.Date;

public class HomePagerAdapter extends FragmentPagerAdapter {

	public HomePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int position) {

		final Bundle args = new Bundle();
		args.putInt("pos", position);

		final Fragment frag = new FHomePage();
		frag.setArguments(args);

		return frag;

	}

	@Override
	public CharSequence getPageTitle(int position) {

		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		int today = date.getDate();

		if (position == 6) {
			return "Today";
		} else if (position == 5) {
			return "Yesterday";
		} else {

			return (today - (6 - position)) + "th";

		}

	}

	public int getCount() {
		return 7; // because week
	}
}