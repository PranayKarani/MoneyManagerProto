package com.moneymanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.moneymanager.R;
import com.moneymanager.utilities.BackupManager;
import com.moneymanager.utilities.ShrPref;

import static com.moneymanager.Common.setupToolbar;
import static com.moneymanager.Common.spBUDGET_LIMIT;

public class ASettings extends MyBaseActivity {

	private final int RESOLVE_CONNECTION_REQUEST_CODE = 234;
	private GoogleApiClient googleApiClient;
	private boolean backup = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_settings);

		setupToolbar(this, R.id.settings_toolbar, "Settings");

		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.addScope(Drive.SCOPE_APPFOLDER)
				.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
					@Override
					public void onConnected(@Nullable Bundle bundle) {


						BackupManager backupManager = new BackupManager(ASettings.this, googleApiClient);
						if (backup) {
							backupManager.startBackup();
						} else {
							backupManager.restoreBackup();
						}

					}

					@Override
					public void onConnectionSuspended(int i) {

					}
				})
				.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult result) {

						log_i("GoogleApiClient connection failed: " + result.getErrorMessage());

						if (!result.hasResolution()) {

							GoogleApiAvailability.getInstance().getErrorDialog(ASettings.this, result.getErrorCode(), 0).show();
							return;

						}

						try {
							result.startResolutionForResult(ASettings.this, RESOLVE_CONNECTION_REQUEST_CODE);
						} catch (IntentSender.SendIntentException e) {
							e.printStackTrace();
						}


					}
				})
				.build();

		final int currentLimit = ShrPref.readData(this, spBUDGET_LIMIT, 10);
		final StringBuilder sb = new StringBuilder();
		sb.append(currentLimit).append("% of the set budget");

		final TextView budgetLimitText = (TextView) findViewById(R.id.a_setting_budget_limit_text);
		budgetLimitText.setText(sb.toString());

	}

	public void onBackupClick(View view) {

		backup = true;
		new AlertDialog.Builder(this)
				.setTitle("Create Backup?")
				.setMessage("Older backups will be deleted")
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showShortToast("backup started");
						googleApiClient.disconnect();
						googleApiClient.connect();
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();

	}

	public void onRestoreClick(View view) {

		backup = false;
		googleApiClient.disconnect();
		googleApiClient.connect();

	}

	public void onBudgetLimitClick(View view) {

		final int[] budgetLimits = {5, 10, 15, 20, 25, 30};
		final String[] budgetLimitsStrings = new String[budgetLimits.length];
		for (int i = 0; i < budgetLimits.length; i++) {
			budgetLimitsStrings[i] = budgetLimits[i] + "%";
		}

		final TextView budgetLimitText = (TextView) findViewById(R.id.a_setting_budget_limit_text);

		final int currentLimit = ShrPref.readData(this, spBUDGET_LIMIT, 10);
		int idx = 1;
		for (int i = 0; i < budgetLimits.length; i++) {
			if (currentLimit == budgetLimits[i]) {
				idx = i;
			}
		}
		final int[] newLimit = {10};

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("Select new budget limit")
				.setSingleChoiceItems(budgetLimitsStrings, idx, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						newLimit[0] = budgetLimits[which];

					}
				})
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						ShrPref.writeData(ASettings.this, spBUDGET_LIMIT, newLimit[0]);
						budgetLimitText.setText(newLimit[0] + "% of the set budget");
						dialog.dismiss();
					}
				})
				.create();
		alertDialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		switch (requestCode) {

			case RESOLVE_CONNECTION_REQUEST_CODE:

				if (resultCode == RESULT_OK) {
					googleApiClient.connect();
				}
				break;

		}


	}

}
