package com.va.strand.biodiversity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.va.strand.biodiversity.model.DateModel;
import com.va.strand.biodiversity.model.GroupModel;
import com.va.strand.biodiversity.model.HabitatModel;
import com.va.strand.biodiversity.model.IParameterModel;
import com.va.strand.biodiversity.model.Observation;
import com.va.strand.biodiversity.model.ObservationManager;
import com.va.strand.biodiversity.model.TextModel;

public class CreateObservationActivity extends Activity {

	private static final String TAG = "CreateObservationActivity";

	protected static final int OBSERVATION_DATE_PICKER = 0;
	protected static final int CAMERA_REQUEST = 0;

	private static final int MAX_IMAGE_DIMENSION = 400;

	private ImageButton photoButton;
	private Gallery observationPhotosView;
	private Button habitatButton;
	private Button groupButton;
	private EditText dateView;
	private int dateDay;
	private int dateMonth;
	private int dateYear;
	private DateModel dateModel;
	private HabitatModel habitatModel;
	private GroupModel groupModel;

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

	private AutoCompleteTextView speciesNameView;

	private AutoCompleteTextView commonNameView;
	
	private AutoCompleteTextView languageView;

	private EditText notesView;

	private Button submitButton;

	private TextView errorText;

	private ScrollView scrollView;

