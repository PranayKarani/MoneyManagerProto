package com.moneymanager.repo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.moneymanager.db.DBHelper
import com.moneymanager.entities.Category
import com.moneymanager.repo.interfaces.ICategory

// Created by PranayKarani on 10-12-2016.
class TCategories(context: Context) : ICategory {

    companion object {
        val TABLE_NAME = "Category"
        val ID = "_ID"
        val NAME = "cat_name"
        val TYPE = "cat_type" // 0- income, 1- expense
        val EXCLUDE = "cat_ex"

		val INCOME = 0
		val EXPENSE = 1

        val q_CREATE_TABLE =
				"CREATE TABLE ${TABLE_NAME} ( " +
						"${ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
						"${NAME} TEXT," +
						"${TYPE} INTEGER," +
						"${EXCLUDE} INTEGER" +
                        ");"

		fun q_GET_CATEGORY(id: Int) = "SELECT * FROM $TABLE_NAME WHERE $ID = $id"

		/** All Categories */
		fun q_SELECT_ALL_CATEGORIES() = "SELECT * FROM $TABLE_NAME"

		/** 1 - Expense, 0 - Income */
		fun q_SELECT_CATEGORIES(type: Int) = "SELECT * FROM $TABLE_NAME WHERE $TYPE = $type"

	}

	val dbHelper = DBHelper(context)

	override fun getCategory(id: Int): Category {

		val c = dbHelper.select(q_GET_CATEGORY(id), null)

		if (c.moveToFirst()) {

			return getCategoryInstance(c)

		} else {
			whenNoCategoryFound()
			return getCategory(id)
		}

	}

	override fun getAllCategories(): Array<Category?> {

		val c = dbHelper.select(q_SELECT_ALL_CATEGORIES(), null)

		if (c.count == 0) {
			whenNoCategoryFound()
			return getAllCategories()

		} else {

			val cats = arrayOfNulls<Category>(c.count)

			while (c.moveToNext()) {

				cats[c.position] = getCategoryInstance(c)

			}

			return cats
		}


	}

	override fun getCategories(type: Int): Array<Category?> {

		val c = dbHelper.select(q_SELECT_CATEGORIES(type), null)

		if (c.count == 0) {
			whenNoCategoryFound()
			return getCategories(type)
		} else {

			val cats = arrayOfNulls<Category>(c.count)

			while (c.moveToNext()) {

				cats[c.position] = getCategoryInstance(c)

			}

			return cats

		}

	}

	private fun whenNoCategoryFound() {
		// insert some categories before hand
		val cv = ContentValues()
		cv.put(NAME, "Movies")
		cv.put(TYPE, 1)
		cv.put(EXCLUDE, 0)
		dbHelper.insert(TABLE_NAME, cv)

		val cv1 = ContentValues()
		cv1.put(NAME, "Salary")
		cv1.put(TYPE, 0)
		cv1.put(EXCLUDE, 0)
		dbHelper.insert(TABLE_NAME, cv1)
	}

	private fun getCategoryInstance(c: Cursor): Category {
		val id = c.getInt(c.getColumnIndex(ID))
		val name = c.getString(c.getColumnIndex(NAME))
		val type = c.getInt(c.getColumnIndex(ID))
		val ex = c.getInt(c.getColumnIndex(EXCLUDE)) == 1

		return Category(id, name, type, ex)
	}

}
