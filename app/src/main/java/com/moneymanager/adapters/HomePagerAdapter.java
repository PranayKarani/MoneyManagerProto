package com.moneymanager.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.moneymanager.fragments.FHomePage;

class HomePagerAdapter extends FragmentPagerAdapter {

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

	public int getCount() {
		return -1;
	}
}