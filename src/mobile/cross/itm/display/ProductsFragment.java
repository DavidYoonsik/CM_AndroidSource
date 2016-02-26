package mobile.cross.itm.display;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;

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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class ProductsFragment extends Fragment {

	Button button;

	//ListView listView;
	GridView gridView;
	ProductArrayAdapter paa;

	JSONArray jsonarray;
	ArrayList<HashMap<String, String>> product_list;
	HashMap<String, String> product_map = new HashMap<String, String>();
	String url = "http://117.17.188.74:8080/CMservlet/DownloadServlet"; 

	SwipeRefreshLayout mSwipeRefreshLayout;
	
	dbHelper helper;
	SQLiteDatabase db;
	String user_pic = "", user_id = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		VariableManager.POSITION = 0;
		VariableManager.FLAG_PRODUCT = true;
		View rootView = inflater.inflate(R.layout.product_fragment,	container, false);
		//View header = inflater.inflate(R.layout.product_fragment_header, listView);
		//button = (Button)header.findViewById(R.id.button1);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(
				R.color.swipeRefresh1,
				R.color.swipeRefresh2,
				R.color.swipeRefresh3,
				R.color.swipeRefresh4);
		mSwipeRefreshLayout.setHapticFeedbackEnabled(true);
		gridView = (GridView)rootView.findViewById(R.id.gridView);
		//listView.addHeaderView(header);
		//listView.setHeaderDividersEnabled(true);
		return rootView;
	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			VariableManager.FLAG_PRODUCT = false;
			new DownloadJSON().execute();
		}
	};

	@Override
	public void onResume() {
		
		helper = new dbHelper(getActivity());
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where
		// _id <
		// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
			user_pic = c.getString(1).toString();			
		}
		
		//Toast.makeText(getActivity(), "ProductFragment_onResume()", Toast.LENGTH_SHORT).show();

		if(VariableManager.FLAG_PRODUCT){
			new DownloadJSON().execute();
			VariableManager.FLAG_PRODUCT = false;
		}else{
			gridView.setSelection(VariableManager.POSITION);
		}


		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private class DownloadJSON extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			if (mSwipeRefreshLayout.isRefreshing()) {
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Create an array
			product_list = new ArrayList<HashMap<String, String>>();
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
					map.put(VariableManager.PRODUCT_CATEGORY, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_STATUS, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_METHOD, jsonarray.getString(i).toString().trim());
					i++;
					map.put(VariableManager.PRODUCT_USER_PIC, jsonarray.getString(i).toString().trim());
					//map.put(VariableManager.PRODUCT_USER_PIC, user_pic.toString().trim());

					product_list.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			paa = new ProductArrayAdapter(getActivity(), product_list);
			gridView.setAdapter(paa);
		}
	}

	private JSONArray sendHttpRequest() {
		StringBuffer buffer = new StringBuffer();
		JSONArray jsonArray = null;
		try {
			// Apache HTTP Reqeust
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> nvList = new ArrayList<NameValuePair>();
			BasicNameValuePair bnvp0 = new BasicNameValuePair("user_id", user_id);
			nvList.add(bnvp0);
			HttpEntity entity = new UrlEncodedFormEntity(nvList, "utf-8");
			post.addHeader(entity.getContentType());
			post.setEntity(entity);

			HttpResponse resp = client.execute(post);

			InputStream is = resp.getEntity().getContent();
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