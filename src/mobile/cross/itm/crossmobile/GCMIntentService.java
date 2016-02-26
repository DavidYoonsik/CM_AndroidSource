package mobile.cross.itm.crossmobile;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import mobile.cross.itm.chatting.ChatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;


public class GCMIntentService extends GCMBaseIntentService { // 서비스를 상속받는다.
	
	class dbHelper extends SQLiteOpenHelper{

		public dbHelper(Context context, String name) {
			super(context, name, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// CREATE
			db.execSQL( "create table if not exists userInfo " +
					"(_id integer primary key autoincrement, " +
					"user_id text, " +
					"user_pw text, " +
					"user_pic text, " +
					"user_date text);" );
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// DROP
			db.execSQL(" drop table if exists userInfo ");
		}

	}
	
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

	private static final String TAG = "GCMIntentService";

	public static final String url = "http://117.17.188.74:8080/CMservlet/gcmregisterServlet";
	
	dbHelper helper;
	SQLiteDatabase db;
	String user_id, user_pic;
	
	/**
	 * Constructor
	 */
	public GCMIntentService() {
		super(ChatInfo.PROJECT_ID);
	}

	@Override
	public void onRegistered(Context context, String registrationId) {
		ChatInfo.RegistrationId = registrationId;
		sendRegid s = new sendRegid();
		s.execute(registrationId);
	}

	class sendRegid extends AsyncTask<String, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			helper = new dbHelper(getApplicationContext(), "userInfo.db");
			db = helper.getWritableDatabase();
			
			Cursor c = db.rawQuery(" select user_id, user_pic from userInfo ", null); // where _id < 10

			while(c.moveToNext()){
				user_id =  c.getString(0).toString();
				user_pic = c.getString(1).toString();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// Apache HTTP Reqeust
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				BasicNameValuePair bnvp = new BasicNameValuePair("user_id", user_id); // String name, String value ; each parameter may be null
				BasicNameValuePair bnvp2 = new BasicNameValuePair("reg_id", params[0]); 
				nvList.add(bnvp);
				nvList.add(bnvp2);

				HttpEntity entity = new UrlEncodedFormEntity(nvList, "utf-8");
				post.addHeader(entity.getContentType());
				post.setEntity(entity);

				client.execute(post);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	

	@Override
	public void onMessage(Context context, Intent intent) {

		int icon = R.drawable.ic_launcher;
		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if(extras.getString("type").toString().trim().equalsIgnoreCase("reply")){
				VariableManager.msg = (String) extras.get("msg");
				VariableManager.from = (String) extras.get("fro");
				VariableManager.when = (String) extras.get("when");
				
				String ext = Environment.getExternalStorageState();
				if(ext.equals(Environment.MEDIA_MOUNTED)){
					FileOutputStream fos = null;
					try{
						File f = new File(Environment.getExternalStorageDirectory(), "gcm");
						if (!f.exists()){
							f.mkdirs();
						}
						String path = f.getPath() + File.separator + "GCMIntentService";
						File f2 = new File(path);
						fos = new FileOutputStream(f2);
						String content = (String) extras.get("name") + ";" + 
										 (String) extras.get("message") + ";" + 
										 (String) extras.get("path") + ";" + 
										 (String) extras.get("email") + ";" + 
										 (String) extras.get("pdate") + ";" + 
										 (String) extras.get("price") + ";" + 
										 (String) extras.get("pindex") + ";" + 
										 (String) extras.get("ptype") + ";" +
										 (String) extras.get("status") + ";" +
										 (String) extras.get("method")+ ";" +
										 (String) extras.get("user_pic");
						fos.write(content.getBytes());
						
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						try{
							fos.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
				
				NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

				Notification notification = new Notification(icon, VariableManager.msg+
						" from "+VariableManager.from+
						", when "+VariableManager.when, System.currentTimeMillis());
				
				NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext());
				nb.setSmallIcon(icon);
				

				Intent intent2 = new Intent(getApplicationContext(), Main.class);
				//intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				intent2.setType("gcm");
				
				PendingIntent intent3 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
				
				notification.setLatestEventInfo(context, "S", VariableManager.msg+" from "+VariableManager.from+", when "+VariableManager.when, intent3);
				
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				
				// 진동추가
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				
				// 사운드추가
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.sound = Uri.parse("android.resource://" + getPackageName() + "sound.mp3");

				notificationManager.notify(0, notification);
				
			}else{
				
				VariableManager.msg2 = (String) extras.get("msg");	// msg
				VariableManager.from2 = (String) extras.get("fro");	// user_id
				VariableManager.when2 = (String) extras.get("when");	// time
				VariableManager.user_pic2 = (String) extras.get("user_pic");
				
				String ext = Environment.getExternalStorageState();
				if(ext.equals(Environment.MEDIA_MOUNTED)){
					FileOutputStream fos = null;
					try{
						File f = new File(Environment.getExternalStorageDirectory(), "gcm");
						if (!f.exists()){
							f.mkdirs();
						}
						String path = f.getPath() + File.separator + "GCMIntentService2";
						File f2 = new File(path);
						fos = new FileOutputStream(f2);
						String content = (String) extras.get("pro_name") + ";" + 
										 (String) extras.get("pro_detail") + ";" + 
										 (String) extras.get("pro_image") + ";" + 
										 VariableManager.from2 + ";" +
										 (String) extras.get("pro_time") + ";" + 
										 (String) extras.get("pro_price") + ";" + 
										 (String) extras.get("pro_index") + ";" + 
										 (String) extras.get("pro_type") + ";" +
										 (String) extras.get("pro_status") + ";" +
										 (String) extras.get("pro_method")+ ";" +
										 VariableManager.user_pic2 + ";" +
										 VariableManager.msg2 + ";" +
										 VariableManager.when2;
						fos.write(content.getBytes());
						
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						try{
							fos.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
				
				Intent i = new Intent(getApplicationContext(), ChatActivity.class);
				i.setType("chat");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
				
				/*NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

				Notification notification = new Notification(icon, VariableManager.msg2+
						" from "+VariableManager.from2+
						", when "+VariableManager.when2, System.currentTimeMillis());
				
				NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext());
				nb.setSmallIcon(icon);
				

				Intent intent2 = new Intent(getApplicationContext(),ChatActivity.class);
				//intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				intent2.setType("chat");
				
				PendingIntent intent3 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
				
				notification.setLatestEventInfo(context, "S", VariableManager.msg2+" from "+VariableManager.from2+", when "+VariableManager.when2, intent3);
				
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				
				// 진동추가
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				
				// 사운드추가
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.sound = Uri.parse("android.resource://" + getPackageName() + "sound.mp3");

				notificationManager.notify(0, notification);*/
			}
		}
	}

	/**
	 * Send status messages for toast display
	 * 
	 * @param context
	 * @param message
	 */
	/*static void sendToastMessage(Context context, String message) {
		Intent intent = new Intent(ChatInfo.TOAST_MESSAGE_ACTION);
		intent.putExtra("message", message);
		context.sendBroadcast(intent);
	}*/
	
	@Override
	public void onUnregistered(Context context, String registrationId) {
		Log.d(TAG, "onUnregistered called.");

		//sendToastMessage(context, "등록해지되었습니다.");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.d(TAG, "onError called.");

		//sendToastMessage(context, "에러입니다 : " + errorId);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.d(TAG, "onDeletedMessages called.");

		super.onDeletedMessages(context, total);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.d(TAG, "onRecoverableError called.");

		return super.onRecoverableError(context, errorId);
	}


}