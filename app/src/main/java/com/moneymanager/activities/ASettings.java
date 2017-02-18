package com.moneymanager.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.moneymanager.R;
import com.moneymanager.utilities.BackupManager;

import static com.moneymanager.Common.setupToolbar;

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

	}

	public void onBackupClick(View view) {

		backup = true;
		googleApiClient.disconnect();
		googleApiClient.connect();
	}

	public void onRestoreClick(View view) {

		backup = false;
		googleApiClient.disconnect();
		googleApiClient.connect();

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
