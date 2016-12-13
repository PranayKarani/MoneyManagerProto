package com.moneymanager.activities.accounts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.moneymanager.R
import com.moneymanager.db.TAccounts
import com.moneymanager.entities.Account
import com.moneymanager.setUpToolbar

class AAddAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_add_account)

        setUpToolbar(this, R.id.add_account_toolbar, "Add New Account")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.add_account_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return true
    }

    fun onInsertAccountClick(view: View) {

        val acc_name = findViewById(R.id.new_account_name) as EditText
        val acc_bal = findViewById(R.id.new_account_bal) as EditText
        val acc_ex = findViewById(R.id.new_account_exclude) as Switch

        val new_acc_name = acc_name.text.toString()
        val new_acc_bal = acc_bal.text.toString()
        val new_acc_ex = acc_ex.isChecked

        val accountTable = TAccounts(this)

        if (new_acc_name.isEmpty()) {
            acc_name.error = "Name cannot be empty"
        } else if (new_acc_bal.isEmpty()) {
            acc_bal.error = "Enter starting balance"
        } else {
            if (accountTable.insertNewAccount(Account(-1, new_acc_name, new_acc_bal.toDouble(), new_acc_ex)) > 1) {
                Toast.makeText(this, "New Account $new_acc_name created!", Toast.LENGTH_LONG).show()
                AAccounts.noAccounts = false
            } else {
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_LONG).show()
            }
            finish()
        }


    }

}
