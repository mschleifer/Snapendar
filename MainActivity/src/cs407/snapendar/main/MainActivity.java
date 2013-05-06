package cs407.snapendar.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;
import cs407.snapendar.main.R;
import microsoft.hawaii.sdk.OcrService.OcrService;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrServiceResult;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.text.format.Time;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.*;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

//TODO: Make it so that when the screen is rotated the activity doesn't reset
//TODO: Need to save data across the "Activity Lifetime Cycle" i.e onCreate/onDestroy
//TODO: http://developer.android.com/guide/topics/resources/runtime-changes.html

public class MainActivity extends HawaiiBaseAuthActivity {
	private static final int CAMERA_REQUEST = 1888;
	private static final int SELECT_IMAGE = 2888;
	
	protected ImageView imageView;
	protected ProgressBar progressBar;
	protected LinearLayout resultContainer;
	protected TextView ocrResultView;
	
	protected Button recognizeButton;
	protected Button loadButton;
	protected Button captureButton;
	protected Button setEventButton;
	protected Button testInfoButton;

	protected Calendar chronicCalendar;
	
	/* Class variable to represent the "photo" captured by the camera */
	protected Bitmap photo = null;

	/* Task for calling the Project Hawaii OCR to keep it off the main thread */
	protected AsyncTask<Void, Integer, AlertDialog.Builder> currentOcrTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* Check if something was saved during onSaveInstanceState() */
		if(savedInstanceState != null) {
			photo = savedInstanceState.getParcelable("bitmap");
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Setup all the class members from the view objects*/
		this.progressBar = (ProgressBar) this
				.findViewById(R.id.ocr_progressbar);
		this.imageView = (ImageView) this.findViewById(R.id.imageView);
		
		this.recognizeButton = (Button) this.findViewById(R.id.recognize_button);
		this.testInfoButton = (Button) this.findViewById(R.id.testinfobtn);
		this.captureButton = (Button) this.findViewById(R.id.captureimage_button);
		this.loadButton = (Button) this.findViewById(R.id.loadImage_button);
		this.setEventButton= (Button) this.findViewById(R.id.testCal_button);
		
		this.resultContainer = (LinearLayout) this
				.findViewById(R.id.ocr_result_container);
		this.ocrResultView = (TextView) this
				.findViewById(R.id.ocrResult_textview);
		this.ocrResultView.setTextColor(Color.GREEN);
		this.ocrResultView.setTextSize(25);

		/* Setup the OnClickListeners for each button of the UI */
		this.recognizeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onRecognizeButtonClick(v);
			}
		});
		
		this.testInfoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent testInty = new Intent(MainActivity.this, InfoActivity.class);
				startActivity(testInty);
			}
		});

		 this.captureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

		this.loadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Create a new Intent to open the picture selector
				Intent loadPicture = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(loadPicture, SELECT_IMAGE);
			}
		});
		
		/* First-run test code for adding a new event to the calendar. May want to 
		 * write our own calendar insertion code instead of using an Intent. Needed
		 * to change the Min Android version to 4.0 for this to work. */
		this.setEventButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(chronicCalendar != null) {
					Intent intent = new Intent(Intent.ACTION_INSERT)
				        .setData(Events.CONTENT_URI)
				        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, chronicCalendar.getTimeInMillis())
				        .putExtra(Events.TITLE, "My Event on " + (chronicCalendar.get(Calendar.MONTH)+1) + "/" + chronicCalendar.get(Calendar.DAY_OF_MONTH))
				        //.putExtra(Events.DESCRIPTION, "Super cool thing")
				        //.putExtra(Events.EVENT_LOCATION, "CS 1240")
				        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
				startActivity(intent);
				}
				// TODO: Might want to throw a pop-up/error or something here
			}
		});

		
		/* If the user has taken/selected a photo we load that to the screen; otherwise WELCOME */
		if(photo == null) {
			this.imageView.setImageResource(R.drawable.example2);
		}
		else {
			this.imageView.setImageBitmap(photo);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		/* If the user pauses (i.e. leaves) the application during the OCR process it's
		 * safest to cancel the OCR task altogether */
		AsyncTask<Void, Integer, AlertDialog.Builder> task = this.currentOcrTask;
		if (task != null) {
			task.cancel(true);
			this.currentOcrTask = null;
		}
	}
	
	/* We need to save the currently taken/selected photo between configuration changes
	 * (e.g. screen rotation) because any config change destroys and recreates the activity */
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putParcelable("bitmap", photo);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// If our Intent calls were a great success
		if (resultCode == Activity.RESULT_OK && data != null) {
			if (requestCode == CAMERA_REQUEST) {
				photo = (Bitmap) data.getExtras().get("data");
				this.imageView.setImageBitmap(photo);
			}
			else if (requestCode == SELECT_IMAGE) {
				// This gets the URI of the image the user selected
				Uri imgUri = data.getData();
				this.imageView.setImageURI(imgUri);
				
				/* Convert the URI to a Bitmap we can store; may break if 
				 * the image of the URI is large*/
				try {
					photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
			/* Hide the result container if it was visible from a previous OCR call */
			this.resultContainer.setVisibility(View.GONE);
			
			/* Reset the recognize/visible button to show recognize */
			recognizeButton.setVisibility(View.VISIBLE);
			setEventButton.setVisibility(View.GONE);
		}
	}

	private void onRecognizeButtonClick(View v) {
		this.recognizeButton.setEnabled(false);

		//final MainActivity thisActivity = this;
		class OcrTask extends AsyncTask<Void, Integer, AlertDialog.Builder> {
			private OcrServiceResult serviceResult;
			
			public final MainActivity thisActivity;

		    public OcrTask(MainActivity a)
		    {
		        thisActivity = a;
		    }

			protected AlertDialog.Builder doInBackground(Void... listTypes) {
				// get image from ImageView
				Bitmap image = ((BitmapDrawable) imageView.getDrawable())
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
					return dialogToShow("Couldn't execute the ocr operation",
							exception);
				}

				if (serviceResult.getStatus() != microsoft.hawaii.hawaiiClientLibraryBase.Status.Success) {
					return dialogToShow(
							"Error when using ocr service to recognize the specified image",
							serviceResult.getException());
				}

				return null;
			}

			protected void onPreExecute() {
				resultContainer.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				ocrResultView.setText("");
				imageView.setVisibility(View.GONE);
			}

			protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
				if (dialogBuilder != null) {
					thisActivity.showErrorMessage(dialogBuilder);
				} else {
					resultContainer.setVisibility(View.VISIBLE);
					List<OcrText> resultList = serviceResult.getOcrTexts();
					if (resultList.size() > 0) {
						String ocrResult = resultList.get(0).getText();
						if (!Utility.isStringNullOrEmpty(ocrResult)) {
							ocrResultView.setText(ocrResult);
							
							Span chronicDate = Chronic.parse(ocrResult);
							if(chronicDate != null) {
								chronicCalendar = chronicDate.getBeginCalendar();
								ocrResultView.setText("Year: " + chronicCalendar.get(Calendar.YEAR) + 
													  "\nMonth: " + (chronicCalendar.get(Calendar.MONTH)+1) +
													  "\nDay: " + chronicCalendar.get(Calendar.DAY_OF_MONTH));
								recognizeButton.setVisibility(View.GONE);
								setEventButton.setVisibility(View.VISIBLE);
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

				recognizeButton.setEnabled(true);
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				currentOcrTask = null;
			}
		}

		currentOcrTask = new OcrTask(this);
		currentOcrTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
