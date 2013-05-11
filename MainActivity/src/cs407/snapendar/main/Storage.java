package cs407.snapendar.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Storage {
	
	protected File sdCard;
	
	public Storage(){
		 sdCard = Environment.getExternalStorageDirectory();
	}
	
	public void writeTestFile(){
		
	    File dir = new File (sdCard.getAbsolutePath() + "/snapendar");
	    dir.mkdirs();
	   
	   // Test code for writing to the directory. Works.
	    File file = new File(dir, "testFile.txt");
	 
	    FileOutputStream f;
		try {
			  f = new FileOutputStream(file);
			  f.flush();
			  f.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String[] getSnaps(){
	    File dir = new File (sdCard.getAbsolutePath() + "/snapendar");
	    return dir.list();
	}
	
	public boolean snapendarDirectoryExisted(){
		    File sdCard = Environment.getExternalStorageDirectory();
		    File dir = new File (sdCard.getAbsolutePath() + "/snapendar");
		   
		    //Returns false if it already existed.
		    boolean existed =  dir.mkdirs();
		    existed = !existed;
		    
		    Log.v("storagedebug", String.valueOf(existed));
		    return existed;
	}

}
