package cs407.snapendar.main;

import java.io.File;
import java.io.FilenameFilter;

import cs407.snapendar.main.R;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
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

	    
	    File[] file = Environment.getExternalStorageDirectory().listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {

                return filename.contains(".png");
            }
        });
	   
	    
	    
	    
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
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
	   
	   
	   int numSnaps = 10; //Hardcoded for now
	   
	   
	    for (int x = 0; x < file.length; x++){
	    	
	    	
		   TextView newView = new TextView(this);
		   newView.setWidth(3000);
		   newView.setLayoutParams(new TableRow.LayoutParams(200,100));
		   newView.setText(file[x].getName());
		   //newView.setWidth(Layout.)
		   listOfDates.addView(newView);
	   }

	   
	   
	}
}
