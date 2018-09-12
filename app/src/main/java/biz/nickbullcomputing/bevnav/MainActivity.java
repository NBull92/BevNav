package biz.nickbullcomputing.bevnav;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import biz.nickbullcomputing.bevnav.ListViewAdapter.ViewHolder;
import biz.nickbullcomputing.bevnav.Classes.Bar;
import biz.nickbullcomputing.bevnav.Classes.Locations;
import biz.nickbullcomputing.bevnav.Classes.Offers;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity 
{
	static GoogleMap googleMap;
	
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    //ArrayList<HashMap<String, String>> locationList;
    
    List<Bar> _barList;
    List<Locations> _locationList;
    List<Offers> _offerList;
    
    
    // urls to get the lists
    private static final String url_all_bars = "http://192.168.0.12:8081/Android%20Test/android_connect/get_all_bars.php";
    private static final String url_all_locations = "http://192.168.0.12:8081/Android%20Test/android_connect/get_all_locations.php";
    private static final String url_all_offers = "http://192.168.0.12:8081/Android%20Test/android_connect/get_all_offers.php";
    private static final String url_all_images = "http://192.168.0.12:8081/Android%20Test/android_connect/get_all_images.php";
    
    // JSON Pubbar Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_Pubbars = "pubbar";
    private static final String TAG_PID = "Pubbar_ID";
    private static final String TAG_NAME = "Pubbar_Name";
    private static final String TAG_ADD1 = "Pubbar_AddressLine1";
    private static final String TAG_ADD2 = "Pubbar_AddressLine2";
    private static final String TAG_LocID = "Pubbar_LocationID";
    private static final String TAG_PCODE = "Pubbar_Postcode";
    private static final String TAG_LAT = "Pubbar_Latitude";
    private static final String TAG_LONG = "Pubbar_Longitude";
    private static final String TAG_TYPEID = "Pubbar_TypeID";  //might not do anything once offers are pulled through from there database
    
    //JSON Offer Node names
    private static final String TAG_OFFERS= "offers";
    private static final String TAG_OID = "Offer_ID";
    private static final String TAG_OTYPEID = "Offer_TypeID";
    private static final String TAG_PUBBARID = "Offer_PubbarID";
    private static final String TAG_DETAILS = "Offer_Details";
    private static final String TAG_MAIN = "Offer_isMain";
    
    // JSON Locations Node names
    private static final String TAG_LOCATIONS= "location";
    private static final String TAG_LID = "Location_ID";
    private static final String TAG_LNAME = "Location_Name";

    // JSON Image Node Names
    private static final String TAG_IMAGES = "images";
    private static final String TAG_IMAGE_ID = "Image_ID";
    private static final String TAG_IMAGE_BARID = "Image_BarID";
    private static final String TAG_IMAGE_IMAGE = "Image_Image";

    //offer types images paths
    int alchopops = R.drawable.ic_alchopops;
    int beers = R.drawable.ic_beers;
    int cocktails = R.drawable.ic_cocktails;
    int shots = R.drawable.ic_shots;
    int spirits = R.drawable.ic_spirits;
    int wine = R.drawable.ic_wine;
    
    // extras images paths
    //int skysports = R.drawable.ic_skysports;
    //int btsports = R.drawable.ic_btsports;
    //int pool = R.drawable.ic_pool;
    //int food = R.drawable.ic_food;
    //int dressCode = R.drawable.ic_dressCode;    
    
    // products JSONArray
    JSONArray bars = null;
    JSONArray locations = null;
    JSONArray offers = null;
    JSONArray images = null;
    
    MenuItem searchItem;
    
    TownSelector ts;
    ListView townList;
    ListViewAdapter townAdapter;
    String[] townID;    
    String[] townName;
    ArrayList<TownSelector> arraylist = new ArrayList<TownSelector>();
    ImageView testPopUp;
    ImageView imgBar;
    Button btnPopupClose;
    TextView txtBarName;
    TextView txtBarAddress;
    ListView lstOtherOffers;
    TextView txtFeatured_Offer;
    TextView txtFeatured_Offer_details;
    TextView txtOther_Offers;


    public static String currentLocationID ;    
       	    
    //For the nav bar drawer
//    private ListView mDrawerList;
//    private ArrayAdapter<String> mAdapter;
//    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;
//    private String mActivityTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        
        // Hashmap for ListView
        _barList = new ArrayList<Bar>();
        _offerList = new ArrayList<Offers>();
        _locationList = new ArrayList<Locations>(); 
     
        // Locate the ListView in listview_main.xml
        townList = (ListView) findViewById(R.id.listview);
        townList.setVisibility(View.INVISIBLE);
        testPopUp = (ImageView)findViewById(R.id.testPopUp);
        testPopUp.setVisibility(View.INVISIBLE);
        imgBar = (ImageView)findViewById(R.id.imgBar);
        imgBar.setVisibility(View.INVISIBLE);
        imgBar.setImageBitmap(null);
        imgBar.setBackground(null);
        btnPopupClose = (Button)findViewById(R.id.btnPopupClose);
        btnPopupClose.setVisibility(View.INVISIBLE);
        txtBarName = (TextView)findViewById(R.id.txtBarName);
        txtBarName.setVisibility(View.INVISIBLE);
        txtBarAddress = (TextView)findViewById(R.id.txtBarAddress);
        txtBarAddress.setVisibility(View.INVISIBLE);
        lstOtherOffers = (ListView) findViewById(R.id.lstOtherOffers);
        lstOtherOffers.setVisibility(View.INVISIBLE);
        txtFeatured_Offer = (TextView)findViewById(R.id.txtFeatured_Offer);
        txtFeatured_Offer.setVisibility(View.INVISIBLE);
        txtFeatured_Offer_details = (TextView)findViewById(R.id.txtFeatured_Offer_details);
        txtFeatured_Offer_details.setVisibility(View.INVISIBLE);
        txtOther_Offers = (TextView)findViewById(R.id.txtOther_Offers);
        txtOther_Offers.setVisibility(View.INVISIBLE);

        btnPopupClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testPopUp.setVisibility(View.INVISIBLE);
                imgBar.setVisibility(View.INVISIBLE);
                imgBar.setImageBitmap(null);
                btnPopupClose.setVisibility(View.INVISIBLE);
                txtBarName.setVisibility(View.INVISIBLE);
                txtBarAddress.setVisibility(View.INVISIBLE);
                lstOtherOffers.setVisibility(View.INVISIBLE);
                txtFeatured_Offer.setVisibility(View.INVISIBLE);
                txtFeatured_Offer_details.setVisibility(View.INVISIBLE);
                txtOther_Offers.setVisibility(View.INVISIBLE);
            }
        });

        townList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                currentLocationID = arraylist.get(position).getID().replace("}", "");
                searchItem.collapseActionView();
                _locationList.clear();
                arraylist.clear();
                _barList.clear();
                _offerList.clear();
                _locationList.clear();
                googleMap.clear();
                setUpMap();
                new LoadAllInfo().execute();
            }
        });

        //side navigation drawer code          
