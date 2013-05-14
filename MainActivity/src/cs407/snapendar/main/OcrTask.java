package cs407.snapendar.main;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;
import microsoft.hawaii.sdk.OcrService.OcrService;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrServiceResult;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Range;

public class OcrTask extends AsyncTask<Void, Integer, AlertDialog.Builder> {

	private OcrServiceResult serviceResult;

	public final MainActivity mainActivity;

	public OcrTask(MainActivity a)
	{
		mainActivity = a;
	}
	
	protected void onPreExecute() {
	}

	protected AlertDialog.Builder doInBackground(Void... listTypes) {
		/* Get the image as a byte array from the photo in MainActivity */
		byte[] imageBytes = getImageByte(mainActivity.photo);
		
		/* Size check; Project Hawaii OCR won't accept images > 1.5 MB */
		if(imageBytes.length >= 1572864) {
			return new AlertDialog.Builder(mainActivity)
				.setTitle("Size Warning")
				.setMessage("Your image is " + 
								String.format("%1$.2f", (double)imageBytes.length / 1048576) + 
								"MB in size. The maximum image size is 1.5MB. Your image " +
								"was not uploaded for parsing.")
				.setNeutralButton("Ok", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { }
				});
		}
		
		/* Send the data off for OCR; success will lead right to PostExecute */
		try {
			OcrService.recognizeImage(mainActivity.getBaseApplication().getClientIdentity(), imageBytes,
					new OnCompleteListener<OcrServiceResult>() {
						public void done(OcrServiceResult result) {
							serviceResult = result;
						}
					}, null);
		} catch (Exception exception) {
			return mainActivity.dialogToShow("Couldn't execute the ocr operation",
					exception);
		}

		if (serviceResult.getStatus() != microsoft.hawaii.hawaiiClientLibraryBase.Status.Success) {
			return mainActivity.dialogToShow(
					"Error when using ocr service to recognize the specified image",
					serviceResult.getException());
		}

		return null;
	}

	protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
		if (dialogBuilder != null) {
			mainActivity.showErrorMessage(dialogBuilder);
		} else {
			mainActivity.resultContainer.setVisibility(View.VISIBLE);
			
			/* Successful OCR returns a non-empty list of OcrText objects */
			List<OcrText> resultList = serviceResult.getOcrTexts();
			if (resultList.size() > 0) {
				String ocrResult = resultList.get(0).getText();
				
				if (!Utility.isStringNullOrEmpty(ocrResult)) {
					mainActivity.ocrResultView.setText(ocrResult);

					/* Parse the text returned from OCR looking for a date */
					Range chronicDate = Chronic.parse(ocrResult);
					if(chronicDate != null) {
						Calendar chronicCalendar;
						chronicCalendar = Calendar.getInstance();
						chronicCalendar.setTimeInMillis(chronicDate.getBegin());
						/* Show the user a nice string of the results we found */
						mainActivity.ocrResultView.setText("Date: " 
								+ new DateFormatSymbols().getMonths()[(chronicCalendar.get(Calendar.MONTH))] 
								+ " " + chronicCalendar.get(Calendar.DAY_OF_MONTH) 
								+ ", " + chronicCalendar.get(Calendar.YEAR)
								+"\nTime: " + new SimpleDateFormat("hh:mm").format(chronicCalendar.getTime()));
						try{
							MainActivity.storage.renameFile(Storage.lastWrittenFile, String.valueOf(chronicCalendar.getTime().toLocaleString()));
						}
						catch(Exception e){
							Log.v("storage",e.toString());
						}
						mainActivity.insertNewEvent(chronicDate.getBegin(), chronicDate.getEnd());
					}
					else {
						mainActivity.showErrorMessage("Couldn't parse a date from specified image",
								null);
					}

				} else {
					mainActivity.showErrorMessage("Couldn't parse text from specified image", 
							null);
				}
			}
		}
		
		/* Show the UI elements again */
		mainActivity.progressBar.setVisibility(View.GONE);
		mainActivity.backButton.setVisibility(View.VISIBLE);
		mainActivity.buttonBar.setVisibility(View.VISIBLE);
		mainActivity.currentOcrTask = null;
	}


	/**
	 * Converts a Bitmap to a byte[]. Need to use in order to send our image
	 * to the Project Hawaii OCR service
	 * @param targetBitmap The Bitmap to convert
	 * @return A byte[] representation of the Bitmap
	 */
	private static byte[] getImageByte(Bitmap targetBitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		targetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}	
	
}


