package com.moneymanager.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.moneymanager.R

class ASearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_search)

        val search_toolbar = findViewById(R.id.search_toolbar) as Toolbar
        search_toolbar.title = "Search"
        setSupportActionBar(search_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}
