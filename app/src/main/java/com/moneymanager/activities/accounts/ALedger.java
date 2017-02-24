package com.moneymanager.activities.accounts;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.moneymanager.R;
import com.moneymanager.activities.MyBaseActivity;

import static com.moneymanager.Common.setupToolbar;

public class ALedger extends MyBaseActivity {

	private final int ALL = 23;
	private final int TRANS = 24;
	private final int DEBT = 25;
	private final int TRANSFER = 26;

	private MenuItem type_text;
	private int selectedType;

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_ledger);

		setupToolbar(this, R.id.a_ledger_toolbar, "Ledger");

		listView = (ListView) findViewById(R.id.a_ledger_listview);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ledger_menu, menu);
		type_text = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.ledger_menu_trans:
				type_text.setTitle("Transactions");
				selectedType = TRANS;
				break;
			case R.id.ledger_menu_debts:
				type_text.setTitle("Debts & Loans");
				selectedType = DEBT;
				break;
			case R.id.ledger_menu_transfers:
				type_text.setTitle("Transfers");
				selectedType = TRANSFER;
				break;
			default: // custom
				type_text.setTitle("All");
				selectedType = ALL;
				break;
		}

		return true;
	}

//	class LedgerLoader extends

}
