package biz.nickbullcomputing.bevnav;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleItemView extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Declare Variables
		TextView txtrank;
		TextView txtcountry;
		String rank;
		String country;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_item_view);
		// Retrieve data from MainActivity on item click event
		Intent i = getIntent();
		// Get the results of rank
		rank = i.getStringExtra("id");
		// Get the results of country
		country = i.getStringExtra("name");

		// Locate the TextViews in singleitemview.xml
		txtrank = (TextView) findViewById(R.id.rank);
		txtcountry = (TextView) findViewById(R.id.country);

		// Load the results into the TextViews
		txtrank.setText(rank);
		txtcountry.setText(country);
	}
}
