package net.azurewebsites.ashittyscheduler.ass.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

    ImageView bmImage;
    String url;

    public DownloadImageTask(ImageView bmImage, String url) {
        this.bmImage = bmImage;
        this.url = url;
    }

    protected Bitmap doInBackground(Void... voids) {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        try {
            bmImage.setImageBitmap(result);
        }
        catch(Exception o_O) {
            // ?
        }
    }
}