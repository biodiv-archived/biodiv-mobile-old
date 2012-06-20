package com.va.strand.biodiversity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class CreateObservationActivity extends Activity {

	private static final String TAG = "CreateObservationActivity";

	protected static final int OBSERVATION_DATE_PICKER = 0;
	protected static final int CAMERA_REQUEST = 0;
	String groups[] = new String[] { "Mammals", "Birds", "Fish", "Amphibians",
			"Reptiles", "Molluscs", "Arthropods", "Plants", "Fungi", "Others" };
	String habitats[] = new String[] { "Forest", "Thicket", "Savanna",
			"Grassland", "Swamp", "Aquatic", "Rocky Outcrops", "Desert",
			"Agriculture", "Urban", "Others" };
	private ImageButton photoButton;
	private Gallery observationPhotos;
	private Button habitatButton;
	private Button groupButton;
	private EditText date;
	private int dateDay;
	private int dateMonth;
	private int dateYear;

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateYear = year;
			dateMonth = monthOfYear;
			dateDay = dayOfMonth;
			updateDisplay();
		}
	};

	private ObservationPhotosAdapter observationPhotosAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);

		photoButton = (ImageButton) findViewById(R.id.observation_click_photo);
		observationPhotos = (Gallery) findViewById(R.id.observation_photo);
		habitatButton = (Button) findViewById(R.id.habitat_button);
		groupButton = (Button) findViewById(R.id.group_button);
		date = (EditText) findViewById(R.id.observation_date);

		observationPhotosAdapter = new ObservationPhotosAdapter(this);
		observationPhotos.setAdapter(observationPhotosAdapter);
		observationPhotos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				observationPhotosAdapter.remove(position);
				observationPhotosAdapter.notifyDataSetChanged();
				return true;
			}
		});

		habitatButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openDialog(habitatButton, habitats);
			}
		});

		groupButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openDialog(groupButton, groups);
			}
		});

		date.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(OBSERVATION_DATE_PICKER);
			}
		});
		date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showDialog(OBSERVATION_DATE_PICKER);
				}
			}
		});

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

		final Calendar c = Calendar.getInstance();
		dateYear = c.get(Calendar.YEAR);
		dateMonth = c.get(Calendar.MONTH);
		dateDay = c.get(Calendar.DAY_OF_MONTH);

		updateDisplay();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_REQUEST:
			Bundle extras = data.getExtras();
			Bitmap photo = (Bitmap) extras.get("data");
			Log.d(TAG,
					"Image dimensions  : " + photo.getWidth() + ", "
							+ photo.getHeight());
			observationPhotosAdapter.add(photo);
			observationPhotosAdapter.notifyDataSetChanged();
		}
	}

	private void updateDisplay() {
		date.setText(new StringBuilder().append("Observed on ").append(dateDay)
				.append("/").append(dateMonth + 1).append("/").append(dateYear));
	}

	protected void openDialog(final Button button, final String[] options) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				options);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		dialog.show();
		modeList.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				button.setText(options[position]);
				dialog.dismiss();
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case OBSERVATION_DATE_PICKER:
			return new DatePickerDialog(this, dateSetListener, dateYear,
					dateMonth, dateDay);
		}
		return null;
	}

	private static class ObservationPhotosAdapter extends BaseAdapter {

		private Activity context;
		List<Bitmap> photos = new ArrayList<Bitmap>();
		
		static class ViewHolder {
			public ImageView image;
		}
		public ObservationPhotosAdapter(Activity context) {
			this.context = context;
		}

		public void remove(int position) {
			photos.remove(position);
		}

		public void add(Bitmap photo) {
			photos.add(photo);
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public Bitmap getItem(int index) {
			return photos.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.observation_photo_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) rowView.findViewById(R.id.observation_photo_item);
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			Bitmap bitmap = photos.get(position);
			
			if (bitmap != null) {
				holder.image.setImageBitmap(bitmap);
			}
			
			return rowView;
		}

	}
}