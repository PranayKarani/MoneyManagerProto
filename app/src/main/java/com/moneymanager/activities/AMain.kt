package com.moneymanager.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.moneymanager.R
import com.moneymanager.activities.accounts.AAccounts
import com.moneymanager.db.TAccounts
import com.moneymanager.exceptions.NoAccountsException

class AMain : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        val home_toolbar = findViewById(R.id.home_toolbar) as Toolbar
        setSupportActionBar(home_toolbar)

        // check if accounts exists else redirect to accounts page
        val accTable = TAccounts(this)
        try {
            val accounts = accTable.getAllAccounts(null, null)


        } catch(e: NoAccountsException) {
            startActivity(Intent(this, AAccounts::class.java))
        }


        // set up fab button to add new transaction
        val fab = findViewById(R.id.fab_add_transaction) as FloatingActionButton
        fab.setOnClickListener {
            startActivity(Intent(this, ATransaction::class.java))
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.home_menu_search -> {

                startActivity(Intent(this, ASearch::class.java))

                return true
            }

            R.id.home_menu_add_account -> {

                startActivity(Intent(this, AAccounts::class.java))

                return true

            }

            else -> {
                return super.onOptionsItemSelected(item)
            }

        }
    }

}
