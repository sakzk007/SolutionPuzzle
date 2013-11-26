package com.anees.solutionpuzzle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class ResponseActivity extends Activity implements OnClickListener {
    private ImageButton main_button, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10;
    private ArrayList<Retweets> rtwts;
    private String main_image_url;
    private GetImagesUser getImageUser = null;
    private GetMainUerImage getMainImageUser = null;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_response);
	initVars();
	Bundle b = getIntent().getExtras();
	if (b != null) {
	    main_image_url = b.getString("main_img_url");
	    rtwts = b.getParcelableArrayList("retweets");
	    for (Retweets e : rtwts) {
		Log.d("After Sorting", e.toString());
	    }
	    if (getMainImageUser == null) {
		getMainImageUser = new GetMainUerImage();
		getMainImageUser.execute(main_image_url);
	    }
	    if (getImageUser == null) {
		getImageUser = new GetImagesUser();
		getImageUser.execute(rtwts);
	    }
	}
    }

    // Initializing variable
    private void initVars() {
	main_button = (ImageButton) findViewById(R.id.image_main);
	btn1 = (ImageButton) findViewById(R.id.image_sub1);
	btn2 = (ImageButton) findViewById(R.id.image_sub2);
	btn3 = (ImageButton) findViewById(R.id.image_sub3);
	btn4 = (ImageButton) findViewById(R.id.image_sub4);
	btn5 = (ImageButton) findViewById(R.id.image_sub5);
	btn6 = (ImageButton) findViewById(R.id.image_sub6);
	btn7 = (ImageButton) findViewById(R.id.image_sub7);
	btn8 = (ImageButton) findViewById(R.id.image_sub8);
	btn9 = (ImageButton) findViewById(R.id.image_sub9);
	btn10 = (ImageButton) findViewById(R.id.image_sub10);
	btn1.setOnClickListener(this);
	btn2.setOnClickListener(this);
	btn3.setOnClickListener(this);
	btn4.setOnClickListener(this);
	btn5.setOnClickListener(this);
	btn6.setOnClickListener(this);
	btn7.setOnClickListener(this);
	btn8.setOnClickListener(this);
	btn9.setOnClickListener(this);
	btn10.setOnClickListener(this);
    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.image_sub1:
	    rtwts.get(0).getFollowers();
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(0).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub2:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(1).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub3:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(2).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub4:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(3).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub5:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(4).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub6:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(5).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub7:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(6).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub8:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(7).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub9:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(8).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	case R.id.image_sub10:
	    Toast.makeText(getApplicationContext(), "" + rtwts.get(9).getFollowers(), Toast.LENGTH_SHORT).show();
	    break;
	}
    }

    // Fetching main Image
    private class GetMainUerImage extends AsyncTask<String, Void, Bitmap> {

	@Override
	protected Bitmap doInBackground(String... params) {
	    Bitmap map = null;
	    try {
		map = downloadImage(params[0]);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    return map;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
	    super.onPostExecute(result);
	    getMainImageUser = null;
	    main_button.setImageBitmap(result);
	}

	@Override
	protected void onCancelled() {
	    super.onCancelled();
	    getMainImageUser = null;
	}
    }

    // Fetching Retweeted Images
    private class GetImagesUser extends AsyncTask<ArrayList<Retweets>, Void, ArrayList<Bitmap>> {
	@Override
	protected ArrayList<Bitmap> doInBackground(ArrayList<Retweets>... urls) {
	    Bitmap map = null;
	    ArrayList<Retweets> list = urls[0];
	    ArrayList<Bitmap> user = new ArrayList<Bitmap>();
	    for (int i = 0; i < 10; i++) {
		try {
		    map = downloadImage(list.get(i).getImageURL());
		} catch (IOException e) {
		    e.printStackTrace();
		}
		user.add(map);
	    }
	    return user;
	}

	// Sets the Bitmap returned by doInBackground
	@Override
	protected void onPostExecute(ArrayList<Bitmap> result) {
	    super.onPostExecute(result);
	    getImageUser = null;
	    int size_of_result = result.size();
	    for (int i = 0; i < size_of_result; i++) {
		Bitmap map = null;
		map = result.get(i);
		switch (i) {
		case 0:
		    btn1.setImageBitmap(map);
		    break;
		case 1:
		    btn2.setImageBitmap(map);
		    break;
		case 2:
		    btn3.setImageBitmap(map);
		    break;
		case 3:
		    btn4.setImageBitmap(map);
		    break;
		case 4:
		    btn5.setImageBitmap(map);
		    break;
		case 5:
		    btn6.setImageBitmap(map);
		    break;
		case 6:
		    btn7.setImageBitmap(map);
		    break;
		case 7:
		    btn8.setImageBitmap(map);
		    break;
		case 8:
		    btn9.setImageBitmap(map);
		    break;
		case 9:
		    btn10.setImageBitmap(map);
		    break;
		default:
		    break;
		}
	    }
	}

	@Override
	protected void onCancelled() {
	    super.onCancelled();
	    getImageUser = null;
	}
    }

    // Creates Bitmap from InputStream
    private Bitmap downloadImage(String url) throws IOException {
	Bitmap bitmap = null;
	InputStream stream = null;
	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	bmOptions.inSampleSize = 1;

	stream = getHttpConnection(url);
	bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
	stream.close();

	Bitmap circleBitmap = getCircularBitmap(bitmap);
	return circleBitmap;
    }

    // Drawing Circle
    private Bitmap getCircularBitmap(Bitmap bitmap) {
	Bitmap output;
	if (bitmap.getWidth() > bitmap.getHeight()) {
	    output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
	} else {
	    output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
	}
	Canvas canvas = new Canvas(output);
	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	float r = 0;
	if (bitmap.getWidth() > bitmap.getHeight()) {
	    r = bitmap.getHeight() / 2;
	} else {
	    r = bitmap.getWidth() / 2;
	}
	paint.setAntiAlias(true);
	canvas.drawARGB(0, 0, 0, 0);
	paint.setColor(color);
	canvas.drawCircle(r, r, r, paint);
	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap, rect, rect, paint);
	return output;
    }

    // Creating InputStream from URL
    private InputStream getHttpConnection(String urlString) throws IOException {
	InputStream stream = null;
	URL url = new URL(urlString);
	URLConnection connection = url.openConnection();

	HttpURLConnection httpConnection = (HttpURLConnection) connection;
	httpConnection.setRequestMethod("GET");
	httpConnection.connect();

	if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    stream = httpConnection.getInputStream();
	}

	return stream;
    }

}
