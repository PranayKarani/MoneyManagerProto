package com.moneymanager.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.moneymanager.R

class FAddDebt : Fragment() {


	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {

		return inflater!!.inflate(R.layout.f_add_debt, container, false)
	}

}
