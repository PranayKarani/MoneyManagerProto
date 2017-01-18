// Created by pranay on 07/01/17.

package com.moneymanager.repo.interfaces;

import com.moneymanager.entities.Category;
import com.moneymanager.entities.Transaction;

import java.util.Date;

public interface ITransaction {

	Transaction[] getAllTransactions(String column, String order);

	double getSumOfTransactionTypeForDay(int type, Date date);

	double getAccountSpecificSumOfTransactionTypeForDay(int acc, int type, Date date);

	Transaction[] getAccountSpecificTransactionsForDay(int accId, Date date);

	Transaction[] getTransactionsForWeek(Date date);

	Transaction[] getAccountSpecificTransactionsForWeek(int accId, Date date);

	void insertNewTransaction(Transaction transaction);

	void removeTransaction(int id);

	void shiftDeletedTransactions(Category cat);
}
