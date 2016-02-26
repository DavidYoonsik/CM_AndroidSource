package mobile.cross.itm.category;

import mobile.cross.itm.crossmobile.R;
import mobile.cross.itm.utils.ImageDownLoad;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductCategoryListViewAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;

	String[] arr = null;

	ImageDownLoad imageLoader;
	Intent intent;

	public ProductCategoryListViewAdapter(Context context, String[] arr){
		this.context = context;
		this.arr = arr;
		imageLoader = new ImageDownLoad(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.length;
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
	public View getView(final int position, View cv, ViewGroup pv) {

		ImageView img;


		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.category_fragment_item, pv, false);

		img = (ImageView) v.findViewById(R.id.category_img);
		if(position == 0){
			img.setImageResource(R.drawable.all);
			img.setPivotX(0);
			img.setPivotY(0);
		}else if(position == 1){
			img.setImageResource(R.drawable.cloth);
		}else if(position == 2){
			img.setImageResource(R.drawable.cosmetic);
		}else if(position == 3){
			img.setImageResource(R.drawable.book);
		}else if(position == 4){
			img.setImageResource(R.drawable.home);
		}else if(position == 5){
			img.setImageResource(R.drawable.pets_logo);
		}

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(position == 0){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("0");
					context.startActivity(intent);
				}else if(position == 1){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("1");
					context.startActivity(intent);
				}else if(position == 2){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("2");
					context.startActivity(intent);
				}else if(position == 3){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("3");
					context.startActivity(intent);
				}else if(position == 4){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("4");
					context.startActivity(intent);
				}else if(position == 5){
					intent = new Intent(context, CategoryDetailView.class);
					intent.setType("5");
					context.startActivity(intent);
				}
			}
		});

		return v;
	}

}
