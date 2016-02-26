package mobile.cross.itm.userinfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.cross.itm.crossmobile.Intro;
import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageDownLoad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UsersFragment extends Fragment {

	GridView gridView;
	LinearLayout user_info, item_sec, following_sec, follower_sec;
	ImageView profile_pic;
	TextView item_num, following_num, follower_num, host_id;
	Button log_out;
	UsersGridAdapter r;
	ProgressDialog mProgressDialog;

	JSONArray jsonarray;
	ArrayList<HashMap<String, String>> product_list, following_list, follower_list;
	HashMap<String, String> product_map = new HashMap<String, String>();

	String url = "http://117.17.188.74:8080/CMservlet/UserGridServlet"; 
	String user_id = "";
	String user_pic = "noimage";
	
	dbHelper helper;
	SQLiteDatabase db;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		VariableManager.FLAG_USER = true;
		View v = inflater.inflate(R.layout.user_fragment, container, false);
		
		user_info = (LinearLayout)v.findViewById(R.id.user_info);
		item_sec = (LinearLayout)v.findViewById(R.id.item_section);
		following_sec = (LinearLayout)v.findViewById(R.id.following_section);
		follower_sec = (LinearLayout)v.findViewById(R.id.follower_section);
		
		profile_pic = (ImageView)v.findViewById(R.id.profile_pic);
		log_out = (Button)v.findViewById(R.id.log_out);
		host_id = (TextView)v.findViewById(R.id.profile_email);
		item_num = (TextView)v.findViewById(R.id.item_num);
		following_num = (TextView)v.findViewById(R.id.following_num);
		follower_num = (TextView)v.findViewById(R.id.follower_num);
		gridView = (GridView)v.findViewById(R.id.gridView);
		return v;		
	}

	@Override
	public void onResume() {
		
		// TODO Auto-generated method stub
		// DB 내용을 불러와 서버와의 통신을 통해 유저의 Identification을 점검한다.
		helper = new dbHelper(getActivity());
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where
		// _id <
		// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
			user_pic = c.getString(1).toString();
		}
		
		if(VariableManager.FLAG_USER){
			new DownloadJSON().execute();	
			
			if(user_pic.toString().trim().equals("noimage")){
				profile_pic.setImageResource(R.drawable.ic_action_person);
			}else{
				new ImageDownLoad(getActivity()).DisplayImage(user_pic, profile_pic);
			}
			
			VariableManager.FLAG_USER = false;
		}else{
			gridView.setSelection(VariableManager.POSITION);
		}
		
		log_out.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.execSQL(" delete from userInfo where user_id = '" + user_id + "';");
				Intent intent = new Intent(getActivity(), Intro.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.setType("logout");
				startActivity(intent);
			}
		});
		
		
		user_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), UserInfoUpdate.class);
				startActivity(i);
			}
		});
		
		// follow function
		
		item_sec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DownloadJSON().execute();
			}
		});
		
		following_sec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		follower_sec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});

		super.onResume();
	}

	private class DownloadJSON extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// Create an array
			product_list = new ArrayList<HashMap<String, String>>();
			following_list = new ArrayList<HashMap<String, String>>();
			follower_list = new ArrayList<HashMap<String, String>>();
			// Retrieve JSON Objects from the given URL address
			jsonarray = sendHttpRequest();
			// JSONfunctions.getJSONfromURL("http://192.168.0.12:8080/servletTest/jsonparsetutorial.json");
			
			try {
				
				
				for (int i = 0; i < jsonarray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();

					map.put(VariableManager.PRODUCT_NAME, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_DETAIL, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_IMAGE, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_HOST, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_TIME, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_PRICE, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_INDEX, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_TYPE, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_STATUS, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_METHOD, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_USER_PIC, jsonarray.getString(i).toString().trim());
					user_pic = jsonarray.getString(i).toString().trim();
					
					product_list.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg) {
			
			new ImageDownLoad(getActivity()).DisplayImage(user_pic, profile_pic);
			
			item_num.setText(product_list.size()+"");
			host_id.setText(user_id);
			r = new UsersGridAdapter(getActivity(), product_list);
			gridView.setAdapter(r);
			gridView.setSelection(VariableManager.POSITION);
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
		}
		catch(Throwable t) {
			t.printStackTrace();
		} finally{
			try {
				jsonArray = new JSONArray(buffer.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
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
}
