package mobile.cross.itm.upload;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import mobile.cross.itm.crossmobile.Main;
import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageVolumeResizing;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class EnrollProduct extends Activity {

	ImageView product_image;
	Button upload_bt;
	EditText product_name, product_detail, product_price;
	RadioButton method_all, method_ptop, method_post;
	ProgressDialog mProgressDialog;

	public static final int TAKE_PHOTO_REQUEST = 1;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int MEDIA_TYPE_IMAGE = 3;
	public static final String PRODUCT_TYPE = "PS";

	private String url = "http://117.17.188.74:8080/CMservlet/UploadServlet";

	Uri pictureUri;
	byte[] fileBytes = {0}; // Initialization
	Context context;
	String imagepath, degree, category, status, method, pro_name, pro_detail, pro_price;

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: // Take picture
				Intent intent = new Intent(); // Not working
				intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				pictureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (pictureUri == null) {

				} else {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
					startActivityForResult(intent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: // Choose picture
				Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // 외장메모리
				Intent intent2 = new Intent(Intent.ACTION_PICK, uri);
				startActivityForResult(intent2, PICK_PHOTO_REQUEST);
				break;
			default:
				Uri uri2 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, uri2);
				shareIntent.setType("image/jpeg");

			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			
			if (isExternalStorageAvailable()) {
				String appName = EnrollProduct.this.getString(R.string.app_name);
				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						return null;
					}
				}

				File mediaFile = null;

				String path = mediaStorageDir.getPath() + File.separator;
				if (mediaType == MEDIA_TYPE_IMAGE) {
					imagepath = path + "IMG_" + System.currentTimeMillis() + ".jpg";
					mediaFile = new File(imagepath);
				} else {
					return null;
				}

				return Uri.fromFile(mediaFile);
			} else {
				return null;
			}
		}

		private boolean isExternalStorageAvailable() {
			String state = Environment.getExternalStorageState();

			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			} else {
				return false;
			}
		}
	};

	dbHelper helper;
	SQLiteDatabase db;
	String user_id;
	String user_pw;
	String price = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enroll_product);

		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id from userInfo ", null);

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
		}
		context = this;

		product_image = (ImageView) findViewById(R.id.product_pic);
		product_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 위에 있다.
				AlertDialog.Builder builder = new AlertDialog.Builder(
						EnrollProduct.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				builder.setCancelable(true); // cancel available
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		Spinner category_spinner = (Spinner) findViewById(R.id.product_category_spinner);
		ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(
				this, // Current context
				R.array.category_array, // Message
				android.R.layout.simple_spinner_item); // Type
		category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(category_adapter);
		category_spinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				category = (String) parent.getSelectedItem();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				category = (String) parent.getSelectedItem();
			}
		});

		// 상품 이름
		product_name = (EditText) findViewById(R.id.product_name);

		// 상품 상태
		Spinner status_spinner = (Spinner) findViewById(R.id.product_status_spinner);
		ArrayAdapter<CharSequence> status_adapter = ArrayAdapter.createFromResource(
				this, R.array.status_array,
				android.R.layout.simple_spinner_item);
		status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status_spinner.setAdapter(status_adapter);
		status_spinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				//parent.getItemAtPosition(pos);
				status = (String) parent.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				status = (String) parent.getSelectedItem();
			}
		});

		// 상품 가격
		product_price = (EditText) findViewById(R.id.product_price);
		product_price.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DecimalFormat df = new DecimalFormat("###,###");
				if (!s.toString().equals(price)) {
					price = df.format(Long.parseLong(s.toString().replaceAll(",", "")))+"";
					product_price.setText(price);
					product_price.setSelection(price.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 거래 형태
		method_all = (RadioButton) findViewById(R.id.method_all);

		method_all.setChecked(true);
		method = "무관";

		method_all.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				method = "무관";
			}
		});

		method_ptop = (RadioButton) findViewById(R.id.method_ptop);
		method_ptop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				method = "직거래";
			}
		});

		method_post = (RadioButton) findViewById(R.id.method_post);
		method_post.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				method = "택배거래";
			}
		});

		// 상품 설명
		product_detail = (EditText) findViewById(R.id.product_detail);

		// 상품 등록
		upload_bt = (Button) findViewById(R.id.product_register);
		upload_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = System.currentTimeMillis() + "";
				if(product_name.getText().toString().trim().length() == 0){
					Toast.makeText(EnrollProduct.this, "Put the title in Name section", Toast.LENGTH_SHORT).show();
					return;
				}else if(product_detail.getText().toString().trim().length() == 0){
					Toast.makeText(EnrollProduct.this, "Put the details in Description section", Toast.LENGTH_SHORT).show();
					return;
				}else if(product_price.getText().toString().trim().length() == 0){
					Toast.makeText(EnrollProduct.this, "Put the price in Price section", Toast.LENGTH_SHORT).show();
					return;
				}else if(fileBytes.length <= 5){
					Toast.makeText(EnrollProduct.this, "Put the image file in Image section", Toast.LENGTH_SHORT).show();
					return;
				}
				// 통신을 통해서 사진파일과 텍스트 data를 함께 서버사이드로 보낸다.
				PictureUpload p = new PictureUpload(context, fileBytes);
				String[] params = new String[] { url, // 서버주소
						product_name.getText().toString(), // 상품이름
						product_detail.getText().toString(), // 상품세부사항
						product_price.getText().toString(), // 상품 가격
						user_id, // 상품 올린 사람의 id
						s, // 시간
						PRODUCT_TYPE, // 상품타입("ps"? or "pr"?)
						degree, // 사진각도
						category, // 카테고리
						status, // 상품상태
						method};  // 거래방법
				p.execute(params);

			}
		});

		Intent intent = getIntent();
		if(intent.getType().toString().startsWith("image/")){
			handleSendImage(intent);
		}
	}

	//공유를 통해 들어온 이미지파일 유아이에 리플레트하는 메서드
	public void handleSendImage(Intent intent){
		
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		
		if(imageUri != null){
			//Toast.makeText(this, "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ", Toast.LENGTH_SHORT).show();
			
			ExifInterface exif = null;
			int degree = 0;
			
			fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, imageUri, product_image));
			try{
				exif = new ExifInterface(getRealPathFromURI(this, imageUri));
				//Toast.makeText(this, getRealPathFromURI(this, mMediaUri).toString() ,	Toast.LENGTH_LONG).show();
				int exifOrientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				degree = exifOrientationToDegrees(exifOrientation);
			}catch(Exception e){
				e.printStackTrace();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inJustDecodeBounds = false;
			Bitmap b = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length, options);

			Matrix m = new Matrix();
			m.setRotate(degree, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

			b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

			this.degree = ""+degree;
			Picasso.with(this).load(imageUri).resize(640, 640).centerCrop().noFade().into(product_image);
			//product_image.setImageBitmap(b);
		}
	}

	// 사진회전을 위한 메서드
	public int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	// 사진의 경로를 가져오는 메서드
	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ExifInterface exif = null;
		int degree = 0;

		if (resultCode == RESULT_OK) {
			if (data == null) {
				//Toast.makeText(this, "NO DATA : " + mMediaUri.getPath(),	Toast.LENGTH_LONG).show();
			} else {

				pictureUri = data.getData();
				//Toast.makeText(this, "OK DATA : " + mMediaUri.,	Toast.LENGTH_LONG).show();
			}
			if (requestCode == PICK_PHOTO_REQUEST) {

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, pictureUri, product_image));
				try{
					exif = new ExifInterface(getRealPathFromURI(this, pictureUri));
					//Toast.makeText(this, getRealPathFromURI(this, mMediaUri).toString() ,	Toast.LENGTH_LONG).show();
					int exifOrientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					degree = exifOrientationToDegrees(exifOrientation);
				}catch(Exception e){
					e.printStackTrace();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				options.inJustDecodeBounds = false;
				Bitmap b = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length, options);

				Matrix m = new Matrix();
				m.setRotate(degree, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

				this.degree = ""+degree;
				Picasso.with(this).load(pictureUri).resize(640, 640).centerCrop().noFade().into(product_image);
				//product_image.setImageBitmap(b);

			} else if (requestCode == TAKE_PHOTO_REQUEST) {

				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(pictureUri);
				sendBroadcast(mediaScanIntent);

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing
						.getByteArrayFromFile(this, pictureUri, product_image));
				try {
					exif = new ExifInterface(imagepath);
					int exifOrientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
					degree = exifOrientationToDegrees(exifOrientation);
					//Toast.makeText(this, "OK DATA : " + degree,	Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				options.inJustDecodeBounds = false;
				Bitmap b = BitmapFactory.decodeByteArray(fileBytes, 0,
						fileBytes.length, options);

				Matrix m = new Matrix();
				m.setRotate(degree, (float) b.getWidth() / 2,
						(float) b.getHeight() / 2);

				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
						m, true);

				this.degree = ""+degree;
				product_image.setImageBitmap(b);
			}
		} else if (resultCode != RESULT_CANCELED) {
			return;
		}
	}

	class PictureUpload extends AsyncTask<String, Void, String> {

		public byte[] fileBytes;
		public Bitmap uploadBm;
		public Context context;

		public PictureUpload(Context context, byte[] file) {
			this.fileBytes = file;
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// Create a progressdialog
			mProgressDialog = new ProgressDialog(context);
			// Set progressdialog title
			mProgressDialog.setTitle("Thank you");
			// Set progressdialog message
			mProgressDialog.setMessage("Loading...");
			// mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
			// Show progressdialog
			mProgressDialog.show();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			String pro_name = params[1];
			String pro_detail = params[2];
			String pro_price = params[3];
			String pro_host = params[4];
			String pro_date = params[5];			
			String pro_type = params[6];
			String file_degree = params[7];
			try {
				Charset chars = Charset.forName("UTF-8");

				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(url);

				MultipartEntity multiPart = new MultipartEntity();
				multiPart.addPart("param1", new StringBody(pro_name, chars));
				multiPart.addPart("param2", new StringBody(pro_detail, chars));
				multiPart.addPart("param3", new StringBody(pro_price, chars));
				multiPart.addPart("param4", new StringBody(pro_host, chars));
				multiPart.addPart("param5", new StringBody(pro_date, chars));
				multiPart.addPart("param6", new StringBody(pro_type, chars));
				multiPart.addPart("param7", new StringBody(file_degree, chars));
				multiPart.addPart("param8", new StringBody(params[8], chars));
				multiPart.addPart("param9", new StringBody(params[9], chars));
				multiPart.addPart("param10", new StringBody(params[10], chars));
				multiPart.addPart("file1", new ByteArrayBody(fileBytes, pro_date + ".jpg"));

				post.setEntity(multiPart);
				if(fileBytes.length > 0){
					client.execute(post);
				}else{
					return null;
				}
				
			} catch (Throwable t) {
				// Handle error here
				t.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String data) {
			mProgressDialog.dismiss();
			VariableManager.FLAG_PRODUCT = true;
			VariableManager.FLAG_USER = true;
			Intent intent = new Intent(EnrollProduct.this, Main.class);
			setResult(0, intent);
			finish();
		}
	}

	class dbHelper extends SQLiteOpenHelper {

		public dbHelper(Context context) {
			super(context, "userInfo.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE
			db.execSQL("create table userInfo "
					+ "(_id integer primary key autoincrement, "
					+ "user_id text, " + "user_pw text, " + "user_pic text, "
					+ "user_date text);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// DROP
			db.execSQL(" drop table if exists userInfo ");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.share_menu, menu);

		MenuItem item = menu.findItem(R.id.menu_item_share);
		
		return true;
	}

}
