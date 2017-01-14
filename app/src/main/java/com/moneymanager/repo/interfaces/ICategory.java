// Created by pranay on 07/01/17.

package com.moneymanager.repo.interfaces;

import com.moneymanager.entities.Category;

public interface ICategory {

	Category getCategory(int id);

	Category[] getAllCategories();

	/**
	 * Retures Category list for income or expense type
	 */
	Category[] getTypeSpecificCategories(int type);

	void insertNewCategory(Category category);

	void updateCategory(Category category);

	void removeCategory(Category cat);
}
