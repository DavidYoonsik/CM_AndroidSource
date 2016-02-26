package mobile.cross.itm.detail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageFileCache;
import mobile.cross.itm.utils.ImageVolumeResizing;
import mobile.cross.itm.utils.ImageDownLoad;

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

public class ProductEdited extends Activity {

	class dbHelper extends SQLiteOpenHelper{

		public dbHelper(Context context) {
			super(context, "userInfo.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE
			db.execSQL( "create table userInfo " +
					"(_id integer primary key autoincrement, user_id text, user_pw text, user_pic text, user_date text);" );
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// DROP
			db.execSQL(" drop table if exists userInfo ");
		}

	}

	dbHelper helper;
	SQLiteDatabase db;

	ImageView product_image;
	Button upload_bt;
	EditText product_name, product_detail, product_price;
	RadioButton method_all, method_ptop, method_post;
	ProgressDialog mProgressDialog;

	ImageFileCache fileCache;

	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final String PRODUCT_TYPE = "ps";

	private String url = "http://117.17.188.74:8080/CMservlet/UploadServlet2"; // 랩실
	// 공유기

	Uri mMediaUri;
	byte[] fileBytes;
	Context context;
	String imagepath, degree, category, status, method, pro_status, pro_method, pro_name, pro_detail, pro_price, pro_image, pro_host, pro_date, pro_index, pro_type, pro_user_pic;

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: // Take picture
				Intent intent = new Intent(); // Not working
				intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (mMediaUri == null) {

				} else {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(intent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: // Choose picture
				Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // 외장메모리
				Intent intent2 = new Intent(Intent.ACTION_PICK, uri);
				startActivityForResult(intent2, PICK_PHOTO_REQUEST);
				break;
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.
			if (isExternalStorageAvailable()) {
				// get the URI
				// 1. Get the external storage directory
				String appName = ProductEdited.this.getString(R.string.app_name);
				File mediaStorageDir = new File(
						Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);

				// 2. Create our subdirectory
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						return null;
					}
				}

				// 3. Create a file name
				// 4. Create the file
				File mediaFile = null;
				// Date now = new Date();
				// String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				// Locale.KOREA).format(now);

				String path = mediaStorageDir.getPath() + File.separator;
				if (mediaType == MEDIA_TYPE_IMAGE) {
					imagepath = path + "IMG_" + System.currentTimeMillis()
							+ ".jpg";
					mediaFile = new File(imagepath);
				} else {
					return null;
				}

				// 5. Return the file's URI
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

	String user_id, user_pw;
	String price = "";

	HttpURLConnection conn = null;
	InputStream is = null;
	OutputStream os = null;
	FileInputStream fis = null;
	ByteArrayOutputStream bos = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_edited);

		// ProductsDetailed.java 로 부터 받은 인텐트 값을 처리합니다.
		Intent i = getIntent();
		//	String imagepath, degree, category, status, method, pro_status, pro_method, pro_name, pro_detail, pro_price, pro_image, pro_host, pro_date, pro_index, pro_type;
		pro_status = i.getStringExtra(VariableManager.PRODUCT_STATUS);
		pro_method = i.getStringExtra(VariableManager.PRODUCT_METHOD);
		pro_name = i.getStringExtra(VariableManager.PRODUCT_NAME);
		pro_detail = i.getStringExtra(VariableManager.PRODUCT_DETAIL);
		pro_price = i.getStringExtra(VariableManager.PRODUCT_PRICE);
		pro_host = i.getStringExtra(VariableManager.PRODUCT_HOST);
		//pro_date = i.getStringExtra(VariableManager.PRODUCT_NAME);
		pro_image = i.getStringExtra(VariableManager.PRODUCT_IMAGE);
		pro_index = i.getStringExtra(VariableManager.PRODUCT_INDEX);
		pro_type = i.getStringExtra(VariableManager.PRODUCT_TYPE);
		pro_user_pic = i.getStringExtra(VariableManager.PRODUCT_USER_PIC);
		
		// DATABASE 내용을 불러와 서버와의 통신을 통해 유저의 Identification 을 점검한다.
		
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id from userInfo ", null); // where
		// _id <
		// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
		}
		context = this;

