package mobile.cross.itm.chatting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.cross.itm.crossmobile.Main;
import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.detail.ProductsDetailed;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	
	class chatInfo extends SQLiteOpenHelper{

		public chatInfo(Context context) {
			super(context, "chatInfo.db", null, 1);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE
			db.execSQL( "create table chatInfo " +
					"(_id integer primary key autoincrement, user_id text, other_id text, msg text, time text);" );
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// DROP
			db.execSQL(" drop table if exists chatInfo ");
		}
	}
	
	class userInfo extends SQLiteOpenHelper{

		public userInfo(Context context) {
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
	
	chatInfo helper;
	userInfo helper2;
	SQLiteDatabase db;
	
	ListView listView;
	EditText chatmessage;
	Button chatbutton;

	String url = "http://117.17.188.74:8080/CMservlet/chatServlet";
	String pro_name, pro_detail, pro_image, pro_time, pro_price, pro_index, pro_type, pro_status, pro_method;
	
	ChatActivityAdapter caa;
	Context context;
	
	int index = -1;
	String user_id;
	String other_id;
	String user_pic, pro_user_pic, msg, from, when;
	long time = 0;
	boolean flag = true;
	
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> map;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);	
		listView = (ListView)findViewById(R.id.listView);
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		
		context = this;
		
		helper2 = new userInfo(this);
		db = helper2.getWritableDatabase();
		
		Cursor c2 = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where _id < 10

		while(c2.moveToNext()){
			user_id =  c2.getString(0).toString();
			user_pic = c2.getString(1).toString();
		}
		
		helper = new chatInfo(this);
		db = helper.getWritableDatabase();		
		
		// ProductsDetailed.java 에서 받아온 내용을 정리한다.
		Intent i = getIntent();
		
		if(i.getType().toString().equals("chat")){
			File f = new File(Environment.getExternalStorageDirectory(), "gcm");
			FileInputStream fis = null;
			String[] content = null;
			byte[] data;
			try{
				fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService2");
				data = new byte[fis.available()];

				while(fis.read(data) != -1){

				}
				content = new String(data).split(";");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pro_name = content[0].trim();
			pro_detail = content[1].trim();
			pro_image = content[2].trim();
			other_id = content[3].trim();		
			pro_time = content[4].trim();
			pro_price = content[5].trim();
			pro_index = content[6].trim();
			pro_type = content[7].trim();
			pro_status = content[8].trim();
			pro_method = content[9].trim();
			pro_user_pic = content[10].trim();
			msg = content[11].trim();
			when = content[12].trim();
			
			helper = new chatInfo(this);
			db = helper.getWritableDatabase();
			
			ContentValues row = new ContentValues();
			row.put("user_id", user_id);
			row.put("other_id", other_id);
			row.put("msg", msg);
			row.put("time", when);
			db.insert("chatInfo", null, row);
			
			/*Cursor c = db.rawQuery(" select user_id, other_id, msg from chatInfo where user_id = ? and other_id = ? ", new String[]{user_id, other_id});

			while(c.moveToNext()){
				map = new HashMap<String, String>();
				map.put("user_id", c.getString(0).toString());
				map.put("other_id", c.getString(1).toString());
				map.put("msg", c.getString(2).toString());
				map.put("img", user_pic);
				list.add(map);
			}
			
			caa = new ChatActivityAdapter(list, context);
			listView.setAdapter(caa);
			listView.setSelection(list.size()-1);*/
			
		}else{
			pro_name = i.getStringExtra(VariableManager.PRODUCT_NAME);
			other_id = i.getStringExtra(VariableManager.PRODUCT_HOST);
			pro_detail = i.getStringExtra(VariableManager.PRODUCT_DETAIL);
			pro_image = i.getStringExtra(VariableManager.PRODUCT_IMAGE);
			pro_time = i.getStringExtra(VariableManager.PRODUCT_TIME);
			pro_price = i.getStringExtra(VariableManager.PRODUCT_PRICE);
			pro_index = i.getStringExtra(VariableManager.PRODUCT_INDEX);
			pro_type = i.getStringExtra(VariableManager.PRODUCT_TYPE);
			pro_status = i.getStringExtra(VariableManager.PRODUCT_STATUS);
			pro_method = i.getStringExtra(VariableManager.PRODUCT_METHOD);
		}		
		
		Cursor c = db.rawQuery(" select user_id, other_id, msg from chatInfo where user_id = ? and other_id = ? ", new String[]{user_id, other_id});

		while(c.moveToNext()){
			map = new HashMap<String, String>();
			map.put("user_id", c.getString(0).toString());
			map.put("other_id", c.getString(1).toString());
			map.put("msg", c.getString(2).toString());
			map.put("img", user_pic);
			list.add(map);
		}
		
		
		caa = new ChatActivityAdapter(list, context);
		listView.setAdapter(caa);
		listView.setSelection(list.size()-1);

		chatmessage = (EditText)findViewById(R.id.chatmessage);
		chatbutton = (Button)findViewById(R.id.chatbutton);
		
		
		
		chatbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				time = System.currentTimeMillis();
				msg = chatmessage.getText().toString().trim();
				if(chatmessage.getText().toString().trim().length() > 0 && flag){
					String[] arr = {url, user_id, user_pic, other_id, chatmessage.getText().toString().trim(), time+"",	pro_name, pro_detail, pro_image, pro_time,	pro_price,	pro_index,	pro_type,	pro_status,	pro_method};
					new NetworkClass().execute();
				}else{
					Toast.makeText(context, "내용이 없습니다.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		chatmessage.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listView.setSelection(list.size()-1);
					}
				}, 240);
				
				
				return false;
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(context, ProductsDetailed.class);
		i.setType("chat");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		Toast.makeText(context, "onNewIntent", Toast.LENGTH_SHORT).show();
		
		if(intent.getType().toString().equals("chat")){
			File f = new File(Environment.getExternalStorageDirectory(), "gcm");
			FileInputStream fis = null;
			String[] content = null;
			byte[] data;
			try{
				fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService2");
				data = new byte[fis.available()];

				while(fis.read(data) != -1){

				}
				content = new String(data).split(";");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pro_name = content[0].trim();
			pro_detail = content[1].trim();
			pro_image = content[2].trim();
			other_id = content[3].trim();		
			pro_time = content[4].trim();
			pro_price = content[5].trim();
			pro_index = content[6].trim();
			pro_type = content[7].trim();
			pro_status = content[8].trim();
			pro_method = content[9].trim();
			pro_user_pic = content[10].trim();
			msg = content[11].trim();
			when = content[12].trim();
			
			helper = new chatInfo(this);
			db = helper.getWritableDatabase();
			
			ContentValues row = new ContentValues();
			row.put("user_id", user_id);
			row.put("other_id", other_id);
			row.put("msg", msg);
			row.put("time", when);
			db.insert("chatInfo", null, row);
			
			Cursor c = db.rawQuery(" select user_id, other_id, msg from chatInfo where user_id = ? and other_id = ? ", new String[]{user_id, other_id});

			while(c.moveToNext()){
				map = new HashMap<String, String>();
				map.put("user_id", c.getString(0).toString());
				map.put("other_id", c.getString(1).toString());
				map.put("msg", c.getString(2).toString());
				map.put("img", user_pic);
				list.add(map);
			}
			
			caa = new ChatActivityAdapter(list, context);
			caa.notifyDataSetChanged();
			listView.setSelection(list.size()-1);
		}
	}
	
	class NetworkClass extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			flag = false;
		}

		@Override
		protected String doInBackground(String... params) {

			StringBuffer buffer = new StringBuffer();
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader reader = null;

			try {
				HttpClient client = new DefaultHttpClient();
				
				// Multiple
				
				HttpPost post = new HttpPost(url);

				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				BasicNameValuePair bnvp0 = new BasicNameValuePair("user_id", user_id);
				BasicNameValuePair bnvp1 = new BasicNameValuePair("user_pic", user_pic);
				BasicNameValuePair bnvp2 = new BasicNameValuePair("other_id", other_id);
				BasicNameValuePair bnvp3 = new BasicNameValuePair("msg", msg);
				BasicNameValuePair bnvp4 = new BasicNameValuePair("time", time+"");
				
				BasicNameValuePair bnvp5 = new BasicNameValuePair("pro_name", pro_name);
				BasicNameValuePair bnvp6 = new BasicNameValuePair("pro_detail", pro_detail);
				BasicNameValuePair bnvp8 = new BasicNameValuePair("pro_image", pro_image);
				BasicNameValuePair bnvp9 = new BasicNameValuePair("pro_time", pro_time);
				BasicNameValuePair bnvp10 = new BasicNameValuePair("pro_price", pro_price);
				BasicNameValuePair bnvp11 = new BasicNameValuePair("pro_index", pro_index);
				BasicNameValuePair bnvp12 = new BasicNameValuePair("pro_type", pro_type);
				BasicNameValuePair bnvp13 = new BasicNameValuePair("pro_status", pro_status);
				BasicNameValuePair bnvp14 = new BasicNameValuePair("pro_method", pro_method);
				

				// Add more
				nvList.add(bnvp0);
				nvList.add(bnvp1);
				nvList.add(bnvp2);
				nvList.add(bnvp3);
				nvList.add(bnvp4);
				nvList.add(bnvp5);
				nvList.add(bnvp6);
				nvList.add(bnvp8);
				nvList.add(bnvp9);
				nvList.add(bnvp10);
				nvList.add(bnvp11);
				nvList.add(bnvp12);
				nvList.add(bnvp13);
				nvList.add(bnvp14);
				HttpEntity entity = new UrlEncodedFormEntity(nvList, "utf-8");
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
			if (result.trim().equals("success")) {
				
				// 응답이 잘 왔다면...
				map = new HashMap<String, String>();
				map.put("user_id", user_id);
				map.put("other_id", other_id);
				map.put("msg", chatmessage.getText().toString().trim());
				map.put("img", user_pic);
				map.put("time", time+"");
				list.add(map);
				
				ContentValues row = new ContentValues();
				row.put("user_id", user_id);
				row.put("other_id", other_id);
				row.put("msg", chatmessage.getText().toString().trim());
				row.put("time", time+"");
				db.insert("chatInfo", null, row);
				
				chatmessage.setText("");
				
				caa = new ChatActivityAdapter(list, context);
				//listView.setAdapter(caa);
				caa.notifyDataSetChanged();
				listView.setSelection(list.size()-1);
				
				flag = true;
				
				Toast.makeText(context, "메시지를 정상적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
				builder.setMessage("ERROR")
				.setTitle("Your Action is Undone.").setPositiveButton("확인", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	}
}
