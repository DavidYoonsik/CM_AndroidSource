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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RequestProduct extends Activity {

	ImageView image;
	Button button, button2;
	EditText product_name, product_detail, product_price;
	ProgressDialog mProgressDialog;

	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final String PRODUCT_TYPE = "ps";

	private String url = "http://117.17.188.74:8080/CMservlet/UploadServlet"; // 랩실 공유기

	Uri mMediaUri;
	byte[] fileBytes;
	Context context;
	String imagepath;

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
			case 0: // Take picture
				Intent intent = new Intent(); // Not working
				intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if(mMediaUri == null){

				}else{
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(intent,1);
				}			
				break;
			case 1: // Choose picture
				Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // 외장메모리
				Intent intent2 = new Intent(Intent.ACTION_PICK, uri);
				startActivityForResult(intent2, 11);
				break;
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.
			if (isExternalStorageAvailable()) {
				// get the URI
				// 1. Get the external storage directory
				String appName = RequestProduct.this.getString(R.string.app_name);
				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);

				// 2. Create our subdirectory
				if (! mediaStorageDir.exists()) {
					if (! mediaStorageDir.mkdirs()) {
						return null;
					}
				}

				// 3. Create a file name
				// 4. Create the file
				File mediaFile = null;
				//Date now = new Date();
				//String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(now);

				String path = mediaStorageDir.getPath() + File.separator;
				if(mediaType == MEDIA_TYPE_IMAGE) {
					imagepath = path + "IMG_" + System.currentTimeMillis() + ".jpg";
					mediaFile = new File(imagepath);
				}else{
					return null;
				}

				// 5. Return the file's URI				
				return Uri.fromFile(mediaFile);
			}
			else {
				return null;
			}
		}

		private boolean isExternalStorageAvailable() {
			String state = Environment.getExternalStorageState();

			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			}
			else {
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
		setContentView(R.layout.request_product);

		// DB 내용을 불러와 서버와의 통신을 통해 유저의 Identification을 점검한다.
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id from userInfo ", null); // where _id < 10

		while(c.moveToNext()){
			user_id =  c.getString(0).toString();
		}
		
		context = this;

		image = (ImageView)findViewById(R.id.pic);
		button = (Button)findViewById(R.id.ok);
		product_name = (EditText)findViewById(R.id.pr_name);
		product_detail = (EditText)findViewById(R.id.pr_detail);
		product_price = (EditText)findViewById(R.id.pr_price);	

		product_price.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DecimalFormat df = new DecimalFormat("###,###");
				if(!s.toString().equals(price)){
					price = df.format(Long.parseLong( s.toString().replaceAll(",", "")));
					product_price.setText(price);
					product_price.setSelection(price.length());
				}
			}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				// TODO Auto-generated method stub				
			}			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(RequestProduct.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				builder.setCancelable(true); // cancel available
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = System.currentTimeMillis()+"";
				// 통신을 통해서 사진파일과 텍스트 data를 함께 서버사이드로 보낸다.
				PictureUpload p = new PictureUpload(context, fileBytes);
				String[] params = new String[]{
						url, // 서버주소
						product_name.getText().toString(), // 상품이름
						product_detail.getText().toString(), // 상품세부사항
						product_price.getText().toString(), // 상품 가격
						user_id, // 상품 올린 사람의 id
						s, // 시간
						PRODUCT_TYPE}; // 상품타입("ps"? or "pr"?)
				p.execute(params);

			}
		});
	}

	class PictureUpload extends AsyncTask<String, Void, String>{

		public byte[] fileBytes;
		public Bitmap uploadBm;
		public Context context;

		public PictureUpload(Context context, byte[] file){
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
			//mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
			// Show progressdialog
			mProgressDialog.show();
		}



		@SuppressLint("SimpleDateFormat") @Override
		protected String doInBackground(String... params) {
			String url = params[0];
			String pro_name = params[1];
			String pro_detail = params[2];
			String pro_price = params[3];
			String pro_host = params[4];
			String pro_date = params[5];
			String pro_type = params[6];
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

				multiPart.addPart("file", new ByteArrayBody(fileBytes, pro_date+".jpg"));


				post.setEntity(multiPart);

				client.execute(post);
			}
			catch(Throwable t) {
				// Handle error here
				t.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String data) {	
			mProgressDialog.dismiss();
			VariableManager.FLAG_PRODUCT = true;
			Intent intent = new Intent(RequestProduct.this, Main.class);
			setResult(0, intent);
			finish();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.request_product, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		ExifInterface exif = null;
		int degree = 0;

		if (resultCode == RESULT_OK) {	
			if (data == null) {
				
			}
			else {
				mMediaUri = data.getData();
			}
			if (requestCode == 11) {

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, mMediaUri, image));
				try{
					exif = new ExifInterface(mMediaUri.getPath());
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

				//Picasso.with(this).load(mMediaUri.toString()).centerCrop().resize(600, 650).into(image);
				image.setImageBitmap(b);
			}
			else if(requestCode == 1){

				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);

				fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, mMediaUri, image));
				try{
					exif = new ExifInterface(imagepath);
					int exifOrientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					degree = exifOrientationToDegrees(exifOrientation);
				}catch(Exception e){
					Toast.makeText(RequestProduct.this, "메모리가 부족한 듯... " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				options.inJustDecodeBounds = false;
				Bitmap b = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length, options);

				Matrix m = new Matrix();
				m.setRotate(degree, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

				image.setImageBitmap(b);
			}
		}
		else if (resultCode != RESULT_CANCELED) {
			return;
		}
	}

	public int exifOrientationToDegrees(int exifOrientation){
		Toast.makeText(RequestProduct.this, ""+exifOrientation, Toast.LENGTH_SHORT).show();
		if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
		{
			return 0;
		}
		else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
		{
			return 180;
		}
		else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
		{
			return 270;
		}
		return 0;
	}

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

}