		/*
		 * 상품 등록 정보 뷰 순서대로 했음.
		 */

		// 상품 사진
		product_image = (ImageView) findViewById(R.id.product_pic);
		new ImageDownLoad(context).DisplayImage(pro_image, product_image);

		new PictureTemp().execute();

		product_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 위에 있다.
				AlertDialog.Builder builder = new AlertDialog.Builder(ProductEdited.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				builder.setCancelable(true); // cancel available
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		// 상품 카테고리
		Spinner category_spinner = (Spinner) findViewById(R.id.product_category_spinner);
		ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(
				this, // Current context
				R.array.category_array, // Message
				android.R.layout.simple_spinner_item); // Type
		category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(category_adapter);
		category_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		product_name.setText(pro_name);

		// 상품 상태
		Spinner status_spinner = (Spinner) findViewById(R.id.product_status_spinner);
		ArrayAdapter<CharSequence> status_adapter = ArrayAdapter.createFromResource(this, R.array.status_array,	android.R.layout.simple_spinner_item);
		status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status_spinner.setAdapter(status_adapter);
		status_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
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

		if(pro_status.equalsIgnoreCase("new")){
			status_spinner.setSelection(0);
		}else if(pro_status.equalsIgnoreCase("a+")){
			status_spinner.setSelection(1);
		}else if(pro_status.equalsIgnoreCase("a")){
			status_spinner.setSelection(2);
		}else if(pro_status.equalsIgnoreCase("b")){
			status_spinner.setSelection(3);
		}else{
			status_spinner.setSelection(4);
		}


		// 상품 가격
		product_price = (EditText) findViewById(R.id.product_price);
		product_price.setText(pro_price);
		product_price.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DecimalFormat df = new DecimalFormat("###,###");
				if (!s.toString().equals(price)) {
					price = df.format(Long.parseLong(s.toString().replaceAll(",", "")));
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

		if(pro_method.equals("무관")){
			method_all.setChecked(true);
			method = "무관";
		}else if(pro_method.equals("직거래")){
			method_ptop.setChecked(true);
			method = "직거래";
		}else{
			method_post.setChecked(true);
			method = "택배거래";
		}

		// 상품 설명
		product_detail = (EditText) findViewById(R.id.product_detail);
		product_detail.setText(pro_detail);

		// 상품 등록
		upload_bt = (Button) findViewById(R.id.product_register);
		upload_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				String s = System.currentTimeMillis() + "";

				pro_name = product_name.getText().toString();
				pro_detail = product_detail.getText().toString();
				pro_price = product_price.getText().toString();

				// 통신을 통해서 사진파일과 텍스트 data를 함께 서버사이드로 보낸다.
				PictureUpload p = new PictureUpload(context, fileBytes);
				String[] params = new String[] { url, // 서버주소
						pro_name, // 상품이름
						pro_detail, // 상품세부사항
						pro_price, // 상품 가격
						pro_host, // 상품 올린 사람의 id
						s, // 시간
						pro_type, // 상품타입("ps"? or "pr"?)
						pro_index,
						degree, // 사진각도
						category, // 카테고리
						status, // 상품상태
						method};  // 거래방법
				p.execute(params);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_edited, menu);
		return true;
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

				mMediaUri = data.getData();
				//Toast.makeText(this, "OK DATA : " + mMediaUri.,	Toast.LENGTH_LONG).show();
			}
			if (requestCode == PICK_PHOTO_REQUEST) {

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, mMediaUri, product_image));
				try{
					exif = new ExifInterface(getRealPathFromURI(this, mMediaUri));
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
				Picasso.with(this).load(mMediaUri).resize(640, 640).centerCrop().noFade().into(product_image);
				//product_image.setImageBitmap(b);

			} else if (requestCode == TAKE_PHOTO_REQUEST) {

				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing
						.getByteArrayFromFile(this, mMediaUri, product_image));
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

			/*url, // 서버주소
			product_name.getText().toString(), // 상품이름
			product_detail.getText().toString(), // 상품세부사항
			product_price.getText().toString(), // 상품 가격
			pro_host, // 상품 올린 사람의 id
			s, // 시간
			pro_type, // 상품타입("ps"? or "pr"?)
			pro_index,
			degree, // 사진각도
			category, // 카테고리
			status, // 상품상태
			method};  // 거래방법*/

			try {
				Charset chars = Charset.forName("UTF-8");

				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(params[0]);
				MultipartEntity multiPart = new MultipartEntity();
				multiPart.addPart("name", new StringBody(params[1], chars));
				multiPart.addPart("detail", new StringBody(params[2], chars));
				multiPart.addPart("price", new StringBody(params[3], chars));
				multiPart.addPart("host", new StringBody(params[4], chars));
				multiPart.addPart("s", new StringBody(params[5], chars));
				multiPart.addPart("type", new StringBody(params[6], chars));
				multiPart.addPart("index", new StringBody(params[7], chars));
				multiPart.addPart("degree", new StringBody(params[8], chars));
				multiPart.addPart("category", new StringBody(params[9], chars));
				multiPart.addPart("status", new StringBody(params[10], chars));
				multiPart.addPart("method", new StringBody(params[11], chars));
				multiPart.addPart("file1", new ByteArrayBody(fileBytes, params[5] + ".jpg"));

				post.setEntity(multiPart);

				client.execute(post);
			} catch (Throwable t) {
				// Handle error here
				t.printStackTrace();
			}
			return params[5];
		}

		@Override
		protected void onPostExecute(String data) {
			mProgressDialog.dismiss();
			VariableManager.FLAG_PRODUCT = true;
			VariableManager.FLAG_USER = true;
			fileCache = new ImageFileCache(getApplicationContext());
			fileCache.clear(pro_image);
			String ext = Environment.getExternalStorageState();
			if(ext.equals(Environment.MEDIA_MOUNTED)){
				FileOutputStream fos = null;
				try{
					File f = new File(Environment.getExternalStorageDirectory(), "gcm");
					if (!f.exists()){
						f.mkdirs();
					}
					String path = f.getPath() + File.separator + "GCMIntentService";
					File f2 = new File(path);
					fos = new FileOutputStream(f2);
					String content = pro_name + ";" + 
							pro_detail + ";" + 
							"http://117.17.188.74:8080/CMservlet/images/" + data + ".jpg;" + 
							pro_host + ";" + 
							data + ";" + 
							pro_price + ";" + 
							pro_index + ";" + 
							pro_type + ";" +
							status + ";" +
							method + ";" +
							pro_user_pic;
					fos.write(content.getBytes());

				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{
						fos.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}

			}
			Intent intent = new Intent(ProductEdited.this, ProductsDetailed.class);
			intent.setType("edit");
			VariableManager.FLAG_CATEGORY = true;
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	class PictureTemp extends AsyncTask<Void, Void, Void>{

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

		@Override
		protected Void doInBackground(Void... params) {
			try {
				//File f = new File(Environment.getExternalStorageDirectory(), "TEMPORARY");
				/*if (!f.exists()) {
					if (!f.mkdirs()) {
						return null;
					}
				}*/
				URL imageUrl = new URL(pro_image);
				conn = (HttpURLConnection) imageUrl.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				conn.setInstanceFollowRedirects(true);
				is = conn.getInputStream();
				//File f2 = new File(f.getPath()+File.separator+"temp.jpg");
				//os = new FileOutputStream(f2.getPath());
				//Utils.CopyStream(is, os);

				bos = new ByteArrayOutputStream();
				byte[] bytesFromFile = new byte[is.available()]; // buffer size (1 MB)
				int bytesRead = is.read(bytesFromFile);
				while (bytesRead != -1) {
					bos.write(bytesFromFile, 0, bytesRead);
					bytesRead = is.read(bytesFromFile);
				}

				fileBytes = bos.toByteArray();
				
				/*ExifInterface exif = null;
				
				exif = new ExifInterface(f2.getPath());
				int exifOrientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);*/
				//degree = exifOrientationToDegrees(exifOrientation) + "";
				degree = "0";

			} catch (Throwable ex) {
				ex.printStackTrace();

			} finally{
				try {
					bos.close();
					is.close();
					conn.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(getApplicationContext(), fileBytes.length+", "+degree, 0).show();
			mProgressDialog.dismiss();
		}

	}
}
