package com.anees.solutionpuzzle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
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
    private String screen_name = null;
    private EditText et_screen_name;
    private ProgressDialog wait;
    private CheckInternet ci;
    private GetFeedTask myAsyncTask = null;
    final String OAUTH_KEY = "Bearer AAAAAAAAAAAAAAAAAAAAAH2TTgAAAAAA0pDT3i7w2c0r1fkkAxgkupuzhR4%3DYwj22QQEXS5INepTzmniEv9rcyfpUEZDb4oSlAOozA";

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
		if (ci.isConnectingToInternet()) {
		    screen_name = et_screen_name.getText().toString().trim();
		    if (screen_name.length() == 0 || screen_name == null) {
			Toast.makeText(getApplicationContext(), "Enter Screen Name", Toast.LENGTH_SHORT).show();
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
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		// set title
		alertDialogBuilder.setTitle("Internet not connected!");
		// set dialog message
		alertDialogBuilder.setMessage("Connect to internet! \n\t Try Again!").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
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
	    StringBuilder url_profile_image = new StringBuilder().append("https://api.twitter.com/1.1/users/show.json?screen_name=").append(params[0]);
	    StringBuilder url_tweeted_ids = new StringBuilder().append("https://api.twitter.com/1.1/statuses/user_timeline.json?suppress_response_codes&trim_user=true&include_entities=false&include_rts=true&exclude_replies=true&count=13&screen_name=").append(params[0]);
	    // Fetching Profile Image URL
	    try {
		fetchProfileImageFromURL(url_profile_image.toString(), url_tweeted_ids.toString());
	    } catch (JSONException e) {
		process_status = false;
		e.printStackTrace();
	    } catch (ClientProtocolException e) {
		process_status = false;
		e.printStackTrace();
	    } catch (IOException e) {
		process_status = false;
		e.printStackTrace();
	    }

	    return null;
	}

	private void fetchProfileImageFromURL(String profileImageURL, String urlTweetedIDs) throws JSONException, ClientProtocolException, IOException {
	    JSONObject json = new JSONObject(getJsonStringFromURL(profileImageURL));
	    profile_image_url = json.getString("profile_image_url");
	    Log.d("Profile Image URL", profile_image_url);
	    // Fetching Tweeted Id's If image URL is not null
	    if (profile_image_url.length() != 0 && profile_image_url != null) {
		fetchTweetedIDsFromURL(urlTweetedIDs);
	    }
	}

	// Generating Json String Response
	private String getJsonStringFromURL(String url) throws ClientProtocolException, IOException {
	    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
	    HttpGet httpget = new HttpGet(url);

	    httpget.setHeader("Authorization", OAUTH_KEY);
	    httpget.setHeader("Content-type", "application/json");

	    InputStream inputStream = null;
	    HttpResponse response = httpclient.execute(httpget);
	    StatusLine statusLine = response.getStatusLine();
	    int statusCode = statusLine.getStatusCode();
	    Log.w("Status Code", "" + statusCode);
	    StringBuilder sb = new StringBuilder();
	    if (statusCode == 200) {
		HttpEntity entity = response.getEntity();
		inputStream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		String line = null;
		while ((line = reader.readLine()) != null) {
		    sb.append(line + "\n");
		}
	    }
	    return sb.toString();
	}

	// Fetching Tweeted Ids from URL
	private void fetchTweetedIDsFromURL(String urlTweetedIDs) throws JSONException, ClientProtocolException, IOException {
	    ArrayList<String> ids = new ArrayList<String>();
	    JSONArray jsonArray_id = new JSONArray(getJsonStringFromURL(urlTweetedIDs));
	    int jsonArray_length = jsonArray_id.length();

	    for (int i = 0; i < jsonArray_length; i++) {
		JSONObject jObj = jsonArray_id.getJSONObject(i);
		ids.add(jObj.getString("id_str"));
	    }
	    Log.d("Tweet IDS : ", ids.toString());
	    Log.d("Tweet IDS Length : ", "" + ids.size());
	    // Fetching Retweeted User Details
	    if (ids.size() != 0) {
		for (String arr : ids) {
		    StringBuilder url_retweet = new StringBuilder().append("https://api.twitter.com/1.1/statuses/retweets/").append(arr).append(".json");
		    // Fetch User Details from ReTweet ID's
		    fetchUserDetailsFromReTweetedIdsURL(url_retweet.toString());
		}
		// Removing Duplicates
		Set<Retweets> s = new TreeSet<Retweets>(new Comparator<Retweets>() {

		    @Override
		    public int compare(Retweets lhs, Retweets rhs) {
			// compare the two object
			return ((Integer) lhs.getFollowers()).compareTo((Integer) rhs.getFollowers());
		    }
		});
		s.addAll(retweet);
		retweet.clear();
		retweet.addAll(s);
		Collections.reverse(retweet);
	    }

	}

	// Collect ReTweeted User Details
	private void fetchUserDetailsFromReTweetedIdsURL(String reTweetedUserIds) throws JSONException, ClientProtocolException, IOException {
	    JSONArray jsonArray_ReTweetedIDs = new JSONArray(getJsonStringFromURL(reTweetedUserIds));
	    int length_of_jsonArray = jsonArray_ReTweetedIDs.length();

	    for (int i = 0; i < length_of_jsonArray; i++) {
		String name1, url1;
		int followers1;
		JSONObject jObj = jsonArray_ReTweetedIDs.getJSONObject(i);
		JSONObject jObj1 = jObj.getJSONObject("user");
		name1 = jObj1.getString("name");
		followers1 = jObj1.getInt("followers_count");
		url1 = jObj1.getString("profile_image_url");
		Retweets tw = new Retweets(name1, url1, followers1);
		retweet.add(tw);
	    }
	}

	@Override
	protected void onPostExecute(Void result) {
	    super.onPostExecute(result);
	    myAsyncTask = null;
	    int retweet_size = retweet.size();
	    if (process_status == false) {
		Toast.makeText(getApplicationContext(), "Unable to Process\n Try Again", Toast.LENGTH_SHORT).show();
	    } else if (retweet_size < 10) {
		Toast.makeText(getApplicationContext(), "Cant Process Response For this User", Toast.LENGTH_SHORT).show();
	    } else {
		// Making ArrayList of Size 10
		retweet.subList(10, retweet_size).clear();
		Intent i = new Intent(getApplicationContext(), ResponseActivity.class);
		Bundle b = new Bundle();
		b.putParcelableArrayList("retweets", retweet);
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
