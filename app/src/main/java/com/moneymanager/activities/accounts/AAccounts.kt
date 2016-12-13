package com.moneymanager.activities.accounts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.moneymanager.R
import com.moneymanager.db.TAccounts
import com.moneymanager.entities.Account
import com.moneymanager.exceptions.NoAccountsException

class AAccounts : AppCompatActivity() {

    companion object {
        var noAccounts = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_accounts)

        // setup toolbar
        val toolbar = findViewById(R.id.accounts_toolbar) as Toolbar
        toolbar.title = "Accounts"
        setSupportActionBar(toolbar)

        // setting up fab
        val fab = findViewById(R.id.fab_add_account)
        fab.setOnClickListener {
            startActivity(Intent(this, AAddAccount::class.java))
        }


    }

    override fun onResume() {
        super.onResume()
        refreshAccountsList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.accounts_menu, menu)
        return true
    }

    override fun onBackPressed() {

        if (!noAccounts) {

            finish()

        }

    }

    private fun refreshAccountsList() {
        val accTable = TAccounts(this)
        try {
            val accounts = accTable.getAllAccounts(TAccounts.ID, null)
            noAccounts = false

            val accListView = findViewById(R.id.account_accounts_list) as ListView
            accListView.adapter = AccListAdapter(this, accounts, accTable)
            accListView.visibility = View.VISIBLE

            val accTxt = findViewById(R.id.accounts_text) as TextView
            accTxt.visibility = View.GONE

        } catch(e: NoAccountsException) {

            val accTxt = findViewById(R.id.accounts_text) as TextView
            accTxt.visibility = View.VISIBLE
            val accListView = findViewById(R.id.account_accounts_list) as ListView
            accListView.visibility = View.GONE


        }
    }

    class AccListAdapter(private val myContext: Activity, val accArr: Array<Account?>, private val accTable: TAccounts) : ArrayAdapter<Account>(myContext, -1, accArr) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            // inflate and setup the layout
            val inflator = myContext.layoutInflater
            val rowView: View
            if (convertView == null) {
                rowView = inflator.inflate(R.layout.x_account_view, parent, false)
            } else {
                rowView = convertView
            }


            // get the account and it's details
            val acc = getItem(position)
            val id = acc.id
            val name = acc.name
            val bal = acc.bal

            // fill up the layout elements
            val nametv = rowView.findViewById(R.id.account_view_name) as TextView
            nametv.text = name
            val baltv = rowView.findViewById(R.id.account_view_bal) as TextView
            baltv.text = bal.toString()

            // set click listener to edit button in each row
            val button = rowView.findViewById(R.id.account_view_edit_button) as ImageButton
            button.setOnClickListener {
                Toast.makeText(myContext, "edit account with ID = $id", Toast.LENGTH_LONG).show()
                accTable.removeAccount(id)

                // refresh account list
                val accTable = TAccounts(myContext)
                try {
                    val accounts = accTable.getAllAccounts(TAccounts.ID, null)
                    noAccounts = false

                    val accListView = myContext.findViewById(R.id.account_accounts_list) as ListView
                    accListView.adapter = AccListAdapter(myContext, accounts, accTable)


                } catch(e: NoAccountsException) {

                    noAccounts = true
                    val accTxt = myContext.findViewById(R.id.accounts_text) as TextView
                    accTxt.visibility = View.VISIBLE
                    val accListView = myContext.findViewById(R.id.account_accounts_list) as ListView
                    accListView.visibility = View.GONE


                }

            }

            return rowView

        }
    }

}
