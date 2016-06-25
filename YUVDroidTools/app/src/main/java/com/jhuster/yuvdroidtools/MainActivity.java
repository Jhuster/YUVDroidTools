package com.jhuster.yuvdroidtools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Spinner mFormatSpinner;
    private Spinner mAlgorithmSpinner;
    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private String mSelectedFilepath;
    private TextView mFilePathTextView;

    public static final String[] FORMAT_ARRAY = {
            "YU12", "YV12", "NV21", "NV12", "YUYV422", "YVYU422", "YUV422P", "UYVY422",
            "GRAY8", "RGB565", "RGB888", "ARGB8888", "ABGR8888", "BGRA8888"
    };

    public static final String[] ALGORITHM_ARRAY = {
            "FAST_BILINEAR", "BILINEAR", "BICUBIC", "X", "POINT", "AREA", "BICUBLIN",
            "GAUSS", "SINC", "LANCZOS", "SPLINE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFormatSpinner = (Spinner) findViewById(R.id.FormatSpinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FORMAT_ARRAY);
        mFormatSpinner.setAdapter(adapter1);

        mAlgorithmSpinner = (Spinner) findViewById(R.id.AlgorithmSpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ALGORITHM_ARRAY);
        mAlgorithmSpinner.setAdapter(adapter2);

        mWidthEditText = (EditText) findViewById(R.id.WidthEditText);
        mHeightEditText = (EditText) findViewById(R.id.HeightEditText);

        mFilePathTextView = (TextView) findViewById(R.id.FilePathTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickBrowse(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Please choose a YUV file"), 0);
    }

    public void onClickPreview(View v) {
        if ("".equals(mWidthEditText.getText().toString()) || "".equals(mHeightEditText.getText().toString())) {
            Toast.makeText(this, "Width & Height can't be empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSelectedFilepath == null || "".equals(mSelectedFilepath)) {
            Toast.makeText(this, "Please click Browse to select a file ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra("format", Integer.valueOf(mFormatSpinner.getSelectedItemPosition()));
        intent.putExtra("algorithm", Integer.valueOf(mAlgorithmSpinner.getSelectedItemPosition()));
        intent.putExtra("width", Integer.valueOf(mWidthEditText.getText().toString()));
        intent.putExtra("height", Integer.valueOf(mHeightEditText.getText().toString()));
        intent.putExtra("filepath", mSelectedFilepath);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mSelectedFilepath = getFilePath(this, data.getData());
            if (mSelectedFilepath != null && !"".equals(mSelectedFilepath)) {
                mFilePathTextView.setText(mSelectedFilepath);
            }
        }
    }

    public static String getFilePath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
