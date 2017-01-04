package com.moneymanager.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.moneymanager.fragments.FHomePage

class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

	override fun getItem(position: Int): Fragment {

		val args = Bundle()
		args.putInt("pos", position)

		val frag = FHomePage()
		frag.arguments = args

		return frag

	}

	override fun getCount(): Int {
		return -1
	}
}