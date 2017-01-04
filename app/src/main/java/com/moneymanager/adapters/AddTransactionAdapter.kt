package com.moneymanager.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.moneymanager.fragments.FAddDebt
import com.moneymanager.fragments.FAddTransaction

class AddTransactionAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

	override fun getItem(position: Int): Fragment {

		if (position == 0)
			return FAddTransaction()
		else
			return FAddDebt()


	}

	override fun getCount(): Int {
		return 2
	}

	override fun getPageTitle(position: Int): CharSequence {

		if (position == 0) {
			return "Transaction"
		} else {
			return "Debt"
		}

	}
}