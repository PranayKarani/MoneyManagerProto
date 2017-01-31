// Created by pranay on 07/01/17.

package com.moneymanager.repo.interfaces;

import com.moneymanager.entities.Account;
import com.moneymanager.exceptions.InsufficientBalanceException;
import com.moneymanager.exceptions.NoAccountsException;

public interface IAccount {

	long insertNewAccount(Account account);

	Account[] getAllAccounts(String column, String order) throws NoAccountsException;

	Account getAccount(int id);

	void removeAccount(int id);

	double getSumOfBalanceOfAllAccounts();

	double getSumOfBalanceOfAccount(int selectedAccount);

	void updateAccountBalance(int id, double amount, boolean add) throws InsufficientBalanceException;

}
