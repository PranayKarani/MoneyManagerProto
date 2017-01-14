// Created by pranay on 07/01/17.

package com.moneymanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.moneymanager.db.DBHelper;
import com.moneymanager.entities.Category;
import com.moneymanager.repo.interfaces.ICategory;

public class TCategories implements ICategory {

	public static final String TABLE_NAME = "Category";
	public static final String ID = "_ID";
	public static final String NAME = "cat_name";
	public static final String TYPE = "cat_type";
	public static final String EXCLUDE = "cat_ex";

	public static final int INCOME = 0;
	public static final int EXPENSE = 1;
	private DBHelper dbHelper;

	private Context context;

	public TCategories(Context context) {
		dbHelper = new DBHelper(context);
		this.context = context;
	}

	/* Query Strings */
	public static String q_CREATE_TABLE() {
		return "CREATE TABLE " + TABLE_NAME + " ( " +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				NAME + " TEXT," +
				TYPE + " INTEGER," +
				EXCLUDE + " INTEGER" +
				");";
	}

	private String q_SELECT_CATEGORY(int id) {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
	}

	private String q_SELECT_ALL_CATEGORIES() {
		return "SELECT * FROM " + TABLE_NAME;
	}

	private String q_SELECT_TYPE_CATEGORIES(int type) {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + TYPE + " = " + type;
	}

	@Override
	public Category getCategory(int id) {

		Cursor c = dbHelper.select(q_SELECT_CATEGORY(id), null);

		if (c.moveToFirst()) {

			return extractCategoryFromCursor(c);

		} else {
			return null;
		}
	}

	@Override
	public Category[] getAllCategories() {
		Cursor c = dbHelper.select(q_SELECT_ALL_CATEGORIES(), null);

		if (c.getCount() == 0) {
			whenNoCategoryFound();
			return getAllCategories();

		} else {

			Category[] cats = new Category[c.getCount()];

			while (c.moveToNext()) {

				cats[c.getPosition()] = extractCategoryFromCursor(c);

			}

			return cats;
		}
	}

	@Override
	public Category[] getTypeSpecificCategories(int type) {
		final Cursor c = dbHelper.select(q_SELECT_TYPE_CATEGORIES(type), null);

		if (c.getCount() == 0) {
			whenNoCategoryFound();
			return getTypeSpecificCategories(type);
		} else {

			final Category[] cats = new Category[c.getCount()];

			while (c.moveToNext()) {

				cats[c.getPosition()] = extractCategoryFromCursor(c);

			}

			return cats;

		}
	}

	@Override
	public void insertNewCategory(Category category) {

		final ContentValues cv = new ContentValues();
		cv.put(NAME, category.getName());
		cv.put(TYPE, category.getType());
		cv.put(EXCLUDE, category.isExclude());
		dbHelper.insert(TABLE_NAME, cv);

	}

	@Override
	public void updateCategory(Category new_cat) {

		final ContentValues cv = new ContentValues();
		cv.put(NAME, new_cat.getName());
		cv.put(TYPE, new_cat.getType());
		dbHelper.update(TABLE_NAME, cv, ID + " = ?", new String[]{String.valueOf(new_cat.getId())});

	}

	@Override
	public void removeCategory(Category cat) {

		// transfer transactions in this category to 'Other'
		TTransactions tTransactions = new TTransactions(context);
		tTransactions.shiftDeletedTransactions(cat);

		// then delete the category
		dbHelper.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(cat.getId())});

	}

	private void whenNoCategoryFound() {
		// insert some categories before hand
		final ContentValues cv = new ContentValues();
		cv.put(NAME, "Other expense");
		cv.put(TYPE, EXPENSE);
		cv.put(EXCLUDE, 0);
		dbHelper.insert(TABLE_NAME, cv);

		final ContentValues cv1 = new ContentValues();
		cv1.put(NAME, "Other income");
		cv1.put(TYPE, INCOME);
		cv1.put(EXCLUDE, 0);
		dbHelper.insert(TABLE_NAME, cv1);
	}

	private Category extractCategoryFromCursor(Cursor c) {
		final int id = c.getInt(c.getColumnIndex(ID));
		final String name = c.getString(c.getColumnIndex(NAME));
		final int type = c.getInt(c.getColumnIndex(TYPE));
		final boolean ex = c.getInt(c.getColumnIndex(EXCLUDE)) == 1;

		return new Category(id, name, type, ex);
	}

}
