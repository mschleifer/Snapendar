package cs407.snapendar.main;

import cs407.snapendar.main.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends HawaiiBaseAuthActivity {
	private static final int SELECT_IMAGE = 2888;

	/* UI Elements */
	protected ImageView imageView;
	protected ProgressBar progressBar;
	protected LinearLayout resultContainer;
	protected TextView ocrResultView;
	protected SurfaceView camSurface;
	protected FrameLayout cameraFrame;
	protected LinearLayout buttonBar;

	protected Button loadButton;
	protected Button backButton;
	protected Button savedButton;
	protected Button helpButton;
	protected Button shutterButton;

	Preview preview;
	Camera camera;
	String fileName;

	protected static Storage storage;

	/* Calendar holding any dates parsed after OCR */
	protected Calendar chronicCalendar;

	/* Class variable to represent the "photo" captured/loaded by user */
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
		storage = new Storage();
		//storage.writeTestFile();

		boolean hasRunBefore = storage.snapendarDirectoryExists();
		if(!hasRunBefore){
			pushToast(getString(R.string.helptext));
		}

		/* Setup all the class members from the view objects*/
		this.progressBar = (ProgressBar) this.findViewById(R.id.ocr_progressbar);
		this.imageView = (ImageView) this.findViewById(R.id.imageView);
		this.camSurface = (SurfaceView)findViewById(R.id.surfaceView);
		this.cameraFrame = (FrameLayout)findViewById(R.id.preview);
		this.buttonBar = (LinearLayout)findViewById(R.id.ButtonBar);

		this.loadButton = (Button) this.findViewById(R.id.LoadBtn);
		this.backButton = (Button) this.findViewById(R.id.BackBtn);
		this.savedButton = (Button) this.findViewById(R.id.SavedSnapsBtn);
		this.helpButton = (Button) this.findViewById(R.id.HelpButton);
		this.shutterButton = (Button) this.findViewById(R.id.SnapItBtn);

		this.resultContainer = (LinearLayout) this.findViewById(R.id.ocr_result_container);
		this.ocrResultView = (TextView) this.findViewById(R.id.ocrResult_textview);
		this.ocrResultView.setTextSize(25);

		/* Setup some stuff for the camera preview */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		preview = new Preview(this, camSurface);
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		preview.setKeepScreenOn(true);

		/* Setup the OnClickListeners for each button of the UI */

		this.helpButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				pushToast(getString(R.string.helptext));
			}
		});

		this.savedButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				//Loading "Gallery" activity for testing
				if(Storage.storageAccessible()){
					Intent intent = new Intent(v.getContext(), InfoActivity.class);
					startActivity(intent);      
				}
				else{
					pushToast("Storage isn't accessible.");
				}
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

		this.backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				imageView.setVisibility(View.GONE);
				resultContainer.setVisibility(View.GONE);
				backButton.setVisibility(View.GONE);

				shutterButton.setVisibility(View.VISIBLE);
				cameraFrame.setVisibility(View.VISIBLE);
				resetCam();
			}
		});

		shutterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				camera.takePicture(shutterCallback, null, jpegCallback);
			}
		});

		shutterButton.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				camera.autoFocus(new AutoFocusCallback(){
					@Override
					public void onAutoFocus(boolean arg0, Camera arg1) {
						camera.takePicture(shutterCallback, null, jpegCallback);
					}
				});
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		camera = Camera.open();

		Camera.Parameters parameters = camera.getParameters();

		/* Select the camera resolution; use 640x480 if available or next largest if not */
		List<Size> sizes = parameters.getSupportedPictureSizes();
		for(int i = sizes.size()-1; i >= 0; i--) {
			if(sizes.get(i).width == 640 && sizes.get(i).height == 480) {
				parameters.setPictureSize(sizes.get(i).width, sizes.get(i).height);
				break;
			}
			if(sizes.get(i).width > 640 && sizes.get(i).height > 480) {
				parameters.setPictureSize(sizes.get(i).width, sizes.get(i).height);
				break;
			}
		}

		camera.setParameters(parameters);

		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			camera.setDisplayOrientation(90);
			//lp.height = previewSurfaceHeight;
			//lp.width = (int) (previewSurfaceHeight / aspect);
		} else {
			camera.setDisplayOrientation(0);
			// lp.width = previewSurfaceWidth;
			//lp.height = (int) (previewSurfaceWidth / aspect);
		}

		preview.setCamera(camera);
		camera.startPreview();
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

		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
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

		// If our Intent call was a great success
		if (resultCode == Activity.RESULT_OK && data != null) {
			if (requestCode == SELECT_IMAGE) {
				// This gets the URI of the image the user selected
				Uri imgUri = data.getData();


				/* Convert the URI to a Bitmap we can store; may break if 
				 * the image of the URI is large*/
				try {
					InputStream input = this.getContentResolver().openInputStream(imgUri);

					BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
					onlyBoundsOptions.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
					input.close();

					if(onlyBoundsOptions.outWidth > 640 && onlyBoundsOptions.outWidth > 480) {
						input = this.getContentResolver().openInputStream(imgUri);
						photo = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input, null, null),
								640, 480, false);
						input.close();
					}
					else {
						photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
					}

					this.imageView.setImageBitmap(photo);

				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
			beginOcr();
		}
	}

	protected void beginOcr(){
		shutterButton.setVisibility(View.GONE);
		cameraFrame.setVisibility(View.GONE);
		buttonBar.setVisibility(View.GONE);

		imageView.setVisibility(View.VISIBLE); //Make the image view visible.
		resultContainer.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		ocrResultView.setText("");

		currentOcrTask = new OcrTask(this);
		currentOcrTask.execute();
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d("snap", "onShutter'd");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			if(Storage.storageAccessible()) {
				/* Write to External Storage */

				storage.writeFile(String.format("SNPNDR_%d.jpg", System.currentTimeMillis()), data);

				Log.d("snap", "onPictureTaken - wrote bytes: " + data.length);
				Log.d("snap", "onPictureTaken - jpeg");
				pushToast("Image saved to " + storage.getDirectoryPath());
			}

			InputStream is = new ByteArrayInputStream(data);
			photo = BitmapFactory.decodeStream(is);

			imageView.setImageBitmap(photo);
			beginOcr();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	public void pushToast(String text){
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);

		toast.show();
	}
}
