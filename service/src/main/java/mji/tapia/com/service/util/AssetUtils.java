package mji.tapia.com.service.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sami on 4/5/2018.
 */

public class AssetUtils {

    static public void copyAssets(Context context, String path, String outPath) {
        AssetManager assetManager = context.getAssets();
        String assets[];
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyAssetFile(context, path, outPath);
            } else {
                String fullPath = outPath + "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdir()) Log.e("TapiaResource", "No create external directory: " + dir );
                for (String asset : assets) {
                    copyAssets(context,path + "/" + asset, outPath);
                }
            }
        } catch (IOException ex) {
            Log.e("TapiaResource", "I/O Exception", ex);
        }
    }

    static public void copyAssetFile(Context context, String filename, String outPath) {
        AssetManager assetManager = context.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String newFileName = outPath + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("TapiaResource", e.getMessage());
        }

    }
}
