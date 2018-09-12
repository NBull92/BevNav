package biz.nickbullcomputing.bevnav;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
	private List<TownSelector> locationlist = null;
	private ArrayList<TownSelector> arraylist;

	public ListViewAdapter(MainActivity mainActivity,
			List<TownSelector> locationlist) {
		mContext = (Context) mainActivity;
		this.locationlist = locationlist;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<TownSelector>();
		this.arraylist.addAll(locationlist);
	}

	public class ViewHolder {
		TextView locationID;
		TextView locationName;
	}

	@Override
	public int getCount() {
		return locationlist.size();
	}

	@Override
	public TownSelector getItem(int position) {
		return locationlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.listview_item, null);
			// Locate the TextViews in listview_item.xml
			holder.locationName = (TextView) view.findViewById(R.id.country);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
		holder.locationName.setText(locationlist.get(position).getName());

		// Listen for ListView Item Click

		return view;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		locationlist.clear();
		if (charText.length() == 0) {
			locationlist.addAll(arraylist);
		} else {
			for (TownSelector ts : arraylist) {
				if (ts.getName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					locationlist.add(ts);
				}
			}
		}
		notifyDataSetChanged();
	}

}