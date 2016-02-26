package mobile.cross.itm.crossmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

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
			db.execSQL(" drop table if exists userInfo ");
		}

	}

	dbHelper helper;
	SQLiteDatabase db;

	/*
	 * 회원가입
	 */
	EditText user_id;
	EditText user_pw;
	Button login_bt;
	TextView register_link;

	String url = "http://117.17.188.74:8080/CMservlet/userInspector";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// 액션바 숨기는 코드
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		user_id = (EditText) findViewById(R.id.email);
		user_pw = (EditText) findViewById(R.id.password);

		login_bt = (Button) findViewById(R.id.sign_in_bt);
		login_bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Date date = new Date();
				String user_date = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(date);

				db.execSQL(" insert into userInfo values (null, '"
						+ user_id.getText().toString() + "' , '"
						+ user_pw.getText().toString() + "' , 'noimage' , '"
						+ user_date + "'); ");

				String[] arr = { url, user_id.getText().toString(),
						user_pw.getText().toString() };
				new NetworkClass().execute(arr);
			}
		});

		register_link = (TextView) findViewById(R.id.sign_up_link);
		register_link.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(Login.this, Register.class);
				startActivity(intent);
			}
		});
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

				// 서버로 부터 받은 응답을 처리한다.
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
				try { // 통신이 에러났을 경우에도 스트림을 닫아 리소소의 릭킹을 막아주어야 한다.
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
			Toast.makeText(Login.this, result.trim(), 0).show();
			if (result.trim().equals("success")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						Login.this);
				builder.setMessage("환영합니다.")
				.setTitle("로그인이 정상 처리되었습니다.")
				.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						Intent intent = new Intent(Login.this,
								Main.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.setType("login");
						startActivity(intent);
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Login.this);
				builder.setMessage("아이디/비밀번호를 확인해 주십시요.")
				.setTitle("잘못된 아이디 입니다.").setPositiveButton("확인", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	}

}
