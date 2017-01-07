// Created by pranay on 07/01/17.
package com.moneymanager.entities;

public class Account {

	private int id;
	private String name;
	private double balance;
	private boolean exclude;

	public Account(int id, String name, double balance, boolean exclude) {
		this.id = id;
		this.name = name;
		this.balance = balance;
		this.exclude = exclude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}
}
