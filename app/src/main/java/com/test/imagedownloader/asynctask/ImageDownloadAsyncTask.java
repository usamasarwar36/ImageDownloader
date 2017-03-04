package com.test.imagedownloader.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.test.imagedownloader.common.ImageUtils;

/**
 * Created by Usama Sarwar on 3/11/2016.
 */

public class ImageDownloadAsyncTask extends AsyncTask<Void, Void, Integer> {

    Context context;
    String url;
    ImageDownloadListener listener;

    public ImageDownloadAsyncTask(Context context, String url, ImageDownloadListener listener) {
        this.context = context;
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int statusCode = ImageUtils.downloadImage(context, url);
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        Log.d("usama", "image download result code: " + statusCode);
        if(statusCode == 200) {
            listener.onSuccess();
        } else {
            listener.onFailure();
        }
    }

    public interface ImageDownloadListener {
        void onSuccess();
        void onFailure();
    }
}
