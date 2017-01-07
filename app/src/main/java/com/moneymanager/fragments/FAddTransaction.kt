package com.moneymanager.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.moneymanager.R
import com.moneymanager.entities.Category
import com.moneymanager.repo.TAccounts
import com.moneymanager.repo.TCategories
import java.util.*

class FAddTransaction : Fragment() {

	/** views */
	// income, expense toggle
	var toggle: ToggleButton? = null // isChecked - expense

    var cat_name_list: ArrayList<String>? = null
    var cat_id_list: ArrayList<Int>? = null
    var acc_name_list: ArrayList<String>? = null
    var acc_id_list: ArrayList<Int>? = null


	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		val rootView = inflater?.inflate(R.layout.f_add_transaction, container, false)

		toggle = rootView?.findViewById(R.id.add_trans_type) as ToggleButton
		toggle?.setOnClickListener {
			updateCategoryList()
		}

        val cat_text = rootView?.findViewById(R.id.add_trans_cat) as TextView
        cat_text.setOnClickListener {

            // setting up listview
            val listView = ListView(context)
            val arr_adp = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cat_name_list)
            listView.adapter = arr_adp
            listView.setOnItemClickListener { adapterView, view, i, l ->

                Log.i("tag", "selected category id: " + cat_id_list?.get(i))

            }

            val builder = AlertDialog.Builder(context)
            builder.setView(listView)
            builder.setCancelable(true)
            builder.create().show()

        }

        val acc_text = rootView?.findViewById(R.id.add_trans_acc) as TextView
        // TODO set current account selected by default here
        acc_text.setOnClickListener {

            // setting up listview
            val listView = ListView(context)
            val arr_adp = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, acc_name_list)
            listView.adapter = arr_adp
            listView.setOnItemClickListener { adapterView, view, i, l ->

                Log.i("tag", "selected account id: " + acc_id_list?.get(i))

            }

            val builder = AlertDialog.Builder(context)
            builder.setView(listView)
            builder.setCancelable(true)
            builder.create().show()

        }

		return rootView

	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

        init()

    }

    /**
     * Initialization stuff goes here.
     * NOTE: No android.view.View init stuff should happen here
     */
    private fun init() {
        cat_name_list = ArrayList()
        cat_id_list = ArrayList()
        acc_name_list = ArrayList()
        acc_id_list = ArrayList()

        updateCategoryList()
        updateAccountsList()
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

        cat_name_list?.clear()
        cat_id_list?.clear()

		for (cat in cat_array) {
            cat_name_list?.add(cat?.name!!)
            cat_id_list?.add(cat?.id!!)
			Log.i("-----", cat?.name)
		}

    }

    // Account update is onlyed needed once and is done in onActivityCreated
    private fun updateAccountsList() {

        val acc = TAccounts(context)

        val acc_array = acc.getAllAccounts(null, null)

        acc_name_list?.clear()
        acc_id_list?.clear()

        for (a in acc_array) {
            acc_name_list?.add(a?.name!!)
            acc_id_list?.add(a?.id!!)
        }

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

}
