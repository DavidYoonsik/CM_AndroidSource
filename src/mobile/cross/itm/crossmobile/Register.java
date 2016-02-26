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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Register extends Activity {

	class dbHelper extends SQLiteOpenHelper {

		public dbHelper(Context context) {
			super(context, "userInfo.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE
			db.execSQL(
					"create table userInfo "
							+"(" +
							"_id integer primary key autoincrement, " +
							"user_id text, " +
							"user_pw text, " +
							"user_pic text, " +
							"user_date text" +
							");"
					);
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
	EditText user_id; // 아이디
	EditText user_pw; // 비밀번호
	EditText user_name; // 닉네임
	EditText user_phone; // 핸드폰
	String user_gender; // 성별
	RadioButton gender_male, gender_female;
	List<String> interests = new ArrayList<String>(); // 관심사 
	CheckBox interest1, interest2, interest3, interest4, interest5; // 관심사 선택지
	Button register; // 가입하기

	String url = "http://117.17.188.74:8080/CMservlet/registerServlet";
	String interest_list = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// 액션바 숨기는 코드
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		helper = new dbHelper(Register.this);
		db = helper.getWritableDatabase();

		user_id = (EditText) findViewById(R.id.email);
		user_pw = (EditText) findViewById(R.id.password);
		user_name = (EditText) findViewById(R.id.name);
		user_phone = (EditText) findViewById(R.id.phone);

		gender_male = (RadioButton) findViewById(R.id.male);
		gender_male.setChecked(true);
		if(gender_male.isChecked()){
			user_gender = VariableManager.GENDER_M;
		}
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

		interest1 = (CheckBox) findViewById(R.id.clothings);
		interest1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_CLOTHES);
				}else {
					//user_interest.put(VariableManager.INT_CLOTHES, false);
				}
			}
		});
		interest2 = (CheckBox) findViewById(R.id.cosmetics);
		interest2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_COSMETIC);
				}else {
					//user_interest.put(VariableManager.INT_COSMETIC, false);
				}
			}
		});
		interest3 = (CheckBox) findViewById(R.id.books);
		interest3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_BOOK);
				}else {
					//user_interest.put(VariableManager.INT_BOOK, false);
				}
			}
		});
		interest4 = (CheckBox) findViewById(R.id.home);
		interest4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_HOME_GADGET);
				}else {
					//user_interest.put(VariableManager.INT_HOME_GADGET, false);
				}
			}
		});
		interest5 = (CheckBox) findViewById(R.id.pets);
		interest5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					interests.add(VariableManager.INT_PET);
				}else {
					//user_interest.put(VariableManager.INT_CLOTHES, false);
				}
			}
		});

		register = (Button) findViewById(R.id.sign_up_bt);
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Date date = new Date();
				String user_date = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(date);

				db.execSQL(" insert into userInfo values (null, '"
						+ user_id.getText().toString() + "' , '"
						+ user_pw.getText().toString() + "' , 'noimage' , '"
						+ user_date + "'); ");

				for(int i = 0; i < interests.size(); i++){
					if(i != interests.size()-1){
						interest_list += interests.get(i)+";";
					}else{
						interest_list += interests.get(i);
					}
				}

				String[] arr = { url, user_id.getText().toString(), user_pw.getText().toString(), user_gender, interest_list };
				new NetworkClass().execute(arr);
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
				BasicNameValuePair user_id = new BasicNameValuePair("user_id", params[1]);
				BasicNameValuePair user_pw = new BasicNameValuePair("user_pw", params[2]);
				BasicNameValuePair gender = new BasicNameValuePair("gender", params[3]);

				// Interest Test
				BasicNameValuePair interests = new BasicNameValuePair("interest", params[4]);

				list.add(user_id);
				list.add(user_pw);
				list.add(gender);
				list.add(interests);

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
			//Toast.makeText(Register.this, result.trim(), 0).show();
			if (result.trim().equalsIgnoreCase("success")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						Register.this);
				builder.setMessage(result)
				.setTitle("정상적으로 등록되었습니다.")
				.setPositiveButton("즐기세요!",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						Intent intent = new Intent(
								Register.this, Main.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.setType("register");
						startActivity(intent);
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Register.this);
				builder.setMessage(result).setTitle("이미 사용중인 아이디 입니다.")
				.setPositiveButton("Try Again!", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	}
}
