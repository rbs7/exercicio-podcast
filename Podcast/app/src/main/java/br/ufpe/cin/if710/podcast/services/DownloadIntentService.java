package br.ufpe.cin.if710.podcast.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Rogerio Santos on 15/10/2017.
 */

public class DownloadIntentService extends IntentService {
    private static final String TAG = DownloadIntentService.class.getSimpleName(); //Nome da classe

    public DownloadIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ItemFeed item = (ItemFeed) intent.getSerializableExtra("ItemFeed");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Iniciando download...");
            try {
                File root = new File(Environment.getExternalStorageDirectory() + "/Podcast");
                root.mkdirs();
                File output = new File(root, Uri.parse(item.getDownloadLink()).getLastPathSegment());
                if (output.exists()) {
                    output.delete();
                }
                URL url = new URL(item.getDownloadLink());
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                FileOutputStream fos = new FileOutputStream(output.getPath());
                BufferedOutputStream out = new BufferedOutputStream(fos);
                try {
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[8192];
                    int len = 0;
                    while ((len = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                }
                finally {
                    fos.getFD().sync();
                    out.close();
                    c.disconnect();
                }

                Log.v(TAG, "Download concluído");

                //TODO: Gravar no banco da dados

                //TODO: Dar um broadcast

            } catch (IOException e) {
                Log.e(getClass().getName(), "Exception durante download", e);
            }
        } else {
            Log.v(TAG, "Permissão negada");
        }
    }
}