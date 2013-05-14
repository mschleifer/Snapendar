package cs407.snapendar.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import cs407.snapendar.main.R;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

public class InfoActivity extends Activity {

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_info);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    //http://developer.android.com/reference/android/os/Environment.html
		 /*
	    File[] files = Environment.getExternalStorageDirectory().listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {

                return true; //filename.contains(".png");
            }
        });
	     */
	   
	    Button finishBtn = (Button) this.findViewById(R.id.BackBtn);
	    
	    finishBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	    
	    //TODO:
	    //Open saved photo directory, get all files, get names, sort chronologically
	    
	   LinearLayout rootElement  = (LinearLayout) this.findViewById(R.id.listContainer);
	   LinearLayout listOfDates = new LinearLayout(this);
	   listOfDates.setOrientation(LinearLayout.VERTICAL);
	   listOfDates.setPadding(55, 55, 55, 55);
	   rootElement.addView(listOfDates);
	   
	   File [] snaps = MainActivity.storage.getSnaps();
	   
	   ArrayList<String> files = new ArrayList<String>();
	   for(int x = 0; x < snaps.length; x++){
		   files.add(snaps[x].getName());
	   }
	   
	   
	  // Log.v("info", "Files" + snaps.length);
	   
	    for (int x = 0; x < snaps.length; x++){
		   ImageView imgView = new ImageView(this);

		 //  listOfDates.addView(imgView);

		   BitmapFactory.Options options = new BitmapFactory.Options();
		  // options.inSampleSize = 1.5f;
		   Bitmap bm = BitmapFactory.decodeFile(snaps[x].getAbsolutePath(), options);
		   
		   
		   Matrix matrix = new Matrix();
		   matrix.postRotate(90);
		   Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		   
		   
		   imgView.setImageBitmap(rotatedBitmap); 
		//   imgView.setBackgroundColor(Color.BLACK);
		   imgView.setPadding(5,5, 5, 5);

		 //  imgView.setRotation(90);
		  // imgView.setPadding(0, 50, 0, 100);
		   listOfDates.addView(imgView);
		   
	   }   
	}
}
