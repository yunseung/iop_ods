package com.ibkc.product.ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by macpro on 2018. 7. 16..
 */

public class ResultDocImageTask extends AsyncTask<byte[], Void, Bitmap> {

    private ImageView mImageView = null;

    public ResultDocImageTask(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(byte[]... bytes) {
        if (bytes == null) {
            // 실패
            return null;
        } else {
            Bitmap imageBitmap = null;

            if (bytes != null) {
                if (imageBitmap != null && !imageBitmap.isRecycled()) {
                    imageBitmap.recycle();
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;

                return BitmapFactory.decodeByteArray(bytes[0], 0, bytes[0].length, options);
            } else {
                // 실패
                return null;
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mImageView.setImageBitmap(bitmap);
    }

}
