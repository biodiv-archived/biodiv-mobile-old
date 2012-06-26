package com.va.strand.biodiversity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

import com.va.strand.biodiversity.model.GroupModel;
import com.va.strand.biodiversity.model.HabitatModel;
import com.va.strand.biodiversity.model.ObservationList;

public class BrowseObservationListAdapter extends BaseAdapter implements ObservationListListener {

	private static final String TAG = "BioDiversity";
	private Activity context;
	private ObservationList observationList;
	private HabitatModel habitatModel;
	private GroupModel groupModel;
	private Map<Long, Bitmap> bitmapCache;

	static class ViewHolder {
		public TextView name;
		public ImageView image;
		public TextView habitat;
		public TextView group;
	}

	public BrowseObservationListAdapter(Activity context, ObservationList observationList) {
		this.context = context;
		this.observationList = observationList;
		this.habitatModel = new HabitatModel(context);
		this.groupModel = new GroupModel(context);
	}

	@Override
	public int getCount() {
		return observationList.size();
	}

	@Override
	public Object getItem(int position) {
		return observationList.getId(position);
	}

	@Override
	public long getItemId(int position) {
		return observationList.getId(position);
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
			viewHolder.habitat = (TextView) rowView.findViewById(R.id.item_habitat);
			viewHolder.group = (TextView) rowView.findViewById(R.id.item_group);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		String habitat = habitatModel.getHabitat(observationList.getData(position).habitat_id + "");
		String group = groupModel.getGroup(observationList.getData(position).group_id + "");
		String name = observationList.getData(position).name;
		if (bitmapCache == null) {
			bitmapCache = new HashMap<Long, Bitmap>();
		}
		if (!bitmapCache.containsKey(getItemId(position))) {
			bitmapCache.put(getItemId(position), getBitmap(1234));
		}
		Bitmap bitmap = bitmapCache.get(getItemId(position));
		
		holder.name.setText(name);
		holder.habitat.setText(habitat);
		holder.group.setText(group);
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

	@Override
	public void listUpdated() {
		notifyDataSetChanged();
	}

}
