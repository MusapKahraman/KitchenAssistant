/*
 * Reference
 * https://developer.android.com/training/camera/photobasics
 */

package com.example.kitchen.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BitmapUtils {

    public static String writeJpegPrivate(Context context, Bitmap bitmap, String filename) {
        File directory = context.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir;
        if (isExternalStorageWritable())
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        else
            storageDir = context.getFilesDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // Check if external storage is available for read and write
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
