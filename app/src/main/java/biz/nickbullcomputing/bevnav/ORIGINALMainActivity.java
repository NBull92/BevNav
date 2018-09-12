package biz.nickbullcomputing.bevnav;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ORIGINALMainActivity extends ActionBarActivity {

	GoogleMap googleMap;

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> barList;
	ArrayList<HashMap<String, String>> locationList;
	ArrayList<HashMap<String, String>> offerList;

	// urls to get the lists
	private static final String url_all_bars = "http://192.168.0.7:8081/Android%20Test/android_connect/get_all_bars.php";
	// private static final String url_all_locations =
	// "http://192.168.0.7:8081/Android%20Test/android_connect/get_all_locations.php";
	private static final String url_all_offers = "http://192.168.0.7:8081/Android%20Test/android_connect/get_all_offers.php";

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
	private static final String TAG_TYPEID = "Pubbar_TypeID"; // might not do
																// anything once
																// offers are
																// pulled
																// through from
																// there
																// database

	// JSON Offer Node names
	private static final String TAG_OFFERS = "offers";
	private static final String TAG_OID = "Offer_ID";
	private static final String TAG_OTYPEID = "Offer_TypeID";
	private static final String TAG_PUBBARID = "Offer_PubbarID";
	private static final String TAG_DETAILS = "Offer_Details";
	private static final String TAG_MAIN = "Offer_isMain";

	// JSON Locations Node names
	// JSON Offers Node names
	// JSON OfferTypes Node names

	// offer types images paths
	int alchopops = R.drawable.ic_alchopops;
	int beers = R.drawable.ic_beers;
	int cocktails = R.drawable.ic_cocktails;
	int shots = R.drawable.ic_shots;
	int spirits = R.drawable.ic_spirits;
	int wine = R.drawable.ic_wine;

	// extras images paths
	// int skysports = R.drawable.ic_skysports;
	// int btsports = R.drawable.ic_btsports;
	// int pool = R.drawable.ic_pool;
	// int food = R.drawable.ic_food;
	// int dressCode = R.drawable.ic_dressCode;

	// products JSONArray
	JSONArray bars = null;
	JSONArray locations = null;
	JSONArray offers = null;

	// Our created menu to use
	private Menu myMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setContentView(R.layout.search);
		setUpMapIfNeeded();

		// Hashmap for ListView
		barList = new ArrayList<HashMap<String, String>>();
		locationList = new ArrayList<HashMap<String, String>>();
		offerList = new ArrayList<HashMap<String, String>>();

		Intent searchIntent = getIntent();
		if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
			String query = searchIntent.getStringExtra(SearchManager.QUERY);
			doCitySearch(query);
		}

	}

	private void doCitySearch(String query) {
		// TODO Auto-generated method stub
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map
		if (googleMap == null) {
			// try to obtain the map from support map fragment
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			if (googleMap != null) {
				setUpMap();

				// Loading products in Background Thread
				new LoadAllPubbars().execute();
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

		// Get current Location
		Location myLocation = locationManager.getLastKnownLocation(provider);

		// set map type
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Get lattitude of the current location
		double latitude = myLocation.getLatitude();

		// get longitude of the current location
		double longitude = myLocation.getLongitude();

		// Create a LatLong object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// show the current location in google map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		// googleMap.addMarker(new MarkerOptions().position(new
		// LatLng(latitude,longitude)).title("Location").snippet("You're here"));
	}

	/**
	 * private void getTempMarkers() { //TEMPORARY MARKERS
	 * 
	 * //Varsity googleMap.addMarker(new MarkerOptions().position(new
	 * LatLng(53.229355032635254
	 * ,-0.54121911525724)).icon(BitmapDescriptorFactory
	 * .fromResource(R.drawable.
	 * ic_alchopops)).title("Varsity").snippet("Alcopops"));
	 * 
	 * //Square Sail googleMap.addMarker(new MarkerOptions().position(new
	 * LatLng(53.22980135221815,-0.5467321276664)).icon(BitmapDescriptorFactory.
	 * fromResource
	 * (R.drawable.ic_beers)).title("Square Sail").snippet("Beers"));
	 * 
	 * //The Forum googleMap.addMarker(new MarkerOptions().position(new
	 * LatLng(53.229450400067485
	 * ,-0.53944808244706)).icon(BitmapDescriptorFactory
	 * .fromResource(R.drawable.
	 * ic_cocktails)).title("The Forum").snippet("Cocktails"));
	 * 
	 * //Revolution Bar googleMap.addMarker(new MarkerOptions().position(new
	 * LatLng(53.23037355681163,-0.5404654741287)).icon(BitmapDescriptorFactory.
	 * fromResource
	 * (R.drawable.ic_shots)).title("Revolution Bar").snippet("Shots"));
	 * 
	 * //Craft Bar & Kitchen googleMap.addMarker(new
	 * MarkerOptions().position(new
	 * LatLng(53.23091905852408,-0.5395779013633)).icon
	 * (BitmapDescriptorFactory.fromResource
	 * (R.drawable.ic_spirits)).title("Craft Bar & Kitchen"
	 * ).snippet("Spirits"));
	 * 
	 * //Horse and Groom googleMap.addMarker(new MarkerOptions().position(new
	 * LatLng
	 * (53.230468924243866,-0.54780310392379)).icon(BitmapDescriptorFactory
	 * .fromResource
	 * (R.drawable.ic_wine)).title("Craft Bar & Kitchen").snippet("Wine")); }
	 **/
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllPubbars extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ORIGINALMainActivity.this);
			pDialog.setMessage("Loading Bars. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> paramsBars = new ArrayList<NameValuePair>();
			List<NameValuePair> paramsOffers = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject jsonBars = jParser.makeHttpRequest(url_all_bars, "GET",
					paramsBars);
			JSONObject jsonOffers = jParser.makeHttpRequest(url_all_offers,
					"GET", paramsOffers);

			// Get all bars
			if (jsonBars != null) {
				// Check your log cat for JSON reponse
				Log.d("All Bars: ", jsonBars.toString());
				try {

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
							String id = c.getString(TAG_PID);
							String name = c.getString(TAG_NAME);
							String add1 = c.getString(TAG_ADD1);
							String add2 = c.getString(TAG_ADD2);
							String locID = c.getString(TAG_LocID);
							String pcode = c.getString(TAG_PCODE);
							String lat = c.getString(TAG_LAT);
							String longi = c.getString(TAG_LONG);
							String typeID = c.getString(TAG_TYPEID);

							// creating new HashMap
							HashMap<String, String> barListMap = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							barListMap.put(TAG_PID, id);
							barListMap.put(TAG_NAME, name);
							barListMap.put(TAG_ADD1, add1);
							barListMap.put(TAG_ADD2, add2);
							barListMap.put(TAG_LocID, locID);
							barListMap.put(TAG_PCODE, pcode);
							barListMap.put(TAG_LAT, lat);
							barListMap.put(TAG_LONG, longi);
							barListMap.put(TAG_TYPEID, typeID);

							// adding HashList to ArrayList
							barList.add(barListMap);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// Get all offer
			if (jsonOffers != null) {
				// Check your log cat for JSON reponse
				Log.d("All Offers: ", jsonOffers.toString());
				try {

					// Checking for SUCCESS TAG
					int success = jsonOffers.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Offers
						offers = jsonOffers.getJSONArray(TAG_OFFERS);

						// looping through All Offers
						for (int i = 0; i < offers.length(); i++) {
							JSONObject c = offers.getJSONObject(i);

							// Storing each json item in variable
							String id = c.getString(TAG_OID);
							String offerTypeID = c.getString(TAG_OTYPEID);
							String pubbarID = c.getString(TAG_PUBBARID);
							String details = c.getString(TAG_DETAILS);
							String main = c.getString(TAG_MAIN);

							// creating new HashMap
							HashMap<String, String> offerListMap = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							offerListMap.put(TAG_OID, id);
							offerListMap.put(TAG_OTYPEID, offerTypeID);
							offerListMap.put(TAG_PUBBARID, pubbarID);
							offerListMap.put(TAG_DETAILS, details);
							offerListMap.put(TAG_MAIN, main);

							// adding HashList to ArrayList
							offerList.add(offerListMap);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into map marker
					 * */
					// setMapMarkers
					for (int i = 0; i <= (barList.size() - 1); i++) {
						// check current locationID
						// int tmpLocationID = 0;

						//
						if (barList.get(i).toString().contains("Pubbar_Name=")) {
							Log.d("Hello",
									barList.get(i).toString()
											.split("Pubbar_Name=")[1].replace(
											"}", ""));
							// Log.d("Hello",
							// barList.get(i).toString().split("Pubbar_Latitude=")[1].split(",")[0]);
							// Log.d("Hello",
							// barList.get(i).toString().split("Pubbar_Longitude=")[1].split(",")[0]);
							// Log.d("Hello",
							// barList.get(i).toString().split("Pubbar_TypeID=")[1].split(",")[0]);
							int offerType = 0; // check Offers_PubbarID with L

							for (int o = 0; o <= (offerList.size() - 1); o++) {
								// Log.d("Hello",
								// "Does the previous line work");
								if (offerList.get(o).toString()
										.contains("Offer_PubbarID=")) {
									// if offer_pubbarid = pubbar_id
									int tmpOfferBarID = Integer
											.parseInt(offerList.get(o)
													.toString()
													.split("Offer_PubbarID=")[1]
													.split(",")[0]);
									int tmpBarID = Integer
											.parseInt(barList.get(i).toString()
													.split("Pubbar_ID=")[1]
													.split(",")[0]);
									if (tmpOfferBarID == tmpBarID) {
										// Log.d("Hello",
										// offerList.get(o).toString().split("Offer_isMain=")[1].split(",")[0].replace("}",""));
										// check if isMain offer (main = 1)
										if (Integer
												.parseInt(offerList.get(o)
														.toString()
														.split("Offer_isMain=")[1]
														.split(",")[0].replace(
														"}", "")) == 1) {
											// Log.d("Hello",
											// offerList.get(o).toString().split("Offer_TypeID=")[1].split(",")[0].replace("}",""));
											offerType = Integer
													.parseInt(offerList
															.get(o)
															.toString()
															.split("Offer_TypeID=")[1]
															.split(",")[0]
															.replace("}", ""));
										}
									}

								}
							}

							switch (offerType) {
							case 1:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(alchopops))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Alchopops"));
								break;
							case 2:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(beers))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Beers"));
								break;
							case 3:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(cocktails))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Cocktails"));
								break;
							case 4:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(shots))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Shots"));
								break;
							case 5:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(spirits))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Spirits"));
								break;
							default:
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Latitude=")[1]
																		.split(",")[0]),
																Double.parseDouble(barList
																		.get(i)
																		.toString()
																		.split("Pubbar_Longitude=")[1]
																		.split(",")[0])))
												.icon(BitmapDescriptorFactory
														.fromResource(wine))
												.title(barList.get(i)
														.toString()
														.split("Pubbar_Name=")[1]
														.replace("}", ""))
												.snippet("Wine"));
								break;
							}
						}
					}
				}
			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		// return true;
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.main, menu);

		myMenu = menu;

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		return super.onCreateOptionsMenu(menu);

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
		switch (item.getItemId()) {
		case R.id.action_refresh:
			setUpMap();
			new LoadAllPubbars().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
