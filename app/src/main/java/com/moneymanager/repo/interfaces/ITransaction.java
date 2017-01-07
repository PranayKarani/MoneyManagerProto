// Created by pranay on 07/01/17.

package com.moneymanager.repo.interfaces;

import com.moneymanager.entities.Transaction;

public interface ITransaction {

	Transaction[] getAllTransactions(String column, String order);

	void insertNewTransaction(Transaction transaction);

	void removeTransaction(int id);

}
