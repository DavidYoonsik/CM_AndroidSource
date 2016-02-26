package mobile.cross.itm.detail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.cross.itm.chatting.ChatActivity;
import mobile.cross.itm.crossmobile.Main;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ProductsDetailed extends Activity {

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

	ProgressDialog progressDialog;

	ImageView product_image, product_host_image;
	TextView product_name, product_price, product_detail, product_time, product_host, product_status, product_method;
	Button contact_button, reply_button, fol_button;
	EditText reply_message;
	ListView list;
	GridView grid, grid2;

	ImageDownLoad imageLoader;
	ProductDetailedListViewAdapter adapter;
	RecommendListAdapter reAdapter;
	RecommendListAdapter2 reAdapter2;
	
	//Viewflipper
	//ViewFlipper vf;
	//Adapter ad;
	//Button pre, next;

	ArrayList<HashMap<String, String>> arr_list;
	ArrayList<HashMap<String, String>> arr_list2, arr_list3;
	JSONArray jsonArray;
	HashMap<String, String> map = new HashMap<String, String>();
	HashMap<String, String> map2 = new HashMap<String, String>();

	String user_id, pro_image, pro_name, pro_price, pro_detail, pro_time, pro_host, pro_index, pro_type, pro_status, pro_method, pro_user_pic, reply_user_pic, msg, when;

	private String init_url = "http://117.17.188.74:8080/CMservlet/replyDownload";
	private String post_url = "http://117.17.188.74:8080/CMservlet/replyDownload2";
	private String del_url = "http://117.17.188.74:8080/CMservlet/productDelete";
	private String fol_url = "http://117.17.188.74:8080/CMservlet/followDownload";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_product_detailed_listview);
		setContentView(R.layout.activity_product_detailed_listview);

		Toast.makeText(ProductsDetailed.this, "onCreate", Toast.LENGTH_SHORT).show();
		
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();
		
		Cursor c = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where _id < 10

		while(c.moveToNext()){
			user_id =  c.getString(0).toString();
			reply_user_pic = c.getString(1).toString();
		}

		imageLoader = new ImageDownLoad(this);

		Intent i = getIntent();

		if(i.getType().toString().equals("gcm2")){
			File f = new File(Environment.getExternalStorageDirectory(), "gcm");
			FileInputStream fis = null;
			String[] content = null;
			byte[] data;
			try{
				fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService");
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
			pro_host = content[3].trim();		
			pro_time = content[4].trim();
			pro_price = content[5].trim();
			pro_index = content[6].trim();
			pro_type = content[7].trim();
			pro_status = content[8].trim();
			pro_method = content[9].trim();
			pro_user_pic = content[10].trim();

		}else if(i.getType().toString().equals("edit")){
			File f = new File(Environment.getExternalStorageDirectory(), "gcm");
			FileInputStream fis = null;
			String[] content = null;
			byte[] data;
			try{
				fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService");
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
			pro_host = content[3].trim();		
			pro_time = content[4].trim();
			pro_price = content[5].trim();
			pro_index = content[6].trim();
			pro_type = content[7].trim();
			pro_status = content[8].trim();
			pro_method = content[9].trim();
			pro_user_pic = content[10].trim();

		}else if(i.getType().toString().equals("chat")){
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
			pro_host = content[3].trim();		
			pro_time = content[4].trim();
			pro_price = content[5].trim();
			pro_index = content[6].trim();
			pro_type = content[7].trim();
			pro_status = content[8].trim();
			pro_method = content[9].trim();
			pro_user_pic = content[10].trim();
			msg = content[11].trim();
			when = content[12].trim();			

		}else{
			pro_image = i.getStringExtra(VariableManager.PRODUCT_IMAGE);
			pro_name = i.getStringExtra(VariableManager.PRODUCT_NAME);
			pro_price = i.getStringExtra(VariableManager.PRODUCT_PRICE);
			pro_detail = i.getStringExtra(VariableManager.PRODUCT_DETAIL);
			pro_time = i.getStringExtra(VariableManager.PRODUCT_TIME);
			pro_host = i.getStringExtra(VariableManager.PRODUCT_HOST);
			pro_index = i.getStringExtra(VariableManager.PRODUCT_INDEX);
			pro_type = i.getStringExtra(VariableManager.PRODUCT_TYPE);
			pro_status = i.getStringExtra(VariableManager.PRODUCT_STATUS);
			pro_method = i.getStringExtra(VariableManager.PRODUCT_METHOD);
			pro_user_pic = i.getStringExtra(VariableManager.PRODUCT_USER_PIC);
		}

		list = (ListView)findViewById(R.id.list);

		View h = getLayoutInflater().inflate(R.layout.products_detailed_header, null, false);
		product_image = (ImageView)h.findViewById(R.id.product_image);
		product_host_image = (ImageView)h.findViewById(R.id.product_host_image);
		product_name = (TextView)h.findViewById(R.id.product_name);
		product_price = (TextView)h.findViewById(R.id.product_price);
		product_detail = (TextView)h.findViewById(R.id.product_des);
		product_time = (TextView)h.findViewById(R.id.product_time);
		product_host = (TextView)h.findViewById(R.id.product_host_email);
		product_host_image = (ImageView)h.findViewById(R.id.product_host_image);
		reply_message = (EditText)h.findViewById(R.id.comment_msg);
		contact_button = (Button)h.findViewById(R.id.contact_bt);
		reply_button = (Button)h.findViewById(R.id.enter_comment_bt);
		fol_button = (Button)h.findViewById(R.id.fol_bt);
		product_status = (TextView)h.findViewById(R.id.product_status);
		product_method = (TextView)h.findViewById(R.id.product_method);

		// 상품 상세내용 채우기
		imageLoader.DisplayImage(pro_image, product_image);
		if(pro_user_pic.toString().trim().equals("noimage")){
			product_host_image.setImageResource(R.drawable.ic_action_person);
		}else{
			imageLoader.DisplayImage(pro_user_pic, product_host_image);
		}
		product_name.setText(pro_name);
		product_price.setText(pro_price+" WON");
		product_detail.setText(pro_detail);
		product_time.setText(pro_time);
		product_host.setText(pro_host);
		product_status.append(pro_status);
		product_method.append(pro_method);

		// 그리드뷰
		View f = getLayoutInflater().inflate(R.layout.product_detailed_footer, null, false);
		/*vf = (ViewFlipper)f.findViewById(R.id.fli);        
        
        pre = (Button)f.findViewById(R.id.pre);
        next = (Button)f.findViewById(R.id.next);
        
        pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vf.showPrevious();
			}
		});
        
        next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vf.showNext();
			}
		});*/
		
		grid = (GridView)f.findViewById(R.id.recommend_grid);
		grid2 = (GridView)f.findViewById(R.id.recommend_grid2);
		//reAdapter = new RecommendListAdapter(ProductsDetailed.this);
		//grid.setAdapter(reAdapter);

		list.addHeaderView(h);
		list.addFooterView(f);

		String[] arr = {pro_index, pro_type, user_id};
		
		new initHandler().execute(arr);		

		reply_button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// input method forced down
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(reply_message.getApplicationWindowToken(), 0);
					}
				}, 100);
				if(reply_message.getText().toString().trim().length() > 0){
					String[] arr = {pro_index, pro_type, user_id, reply_message.getText().toString().trim(), System.currentTimeMillis()+"", reply_user_pic};
					new postHandler().execute(arr);
				}else{
					Toast.makeText(ProductsDetailed.this, "내용이 없습니다.", Toast.LENGTH_SHORT).show();
					reply_message.setText("");
				}
			}
		});

		contact_button.setOnClickListener(new OnClickListener() {
			// 이 버튼을 누르면서 가져가야 할 데이터
			// user_id, 상대방 id, 상품의 index와 type, 방 생성 날짜(시간)
			// 채팅을 하는 사람의 id를 이용해서 방의 이름을 만들고 관련 상품의 정보를 매칭시킨다.
			@Override
			public void onClick(View v) {
				if(user_id.toString().equals(pro_host)){
					Toast.makeText(ProductsDetailed.this, "자신과의 대화는 미친짓입니다.", Toast.LENGTH_SHORT).show();
				}else{
					Intent i = new Intent(ProductsDetailed.this, ChatActivity.class);
					i.setType("chatactivity");
					i.putExtra(VariableManager.PRODUCT_NAME, pro_name);
					i.putExtra(VariableManager.PRODUCT_DETAIL, pro_detail);
					i.putExtra(VariableManager.PRODUCT_IMAGE, pro_image);
					i.putExtra(VariableManager.PRODUCT_HOST, pro_host);
					i.putExtra(VariableManager.PRODUCT_TIME, pro_time);
					i.putExtra(VariableManager.PRODUCT_PRICE, pro_price);
					i.putExtra(VariableManager.PRODUCT_INDEX, pro_index);
					i.putExtra(VariableManager.PRODUCT_TYPE, pro_type);
					i.putExtra(VariableManager.PRODUCT_STATUS, pro_status);
					i.putExtra(VariableManager.PRODUCT_METHOD, pro_method);
					i.putExtra(VariableManager.PRODUCT_USER_PIC, pro_user_pic);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					//startActivity(i);
				}

			}			
		});
		
		if(user_id.equals(pro_host)){
			fol_button.setVisibility(View.INVISIBLE);
		}
		
		fol_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				String[] arr = {fol_url, user_id, pro_host};
				new FollowClass().execute(arr);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//Toast.makeText(ProductsDetailed.this, "onActivityResult", Toast.LENGTH_SHORT).show();

		File f = new File(Environment.getExternalStorageDirectory(), "gcm");
		FileInputStream fis = null;
		String[] content = null;
		byte[] data2;
		try{
			fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService");
			data2 = new byte[fis.available()];

			while(fis.read(data2) != -1){

			}
			content = new String(data2).split(";");
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
		pro_host = content[3].trim();		
		pro_time = content[4].trim();
		pro_price = content[5].trim();
		pro_index = content[6].trim();
		pro_type = content[7].trim();
		pro_status = content[8].trim();
		pro_method = content[9].trim();
		pro_user_pic = content[10].trim();

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		
		Toast.makeText(ProductsDetailed.this, "onNewIntent", Toast.LENGTH_SHORT).show();
		
		File f = new File(Environment.getExternalStorageDirectory(), "gcm");
		FileInputStream fis = null;
		String[] content = null;
		byte[] data;
		try{
			fis = new FileInputStream(f.getPath() + File.separator + "GCMIntentService");
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
		pro_host = content[3].trim();		
		pro_time = content[4].trim();
		pro_price = content[5].trim();
		pro_index = content[6].trim();
		pro_type = content[7].trim();
		pro_status = content[8].trim();
		pro_method = content[9].trim();
		pro_user_pic = content[10].trim();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(user_id.equals(pro_host)){
			getMenuInflater().inflate(R.menu.products_detailed, menu);
		}else{
			getMenuInflater().inflate(R.menu.products_detailed2, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.edit:
			Intent i = new Intent(ProductsDetailed.this, ProductEdited.class);
			i.putExtra(VariableManager.PRODUCT_NAME, pro_name);
			i.putExtra(VariableManager.PRODUCT_DETAIL, pro_detail);
			i.putExtra(VariableManager.PRODUCT_IMAGE, pro_image);
			i.putExtra(VariableManager.PRODUCT_HOST, pro_host);
			i.putExtra(VariableManager.PRODUCT_TIME, pro_time);
			i.putExtra(VariableManager.PRODUCT_PRICE, pro_price);
			i.putExtra(VariableManager.PRODUCT_INDEX, pro_index);
			i.putExtra(VariableManager.PRODUCT_TYPE, pro_type);
			i.putExtra(VariableManager.PRODUCT_STATUS, pro_status);
			i.putExtra(VariableManager.PRODUCT_METHOD, pro_method);
			i.putExtra(VariableManager.PRODUCT_USER_PIC, pro_user_pic);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		case R.id.delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(ProductsDetailed.this);
			builder.setMessage("ㅜㅜ")
			.setTitle("정말 삭제하시겠습니까?")
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,	int which) {
					new DeleteClass().execute(del_url, pro_index, pro_type);
				}
			}).setNegativeButton("취소", null);
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		case R.id.rec:
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//follow
	class FollowClass extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ProductsDetailed.this);
			progressDialog.show();
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
				BasicNameValuePair p_index = new BasicNameValuePair("user_id", params[1]);
				BasicNameValuePair p_type = new BasicNameValuePair("fol_id", params[2]);

				list.add(p_index);
				list.add(p_type);
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
				
			} catch (Exception t) {
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

			return new String(buffer.toString()).trim();
		}

		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(ProductsDetailed.this, result.trim(), 0).show();
			if (result.trim().equals("success")) {
				Toast.makeText(ProductsDetailed.this, pro_host+"님을 팔로우 합니다.", Toast.LENGTH_SHORT).show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ProductsDetailed.this);
				builder.setMessage("실행과정에서 오류가 발생되었습니다.")
				.setTitle("다시 시도해 주세요.").setPositiveButton("확인", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			progressDialog.dismiss();
		}
	}
	
	//delete
	class DeleteClass extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ProductsDetailed.this);
			progressDialog.show();
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
				BasicNameValuePair p_index = new BasicNameValuePair("p_index", params[1]);
				BasicNameValuePair p_type = new BasicNameValuePair("p_type", params[2]);

				list.add(p_index);
				list.add(p_type);
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
				
			} catch (Exception t) {
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

			return new String(buffer.toString()).trim();
		}

		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(ProductsDetailed.this, result.trim(), 0).show();
			if (result.trim().equals("success")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(ProductsDetailed.this);
				builder.setMessage("환영합니다.")
				.setTitle("삭제가 정상저으로 수행되었습니다.")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int which) {
						Intent intent = new Intent(ProductsDetailed.this, Main.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.setType("intro");
						startActivity(intent);
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ProductsDetailed.this);
				builder.setMessage("실행과정에서 오류가 발생되었습니다.")
				.setTitle("다시 시도해 주세요.").setPositiveButton("확인", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			progressDialog.dismiss();
		}
	}
	
	/*@Override
	public void onBackPressed() {
		Intent i = new Intent(this, Main.class);
		i.setType("intro");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}*/

	private class postHandler extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ProductsDetailed.this);
			progressDialog.setTitle("Uploading...");
			progressDialog.setMessage("Data sending now");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			arr_list = new ArrayList<HashMap<String,String>>();
			jsonArray = responseUpload(post_url, params[0], params[1], params[2], params[3], params[4], params[5]);

			try{
				for(int i = 0; i < jsonArray.length(); i++){
					HashMap<String, String> response = new HashMap<String, String>();

					response.put(VariableManager.RES_VISITOR, jsonArray.get(i).toString());
					++i;
					response.put(VariableManager.RES_MESSAGE, jsonArray.get(i).toString());
					++i;
					response.put(VariableManager.RES_TIME, jsonArray.get(i).toString());
					++i;
					response.put(VariableManager.RES_PICTURE, jsonArray.get(i).toString());

					// arraylist 에 담는다.
					arr_list.add(response);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			reply_message.setText("");
			adapter = new ProductDetailedListViewAdapter(ProductsDetailed.this, arr_list, reply_message);
			list.setAdapter(adapter);
			progressDialog.dismiss();
		}

	}

	private JSONArray responseUpload(String url, String pindex, String ptype, String vid, String vresponse, String vtime, String vpic) {
		StringBuffer buffer = new StringBuffer();
		try {
			// Apache HTTP Reqeust
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvList = new ArrayList<NameValuePair>();
			BasicNameValuePair bnvp0 = new BasicNameValuePair("pindex", pindex);
			BasicNameValuePair bnvp1 = new BasicNameValuePair("ptype", ptype);
			BasicNameValuePair bnvp2 = new BasicNameValuePair("vid", vid);
			BasicNameValuePair bnvp3 = new BasicNameValuePair("vresponse", vresponse);
			BasicNameValuePair bnvp4 = new BasicNameValuePair("vtime", vtime);
			BasicNameValuePair bnvp5 = new BasicNameValuePair("vpic", vpic);

			// Add more
			nvList.add(bnvp0);
			nvList.add(bnvp1);
			nvList.add(bnvp2);
			nvList.add(bnvp3);
			nvList.add(bnvp4);
			nvList.add(bnvp5);
			HttpEntity entity = new UrlEncodedFormEntity(nvList, "utf-8");
			post.addHeader(entity.getContentType());
			post.setEntity(entity);

			// Connection
			HttpResponse resp = client.execute(post);

			// Read the response
			InputStream is  = resp.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(is, "utf8");
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				str.append(line + "\n");
			}
			is.close();
			buffer.append(str.toString());			
			// Done!
		}
		catch(Throwable t) {
			t.printStackTrace();
		} finally{
			try {
				jsonArray = new JSONArray(buffer.toString().trim());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// return buffer.toString();
		return jsonArray;
	}


	private class initHandler extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ProductsDetailed.this);
			progressDialog.setTitle("Uploading...");
			progressDialog.setMessage("Data sending now");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			arr_list = new ArrayList<HashMap<String,String>>();
			arr_list2 = new ArrayList<HashMap<String,String>>();
			arr_list3 = new ArrayList<HashMap<String,String>>();
			jsonArray = responseDownload(init_url, params[0], params[1], params[2]);
			if(jsonArray == null){
				return null;
			}else{
				try{
					for(int i = 0; i < jsonArray.length(); i++){	
						if(jsonArray.get(i).toString().equals(";")){
							i++;
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(VariableManager.PRODUCT_NAME, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_DETAIL, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_IMAGE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_HOST, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_TIME, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_PRICE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_INDEX, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_TYPE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_CATEGORY, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_STATUS, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_METHOD, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_USER_PIC, jsonArray.getString(i).toString().trim());
							arr_list2.add(map);
						}else if(jsonArray.get(i).toString().equals(":")){
							i++;
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(VariableManager.PRODUCT_NAME, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_DETAIL, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_IMAGE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_HOST, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_TIME, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_PRICE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_INDEX, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_TYPE, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_CATEGORY, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_STATUS, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_METHOD, jsonArray.getString(i).toString().trim());
							i++;
							map.put(VariableManager.PRODUCT_USER_PIC, jsonArray.getString(i).toString().trim());
							arr_list3.add(map);
						}else{
							HashMap<String, String> response = new HashMap<String, String>();
							response.put(VariableManager.RES_VISITOR, jsonArray.get(i).toString());
							++i;
							response.put(VariableManager.RES_MESSAGE, jsonArray.get(i).toString());
							++i;
							response.put(VariableManager.RES_TIME, jsonArray.get(i).toString());
							++i;
							response.put(VariableManager.RES_PICTURE, jsonArray.get(i).toString());
							arr_list.add(response);
						}
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			adapter = new ProductDetailedListViewAdapter(ProductsDetailed.this, arr_list, reply_message);
			list.setAdapter(adapter);
			
			reAdapter = new RecommendListAdapter(ProductsDetailed.this, arr_list2);
			grid.setAdapter(reAdapter);
			
			/*for(int i = 0; i < 3; i++){
				if(!arr_list3.isEmpty()){
					arr_list3.remove(0);
				}
			}*/
			
			reAdapter2 = new RecommendListAdapter2(ProductsDetailed.this, arr_list3);
			grid2.setAdapter(reAdapter2);
			/*ad = new Adapter(ProductsDetailed.this, arr_list2);
	        if(arr_list2.size()%3 == 0){
	        	for(int i = 0; i < arr_list2.size()/3; i++){
	            	vf.addView(ad.getView(i, null, null));
	            }
	        }else{
	        	for(int i = 0; i < arr_list2.size()/3+1; i++){
	            	vf.addView(ad.getView(i, null, null));
	            }
	        }*/
			
			progressDialog.dismiss();
		}
	}

	private JSONArray responseDownload(String url, String pindex, String ptype, String id) {
		StringBuffer buffer = new StringBuffer();
		InputStream is = null;
		InputStreamReader isr = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvList = new ArrayList<NameValuePair>();
			BasicNameValuePair bnvp = new BasicNameValuePair("pindex", pindex); // String name, String value ; each parameter may be null
			BasicNameValuePair bnvp1 = new BasicNameValuePair("ptype", ptype);
			BasicNameValuePair bnvp2 = new BasicNameValuePair("pid", id);

			nvList.add(bnvp);
			nvList.add(bnvp1);
			nvList.add(bnvp2);
			HttpEntity entity = new UrlEncodedFormEntity(nvList, "utf-8");
			post.addHeader(entity.getContentType());
			post.setEntity(entity);

			HttpResponse resp = client.execute(post);

			is  = resp.getEntity().getContent();
			isr = new InputStreamReader(is, "utf8");
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				str.append(line + "\n");
			}

			buffer.append(str.toString());			
		}
		catch(Throwable t) {
			t.printStackTrace();
		} finally{
			try {
				isr.close();
				is.close();
				jsonArray = new JSONArray(buffer.toString().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
}
