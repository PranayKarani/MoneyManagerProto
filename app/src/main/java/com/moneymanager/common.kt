package com.moneymanager

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

// Created by PranayKarani on 13-12-2016.
fun setUpToolbar(activity: AppCompatActivity, id: Int, title: String) {

    val toolbar = activity.findViewById(id) as Toolbar
    toolbar.title = title
    activity.setSupportActionBar(toolbar)

}

val mylog = "mylog"