	protected Uri fileUri;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);

		dateModel = new DateModel(this);
		habitatModel = new HabitatModel(this);
		groupModel = new GroupModel(this);

		scrollView = (ScrollView) findViewById(R.id.create_activity_scroll_view);
		errorText = (TextView) findViewById(R.id.error_text);
		photoButton = (ImageButton) findViewById(R.id.observation_click_photo);
		observationPhotosView = (Gallery) findViewById(R.id.observation_photo);
		habitatButton = (Button) findViewById(R.id.habitat_button);
		groupButton = (Button) findViewById(R.id.group_button);
		dateView = (EditText) findViewById(R.id.observation_date);
		speciesNameView = (AutoCompleteTextView) findViewById(R.id.species_name);
		commonNameView = (AutoCompleteTextView) findViewById(R.id.common_name);
		languageView = (AutoCompleteTextView) findViewById(R.id.language);
		notesView = (EditText) findViewById(R.id.observation_notes);
		submitButton = (Button) findViewById(R.id.observation_submit);

		observationPhotosAdapter = new ObservationPhotosAdapter(this);
		observationPhotosView.setAdapter(observationPhotosAdapter);
		observationPhotosView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						observationPhotosAdapter.remove(position);
						observationPhotosAdapter.notifyDataSetChanged();
						return true;
					}
				});

		habitatButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openDialog(habitatButton, habitatModel.HABITATS);
			}
		});

		groupButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openDialog(groupButton, groupModel.GROUPS);
			}
		});

		dateView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(OBSERVATION_DATE_PICKER);
			}
		});
		dateView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

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
				try {
					Intent cameraIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputImageFileUri();
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
				} catch (NullPointerException e) {
					e.printStackTrace();
					Toast.makeText(CreateObservationActivity.this,
							"Can't store image", Toast.LENGTH_LONG).show();
				}
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveObservation();
			}
		});

		SuggestionAdapter speciesNameAdapter = new SuggestionAdapter(
				this, "scientificNames");
		speciesNameView.setAdapter(speciesNameAdapter);
		
		SuggestionAdapter commonNameAdapter = new SuggestionAdapter(this, "commonNames");
		commonNameView.setAdapter(commonNameAdapter);

		SuggestionAdapter languageAdapter = new SuggestionAdapter(this, "language");
		languageView.setAdapter(languageAdapter);
		
		final Calendar c = Calendar.getInstance();
		dateYear = c.get(Calendar.YEAR);
		dateMonth = c.get(Calendar.MONTH);
		dateDay = c.get(Calendar.DAY_OF_MONTH);

		updateDisplay();
	}

	private Uri getOutputImageFileUri() {
		return Uri.fromFile(getOutputImageFile());
	}

	private File getOutputImageFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				getResources().getText(R.string.app_name).toString());
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	protected void saveObservation() {
		String habitat = habitatButton.getText().toString();
		String group = groupButton.getText().toString();
		String speciesName = speciesNameView.getText().toString();
		String commonName = commonNameView.getText().toString();
		String language = languageView.getText().toString();
		String date = dateDay + "/" + (dateMonth + 1) + "/" + dateYear;
		String notes = notesView.getText().toString();
		File[] photosFiles = getPhotos();

		List<IParameterModel> errorModels = validate(habitat, group,
				speciesName, commonName, language, date, notes, photosFiles);
		errorText.setText("");
		for (IParameterModel model : errorModels) {
			errorText.append(model.getError() + "\n");
			scrollView.smoothScrollTo(0, 0);
		}
		if (errorModels.size() > 0) {
			return;
		}
		String habitatId = habitatModel.getId(habitat);
		String groupId = groupModel.getId(group);
		Observation observation = ObservationManager.getInstance()
				.createObservation(habitatId, groupId, speciesName, commonName,
						language, date, notes, photosFiles);
		ObservationManager.getInstance().uploadObservation(observation, this);
	}

	private List<IParameterModel> validate(String habitat, String group,
			String speciesName, String commonName, String language, String date,
			String notes, File[] photosFiles) {
		List<IParameterModel> errorModels = new ArrayList<IParameterModel>();
		if (!habitatModel.validate(habitat))
			errorModels.add(habitatModel);
		if (!groupModel.validate(group))
			errorModels.add(groupModel);
		if (!dateModel.validate(date))
			errorModels.add(dateModel);
//		if (speciesName.isEmpty())
//			errorModels.add(new TextModel(this, "Species"));
		if (photosFiles.length == 0)
			errorModels.add(new TextModel(this, "Photos"));
		return errorModels;
	}

	private File[] getPhotos() {
		int count = observationPhotosAdapter.getCount();
		File[] photos = new File[count];
		for (int i = 0; i < count; i++) {
			photos[i] = observationPhotosAdapter.getItem(i);
		}
		return photos;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_REQUEST:
			new PhotoAsyncTask().execute(fileUri);
		}
	}

	private class PhotoAsyncTask extends AsyncTask<Uri, Void, Bitmap> {

		private Uri fileUri;

		@Override
		protected Bitmap doInBackground(Uri... params) {
			Bitmap photo;
			try {
				this.fileUri = params[0];
				photo = MediaStore.Images.Media.getBitmap(
						CreateObservationActivity.this.getContentResolver(),
						fileUri);
				photo = getCorrectlyOrientedImage(
						CreateObservationActivity.this, fileUri);
				return photo;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap photo) {
			if (photo != null) {
				Log.d(TAG, "Image dimensions  : " + photo.getWidth() + ", "
						+ photo.getHeight());
				observationPhotosAdapter
						.add(photo, new File(fileUri.getPath()));
				observationPhotosAdapter.notifyDataSetChanged();
			}
		}

	}

	public int getOrientation(Context context, Uri photoUri) throws IOException {
		ExifInterface exif = new ExifInterface(photoUri.getPath());
		int orientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		switch (orientation) {
		case 3:
			return 180;
		case 6:
			return 90;
		case 8:
			return 270;
		default:
			return 0;
		}
	}

	public Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri)
			throws IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();

		int rotatedWidth, rotatedHeight;
		int orientation = getOrientation(context, photoUri);

		if (orientation == 90 || orientation == 270) {
			rotatedWidth = dbo.outHeight;
			rotatedHeight = dbo.outWidth;
		} else {
			rotatedWidth = dbo.outWidth;
			rotatedHeight = dbo.outHeight;
		}

		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (rotatedWidth > MAX_IMAGE_DIMENSION
				|| rotatedHeight > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) rotatedWidth)
					/ ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) rotatedHeight)
					/ ((float) MAX_IMAGE_DIMENSION);
			float maxRatio = Math.max(widthRatio, heightRatio);

			// Create the bitmap from file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = (int) maxRatio;
			srcBitmap = BitmapFactory.decodeStream(is, null, options);
		} else {
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();

		/*
		 * if the orientation is not 0 (or -1, which means we don't know), we
		 * have to do a rotation.
		 */
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);

			srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
					srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		}

		return srcBitmap;
	}

	private void updateDisplay() {
		dateView.setText(new StringBuilder().append("Observed on ")
				.append(dateDay).append("/").append(dateMonth + 1).append("/")
				.append(dateYear));
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
		private List<Bitmap> photos = new ArrayList<Bitmap>();
		private List<File> photosFiles = new ArrayList<File>();

		static class ViewHolder {
			public ImageView image;
		}

		public ObservationPhotosAdapter(Activity context) {
			this.context = context;
		}

		public void remove(int position) {
			photos.remove(position);
			photosFiles.remove(position);
		}

		public void add(Bitmap photo, File photoFile) {
			photos.add(photo);
			photosFiles.add(photoFile);
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public File getItem(int index) {
			return photosFiles.get(index);
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
				rowView = inflater.inflate(R.layout.observation_photo_item,
						null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) rowView
						.findViewById(R.id.observation_photo_item);
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