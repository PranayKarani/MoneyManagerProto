package com.moneymanager.repo.interfaces

import com.moneymanager.entities.Category

interface ICategory {

	fun getCategory(id: Int): Category

	fun getAllCategories(): Array<Category?>
	fun getCategories(type: Int): Array<Category?>
}