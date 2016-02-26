package mobile.cross.itm.category;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.GridView;

public class CategoryDetailView extends Activity {

	Context context;
	GridView gridView;
	Button button;
	CategoryDetailViewAdapter cdva;

	ArrayList<HashMap<String, String>> product_list;
	HashMap<String, String> product_map = new HashMap<String, String>();
	JSONArray jsonarray;

	String url = "http://117.17.188.74:8080/CMservlet/CategoryGridServlet";
	String[] pro_category = new String[1];

	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_detail_view);
		VariableManager.FLAG_CATEGORY = false;
		context = this;

		gridView = (GridView)findViewById(R.id.gridView);
		button = (Button)findViewById(R.id.button);
		Intent i = getIntent();
		if(i.getType().toString().trim().equals("0")){
			pro_category[0] = "All";
			button.setText("All");
			new DownloadJSON().execute(pro_category);

		}else if(i.getType().toString().trim().equals("1")){
			pro_category[0] = "Clothings";
			button.setText("Clothings");
			new DownloadJSON().execute(pro_category);

		}else if(i.getType().toString().trim().equals("2")){
			pro_category[0] = "Cosmetics";
			button.setText("Cosmetics");
			new DownloadJSON().execute(pro_category);

		}else if(i.getType().toString().trim().equals("3")){
			pro_category[0] = "Books";
			button.setText("Books");
			new DownloadJSON().execute(pro_category);

		}else if(i.getType().toString().trim().equals("4")){
			pro_category[0] = "Home";
			button.setText("Home");
			new DownloadJSON().execute(pro_category);

		}else if(i.getType().toString().trim().equals("5")){
			pro_category[0] = "Pets";
			button.setText("Pets");
			new DownloadJSON().execute(pro_category);

		}				

	}
	
	@Override
	protected void onResume() {
		if(VariableManager.FLAG_CATEGORY){
			if(pro_category[0].toString().trim().equals("All")){
				pro_category[0] = "All";
				button.setText("All");
				new DownloadJSON().execute(pro_category);
			}else if(pro_category[0].toString().trim().equals("Clothings")){
				pro_category[0] = "Clothings";
				button.setText("Clothings");
				new DownloadJSON().execute(pro_category);
			}
			else if(pro_category[0].toString().trim().equals("Cosmetics")){
				pro_category[0] = "Cosmetics";
				button.setText("Cosmetics");
				new DownloadJSON().execute(pro_category);
			}
			else if(pro_category[0].toString().trim().equals("Books")){
				pro_category[0] = "Books";
				button.setText("Books");
				new DownloadJSON().execute(pro_category);
			}
			else if(pro_category[0].toString().trim().equals("Home")){
				pro_category[0] = "Home";
				button.setText("Home");
				new DownloadJSON().execute(pro_category);
			}
			else if(pro_category[0].toString().trim().equals("Pets")){
				pro_category[0] = "Pets";
				button.setText("Pets");
				new DownloadJSON().execute(pro_category);
			}
			VariableManager.FLAG_CATEGORY = false;
		}else{
			
		}
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String category = data.getStringExtra("category");
		if(VariableManager.FLAG_CATEGORY){
			if(category.toString().trim().equals("All")){
				pro_category[0] = "All";
				button.setText("All");
				new DownloadJSON().execute(pro_category);
			}else if(category.toString().trim().equals("Clothings")){
				pro_category[0] = "Clothings";
				button.setText("Clothings");
				new DownloadJSON().execute(pro_category);
			}
			else if(category.toString().trim().equals("Cosmetics")){
				pro_category[0] = "Cosmetics";
				button.setText("Cosmetics");
				new DownloadJSON().execute(pro_category);
			}
			else if(category.toString().trim().equals("Books")){
				pro_category[0] = "Books";
				button.setText("Books");
				new DownloadJSON().execute(pro_category);
			}
			else if(category.toString().trim().equals("Home")){
				pro_category[0] = "Home";
				button.setText("Home");
				new DownloadJSON().execute(pro_category);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category_detail_view, menu);
		return true;
	}

	private class DownloadJSON extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// Create an array
			product_list = new ArrayList<HashMap<String, String>>();
			// Retrieve JSON Objects from the given URL address
			jsonarray = sendHttpRequest(params[0]);
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

					product_list.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			cdva = new CategoryDetailViewAdapter(context, product_list);
			gridView.setAdapter(cdva);
		}
	}

	private JSONArray sendHttpRequest(String category) {
		StringBuffer buffer = new StringBuffer();
		JSONArray jsonArray = null;
		try {
			// Apache HTTP Reqeust
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			BasicNameValuePair bnvp = new BasicNameValuePair("pro_category", category);
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

}
