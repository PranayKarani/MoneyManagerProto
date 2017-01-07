// Created by pranay on 07/01/17.

package com.moneymanager.entities;

public class Category {

	private int id;
	private String name;
	private int type; // 0- income, 1- expense
	private boolean exclude;

	public Category(int id, String name, int type, boolean exclude) {
		this.id = id;
		this.name = name;
		this.type = type;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}
}
