package com.moneymanager.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.moneymanager.R
import com.moneymanager.entities.Category
import com.moneymanager.repo.TCategories
import java.util.*

class FAddTransaction : Fragment() {

	/** views */
	// income, expense toggle
	var toggle: ToggleButton? = null // isChecked - expense
	var autoCompView: AutoCompleteTextView? = null

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		val rootView = inflater?.inflate(R.layout.f_add_transaction, container, false)

		toggle = rootView?.findViewById(R.id.add_trans_type) as ToggleButton
		toggle?.setOnClickListener {
			updateCategoryList()
		}

		autoCompView = rootView?.findViewById(R.id.add_trans_cat) as AutoCompleteTextView

		return rootView

	}

	class TimePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

		override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
			val cal = Calendar.getInstance()
			val y = cal.get(Calendar.YEAR)
			val m = cal.get(Calendar.MONTH)
			val d = cal.get(Calendar.DAY_OF_MONTH)
			return DatePickerDialog(activity, this, y, m, d)
		}

		override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

			val text = activity.findViewById(R.id.add_trans_date) as TextView
			text.text = "Day: $dayOfMonth\nMonth: $month\nYear: $year"

		}

	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		updateCategoryList()

	}

	// update Category list according to 'income' 'expense' selection
	private fun updateCategoryList() {

		val income = !toggle?.isChecked!!

		val cats = TCategories(context)

		val cat_array: Array<Category?>

		if (income) {
			cat_array = cats.getCategories(TCategories.INCOME)
		} else {
			cat_array = cats.getCategories(TCategories.EXPENSE)
		}

		val cat_str_arr = ArrayList<String>()

		for (cat in cat_array) {
			cat_str_arr.add(cat?.name!!)
			Log.i("-----", cat?.name)
		}

		val arr_adp = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cat_str_arr)
		autoCompView?.setAdapter(arr_adp)

		autoCompView?.showDropDown()
	}

}
