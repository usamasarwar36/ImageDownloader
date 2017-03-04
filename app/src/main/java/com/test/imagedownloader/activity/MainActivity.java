package com.test.imagedownloader.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.imagedownloader.R;
import com.test.imagedownloader.asynctask.ImageDownloadAsyncTask;
import com.test.imagedownloader.common.ImageUtils;
import com.test.imagedownloader.common.Utility;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ImageDownloadAsyncTask.ImageDownloadListener{

    Context context;
    FloatingActionButton addUrl;
    TextView textError;
    ImageView imgDownload;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        addUrl = (FloatingActionButton) findViewById(R.id.fab_add_url);
        addUrl.setOnClickListener(this);

        textError = (TextView) findViewById(R.id.text_error);
        imgDownload = (ImageView) findViewById(R.id.img_download);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_add_url:
                askForUrl();
        }
    }

    private void askForUrl() {

        // Dialog box to enter URL.
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        if(!TextUtils.isEmpty(url)) {
            edittext.setText(url);
        }

        alert.setTitle(getString(R.string.alert_download_title));
        alert.setMessage(getString(R.string.alert_download_message));
        alert.setView(edittext);
        alert.setPositiveButton(getString(R.string.action_download), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                textError.setVisibility(View.GONE);
                // check for network availability
                if(!Utility.isNetworkAvailable(getApplicationContext())) {
                    Utility.showAlertDialog(context, getString(R.string.alert_network_not_available));
                    return;
                }
                //check edge cases on URL string and then download the image
                String imageUrl = edittext.getText().toString();
                if(TextUtils.isEmpty(imageUrl)) {
                    Utility.showAlertDialog(context, getString(R.string.alert_no_url_provided));
                    return;
                }
                Log.d("usama", "url provided by user: " + imageUrl);
                if(!imageUrl.contains("http")) {
                    imageUrl = "http://" + imageUrl;
                }
                startDownloadingImage(imageUrl);
            }
        });

        alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void startDownloadingImage(String imageUrl) {
        url = imageUrl;

        // create aysnc task to download image
        Utility.showProgress(getWindow().getDecorView().getRootView(), R.id.progress, View.VISIBLE);
        new ImageDownloadAsyncTask(context, url, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess() {
        Utility.showProgress(getWindow().getDecorView().getRootView(), R.id.progress, View.GONE);
        textError.setVisibility(View.GONE);
        imgDownload.setVisibility(View.VISIBLE);
        Log.d("usama", "image download success");
        // url hashcode is used as file name
        String path = ImageUtils.getLocalPathFromUrl(context, url);
        rotateAndLoadSavedImage(path, 180, imgDownload);
    }

    private void rotateAndLoadSavedImage(String localFilename, float rotation, ImageView imageView) {
        Bitmap bitmap = BitmapFactory.decodeFile(localFilename);
        imageView.setRotation(rotation);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onFailure() {
        Utility.showProgress(getWindow().getDecorView().getRootView(), R.id.progress, View.GONE);
        imgDownload.setVisibility(View.GONE);
        textError.setVisibility(View.VISIBLE);
        Log.d("usama", "image download failure");
    }
}
