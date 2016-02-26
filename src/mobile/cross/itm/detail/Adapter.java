package mobile.cross.itm.detail;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.crossmobile.VariableManager;
import mobile.cross.itm.utils.ImageDownLoad;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class Adapter extends BaseAdapter {

	//int[] image;
	Context context;

	LayoutInflater inflater;

	ArrayList<HashMap<String, String>> list;
	HashMap<String, String> map = new HashMap<String, String>();

	ImageDownLoad imageLoader;

	public Adapter(Context context, ArrayList<HashMap<String, String>> list){
		this.context = context;
		this.list = list;
		imageLoader = new ImageDownLoad(context);
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
		// TODO Auto-generated method stub

		Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = convertView;
		if(v == null){
			v = inflater.inflate(R.layout.rec, parent, false);
		}

		ImageView imageView, imageView2, imageView3;

		map = list.get(position);

		imageView = (ImageView)v.findViewById(R.id.imageView1);
		imageView2 = (ImageView)v.findViewById(R.id.imageView2);
		imageView3 = (ImageView)v.findViewById(R.id.imageView3);

		if(list.size()%3 == 0){
			for(int i = 0; i < list.size()/3; i++){
				map = list.get(3*position);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView);
				map = list.get(3*position+1);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView2);
				map = list.get(3*position+2);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView3);
			}
		}else if(list.size()%3 == 1){
			if(position < list.size()/3){
				for(int i = 0; i < list.size()/3; i++){
					map = list.get(3*position);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView);
					map = list.get(3*position+1);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView2);
					map = list.get(3*position+2);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView3);
				}
			}else{
				map = list.get(3*position);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView);
			}


		}else{
			if(position < list.size()/3){
				for(int i = 0; i < list.size()/3; i++){
					map = list.get(3*position);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView);
					map = list.get(3*position+1);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView2);
					map = list.get(3*position+2);
					imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView3);
				}
			}else{
				map = list.get(3*position);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView);
				map = list.get(3*position+1);
				imageLoader.DisplayImage(map.get(VariableManager.PRODUCT_IMAGE), imageView2);
			}
		}



		return v;
	}


}
