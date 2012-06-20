package com.va.strand.biodiversity;

import java.io.File;

import com.va.strand.biodiversity.model.ObservationList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BrowseObservationListAdapter extends BaseAdapter {

	private String[] names;
	private Activity context;
	private ObservationList observationList;

	static class ViewHolder {
		public TextView name;
		public ImageView image;
	}

	public BrowseObservationListAdapter(Activity context, ObservationList observationList) {
		this.context = context;
		this.observationList = observationList;
		names = new String[] { "Android", "Hello", "World", "Varun", "Android",
				"Hello", "World", "Varun", "Android", "Hello", "World",
				"Varun", "Android", "Hello", "World", "Varun", "Android",
				"Hello", "World", "Varun", "Agrawal", "Testing" };
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// R.layout.browse_item, R.id.item_name, names);
	}

	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return names[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.browse_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.item_name);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.item_pic);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = names[position];
		Bitmap bitmap = getBitmap(1234);
		
		holder.name.setText(s);
		if (bitmap != null) {
			holder.image.setImageBitmap(bitmap);
		}
		
		return rowView;
	}

	private Bitmap getBitmap(int id) {
		File filePath = Environment.getExternalStorageDirectory();
		filePath = new File(filePath, "biodiversity");
		filePath = new File(filePath, "cache");
		filePath = new File(filePath, id + "");
		filePath = new File(filePath, "pic.jpg");
		
		if (filePath.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
			return bitmap;
		}
		return null;
	}

}
