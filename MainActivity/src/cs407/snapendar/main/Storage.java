package cs407.snapendar.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;

import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class Storage {

	private File snapendarDir;
	public static String lastWrittenFile;
	public static String lastRenamedFile;

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
			Storage.lastWrittenFile = filename;
			return true;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public  boolean renameFile(String from, String to){
	
		createStorageDirectory();
		File fromFile = new File(snapendarDir,from);
		Log.v("storage",fromFile.getAbsolutePath().toString());
		
		Log.v("storage", "space taken: " + fromFile.getTotalSpace());
		
		to = to.replace(',', ' ');
		to = to.replace(':', '-');
		to += ".jpg";
		Log.v("storage", "Trying to rename" + from + " to: " + to);
		
		File toFile = new File(snapendarDir, to);
		boolean status = fromFile.renameTo(toFile);
		Storage.lastRenamedFile = to;
		
		Log.v("storage", "Rename status" + String.valueOf(status));
		return status;
	}
	
	public boolean setExifMilliseconds(String fileName, String ms){
		File snapFile = new File(snapendarDir, fileName);
		try{
			ExifInterface eInterface = new ExifInterface(snapFile.getPath());
			Log.v("storage","oldDateTime" +  eInterface.getAttribute(ExifInterface.TAG_DATETIME));
			eInterface.setAttribute(ExifInterface.TAG_DATETIME, ms);
			eInterface.saveAttributes();
		}
		catch(Exception e){
		//.v("storage", e.getLocalizedMessage());
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

	public File[] getSnaps(){

		File[] files = snapendarDir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase(Locale.ENGLISH).endsWith(".jpg");
		    }
		});
		return files;
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
	
	public String getDirectoryPath() {
		return snapendarDir.getAbsolutePath();
	}
}