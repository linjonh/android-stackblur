package com.example.stackblurdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.enrique.stackblur.StackBlurManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity {
    
	@InjectView(R.id.imageView)        ImageView    _imageView;
	@InjectView(R.id.blur_amount)      SeekBar      _seekBar;
	@InjectView(R.id.toggleButton)     ToggleButton _toggleButton;
	@InjectView(R.id.typeSelectSpinner) Spinner     _typeSelectSpinner;
	
	
	private StackBlurManager _stackBlurManager;
	
	private String IMAGE_TO_ANALYZE = "nav_header.jpg";
	
	private int blurMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_stackBlurManager = new StackBlurManager(getBitmapFromAsset(this, "nav_header.jpg"));
		
		_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
				onBlur();
			}
		});
		
		_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					IMAGE_TO_ANALYZE = "image_transparency.png";
					_stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
					onBlur();
				} else {
//					IMAGE_TO_ANALYZE = "android_platform_256.png";
					IMAGE_TO_ANALYZE = "nav_header.jpg";
					_stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
					onBlur();
				}
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blur_modes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_typeSelectSpinner.setAdapter(adapter);
		_typeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setBlurMode(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
			case R.id.menu_benchmark:
				Intent intent = new Intent(this, BenchmarkActivity.class);
				startActivity(intent);
				return true;
			case R.id.save:
				saveImage();
				return true;
			default:
				return false;
		}
	}

	private void saveImage() {
		File dir  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(dir.getAbsolutePath() + File.separator + "blur.jpg");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			mIntentSaveImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			Toast.makeText(this,"save success!",Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }

	public void setBlurMode(int mode) {
		this.blurMode = mode;
		onBlur();
	}
	private Bitmap mIntentSaveImage;
	private void onBlur() {
		int radius = _seekBar.getProgress() * 5;
		switch(blurMode) {
			case 0:
				mIntentSaveImage = _stackBlurManager.process(radius);

				break;
			case 1:
				mIntentSaveImage = _stackBlurManager.processNatively(radius);
				break;
			case 2:
				mIntentSaveImage = _stackBlurManager.processRenderScript(this, radius);
				break;
		}
		_imageView.setImageBitmap(mIntentSaveImage);
	}
}
