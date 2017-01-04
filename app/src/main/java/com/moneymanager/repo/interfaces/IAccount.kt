package com.moneymanager.repo.interfaces

import com.moneymanager.entities.Account

interface IAccount {

	fun insertNewAccount(acc: Account): Long

	fun getAllAccounts(column: String?, order: String?): Array<Account?>

	fun getAccount(id: Int): Account

	fun removeAccount(id: Int)

	fun getAllAcountsOverView(): Int

}