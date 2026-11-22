package com.example.shop_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageLoader {

    public static void loadInto(Context ctx, String source, ImageView iv, int targetWidthPx) {
        if (source == null || source.isEmpty()) return;
        if (targetWidthPx <= 0) targetWidthPx = 400;

        Bitmap bmp = loadScaled(ctx, source, targetWidthPx);
        if (bmp != null) iv.setImageBitmap(bmp);
        else iv.setImageDrawable(null);
    }

    private static Bitmap loadScaled(Context ctx, String source, int targetWidthPx) {
        try {
            InputStream is = openStream(ctx, source);
            if (is == null) return null;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opts);
            is.close();
            if (opts.outWidth <= 0) return null;

            int sample = 1;
            while (opts.outWidth / sample > targetWidthPx) sample *= 2;
            opts.inSampleSize = sample;
            opts.inJustDecodeBounds = false;

            is = openStream(ctx, source);
            Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
            is.close();
            if (bmp == null) return null;

            if (bmp.getWidth() != targetWidthPx) {
                int newH = (int) ((float) bmp.getHeight() * targetWidthPx / bmp.getWidth());
                Bitmap scaled = Bitmap.createScaledBitmap(bmp, targetWidthPx, newH, true);
                if (scaled != bmp) bmp.recycle();
                bmp = scaled;
            }
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }

    private static InputStream openStream(Context ctx, String source) throws Exception {
        if (source.startsWith("content://") || source.startsWith("file://")) {
            return ctx.getContentResolver().openInputStream(Uri.parse(source));
        }
        int res = ctx.getResources().getIdentifier(source, "drawable", ctx.getPackageName());
        if (res == 0) return null;
        return ctx.getResources().openRawResource(res);
    }
}