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

	Transaction[] getTransactionsForMonth(Date date);

	Transaction[] getAccountSpecificTransactionsForMonth(int accID, Date date);

	/* Custom Period Transactions */
	Transaction[] getTransactionsForCustomPeriod(Date startDate, Date endDate);

	Transaction[] getAccountSpecificTransactionsForCustomPeriod(int accId, Date startDate, Date endDate);

	void insertNewTransaction(Transaction transaction);

	void removeTransaction(Transaction t);

	void shiftDeletedTransactions(Category cat);

	Transaction getTransaction(int selectedTransactionID);
}
