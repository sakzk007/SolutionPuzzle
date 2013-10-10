package com.anees.solutionpuzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	String screen_name = null;
	EditText et_screen_name;
	ProgressDialog wait;
	CheckInternet ci;
	Boolean isInternetPresent = false;
	public GetFeedTask myAsyncTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_screen_name = (EditText) findViewById(R.id.et_screen);
		Button btn_get_feed = (Button) findViewById(R.id.btn_get_feed);
		ci = new CheckInternet(getApplicationContext());
		btn_get_feed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isInternetPresent = ci.isConnectingToInternet();
				if (isInternetPresent) {
					screen_name = et_screen_name.getText().toString().trim();
					if (screen_name.length() == 0 || screen_name == null) {
						Toast.makeText(getApplicationContext(),
								"Enter Screen Name", Toast.LENGTH_SHORT).show();
					} else {
						if (myAsyncTask == null) {
							myAsyncTask = new GetFeedTask();
							myAsyncTask.execute(screen_name);
						}
					}
				} else {
					showAlert();
				}

			}

			// Alert If Internet Not Present
			private void showAlert() {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				// set title
				alertDialogBuilder.setTitle("Internet not connected!");

				// set dialog message
				alertDialogBuilder
						.setMessage("Connect to internet! \n\t Try Again!")
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										// MainActivity.this.finish();
									}
								});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();

			}
		});
	}

	protected class GetFeedTask extends AsyncTask<String, Void, Void> {
		String profile_image_url;
		ArrayList<Retweets> retweet = new ArrayList<Retweets>();
		boolean process_status = true;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			wait = new ProgressDialog(MainActivity.this);
			wait.setCancelable(false);
			wait.setMessage("Processing Please wait..");
			wait.setIndeterminate(true);
			wait.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			String URL_PROFILE_IMAGE = "https://api.twitter.com/1.1/users/show.json?screen_name="
					+ params[0];
			String URL_TWEETED_IDS = "https://api.twitter.com/1.1/statuses/user_timeline.json?suppress_response_codes&trim_user=true&include_entities=false&include_rts=true&exclude_replies=true&count=13&screen_name="
					+ params[0];
			// Fetching Profile Image URL
			try {
				JsonParser jsonParser = new JsonParser();
				JSONObject json = null;
				JSONArray jsonArray = null;
				json = jsonParser.getJsonObjectFromURL(URL_PROFILE_IMAGE);
				profile_image_url = json.getString("profile_image_url");
				Log.d("Profile Image URL", profile_image_url);
				// Fetching Tweeted Id's If image URL is not null
				if (profile_image_url.length() != 0
						&& profile_image_url != null) {
					try {
						ArrayList<String> ids = new ArrayList<String>();
						jsonArray = jsonParser
								.getJSONArrayFromURL(URL_TWEETED_IDS);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jObj = jsonArray.getJSONObject(i);
							ids.add(jObj.getString("id_str"));
						}
						Log.d("Tweet IDS : ", ids.toString());
						Log.d("Tweet IDS Length : ", "" + ids.size());
						// Fetching Retweeted User Details
						if (ids.size() != 0) {
							jsonArray = null;
							for (String arr : ids) {
								String URL_RETWEET = "https://api.twitter.com/1.1/statuses/retweets/"
										+ arr + ".json";
								try {
									jsonArray = jsonParser
											.getJSONArrayFromURL(URL_RETWEET);
									for (int i = 0; i < jsonArray.length(); i++) {
										String name1, url1;
										int followers1;
										JSONObject jObj = jsonArray
												.getJSONObject(i);
										JSONObject jObj1 = jObj
												.getJSONObject("user");
										name1 = jObj1.getString("name");
										followers1 = jObj1
												.getInt("followers_count");
										url1 = jObj1
												.getString("profile_image_url");
										Retweets tw = new Retweets(name1, url1,
												followers1);
										retweet.add(tw);

									}
								} catch (Exception e) {
									Log.e("Error Getting Retweeted Details",
											URL_RETWEET);
								}
							}
							// Removing Duplicates
							try {
								Set<Retweets> s = new TreeSet<Retweets>(
										new Comparator<Retweets>() {

											@Override
											public int compare(Retweets lhs,
													Retweets rhs) {
												// compare the two object
												return ((Integer) lhs
														.getFollowers())
														.compareTo((Integer) rhs
																.getFollowers());
											}

										});
								s.addAll(retweet);
								retweet.clear();
								retweet.addAll(s);
								Log.d("-------------", "*******************");
								Collections.reverse(retweet);
							} catch (Exception e) {
								Log.e("Error SORTING", e.getMessage());
							}
						}
					} catch (Exception e) {
						process_status = false;
						Log.e("Error Getting Tweeted Ids", URL_TWEETED_IDS);
					}
				}

			} catch (Exception e) {
				process_status = false;
				Log.e("Error Downloading Profile Image from URL",
						URL_PROFILE_IMAGE);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			myAsyncTask = null;
			if (process_status == false) {
				Toast.makeText(getApplicationContext(),
						"Unable to Process\n Try Again", Toast.LENGTH_SHORT)
						.show();
			} else if (retweet.size() < 10) {
				Toast.makeText(getApplicationContext(),
						"Cant Process Response For this User",
						Toast.LENGTH_SHORT).show();
			} else {
				// Processing Details
				ArrayList<Retweets> parceRetweet = new ArrayList<Retweets>();
				for (int i = 0; i < 10; i++) {
					parceRetweet.add(retweet.get(i));
				}

				Intent i = new Intent(getApplicationContext(),
						ResponseActivity.class);
				Bundle b = new Bundle();
				b.putParcelableArrayList("retweets", parceRetweet);
				b.putString("main_img_url", profile_image_url);
				i.putExtras(b);
				startActivity(i);
			}
			wait.dismiss();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			myAsyncTask = null;
		}
	}
}
