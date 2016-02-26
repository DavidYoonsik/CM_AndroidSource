package mobile.cross.itm.display;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.detail.ProductsDetailed;
import mobile.cross.itm.utils.ImageFileCache;
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

public class ProductArrayAdapter extends BaseAdapter {

	Context context;
	ArrayList<HashMap<String, String>> product_list;
	ImageDownLoad imageLoader;
	HashMap<String, String> product_map = new HashMap<String, String>();

	LayoutInflater inflater;
	
	ImageFileCache fileCache;

	public ProductArrayAdapter(Context context, ArrayList<HashMap<String, String>> list) { // ArrayList<HashMap<String, String>> list
		this.context = context;
		this.product_list = list;
		this.imageLoader = new ImageDownLoad(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return product_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View cView, ViewGroup pView) {

		TextView PRODUCT_NAME;
		TextView PRODUCT_PRICE;
		TextView PRODUCT_HOST;
		TextView PRODUCT_TIME;
		ImageView PRODUCT_IMAGE, PROFILE_IMAGE, PRODUCT_REC;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = cView;
		if(v == null){
			v = inflater.inflate(R.layout.product_fragment_item, pView, false);
		}

		product_map = product_list.get(position);

		
		PRODUCT_IMAGE = (ImageView) v.findViewById(R.id.product_image);
		PROFILE_IMAGE = (ImageView) v.findViewById(R.id.profile_pic);
		PRODUCT_NAME = (TextView) v.findViewById(R.id.product_name);
		PRODUCT_PRICE = (TextView) v.findViewById(R.id.product_price);
		PRODUCT_HOST = (TextView) v.findViewById(R.id.profile_id);
		PRODUCT_TIME = (TextView)v.findViewById(R.id.product_time);

		imageLoader.DisplayImage(product_map.get(VariableManager.PRODUCT_IMAGE), PRODUCT_IMAGE);
		if(product_map.get(VariableManager.PRODUCT_USER_PIC).toString().trim().equals("noimage")){
			PROFILE_IMAGE.setImageResource(R.drawable.ic_action_person);
		}else{
			imageLoader.DisplayImage(product_map.get(VariableManager.PRODUCT_USER_PIC), PROFILE_IMAGE);
		}
		
		/*if(product_map.get("rec").equals("t")){
			PRODUCT_REC = (ImageView) v.findViewById(R.id.r);
			PRODUCT_REC.setImageResource(R.drawable.r);
		}*/
		
		
		//File f = fileCache.getFile(product_map.get(VariableManager.PRODUCT_IMAGE));
		//Picasso.with(context).load().resize(640, 640).centerCrop().noFade().into(PRODUCT_IMAGE);
		
		// 뷰에 내용 뿌려주기
		PRODUCT_NAME.setText(product_map.get(VariableManager.PRODUCT_NAME));
		PRODUCT_PRICE.setText(product_map.get(VariableManager.PRODUCT_PRICE)+"원");
		PRODUCT_HOST.setText(product_map.get(VariableManager.PRODUCT_HOST));
		PRODUCT_TIME.setText(DateFormat.format("yyyy:mm:dd", Long.parseLong(product_map.get(VariableManager.PRODUCT_TIME))));
		//PRODUCT_TIME.setText("방금 전");
		
		// Capture ListView item click
		PRODUCT_IMAGE.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				product_map = product_list.get(position);
				VariableManager.POSITION = position;
				Intent intent = new Intent(context, ProductsDetailed.class);
				intent.setType("fragment");
				intent.putExtra(VariableManager.PRODUCT_NAME, product_map.get(VariableManager.PRODUCT_NAME));
				intent.putExtra(VariableManager.PRODUCT_PRICE, product_map.get(VariableManager.PRODUCT_PRICE));
				intent.putExtra(VariableManager.PRODUCT_DETAIL, product_map.get(VariableManager.PRODUCT_DETAIL));
				intent.putExtra(VariableManager.PRODUCT_HOST, product_map.get(VariableManager.PRODUCT_HOST));
				intent.putExtra(VariableManager.PRODUCT_TIME, DateFormat.format("yyyy-MM-dd _ HH:mm:ss", Long.parseLong(product_map.get(VariableManager.PRODUCT_TIME))));
				intent.putExtra(VariableManager.PRODUCT_IMAGE, product_map.get(VariableManager.PRODUCT_IMAGE));
				intent.putExtra(VariableManager.PRODUCT_INDEX, product_map.get(VariableManager.PRODUCT_INDEX));
				intent.putExtra(VariableManager.PRODUCT_TYPE, product_map.get(VariableManager.PRODUCT_TYPE));
				intent.putExtra(VariableManager.PRODUCT_STATUS, product_map.get(VariableManager.PRODUCT_STATUS));
				intent.putExtra(VariableManager.PRODUCT_METHOD, product_map.get(VariableManager.PRODUCT_METHOD));
				intent.putExtra(VariableManager.PRODUCT_USER_PIC, product_map.get(VariableManager.PRODUCT_USER_PIC));
				context.startActivity(intent);
			}
		});
		return v;
	}

}
