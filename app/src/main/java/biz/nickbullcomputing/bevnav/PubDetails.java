package biz.nickbullcomputing.bevnav;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class PubDetails extends Activity {
	//private int _pubID;
	private String _pubName;
	private String _pubAdd1;
	private String _pubAdd2;
	//private int _pubLocID;
	private String _pubPCode;
	private int _offerTypeID;
	private String _featuredOfferDetails;
	private ArrayList<String> _otherOfferDetails;
	

	//@SuppressWarnings("unused")
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_details);

		Bundle pubBundle = getIntent().getExtras();

		//_pubID = pubBundle.getInt("PubID");
		_pubName = pubBundle.getString("PubName");
		_pubAdd1 = pubBundle.getString("PubAdd1");
		_pubAdd2 = pubBundle.getString("PubAdd2");
       // _pubLocID = pubBundle.getInt("PubLocID");
        _pubPCode = pubBundle.getString("PubPCode");
        _offerTypeID = pubBundle.getInt("OfferTypeID");
        _featuredOfferDetails = pubBundle.getString("FeaturedOfferDetails");
        _otherOfferDetails = pubBundle.getStringArrayList("OtherOffers");

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);
		switch(_offerTypeID)
		{
			case 1: rl.setBackgroundResource(R.drawable.alchopops_background);
				break;
			case 2: rl.setBackgroundResource(R.drawable.beer_background);
				break;
			case 3: rl.setBackgroundResource(R.drawable.cocktails_background);
				break;
			case 4: rl.setBackgroundResource(R.drawable.shots_background);
				break;
			case 5: rl.setBackgroundResource(R.drawable.spirits_background);
				break;
			case 6: rl.setBackgroundResource(R.drawable.wine_background);
				break;
		}
		
		TextView txtPub_name =(TextView)findViewById(R.id.txtPub_name);

		TextView txtPub_address =(TextView)findViewById(R.id.txtPub_address);
		//TextView txtFeatured_Offer =(TextView)findViewById(R.id.txtFeatured_Offer);
		TextView txtFeatured_Offer_details =(TextView)findViewById(R.id.txtFeatured_Offer_details);
		TextView txtOther_Offers =(TextView)findViewById(R.id.txtOther_Offers);
		ListView lstOtherOffers = (ListView)findViewById(R.id.lstOtherOffers);

		//in your OnCreate() method
		txtPub_name.setText(_pubName);
		String Address = String.format(_pubAdd1 + "\n" + _pubAdd2 + "\n" + _pubPCode ); //_pubAdd1 + _pubAdd2 + _pubPCode
		txtPub_address.setText(Address);
		
		txtFeatured_Offer_details.setText(_featuredOfferDetails);
				
         
		if(_otherOfferDetails.size() != 0)
		{
			// Create The Adapter with passing ArrayList as 3rd parameter
	         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _otherOfferDetails);
	         // Set The Adapter
	         if(arrayAdapter != null)
	         {
	        	 lstOtherOffers.setAdapter(arrayAdapter); 
	         }
		}
        else
        {
        	txtOther_Offers.setVisibility(View.INVISIBLE);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pub_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
