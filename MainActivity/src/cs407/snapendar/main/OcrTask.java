package cs407.snapendar.main;

import java.io.ByteArrayOutputStream;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

public class OcrTask extends AsyncTask<Void, Integer, AlertDialog.Builder> {

	private OcrServiceResult serviceResult;

	public final MainActivity thisActivity;

	public OcrTask(MainActivity a)
	{
		thisActivity = a;
	}
	
	protected void onPreExecute() {
		// TODO: Set all this stuff up in MainActivity in the beginOCR() call
		thisActivity.resultContainer.setVisibility(View.GONE);
		thisActivity.progressBar.setVisibility(View.VISIBLE);
		thisActivity.ocrResultView.setText("");
		thisActivity.imageView.setVisibility(View.GONE);
	}

	protected AlertDialog.Builder doInBackground(Void... listTypes) {
		// get image from ImageView
		Bitmap image = ((BitmapDrawable) thisActivity.imageView.getDrawable())
				.getBitmap();
		byte[] imageBytes = getImageByte(image);
		if(imageBytes.length >= 1572864) {

			return new AlertDialog.Builder(thisActivity)
			.setTitle("Size Warning")
			.setMessage("Your image is " + (double)imageBytes.length + 
					"B in size. This may cause unexpected errors.")
					.setPositiveButton("Continue", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							// continue with delete
						}
					})
					.setNegativeButton("Cancel", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							// do nothing
						}
					});

			/*return dialogToShow(
						"Your image is " + 
						(double)imageBytes.length + 
						"B in size. This may cause unexpected errors.",
						new Exception());*/
		}
		try {
			OcrService.recognizeImage(thisActivity.getBaseApplication()
					.getClientIdentity(), imageBytes,
					new OnCompleteListener<OcrServiceResult>() {
				public void done(OcrServiceResult result) {
					serviceResult = result;

				}
			}, null);
		} catch (Exception exception) {
			return thisActivity.dialogToShow("Couldn't execute the ocr operation",
					exception);
		}

		if (serviceResult.getStatus() != microsoft.hawaii.hawaiiClientLibraryBase.Status.Success) {
			return thisActivity.dialogToShow(
					"Error when using ocr service to recognize the specified image",
					serviceResult.getException());
		}

		return null;
	}

	protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
		if (dialogBuilder != null) {
			thisActivity.showErrorMessage(dialogBuilder);
		} else {
			thisActivity.resultContainer.setVisibility(View.VISIBLE);
			List<OcrText> resultList = serviceResult.getOcrTexts();
			if (resultList.size() > 0) {
				String ocrResult = resultList.get(0).getText();
				if (!Utility.isStringNullOrEmpty(ocrResult)) {
					thisActivity.ocrResultView.setText(ocrResult);

					Span chronicDate = Chronic.parse(ocrResult);
					if(chronicDate != null) {
						thisActivity.chronicCalendar = chronicDate.getBeginCalendar();
						thisActivity.ocrResultView.setText("Year: " + thisActivity.chronicCalendar.get(Calendar.YEAR) + 
								"\nMonth: " + (thisActivity.chronicCalendar.get(Calendar.MONTH)+1) +
								"\nDay: " + thisActivity.chronicCalendar.get(Calendar.DAY_OF_MONTH));
					}
					else {
						thisActivity.showErrorMessage("Couldn't parse a date from specified image",
								null);
					}

				} else {
					thisActivity.showErrorMessage("Couldn't recognize the specified image", 
							null);
				}
			}
		}

		thisActivity.progressBar.setVisibility(View.GONE);
		thisActivity.imageView.setVisibility(View.VISIBLE);
		thisActivity.currentOcrTask = null;
	}


	/**
	 * Converts a Bitmap to a byte[]. Need to use in order to send our image
	 * to the Project Hawaii OCR service
	 * @param targetBitmap The Bitmap to convert
	 * @return A byte[] representation of the Bitmap
	 */
	private static byte[] getImageByte(Bitmap targetBitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		targetBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}	
	
}


