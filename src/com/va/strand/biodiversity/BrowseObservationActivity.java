package com.va.strand.biodiversity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.va.strand.biodiversity.model.ObservationFactory;
import com.va.strand.biodiversity.model.ObservationList;

public class BrowseObservationActivity extends ListActivity {
	private static final int COUNT = 10;
	
	private int offset = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);
        
        ObservationList observationList = ObservationFactory.getInstance().getObservationList(offset, COUNT);
        
        BaseAdapter adapter = new BrowseObservationListAdapter(this, observationList);
        
        setListAdapter(adapter);
    }
}