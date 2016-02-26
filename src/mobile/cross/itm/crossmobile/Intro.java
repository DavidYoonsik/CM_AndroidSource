package mobile.cross.itm.crossmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mobile.cross.itm.upload.EnrollProduct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

class dbHelper extends SQLiteOpenHelper {

	public dbHelper(Context context) {
		super(context, "userInfo.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// CREATE
		db.execSQL("create table userInfo "
				+ "(_id integer primary key autoincrement, user_id text, user_pw text, user_pic text, user_date text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// DROP
		db.execSQL(" drop table if exists product ");
	}

}

public class Intro extends Activity {

	ImageView intro_image;

	dbHelper helper;
	SQLiteDatabase db;
	String user_id;
	String user_pw;
	String url = "http://117.17.188.74:8080/CMservlet/userInspector";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);

		// �׼ǹ� ����� �ڵ�
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// DB ������ �ҷ��� �������� ����� ���� ������ Identification�� �����Ѵ�.
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id, user_pw from userInfo ", null); // where
																					// _id
																					// <
																					// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
			user_pw = c.getString(1).toString();
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// �����δ� url, user_id, user_pw�� �Ķ���ͷ� �ؼ� ����Ѵ�. ������ ���� �޴� ���信 ����
				// �״��� �༱���� �����ȴ�.
				String[] arr = { url, user_id, user_pw };
				new NetworkClass().execute(arr);
			}
		}, 800);
		
		
	}

	class NetworkClass extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			StringBuffer buffer = new StringBuffer();
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader reader = null;

			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(params[0]);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				BasicNameValuePair user_id = new BasicNameValuePair("user_id",
						params[1]);
				BasicNameValuePair user_pw = new BasicNameValuePair("user_pw",
						params[2]);

				list.add(user_id);
				list.add(user_pw);
				HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
				post.addHeader(entity.getContentType());

				post.setEntity(entity);

				HttpResponse resp = client.execute(post);

				// ������ ���� ���� ������ ó���Ѵ�.
				is = resp.getEntity().getContent();
				isr = new InputStreamReader(is, "utf8");
				reader = new BufferedReader(isr);
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}

				buffer.append(str.toString());
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				try { // ����� �������� ��쿡�� ��Ʈ���� �ݾ� ���Ҽ��� ��ŷ�� �����־�� �Ѵ�.
					reader.close();
					isr.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return new String(buffer.toString());
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.trim().equalsIgnoreCase("success")) {

				Toast.makeText(Intro.this, user_id + " �� ȯ���մϴ�.",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intro.this, Main.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.setType("intro");
				startActivity(intent);

			} else {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						Intro.this);
				builder.setMessage("WELCOME NEWBIE ^^")
						.setTitle("�α����� �ʿ��մϴ�.")
						.setPositiveButton("Ȯ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(Intro.this,
												Login.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									}
								});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	}
}