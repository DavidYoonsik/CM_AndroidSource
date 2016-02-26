package mobile.cross.itm.detail;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageDownLoad;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendListAdapter2 extends BaseAdapter {

	Context context;
	LayoutInflater inflater;

	ArrayList<HashMap<String, String>> list;
	HashMap<String, String> map = new HashMap<String, String>();

	ImageDownLoad imageLoader;

	public RecommendListAdapter2(Context c , ArrayList<HashMap<String, String>> list) {
		context = c;
		this.list = list;
		imageLoader = new ImageDownLoad(context);
		
		
	}
	
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int k = 0;

		if(list.size() <= 3){
			k = list.size();
		}else{
			k = 3;
		}
		return k;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ImageView pro_image;
		TextView pro_name;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = convertView;
		if(v == null){
			v = inflater.inflate(R.layout.user_grid_fragment, parent, false);
		}
		
		map = list.get(position);
		
		pro_image = (ImageView)v.findViewById(R.id.pro_image);
		pro_name = (TextView)v.findViewById(R.id.pro_name);
		
		imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), pro_image);
		
		pro_name.setText(map.get(VariableManager.PRODUCT_NAME));
		
		v.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				map = list.get(position);
				VariableManager.POSITION = position;
				Intent intent = new Intent(context, ProductsDetailed.class);
				intent.setType("userf");
				intent.putExtra(VariableManager.PRODUCT_NAME, map.get(VariableManager.PRODUCT_NAME));
				intent.putExtra(VariableManager.PRODUCT_PRICE, map.get(VariableManager.PRODUCT_PRICE));
				intent.putExtra(VariableManager.PRODUCT_DETAIL, map.get(VariableManager.PRODUCT_DETAIL));
				intent.putExtra(VariableManager.PRODUCT_HOST, map.get(VariableManager.PRODUCT_HOST));
				intent.putExtra(VariableManager.PRODUCT_TIME, DateFormat.format("yyyy-MM-dd _ HH:mm:ss", Long.parseLong(map.get(VariableManager.PRODUCT_TIME))));
				intent.putExtra(VariableManager.PRODUCT_IMAGE, map.get(VariableManager.PRODUCT_IMAGE));
				intent.putExtra(VariableManager.PRODUCT_INDEX, map.get(VariableManager.PRODUCT_INDEX));
				intent.putExtra(VariableManager.PRODUCT_TYPE, map.get(VariableManager.PRODUCT_TYPE));
				intent.putExtra(VariableManager.PRODUCT_STATUS, map.get(VariableManager.PRODUCT_STATUS));
				intent.putExtra(VariableManager.PRODUCT_METHOD, map.get(VariableManager.PRODUCT_METHOD));
				intent.putExtra(VariableManager.PRODUCT_USER_PIC, map.get(VariableManager.PRODUCT_USER_PIC));
				
				context.startActivity(intent);
			}
		});
		
		return v;
		
	}
}