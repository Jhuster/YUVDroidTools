package com.jhuster.yuvdroidtools;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DisplayActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = (ImageView) findViewById(R.id.ImageView);

        int format = getIntent().getIntExtra("format", 0);
        int algorithm = getIntent().getIntExtra("algorithm", 0);
        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);
        String filepath = getIntent().getStringExtra("filepath");

        byte[] buffer = new byte[FFConverter.calcSize(width, height, format)];
        int length = loadFile(filepath, buffer);
        if (length < 0) {
            Toast.makeText(this, "Failed to load file !", Toast.LENGTH_SHORT).show();
            return;
        }

        displayImage(buffer, length, width, height, format, algorithm);
    }

    protected int loadFile(String filepath, byte[] buffer) {
        int ret = -1;
        try {
            DataInputStream is = new DataInputStream(new FileInputStream(filepath));
            ret = is.read(buffer);
            closeSilently(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected void displayImage(byte[] data, int length, int width, int height, int format, int alg) {
        byte[] rgb565 = FFConverter.process(width, height, format, width, height, FFConverter.CV_FMT_RGB565,
                FFConverter.CV_ALG_FAST_BILINEAR, data, length);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rgb565));
        mImageView.setImageBitmap(bitmap);
    }

    protected void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return true;
    }
}
