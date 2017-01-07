package com.moneymanager.exceptions;

// Created by PranayKarani on 12-12-2016.
public class NoAccountsException extends Exception {

	public NoAccountsException() {
		super("Account table should not be empty");
	}

	NoAccountsException(String mess) {
		super(mess);
	}

}
