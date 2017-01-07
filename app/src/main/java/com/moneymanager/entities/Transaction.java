// Created by pranay on 07/01/17.

package com.moneymanager.entities;

import java.util.Date;

public class Transaction {

	private int id;
	private double amount;
	private Category category;
	private Account account;
	private String info;
	private Date dateTime;
	private boolean exclude;

	public Transaction(int id, double amount, Category category, Account account, String info, Date dateTime, boolean exclude) {
		this.id = id;
		this.amount = amount;
		this.category = category;
		this.account = account;
		this.info = info;
		this.dateTime = dateTime;
		this.exclude = exclude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}
}
