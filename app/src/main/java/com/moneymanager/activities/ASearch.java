package com.moneymanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.moneymanager.R;

import static com.moneymanager.Common.setupToolbar;

class ASearch extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_search);

        setupToolbar(this, R.id.search_toolbar, "Search");

    }


}
