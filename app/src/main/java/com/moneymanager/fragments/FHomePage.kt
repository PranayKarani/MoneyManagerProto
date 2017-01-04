package com.moneymanager.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.moneymanager.R

class FHomePage : Fragment() {

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  bundle: Bundle?): View? {

		val pos = bundle?.getInt("pos")

		// alter the layout according to the pos

		return inflater?.inflate(R.layout.f_home_page, container, false)
	}

}
