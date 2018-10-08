package com.iscool.michael.represent;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Rep_Detail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep__detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView name = (TextView) findViewById(R.id.rep_name);
        TextView party = (TextView) findViewById(R.id.rep_party);
        TextView committee_header = (TextView) findViewById(R.id.committees);
        TextView committees = (TextView) findViewById(R.id.committee_list);
        TextView bill_header = (TextView) findViewById(R.id.bills);
        TextView bills = (TextView) findViewById(R.id.bill_list);

        Intent intent = getIntent();
        final Representative representative = intent.getParcelableExtra("this_rep");

        name.setText(representative.getName());
        party.setText(representative.getParty());

        committee_header.setText("Committees:");

        String committees_string = "";
        ArrayList<String> committees_list = representative.getCommittees();
        for (int i = 0; i < committees_list.size(); i++) {
            committees_string += committees_list.get(i) + "\n";
        }
        committees.setText(committees_string);

        bill_header.setText("Bills:");

        String bills_string = "";
        Iterator<Map.Entry<String, String>> bill_list = representative.getBills().entrySet().iterator();
        while(bill_list.hasNext()) {
            Map.Entry<String, String> next = bill_list.next();
            bills_string += next.getKey() + ", introduced on " + next.getValue() + "\n";
        }
        bills.setText(bills_string);

    }

}
