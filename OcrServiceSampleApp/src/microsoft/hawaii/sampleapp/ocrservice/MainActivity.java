package microsoft.hawaii.sampleapp.ocrservice;

import java.io.ByteArrayOutputStream;
import java.util.List;

import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;
import microsoft.hawaii.sampleappbase.HawaiiBaseAuthActivity;
import microsoft.hawaii.sdk.OcrService.OcrService;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrServiceResult;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrText;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends HawaiiBaseAuthActivity {
	private static final int CAMERA_REQUEST = 1888;
	private static final int SELECT_IMAGE = 2888;

	private ImageView imageView;
	private Button recognizeButton;
	private ProgressBar progressBar;
	private LinearLayout resultContainer;
	private TextView ocrResultView;

	private AsyncTask<Void, Integer, AlertDialog.Builder> currentOcrTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.progressBar = (ProgressBar) this
				.findViewById(R.id.ocr_progressbar);
		this.imageView = (ImageView) this.findViewById(R.id.imageView);
		this.recognizeButton = (Button) this
				.findViewById(R.id.recognize_button);
		this.resultContainer = (LinearLayout) this
				.findViewById(R.id.ocr_result_container);
		this.ocrResultView = (TextView) this
				.findViewById(R.id.ocrResult_textview);
		this.ocrResultView.setTextColor(Color.GREEN);
		this.ocrResultView.setTextSize(25);

		this.recognizeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onRecognizeButtonClick(v);
			}
		});

		Button captureButton = (Button) this
				.findViewById(R.id.captureimage_button);
		captureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

		Button loadButton = (Button) this.findViewById(R.id.loadImage_button);
		loadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Create a new Intent to open the picture selector
				Intent loadPicture = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(loadPicture, SELECT_IMAGE);
			}
		});

		// load default image from resource file
		this.imageView.setImageResource(R.drawable.welcome);
	}

	@Override
	protected void onPause() {
		super.onPause();

		AsyncTask<Void, Integer, AlertDialog.Builder> task = this.currentOcrTask;
		if (task != null) {
			task.cancel(true);
			this.currentOcrTask = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK && data != null) {
			if (requestCode == CAMERA_REQUEST) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				this.imageView.setImageBitmap(photo);
				this.resultContainer.setVisibility(View.GONE);
			} else if (requestCode == SELECT_IMAGE) {
				// This gets the URI of the image the user selected
				Uri imgUri = data.getData();
				this.imageView.setImageURI(imgUri);
				this.resultContainer.setVisibility(View.GONE);
			}
		}
	}

	private void onRecognizeButtonClick(View v) {
		this.recognizeButton.setEnabled(false);

		final MainActivity thisActivity = this;
		class OcrTask extends AsyncTask<Void, Integer, AlertDialog.Builder> {
			private OcrServiceResult serviceResult;

			protected AlertDialog.Builder doInBackground(Void... listTypes) {
				// get image from ImageView
				Bitmap image = ((BitmapDrawable) imageView.getDrawable())
						.getBitmap();
				byte[] imageBytes = getImageByte(image);
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
						} else {
							thisActivity.showErrorMessage(
									"Couldn't recognize the specified image",
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

		currentOcrTask = new OcrTask();
		currentOcrTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private static byte[] getImageByte(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
