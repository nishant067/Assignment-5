package iiitd.nishant.aboutiiitd;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private TextView mTextview;
    //private URL mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            mUrl = new URL("https://iiitd.ac.in/about");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        mTextview = (TextView) findViewById(R.id.about);

        String sUrl = "https://iiitd.ac.in/about";
        ConnectivityManager connM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connM.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnected()) {
            new DownloadAbout().execute(sUrl);
        }
        else {
            mTextview.setText("NO CONNECTION");
        }
    }

    private class DownloadAbout extends AsyncTask<String, Void, String>{
        String myData = "";
        @Override
        protected String doInBackground(String... strings) {
            String ms = strings[0];
            try {
                URL finalUrl = new URL(ms);
                URLConnection newConnection = (URLConnection) finalUrl.openConnection();
                InputStreamReader iStreamReader = new InputStreamReader(newConnection.getInputStream());
                BufferedReader bReader = new BufferedReader(iStreamReader);
                String nextLine = null;

                while((nextLine = bReader.readLine())!=null){
                    myData += nextLine;
                    myData += "\n";
                }
                bReader.close();
                //return downloadFromUrl(strings[0]);
            } catch (IOException e) {
                return "Unable to retrive web page";
            }

            return myData;
        }

        @Override
        protected void onPostExecute(String result) {
            //mTextview.setText(result);
            super.onPostExecute(result);
            Document document = Jsoup.parse(result);
            result = document.text();
            mTextview.setText(result.split("2011-2012 | 2010-2011 2009-2010")[3]);
        }
    }

    private String downloadFromUrl(String myUrl) throws IOException {

        InputStream inputstream = null;
        int len = 5000;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            //Log.d(DEBUG_TAG, "The response is: " + response);
            inputstream = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(inputstream, len);
            return contentAsString;

        } finally {
            if (inputstream != null) {
                inputstream.close();
            }
        }
    }

    private String readIt(InputStream inputstream, int len) throws IOException, UnsupportedEncodingException{
        Reader reader = null;
        reader = new InputStreamReader(inputstream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        return new String(buffer);
    }
}


