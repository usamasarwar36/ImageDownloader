package com.test.imagedownloader.common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Usama Sarwar on 3/11/2016.
 */

public class ImageUtils {

    public final static String IMAGE_EXTENSION = ".jpeg";

    public static int downloadImage(Context context, String url) {

        // Allowed Content types for this application.
        String[] allowedContentTypes = {"image/jpg","image/jpeg","image/png", "image/bmp", "image/gif"};

        File file = null;
        String path = getLocalPathFromUrl(context, url);
        //Log.d("usama", "Saving on path: " + path);
        file = new File(path);
        HttpURLConnection conn = null;
        try {
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(35000);
            conn.setReadTimeout(35000);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            copyFile(is, os);
            os.close();
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int statusCode = -1;
        try {
            statusCode = conn.getResponseCode();
            String returnedContentType = conn.getContentType();
            if(returnedContentType.contains(";")) {
                returnedContentType = returnedContentType.split(";")[0];
            }
            Log.d("usama", returnedContentType);
            if(!Arrays.asList(allowedContentTypes).contains(returnedContentType)) {
                file.delete();
                statusCode = -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return statusCode;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private static File getStorageDirectory(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static String getLocalPathFromUrl(Context context, String url) {
        File dir = getStorageDirectory(context);
        String path = dir.getPath() + File.separator + url.hashCode()+ IMAGE_EXTENSION;
        return path;
    }
}
