package mobile.cross.itm.chatting;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.utils.ImageDownLoad;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivityAdapter extends BaseAdapter {

	ArrayList<HashMap<String, String>> list;
	HashMap<String, String> map;
	Context context;
	
	LayoutInflater inflater;
	ImageDownLoad imageLoader;
	
	public ChatActivityAdapter(ArrayList<HashMap<String, String>> list, Context context){
		this.list = list;
		this.context = context;
		this.imageLoader = new ImageDownLoad(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView msg;
		ImageView pic;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = convertView;
		if(v == null){
			v = inflater.inflate(R.layout.activity_chat_adapter, parent, false);
		}
		
		map = list.get(position);
		
		msg = (TextView)v.findViewById(R.id.msg);
		pic = (ImageView)v.findViewById(R.id.pic);
		
		msg.setText(map.get("user_id").toString() + ", " + map.get("msg").toString() + ", from " + map.get("other_id").toString());	
		imageLoader.DisplayImage(map.get("img"), pic);
		
		return v;
	}

}