//        mDrawerList = (ListView)findViewById(R.id.navList);
//        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        mActivityTitle = getTitle().toString();
//        addDrawerItems();
//        setupDrawer();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }
    
    //add items to the list view of the nav bar drawer
//    private void addDrawerItems() 
//    {
//        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
//        
//        //Load all the drink types into one array
//        
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
//        mDrawerList.setAdapter(mAdapter);
//        
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
//        {
//        	
//        	//take the ID of the currently selected drink type and reload the map
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
//            {
//                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    
//    //Set up for the drawer
//    private void setupDrawer() 
//    {    	
//    	 mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
//
//             /** Called when a drawer has settled in a completely open state. */
//             public void onDrawerOpened(View drawerView) {
//                 super.onDrawerOpened(drawerView);
//                 getSupportActionBar().setTitle("Key");
//                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//             }
//
//             /** Called when a drawer has settled in a completely closed state. */
//             public void onDrawerClosed(View view) {
//                 super.onDrawerClosed(view);
//                 getSupportActionBar().setTitle(mActivityTitle);
//                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//             }
//         };
//
//         mDrawerToggle.setDrawerIndicatorEnabled(true);
//         mDrawerLayout.setDrawerListener(mDrawerToggle);
//    }
////    
//    
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }

    
	private void setUpMapIfNeeded() 
	{
    	// Do a null check to confirm that we have not already instantiated the map
		if(googleMap == null){
			// try to obtain the map from support map fragment
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

			if(googleMap != null)
	        {
				setUpMap();
		        new LoadAllInfo().execute();
			}
		}		
	}
	
    private void setUpMap() {
		// enable MyLocation Layer of the Map
        googleMap.setMyLocationEnabled(true);
        
        // get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        //set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(currentLocationID == null)
        {
            // Get current Location
            Location myLocation = locationManager.getLastKnownLocation(provider);

            // Get lattitude of the current location
            double latitude = myLocation.getLatitude();

            //get longitude of the current location
            double longitude = myLocation.getLongitude();

            //Create a LatLong object for the current location
            LatLng latLng = new LatLng(latitude,longitude);

            // show the current location in google map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }

	}
    
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    
    class LoadAllInfo extends AsyncTask<String, String, String> 
    {
        /**
         * Before starting background thread Show Progress Dialog
         * */
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Pubs & Bars. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        /**
         * getting All products from url
         * */
        
        protected String doInBackground(String... args) 
        {
            // Building Parameters
            List<NameValuePair> paramsBars = new ArrayList<NameValuePair>();
            List<NameValuePair> paramsOffers = new ArrayList<NameValuePair>();
            List<NameValuePair> paramsLocations = new ArrayList<NameValuePair>();
            List<NameValuePair> paramsImages = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            JSONObject jsonBars = jParser.makeHttpRequest(url_all_bars, "GET", paramsBars);
            JSONObject jsonOffers = jParser.makeHttpRequest(url_all_offers, "GET", paramsOffers);
            JSONObject jsonLocations = jParser.makeHttpRequest(url_all_locations, "GET", paramsLocations);
            JSONObject jsonImages = jParser.makeHttpRequest(url_all_images, "GET",paramsImages);
                     
            //Get all bars
            if(jsonBars != null)
			{
                // Check your log cat for JSON reponse
				try 
				{
			        // Checking for SUCCESS TAG
			        int success = jsonBars.getInt(TAG_SUCCESS);
			        
			        if (success == 1) {
			            // products found
			            // Getting Array of Bars
			        	bars = jsonBars.getJSONArray(TAG_Pubbars);
			        	
			            // looping through All Bars
			            for (int i = 0; i < bars.length(); i++) {
			                JSONObject c = bars.getJSONObject(i);
			                
			                // Storing each json item in variable
			                Bar newBar = new Bar();
			               	newBar.ID = Integer.parseInt(c.getString(TAG_PID));
			                newBar.Name = c.getString(TAG_NAME);
			                newBar.AddressLine1 = c.getString(TAG_ADD1);
			                newBar.AddressLine2 = c.getString(TAG_ADD2);
			                newBar.Postcode = c.getString(TAG_PCODE);
			                newBar.Latitude = Double.parseDouble(c.getString(TAG_LAT));
			                newBar.Longitude = Double.parseDouble(c.getString(TAG_LONG));
			                newBar.LocationID = Integer.parseInt(c.getString(TAG_LocID));
			                newBar.TypeID = Integer.parseInt(c.getString(TAG_TYPEID));			                
			                _barList.add(newBar);
			            }
			        } 			        
			    } catch (JSONException e) 
			    {
			        e.printStackTrace();
			    }
			}

            //Get all offer
            if(jsonOffers != null)
			{
                // Check your log cat for JSON response
				try 
				{
			        // Checking for SUCCESS TAG
			        int success = jsonOffers.getInt(TAG_SUCCESS);
			        
			        if (success == 1) {
			            // products found
			            // Getting Array of Offers
			        	offers = jsonOffers.getJSONArray(TAG_OFFERS);
			        	
			            // looping through All Offers
			        	for (int i = 0; i < offers.length(); i++) 
			        	{
			                JSONObject c = offers.getJSONObject(i);
			                
			                Offers newOffers = new Offers();			                
			                newOffers.ID = c.getInt(TAG_OID);
			                newOffers.TypeID = c.getInt(TAG_OTYPEID);
			                newOffers.BarID = c.getInt(TAG_PUBBARID);
			                newOffers.Details = c.getString(TAG_DETAILS);
			                
			                //check if details contains an dollar sign
			                if(newOffers.Details.contains("$"))
			                {
			                	//change dollar sign to pound sign using Unicode instead of �
			                	String tmp = newOffers.Details.replace("$", "\u00a3");
			                	newOffers.Details = tmp;
			                }
			                
			                if(c.getString(TAG_MAIN).equals("1"))
			                {
				                newOffers.IsMain = true;			                	
			                }
			                else
			                {
			                	newOffers.IsMain = false;
			                }
			                
			                _offerList.add(newOffers);				                 		                
			            }
			        } 			        
			    } catch (JSONException e) 
			    {
			        e.printStackTrace();
			    }
			}
            
            //Get all Locations
            if(jsonLocations != null)
			{
                // Check your log cat for JSON reponse
				try 
				{
			        // Checking for SUCCESS TAG
			        int success = jsonLocations.getInt(TAG_SUCCESS);
			        
			        if (success == 1) {
			            // products found
			            // Getting Array of Locations
			        	locations = jsonLocations.getJSONArray(TAG_LOCATIONS);
			        	
			            // looping through All Offers
			        	for (int i = 0; i < locations.length(); i++) 
			        	{
			                JSONObject c = locations.getJSONObject(i);
			                
			                Locations newLocation = new Locations();
			                newLocation.ID = c.getInt(TAG_LID);
			                newLocation.Name = c.getString(TAG_LNAME);
			                _locationList.add(newLocation);	
			            }
			        } 			        
			    } catch (JSONException e) 
			    {
			        e.printStackTrace();
			    }
			}

            //get all images
            if(jsonImages != null)
            {
                Log.d("HELLO", "theres images");
                // Check your log cat for JSON reponse
                try
                {
                    // Checking for SUCCESS TAG
                    int success = jsonImages.getInt(TAG_SUCCESS);

                    if (success == 1)
                    {
                        // products found
                        // Getting Array of Locations
                        images = jsonImages.getJSONArray(TAG_IMAGES);

                        Log.d("HELLO", Integer.toString(images.length()) );
                        // looping through All Offers
                        for (int i = 0; i < images.length(); i++) {
                            JSONObject c = images.getJSONObject(i);

                            Log.d("HELLO", "searching through");
                            for (int b = 0; b < _barList.size(); b++)
                            {
                                if(_barList.get(b).ID == c.getInt(TAG_IMAGE_BARID))
                                {
                                    //Get related bar image and save it
                                    String imageTemp  = c.getString(TAG_IMAGE_IMAGE);
                                    byte[] imageData = Base64.decode(imageTemp, Base64.DEFAULT);
                                    Bitmap bi = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, null);

                                    _barList.get(b).Image = bi;

                                    Log.d("HELLO", "Image Saved");
                                }
                            }
                        }
                    }
                }
                catch (JSONException e)
                {
                    Log.d("HELLO", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;            
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) 
        {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into map marker
                     * */

                    //setMapMarkers
                    townID = new String[_locationList.size()];
                    townName = new String[_locationList.size()];

                    for (int l = 0; l < (_locationList.size()); l++) //cycle through the list of locations
                    {    //add each ones ID and Name to the oppropriate list
                        townID[l] = Integer.toString(_locationList.get(l).ID);       //townID[l] = locationList.get(l).toString().split("Location_ID=")[1].split(",")[0];
                        townName[l] = _locationList.get(l).Name; // locationList.get(l).toString().split("Location_Name=")[1].split(",")[0].replace("}","");

                    }

                    for (int i = 0; i < townID.length; i++) {
                        ts = new TownSelector(townID[i], townName[i]);
                        // Binds all strings into an array
                        arraylist.add(ts);
                        //resort list alphabetically
                        Collections.sort(arraylist, new Comparator<TownSelector>() {
                            public int compare(TownSelector v1, TownSelector v2) {
                                return v1.getName().compareTo(v2.getName());
                            }
                        });
                    }

                    // Pass results to ListViewAdapter Class
                    townAdapter = new ListViewAdapter(MainActivity.this, arraylist);

                    if (townList != null) {
                        // Binds the Adapter to the ListView
                        townList.setAdapter(townAdapter);
                    }

                    for (int i = 0; i <= (_barList.size() - 1); i++) {
                        int offerType = 0;
                        Bar currentBar = null;

                        if (currentLocationID == null)
                        {
                            if (_barList.get(i).ID != 0) {
                                currentBar = _barList.get(i);

                                for (int o = 0; o <= (_offerList.size() - 1); o++) {
                                    if (_offerList.get(o).BarID != 0) {
                                        int currentOfferBarID = _offerList.get(o).BarID;

                                        if (currentOfferBarID == currentBar.ID) {
                                            //check if isMain offer (main = 1)
                                            if (_offerList.get(o).IsMain == true) {
                                                offerType = _offerList.get(o).TypeID;
                                            }
                                        }
                                    }
                                }

                                switch (offerType) {
                                    //case 1 is alchopops
                                    case 1:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(alchopops)).title(currentBar.Name).snippet("Alchopops"));
                                        break;
                                    // case 2 is beers
                                    case 2:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(beers)).title(currentBar.Name).snippet("Beers"));
                                        break;
                                    // case 3 is cocktails
                                    case 3:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(cocktails)).title(currentBar.Name).snippet("Cocktails"));
                                        break;
                                    // case 4 is shots
                                    case 4:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(shots)).title(currentBar.Name).snippet("Shots"));
                                        break;
                                    //case 5 is spirits
                                    case 5:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(spirits)).title(currentBar.Name).snippet("Spirits"));
                                        break;
                                    // case 6 is wine
                                    case 6:
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(wine)).title(currentBar.Name).snippet("Wine"));
                                        break;
                                }
                            }
                        }
                        else
                        {
                            if (_barList.get(i).ID != 0) {
                                currentBar = _barList.get(i);

                                if (currentBar.LocationID == Integer.parseInt(currentLocationID)) {
                                    for (int o = 0; o <= (_offerList.size() - 1); o++) {
                                        if (_offerList.get(o).BarID != 0) {
                                            //if offer_pubbarid = pubbar_id
                                            int currentOfferBarID = _offerList.get(o).BarID;

                                            if (currentOfferBarID == currentBar.ID) {
                                                //check if isMain offer (main = 1)
                                                if (_offerList.get(o).IsMain == true) {
                                                    offerType = _offerList.get(o).TypeID;
                                                }
                                            }
                                        }
                                    }
                                    switch (offerType) {
                                        //case 1 is alchopops
                                        case 1:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(alchopops)).title(currentBar.Name).snippet("Alchopops"));
                                            break;
                                        // case 2 is beers
                                        case 2:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(beers)).title(currentBar.Name).snippet("Beers"));
                                            break;
                                        // case 3 is cocktails
                                        case 3:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(cocktails)).title(currentBar.Name).snippet("Cocktails"));
                                            break;
                                        // case 4 is shots
                                        case 4:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(shots)).title(currentBar.Name).snippet("Shots"));
                                            break;
                                        //case 5 is spirits
                                        case 5:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(spirits)).title(currentBar.Name).snippet("Spirits"));
                                            break;
                                        // case 6 is wine
                                        case 6:
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(currentBar.Latitude, currentBar.Longitude)).icon(BitmapDescriptorFactory.fromResource(wine)).title(currentBar.Name).snippet("Wine"));
                                            break;
                                    }
                                }

                            }
                        }
                    }

                    for (int i = 0; i <= (_barList.size() - 1); i++)
                    {
                        if(_barList.get(i).LocationID != 0 && currentLocationID != null)
                        {
                            if (_barList.get(i).LocationID == Integer.parseInt(currentLocationID))
                            {
                                // Get current Location
                                //Location myLocation = _barList.get(0). //locationManager.getLastKnownLocation(provider);

                                // Get lattitude of the current location
                                double latitude = _barList.get(i).Latitude;

                                //get longitude of the current location
                                double longitude = _barList.get(i).Longitude;

                                //Create a LatLong object for the current location
                                LatLng latLng = new LatLng(latitude, longitude);

                                // show the current location in google map
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                // Zoom in the map
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                                break;
                            }
                        }
                    }

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {

                                if (latLng != null) {
                                    testPopUp.setVisibility(View.INVISIBLE);
                                    imgBar.setVisibility(View.INVISIBLE);
                                    imgBar.setImageBitmap(null);
                                    btnPopupClose.setVisibility(View.INVISIBLE);
                                    txtBarName.setVisibility(View.INVISIBLE);
                                    txtBarAddress.setVisibility(View.INVISIBLE);
                                    lstOtherOffers.setVisibility(View.INVISIBLE);
                                    txtFeatured_Offer.setVisibility(View.INVISIBLE);
                                    txtFeatured_Offer_details.setVisibility(View.INVISIBLE);
                                    txtOther_Offers.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                            public void onInfoWindowClick(Marker marker) {
                                Intent intent = new Intent(MainActivity.this, PubDetails.class);
                                // need to send some data over about the current bar

                                testPopUp.setVisibility(View.VISIBLE);

                                for (int i = 0; i < _barList.size(); i++) {
                                    if (_barList.get(i).Latitude == marker.getPosition().latitude) {
                                        if (_barList.get(i).Longitude == marker.getPosition().longitude) {
                                            ArrayList<String> otherOffers = new ArrayList<String>();

                                            for (int o = 0; o <= (_offerList.size() - 1); o++) {
                                                if (_offerList.get(o).BarID != 0) {
                                                    if (_offerList.get(o).BarID == _barList.get(i).ID) {
                                                        if (_offerList.get(o).IsMain == true) {
                                                            switch (_offerList.get(o).TypeID) {
                                                                case 1:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.alchopops_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                                case 2:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.beers_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                                case 3:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.cocktails_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                                case 4:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.shots_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                                case 5:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.spirits_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                                case 6:
                                                                    testPopUp.setVisibility(View.VISIBLE);
                                                                    testPopUp.setBackgroundResource(R.drawable.wine_popup_background);
                                                                    btnPopupClose.setVisibility(View.VISIBLE);
                                                                    txtBarName.setVisibility(View.VISIBLE);
                                                                    txtBarName.setText(_barList.get(i).Name);
                                                                    txtBarAddress.setVisibility(View.VISIBLE);
                                                                    if (_barList.get(i).AddressLine2 != null && !_barList.get(i).AddressLine2.isEmpty()) {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).AddressLine2 + "\n" + _barList.get(i).Postcode));
                                                                    } else {
                                                                        txtBarAddress.setText(String.format(_barList.get(i).AddressLine1 + "\n" + _barList.get(i).Postcode));
                                                                    }
                                                                    txtFeatured_Offer.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setVisibility(View.VISIBLE);
                                                                    txtFeatured_Offer_details.setText(_offerList.get(o).Details);
                                                                    imgBar.setVisibility(View.VISIBLE);
                                                                    if(_barList.get(i).Image != null)
                                                                    {
                                                                        imgBar.setImageBitmap(_barList.get(i).Image);
                                                                    }
                                                                    else
                                                                    {
                                                                        imgBar.setBackgroundResource(R.drawable.default_bar_image);
                                                                    }
                                                                    break;
                                                            }
                                                        } else {
                                                            otherOffers.add(_offerList.get(o).Details);
                                                        }
                                                    }
                                                }
                                            }
                                            if (otherOffers.size() != 0) {
                                                // Create The Adapter with passing ArrayList as 3rd parameter
                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, otherOffers);
                                                // Set The Adapter
                                                if (arrayAdapter != null) {
                                                    txtOther_Offers.setVisibility(View.VISIBLE);
                                                    lstOtherOffers.setVisibility(View.VISIBLE);
                                                    lstOtherOffers.setAdapter(arrayAdapter);
                                                }
                                            } else {
                                                txtOther_Offers.setVisibility(View.INVISIBLE);
                                                lstOtherOffers.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }

                );
            }
        }
    
   /** public void initMap()
    {
		searchItem.collapseActionView();
		locationList.clear();
		arraylist.clear();
		setUpMap();
		new LoadAllInfo().execute();
    }**/
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
         //Inflate the menu; this adds items to the action bar if it is present.
         //getMenuInflater().inflate(R.menu.main, menu);
         //return true;
    	 MenuInflater Inflater = getMenuInflater();
         Inflater.inflate(R.menu.main, menu);
         
         SearchView searchview = (SearchView) menu.findItem(R.id.action_search).getActionView();
         searchview.setOnQueryTextListener(new OnQueryTextListener()
         {
			@Override
			public boolean onQueryTextChange(String text) 
			{
				townAdapter.filter(text);
				return false;
			}
			
			@Override
			public boolean onQueryTextSubmit(String text) 
			{
				return false;
			}
         });
          
         //townAdapter.filter(charText) 
         searchItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand
                        townList.setVisibility(View.VISIBLE);

                        testPopUp.setVisibility(View.INVISIBLE);
                        imgBar.setVisibility(View.INVISIBLE);
                        imgBar.setImageBitmap(null);
                        btnPopupClose.setVisibility(View.INVISIBLE);
                        txtBarName.setVisibility(View.INVISIBLE);
                        txtBarAddress.setVisibility(View.INVISIBLE);
                        lstOtherOffers.setVisibility(View.INVISIBLE);
                        txtFeatured_Offer.setVisibility(View.INVISIBLE);
                        txtFeatured_Offer_details.setVisibility(View.INVISIBLE);
                        txtOther_Offers.setVisibility(View.INVISIBLE);

                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the action view is collapsed, reset the query
                        townList.setVisibility(View.INVISIBLE);
                        // Return true to allow the action view to collapse
                        return true;
                    }
                });

         return super.onCreateOptionsMenu(menu);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()) 
        {
	        case R.id.action_refresh:
	    		_locationList.clear();
	    		arraylist.clear();
	    		//currentLocationID = null;
	        	setUpMap();
	        	new LoadAllInfo().execute();        	
	            return true;
	        case R.id.action_settings:
	        	return true;
            case R.id.action_remove:
                if(currentLocationID != null)
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Location filter removed";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    currentLocationID = null;
                    _locationList.clear();
                    arraylist.clear();
                    //currentLocationID = null;
                    setUpMap();
                    new LoadAllInfo().execute();
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "No location filter to remove";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
        }
        // Activate the navigation drawer toggle 
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        
        return super.onOptionsItemSelected(item);
    }    
}
