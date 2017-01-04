package com.moneymanager.activities.transaction

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.moneymanager.R
import com.moneymanager.adapters.AddTransactionAdapter
import com.moneymanager.entities.Transaction
import com.moneymanager.fragments.FAddTransaction
import com.moneymanager.repo.TAccounts
import com.moneymanager.repo.TCategories
import com.moneymanager.repo.TTransactions
import com.moneymanager.setUpToolbar
import java.util.*

class AAddTransaction : AppCompatActivity() {

	var viewPager: ViewPager? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.a_add_transaction)

		setUpToolbar(this, R.id.transaction_toolbar, "Add a Transaction")

		// setting up ViewPager Stuff
		val ta = AddTransactionAdapter(supportFragmentManager)

		viewPager = findViewById(R.id.transaction_viewpager) as ViewPager
		viewPager?.adapter = ta

	}

	fun OnSetDateClick(view: View) {

		val timePickerFragment = FAddTransaction.TimePickerFragment()
		timePickerFragment.show(supportFragmentManager, "Pick a Date")

	}

	private fun getNewTransaction(): Transaction {

		val cat_table = TCategories(this)
		val acc_table = TAccounts(this)

		// amount
		val amt = (findViewById(R.id.add_trans_amt) as TextView).text
		// category
		val cat = cat_table.getCategory(1)// for testing purpose, all get Category with id = 1
		// Account
		val acc = acc_table.getAccount(Random().nextInt(3) + 1)// for testing purpose, select random account between IDs 1 and 2

		// info
		val info = (findViewById(R.id.add_trans_info) as TextView).text

		// date
		val cal = java.util.Calendar.getInstance()
		val date = Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

		// exclude
		val ex = (findViewById(R.id.add_trans_ex) as Switch).isActivated

		return Transaction(-1, amt.toString().toDouble(), cat, acc, info.toString(), date, ex)

	}

	/**
	 * Toolbar Menu Stuff
	 */
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.add_trans_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		// since only one item in menu
		// no need for switch case

		// whether to insert transaction or debt
		when (viewPager?.currentItem) {
			0 -> {
				// insert new transaction data into database
				val trans_table = TTransactions(this)
				trans_table.insertNewTransaction(getNewTransaction())
			}
			1 -> Toast.makeText(this, "debt insert coming soon", Toast.LENGTH_SHORT).show()
		}


		return true
	}

}
