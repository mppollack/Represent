package com.iscool.michael.represent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;


public class RepList_Adapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;


    //to store the list of Names
    private final String[] nameArray;

    //to store the list of parties
    private final String[] partyArray;

    private final String[] emailArray;

    private final String[] websiteArray;

    public RepList_Adapter(Activity context, String[] nameArray, String[] partyArray,
                           String[] emailArray, String[] websiteArray){

        super(context, R.layout.representatives_listview, nameArray);

        this.context = context;
        this.nameArray = nameArray;
        this.partyArray = partyArray;
        this.emailArray = emailArray;
        this.websiteArray = websiteArray;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.representatives_listview, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView party = (TextView) rowView.findViewById(R.id.party);
        TextView email = (TextView) rowView.findViewById(R.id.email);
        TextView website = (TextView) rowView.findViewById(R.id.website);

        //this code sets the values of the objects to values from the arrays
        name.setText(nameArray[position]);
        party.setText(partyArray[position]);
        email.setText(emailArray[position]);
        website.setText(websiteArray[position]);

        return rowView;

    };
}
