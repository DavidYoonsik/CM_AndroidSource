package mobile.cross.itm.category;

import mobile.cross.itm.crossmobile.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CategoryFragment extends ListFragment {
	
	ListView listView;
	ProductCategoryListViewAdapter pcl;
	String[] arr = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.category_fragment, container, false);

		listView = (ListView)rootView.findViewById(android.R.id.list);
		
		arr = getResources().getStringArray(R.array.category);
		
		rootView.setFitsSystemWindows(true);
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		
		pcl = new ProductCategoryListViewAdapter(getListView().getContext(), arr);
		listView.setAdapter(pcl);
		
		super.onResume();
	}
}
