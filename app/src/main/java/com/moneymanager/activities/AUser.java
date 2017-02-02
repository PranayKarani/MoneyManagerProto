package com.moneymanager.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.moneymanager.R;
import com.moneymanager.entities.User;
import com.moneymanager.exceptions.UserExistsException;
import com.moneymanager.repo.TUser;

import java.util.ArrayList;

import static com.moneymanager.Common.setupToolbar;

public class AUser extends AppCompatActivity {

	private ArrayList<String> user_name_list = new ArrayList<>();
	private ArrayList<Integer> user_name_id = new ArrayList<>();

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_user);
		setupToolbar(this, R.id.a_user_toolbar, "Manage Users");

		// Setup List view
		listView = (ListView) findViewById(R.id.a_user_listview);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, user_name_list));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				TUser tUser = new TUser(AUser.this);

				tUser.removeUser(new User(user_name_id.get(position), user_name_list.get(position)));
				refreshUserList();

			}
		});

		// set up insert dialog
		final View alertView = getLayoutInflater().inflate(R.layout.d_add_user, null);
		final EditText user_name_edittext = (EditText) alertView.findViewById(R.id.d_add_user_name);


		final AlertDialog dialog = new AlertDialog.Builder(AUser.this)
				.setView(alertView)
				.setPositiveButton("add", null)
				.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(final DialogInterface dialogX) {
				final Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// insert new Category
						final String user_name = user_name_edittext.getText().toString();

						if (user_name.equals("")) {
							user_name_edittext.setError("Enter a valid name");
						} else {

							User user = new User(-1, user_name);

							TUser tUser = new TUser(AUser.this);
							try {
								tUser.addUser(user);
								Toast.makeText(AUser.this, user.getName() + " added", Toast.LENGTH_LONG).show();
								dialogX.dismiss();
							} catch (UserExistsException e) {
								user_name_edittext.setError(e.getMessage());
							}

						}


					}

				});

			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// refresh category list
				refreshUserList();

			}
		});

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.a_add_user_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.show();
			}
		});

		refreshUserList();

	}

	private void refreshUserList() {
		final TUser tUser = new TUser(this);

		final User[] user_array = tUser.getAllUsers();

		user_name_list.clear();
		user_name_id.clear();

		for (User user : user_array) {
			user_name_list.add(user.getName());
			user_name_id.add(user.getId());
		}
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, user_name_list));
	}
}
