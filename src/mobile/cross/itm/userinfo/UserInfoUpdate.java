package mobile.cross.itm.userinfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageVolumeResizing;
import mobile.cross.itm.utils.ImageDownLoad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class UserInfoUpdate extends Activity {

	ImageView user_pic;
	TextView user_name;
	EditText selfone;
	Button changeUpload;
	RadioButton gender_male, gender_female, push_on, push_off;
	CheckBox interest1, interest2, interest3, interest4, interest5;
	List<String> interests = new ArrayList<String>(); // 관심사 
	String user_gender, user_interest, user_image, user_say, push_check; // 성별
	String[] interest = new String[4];

	ProgressDialog mProgressDialog;
	JSONArray jsonarray;
	ArrayList<HashMap<String, String>> product_list;
	HashMap<String, String> product_map = new HashMap<String, String>();
	String url = "http://117.17.188.74:8080/CMservlet/UserInfoServlet";
	String url2 = "http://117.17.188.74:8080/CMservlet/UserInfoUpdateServlet";
	String user_id = "";
	String interest_list = "";
	String imagepath = "";
	String degree = "";

	dbHelper helper;
	SQLiteDatabase db;
	ImageDownLoad imageLoader;
	
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int MEDIA_TYPE_IMAGE = 4;
	
	HttpURLConnection conn = null;
	InputStream is = null;
	OutputStream os = null;
	FileInputStream fis = null;
	ByteArrayOutputStream bos = null;
	
	Uri mMediaUri;
	byte[] fileBytes = {0};
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_fragment_edit_profile);
		imageLoader = new ImageDownLoad(this);
		// DB 내용을 불러와 서버와의 통신을 통해 유저의 Identification을 점검한다.
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where
		// _id <
		// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
			user_image = c.getString(1).toString();
		}
		
		

		user_pic = (ImageView)findViewById(R.id.profile_pic);
		user_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserInfoUpdate.this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				builder.setCancelable(true); // cancel available
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		
		
		user_name = (TextView)findViewById(R.id.profile_name);
		user_name.setText(user_id);

		gender_male = (RadioButton) findViewById(R.id.male);
		gender_male.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				user_gender = VariableManager.GENDER_M;

			}
		});
		gender_female = (RadioButton) findViewById(R.id.female);
		gender_female.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				user_gender = VariableManager.GENDER_F;
			}
		});
		
		

		push_on = (RadioButton) findViewById(R.id.push_on);
		push_on.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				push_check = "on";

			}
		});
		push_off = (RadioButton) findViewById(R.id.push_off);
		push_off.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				push_check = "off";
			}
		});
		
		

		interest1 = (CheckBox) findViewById(R.id.clothings);
		interest1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_CLOTHES);
				}else {
					interests.remove(VariableManager.INT_CLOTHES);
				}
			}
		});
		interest2 = (CheckBox) findViewById(R.id.cosmetics);
		interest2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_COSMETIC);
				}else {
					interests.remove(VariableManager.INT_COSMETIC);
				}
			}
		});
		interest3 = (CheckBox) findViewById(R.id.books);
		interest3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_BOOK);
				}else {
					interests.remove(VariableManager.INT_BOOK);
				}
			}
		});
		interest4 = (CheckBox) findViewById(R.id.home);
		interest4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_HOME_GADGET);
				}else {
					interests.remove(VariableManager.INT_HOME_GADGET);
				}
			}
		});
		
		interest5 = (CheckBox) findViewById(R.id.pets);
		interest5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_PET);
				}else {
					interests.remove(VariableManager.INT_PET);
				}
			}
		});
		
		
		
		selfone = (EditText)findViewById(R.id.selfone);
		
		
		changeUpload = (Button)findViewById(R.id.changeUpload);
		changeUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

				for(int i = 0; i < interests.size(); i++){
					if(i != interests.size()-1){
						interest_list += interests.get(i)+";";
					}else{
						interest_list += interests.get(i);
					}
				}
				
				String[] arr  = {user_id, user_gender, push_check, selfone.getText().toString(), interest_list};
				new NetworkClass().execute(arr);
			}
		});
		
		// 사용자 정보 DB에서 불러옴
		new DownloadJSON().execute();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_info_update, menu);
		return true;
	}
	
	class NetworkClass extends AsyncTask<String, Void, Void> {

		String user_pic_url = System.currentTimeMillis() + "_" + user_id + ".jpg";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				Charset chars = Charset.forName("UTF-8");
				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(url2);
				MultipartEntity multiPart = new MultipartEntity();
				multiPart.addPart("user_id", new StringBody(params[0], chars));
				multiPart.addPart("user_gender", new StringBody(params[1], chars));
				multiPart.addPart("push_check", new StringBody(params[2], chars));
				multiPart.addPart("selfone", new StringBody(params[3], chars));
				multiPart.addPart("interest", new StringBody(params[4], chars));
				multiPart.addPart("degree", new StringBody(degree, chars));
				multiPart.addPart("file1", new ByteArrayBody(fileBytes, user_pic_url));

				post.setEntity(multiPart);

				client.execute(post);

			} catch (Exception t) {
				t.printStackTrace();
			} finally {
				
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			db.execSQL(" update userInfo set user_pic = '" + "http://117.17.188.74:8080/CMservlet/users/"+user_pic_url + "'");
			VariableManager.FLAG_USER = true;
			VariableManager.FLAG_PRODUCT = true;
			// "http://117.17.188.74:8080/CMservlet/users/"+user_pic_url
			finish();
			super.onPostExecute(result);
		}
	}

	private class DownloadJSON extends AsyncTask<Void, Void, Void> {

		@Override
		protected synchronized Void doInBackground(Void... params) {
			// Retrieve JSON Objects from the given URL address
			jsonarray = sendHttpRequest();
			try {

				for (int i = 0; i < jsonarray.length(); i++) {
					user_gender = jsonarray.getString(i).toString().trim();
					i++;
					user_interest = jsonarray.getString(i).toString().trim();
					i++;
					push_check = jsonarray.getString(i).toString().trim();
					i++;
					user_image = jsonarray.getString(i).toString().trim();
					i++;
					user_say = jsonarray.getString(i).toString().trim();
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			// Image 선택
			if(user_image.toString().trim().equals("noimage")){
				user_pic.setImageResource(R.drawable.ic_action_person);
			}else{
				imageLoader.DisplayImage(user_image, user_pic);
			}
			
			// Gender 선택
			if(user_gender.toString().trim().equals(VariableManager.GENDER_M)){
				gender_male.setChecked(true);
			}else{
				gender_female.setChecked(true);
			}
			
			// Push 허용 선택
			if(push_check.toString().trim().equals("on")){
				push_on.setChecked(true);
			}else{
				push_off.setChecked(true);
			}
			
			// Interest 체크
			interest = user_interest.split(";");
			for(int i = 0; i< interest.length; i++){
				interests.add(interest[i]);
				if(interest[i].toString().trim().equals("0")){
					interest1.setChecked(true);
				}else if(interest[i].toString().trim().equals("1")){
					interest2.setChecked(true);
				}else if(interest[i].toString().trim().equals("2")){
					interest3.setChecked(true);
				}else if(interest[i].toString().trim().equals("3")){
					interest4.setChecked(true);
				}else if(interest[i].toString().trim().equals("4")){
					interest5.setChecked(true);
				}
			}
			
			selfone.setText(user_say);
		}
	}

	private JSONArray sendHttpRequest() {
		StringBuffer buffer = new StringBuffer();
		JSONArray jsonArray = null;
		try {
			// Apache HTTP Reqeust
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			BasicNameValuePair bnvp = new BasicNameValuePair("user_id", user_id);
			list.add(bnvp);


			HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
			post.addHeader(entity.getContentType());
			post.setEntity(entity);

			HttpResponse resp = client.execute(post);

			entity = resp.getEntity();

			InputStream is = entity.getContent();
			InputStreamReader isr = new InputStreamReader(is, "utf8");
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				str.append(line + "\n");
			}
			is.close();
			buffer.append(str.toString());
			
			/*URL imageUrl = new URL(user_image);
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
			
			ExifInterface exif = null;
			
			exif = new ExifInterface(f2.getPath());
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			//degree = exifOrientationToDegrees(exifOrientation) + "";
			degree = "0";*/
			
		}
		catch(Exception t) {
			t.printStackTrace();
		} finally{
			try {
				/*bos.close();
				is.close();
				conn.disconnect();*/
				jsonArray = new JSONArray(buffer.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
	
	/*class PictureTemp extends AsyncTask<Void, Void, Void>{

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
				if (!f.exists()) {
					if (!f.mkdirs()) {
						return null;
					}
				}
				URL imageUrl = new URL(user_image);
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
				
				ExifInterface exif = null;
				
				exif = new ExifInterface(f2.getPath());
				int exifOrientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
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

	}*/

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
				String appName = UserInfoUpdate.this
						.getString(R.string.app_name);
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

					fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing.getByteArrayFromFile(this, mMediaUri, user_pic));
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
					Picasso.with(this).load(mMediaUri).resize(150, 150).centerCrop().noFade().into(user_pic);
					//product_image.setImageBitmap(b);

				} else if (requestCode == TAKE_PHOTO_REQUEST) {

					Intent mediaScanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					mediaScanIntent.setData(mMediaUri);
					sendBroadcast(mediaScanIntent);

					fileBytes = ImageVolumeResizing.reduceImageForUpload(ImageVolumeResizing
							.getByteArrayFromFile(this, mMediaUri, user_pic));
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
					user_pic.setImageBitmap(b);
				}
			} else if (resultCode != RESULT_CANCELED) {
				return;
			}
		}
		
}
