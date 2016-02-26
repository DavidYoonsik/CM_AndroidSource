package mobile.cross.itm.crossmobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mobile.cross.itm.category.CategoryFragment;
import mobile.cross.itm.detail.ProductsDetailed;
import mobile.cross.itm.display.ProductsFragment;
import mobile.cross.itm.upload.EnrollProduct;
import mobile.cross.itm.upload.RequestProduct;
import mobile.cross.itm.userinfo.UsersFragment;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class Main extends FragmentActivity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	ActionBar actionBar;

	String user_id;
	String reg_id;
	String url = "http://117.17.188.74:8080/CMservlet/gcmregisterServlet";
	
	int position;

	dbHelper helper;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// DB 내용을 불러와 서버와의 통신을 통해 유저의 Identification을 점검한다.
		helper = new dbHelper(this);
		db = helper.getWritableDatabase();

		Cursor c = db.rawQuery(" select user_id from userInfo ", null); // where
		// _id <
		// 10

		while (c.moveToNext()) {
			user_id = c.getString(0).toString();
		}

		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewPager.setCurrentItem(1);
			}
		}, 10);

		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
				Main.this.position = position;
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}

		Intent intent = getIntent();
		if (intent.getType().toString().equals("intro")) {
			// 등록을 하지 않을 것입니다. 이미 완료했다는 가정을 합니다.
		} else if (intent.getType().toString().equals("login")) {
			registerDevice();
		} else if (intent.getType().toString().equals("register")) {
			registerDevice();
		} else if (intent.getType().toString().equals("gcm")){
			Intent intent2 = new Intent(Main.this, ProductsDetailed.class);
			intent2.setType("gcm2");
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
		} else{
			if(intent.getType().toString().startsWith("image/")){
				Intent intent2 = new Intent(this, EnrollProduct.class);
				Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				if(imageUri != null){
					intent2.setData(imageUri);
					intent2.setType("image/");
					intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent2);
				}
				
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Toast.makeText(this, "onNewIntent", 0).show();
		Intent intent2 = new Intent(Main.this, ProductsDetailed.class);
		intent2.setType("gcm2");
		intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onPrepareOptionsMenu(menu);
	}*/

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: // PS(상품등록)
				Intent intent1 = new Intent(Main.this, EnrollProduct.class);
				intent1.setType("from_Main1");
				startActivityForResult(intent1, 0);
				break;
			case 1: // PR(상품요청)
				Intent intent2 = new Intent(Main.this, RequestProduct.class);
				intent2.setType("from_Main2");
				startActivity(intent2);
				break;
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.product_upload:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(R.array.pro_type_choices, mDialogListener);
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int req_code, int res_code, Intent data) {
		Toast.makeText(Main.this, "sdfs", Toast.LENGTH_LONG).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewPager.setCurrentItem(1);
			}
		}, 1);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new CategoryFragment();
			case 1:
				return new ProductsFragment();
			case 2:
				return new UsersFragment();
			}

			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}

		public int getIcon(int position) {
			switch(position) {
			case 0:
				return R.drawable.ic_action_search;
			case 1:
				return R.drawable.ic_action_list;
			case 2:
				return R.drawable.ic_action_account;
			}
			return position;
		}
	}

	@Override
	public void onBackPressed() {

		// Toast.makeText(this, "" + VariableManager.ct,
		// Toast.LENGTH_SHORT).show();
		if (System.currentTimeMillis() > VariableManager.ct + 4000) {
			VariableManager.ct = System.currentTimeMillis();
			Toast.makeText(this, "One more click, if you want to go out.",
					Toast.LENGTH_SHORT).show();
		} else if (System.currentTimeMillis() <= VariableManager.ct + 4000) {
			finish();
		}
	}

	public void registerDevice() {
		// 보낼 준비...
		sendRegid s = new sendRegid();
		// 디바이스 체크
		GCMRegistrar.checkDevice(this);
		// 매니페스트 체크 개발 옵션
		GCMRegistrar.checkManifest(this);

		if (GCMRegistrar.isRegistered(this)) {
			reg_id = GCMRegistrar.getRegistrationId(this);
			if (!reg_id.equals("")) {
				s.execute(reg_id);
			}
		} else {
			GCMRegistrar.register(this, ChatInfo.PROJECT_ID);
			Toast.makeText(this, "새로 등록하였습니다.", Toast.LENGTH_SHORT).show();
		}
	}

	class sendRegid extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				// Apache HTTP Reqeust
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				BasicNameValuePair bnvp1 = new BasicNameValuePair("user_id",
						user_id);
				BasicNameValuePair bnvp2 = new BasicNameValuePair("reg_id",
						params[0]);
				list.add(bnvp1);
				list.add(bnvp2);

				HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
				post.addHeader(entity.getContentType());
				post.setEntity(entity);

				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
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
