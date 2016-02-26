package mobile.cross.itm.detail;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageDownLoad;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetailedListViewAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;

	ArrayList<HashMap<String, String>> list;
	HashMap<String, String> map = new HashMap<String, String>();
	EditText reply_message;

	ImageDownLoad imageLoader;

	public ProductDetailedListViewAdapter(Context context, ArrayList<HashMap<String, String>> list, EditText reply_message){
		this.context = context;
		this.list = list;
		this.reply_message = reply_message;
		imageLoader = new ImageDownLoad(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
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
	public View getView(int position, View cv, ViewGroup pv) {
		map = list.get(position);
		
		TextView comment_id, comment_time, comment_message;
		ImageView profile_pic;
		
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.product_detailed_reply, pv, false);
		
		comment_id = (TextView)v.findViewById(R.id.comment_id);
		comment_time = (TextView)v.findViewById(R.id.comment_time);
		comment_message = (TextView)v.findViewById(R.id.comment_message);
		profile_pic = (ImageView)v.findViewById(R.id.profile_pic);
		
		comment_id.setText(map.get(VariableManager.RES_VISITOR));
		comment_time.setText(map.get(VariableManager.RES_TIME));
		comment_message.setText(map.get(VariableManager.RES_MESSAGE));
		imageLoader.DisplayImage(map.get(VariableManager.RES_PICTURE), profile_pic);
		
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] j = reply_message.getText().toString().trim().split("/");
				if(reply_message.getText().toString().equals(j[0])){
					int i = reply_message.length();
					reply_message.setSelection(i);
				}else{
					reply_message.setText(map.get(VariableManager.RES_VISITOR)+"/");
					int i = reply_message.length();
					reply_message.setSelection(i);
				}
				
			}
		});
		
		return v;
	}

}
