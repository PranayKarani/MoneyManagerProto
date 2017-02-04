// Created by pranay on 01/02/17.

package com.moneymanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.moneymanager.db.DBHelper;
import com.moneymanager.entities.User;
import com.moneymanager.exceptions.UserExistsException;
import com.moneymanager.repo.interfaces.IUser;

public class TUser implements IUser {

	public static final String TABLE_NAME = "User";
	public static final String ID = "_ID";
	public static final String NAME = "user_name";

	private final String Uid_alias = "uid_alias";
	private final String SELECT = "SELECT " + ID + " AS " + Uid_alias + ", * FROM " + TABLE_NAME;
	private final String SELECT_USER_JOIN_DEBT =
			"SELECT " +
					TABLE_NAME + "." + ID + " AS " + Uid_alias + "," +
					TDebt.TABLE_NAME + "." + TDebt.ID + " AS dID, * FROM " + TABLE_NAME +
					" JOIN " + TDebt.TABLE_NAME + " ON " + TABLE_NAME + "." + ID + " = " + TDebt.TABLE_NAME + "." + TDebt.USER;
	private DBHelper dbHelper;
	private Context context;

	public TUser(Context context) {
		this.dbHelper = new DBHelper(context);
		this.context = context;
	}

	/* Quey Strings */
	public static String q_CREATE_TABLE() {

		return
				"CREATE TABLE " + TABLE_NAME + " (" +
						ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						NAME + " TEXT" +
						")";

	}

	private String q_SELECT_USER(int id) {
		return
				SELECT + " WHERE " + ID + " = " + id;
	}

	private String q_SELECT_USER(String name) {
		return
				SELECT + " WHERE " + NAME + " = '" + name + "'";
	}

	private String q_SELECT_ALL_USERS() {
		return
				SELECT + " ORDER BY " + NAME;
	}

	private String q_SELECT_SPECIFIC_USERS_FROM_ALL_ACCOUNTS(int type) {
		return
				SELECT_USER_JOIN_DEBT +
						" WHERE " + TDebt.TYPE + " = " + type +
						" ORDER BY " + NAME;
	}

	private String q_SELECT_SPECIFIC_USERS(int acc, int type) {
		return
				SELECT_USER_JOIN_DEBT +
						" WHERE " + TDebt.TYPE + " = " + type +
						" AND " + TDebt.ACCOUNT + " = " + acc +
						" GROUP BY " + NAME +
						" ORDER BY " + NAME;
	}

	@Override
	public long addUser(User user) throws UserExistsException {

		Cursor c = dbHelper.select(q_SELECT_USER(user.getName()), null);

		if (c.getCount() > 0) {
			throw new UserExistsException();
		} else {

			final ContentValues cv = new ContentValues();
			cv.put(NAME, user.getName());

			return dbHelper.insert(TABLE_NAME, cv);

		}

	}

	@Override
	public User getUser(int id) {
		final Cursor c = dbHelper.select(q_SELECT_USER(id), null);

		if (c.moveToFirst()) {
			return extractUserFromCursor(c);
		} else {
			return null;
		}

	}

	@Override
	public User[] getAllUsers() {
		final Cursor c = dbHelper.select(q_SELECT_ALL_USERS(), null);

		final User[] users = new User[c.getCount()];

		while (c.moveToNext()) {

			users[c.getPosition()] = extractUserFromCursor(c);

		}

		return users;
	}

	@Override
	public User[] getSpecificUsers(int selectedAccountID, int type) {
		final Cursor c = dbHelper.select(q_SELECT_SPECIFIC_USERS(selectedAccountID, type), null);

		final User[] users = new User[c.getCount()];

		while (c.moveToNext()) {

			users[c.getPosition()] = extractUserFromCursor(c);

		}

		return users;
	}

	@Override
	public User[] getSpecificUsersFromAllAccounts(int type) {
		final Cursor c = dbHelper.select(q_SELECT_SPECIFIC_USERS_FROM_ALL_ACCOUNTS(type), null);

		final User[] users = new User[c.getCount()];

		while (c.moveToNext()) {

			users[c.getPosition()] = extractUserFromCursor(c);

		}

		return users;
	}

	@Override
	public void removeUser(User user) {

		dbHelper.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(user.getId())});

	}

	private User extractUserFromCursor(Cursor c) {
		final int id = c.getInt(c.getColumnIndex(Uid_alias));
		final String name = c.getString(c.getColumnIndex(NAME));

		return new User(id, name);
	}

}
