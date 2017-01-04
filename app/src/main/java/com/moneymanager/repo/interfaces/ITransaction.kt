package com.moneymanager.repo.interfaces

import com.moneymanager.entities.Transaction

/**
 * All the method declarations for Transaction DB operations
 *
 * Created by pranay on 16/12/16.
 */
interface ITransaction {

	fun getAllTransactions(column: String?, order: String?): Array<Transaction?>

	fun insertNewTransaction(trans: Transaction)

	fun removeTransaction(transID: Int)

}