package com.moneymanager.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.moneymanager.R;
import com.moneymanager.adapters.DebtAdapter;

import static com.moneymanager.Common.setupToolbar;

public class ADebts extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_debt);

		setupToolbar(this, R.id.a_debt_toolbar, "Debts and Loans");

		// View Pager Stuff
		ViewPager viewPager = (ViewPager) findViewById(R.id.debt_viewpager);
		viewPager.setAdapter(new DebtAdapter(getSupportFragmentManager()));

	}
}
