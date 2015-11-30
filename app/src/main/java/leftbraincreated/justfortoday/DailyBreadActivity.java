package leftbraincreated.justfortoday;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DailyBreadActivity extends Activity {

    String mHtmlString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_bread);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(getString(R.string.DailyBreadActivity_string_html_bundle_key)))
        {
            mHtmlString = savedInstanceState.getString(getString(R.string.DailyBreadActivity_string_html_bundle_key));
            loadWebViewFromHtml(mHtmlString);
        } else {
            readWebpage((WebView) findViewById(R.id.webViewDailyBread));

            //TODO: Fix snackbar
//            Snackbar.make(findViewById(R.id.webViewDailyBread), "and your 'Daily Bread'...", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!mHtmlString.equals("")) {
            outState.putString(getString(R.string.DailyBreadActivity_string_html_bundle_key), mHtmlString);
        }

    }

    private void loadWebViewFromHtml(String htmlString) {
        WebView webView = (WebView) findViewById(R.id.webViewDailyBread);
        webView.loadData(htmlString, "text/html", null);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            //TODO: Clean-up and do this right...

            try {
                URL url = new URL("http://jftna.org/jft/");
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }

                response = total.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            mHtmlString = result;
            loadWebViewFromHtml(result);
        }
    }

    public void readWebpage(View view) {
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{"http://jftna.org/jft/"});

    }
}
