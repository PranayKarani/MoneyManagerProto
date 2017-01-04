package com.moneymanager.entities

import java.util.*

/**
 * Created by pranay on 16/12/16.
 */
class Transaction(val id: Int, val amt: Double, val cat: Category, val acc: Account, val info: String, val dateTime: Date, val ex: Boolean)
