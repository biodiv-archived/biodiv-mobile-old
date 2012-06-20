package com.va.strand.biodiversity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class ObservationActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observation_activity);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, CreateObservationActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("createObservation").setIndicator("New",
                          res.getDrawable(R.drawable.icon_tab_observation))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, BrowseObservationActivity.class);
        spec = tabHost.newTabSpec("browseObservation").setIndicator("Browse",
                          res.getDrawable(R.drawable.icon_tab_observation))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
    }
}