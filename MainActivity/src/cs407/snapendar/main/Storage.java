package cs407.snapendar.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Storage {

	private File snapendarDir;

	public Storage(){
		snapendarDir = new File(Environment.getExternalStorageDirectory(), "Snapendar/");
	}

	/**
	 * Write a file with name <i>filename</i> to the directory setup by this Storage object.
	 * @param filename Name of the file to be written/created
	 * @param data Data to write to the file in byte[] format
	 * @return True if the write was successful; false otherwise
	 */
	public boolean writeFile(String filename, byte[] data) {
		
		createStorageDirectory();
		
		try {
			FileOutputStream outStream = null;
			File outputFile = new File(snapendarDir, filename);
			outStream = new FileOutputStream(outputFile);
			outStream.write(data);
			outStream.close();
			return true;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public void writeTestFile(){
		
		createStorageDirectory();

		// Test code for writing to the directory. Works.
		File file = new File(snapendarDir, "testFile.txt");

		FileOutputStream f;
		try {
			f = new FileOutputStream(file);
			f.flush();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the directory specified by this Storage object if it doesn't already exist.
	 */
	private void createStorageDirectory() {
		if (!snapendarDir.exists()) {
			if (!snapendarDir.mkdirs()) {
				Log.e("snap", "Problem creating folder for Snapendar:" + 
						Environment.getExternalStorageDirectory() + "Snapendar/");
			}
		}
	}

	/**
	 * Can we read & write to the external storage?
	 * @return True if fully accessible; false otherwise
	 */
	public static boolean storageAccessible() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Is the storage in read-only mode?
	 * @return True if read-only; false otherwise
	 */
	public static boolean storageReadOnly() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	public String[] getSnaps(){
		return snapendarDir.list();
	}

	/**
	 * Checks if the /Snapendar/ directory exists. If it doesn't attempt to create it.
	 * @return True if the /Snapendar/ directory exists at the time of call
	 */
	public boolean snapendarDirectoryExists(){
		boolean exists = false;
		if (snapendarDir.exists()) {
			exists = true;
		}
		else {
			if (!snapendarDir.mkdirs()) {
				Log.e("snap", "Problem creating folder for Snapendar:" + snapendarDir);
			}
		}
		Log.v("snap", "storageDebug - dirExists: " + String.valueOf(exists));
		return exists;
	}
}
