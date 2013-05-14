package cs407.snapendar.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import cs407.snapendar.main.R;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

public class InfoActivity extends Activity {

	/** Called when the activity is first created. */
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
	   
	   ArrayList<File> files = new ArrayList<File>();
	   
	   for(int x = 0; x < snaps.length;x++){
		   try {
			ExifInterface exif = new ExifInterface(snaps[x].getAbsolutePath());
			int dateTime = exif.getAttributeInt(ExifInterface.TAG_DATETIME, 0);
			Log.v("exif",String.valueOf(dateTime));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  // files.add();
	   }
	   
	 
	   
	   
	   
	  // Log.v("info", "Files" + snaps.length);
	   
	    for (int x = 0; x < snaps.length; x++){
		   TextView newView = new TextView(this);
		   newView.setPadding(0, 0, 0, 25);
		   //newView.setLayoutParams(new TableRow.LayoutParams(200,400));
		   newView.setText(snaps[x].getName());
		   listOfDates.addView(newView);
	   }   
	}
}
