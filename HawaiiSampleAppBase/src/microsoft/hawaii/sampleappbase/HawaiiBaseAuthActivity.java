package microsoft.hawaii.sampleappbase;

import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;
import microsoft.hawaii.sampleappbase.HawaiiBaseApplication.AuthenticationType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Base Activity for Hawaii authentication
 */
public class HawaiiBaseAuthActivity extends Activity {

	/**
	 * Error code for missing authentication type in configuration file
	 */
	static final int MISSING_NETWORK_CONNECTION = 0;

	/**
	 * Error code for missing authentication value in configuration file
	 */
	static final int MISSING_AUTHENTICATION_TYPE = 1;

	/**
	 * Error code for missing authentication value in configuration file
	 */
	static final int MISSING_AUTHENTICATION_VALUES = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			this.showDialog(MISSING_NETWORK_CONNECTION);
			return;
		}

		if (!this.shouldAuthenticate()) {
			return;
		}

		AuthenticationType authType = this.getAuthenticationTypeFromApp();
		if (authType == AuthenticationType.NOVALUE) {
			this.showDialog(MISSING_AUTHENTICATION_TYPE);
			return;
		}

		// create ClientIdentity object based on configured Hawaii client
		// credential
		ClientIdentity identity = null;
		try {
			switch (authType) {
			case GUID:
				String appId = this.getString(R.string.hawaii_GUID_app_ID);
				if (Utility.isStringNullOrEmpty(appId)) {
					this.showDialog(MISSING_AUTHENTICATION_VALUES);
					return;
				}

				identity = ClientIdentity.createClientIdentity(appId);
				break;
			case ADM:
				String clientId = this.getString(R.string.hawaii_ADM_client_ID);
				String clientSecret = this
						.getString(R.string.hawaii_ADM_client_secret);
				String serviceScope = this
						.getString(R.string.hawaii_ADM_service_scope);

				if (Utility.isStringNullOrEmpty(clientId)
						|| Utility.isStringNullOrEmpty(clientSecret)
						|| Utility.isStringNullOrEmpty(serviceScope)) {
					this.showDialog(MISSING_AUTHENTICATION_VALUES);
					return;
				}
				identity = ClientIdentity.createClientIdentity(clientId,
						clientSecret, serviceScope);
				break;
			case NOVALUE:
				this.showErrorMessage(
						"Couldn't configure client identity for calling Hawaii service",
						null);
				return;
			}
		} catch (Exception exception) {
			this.showErrorMessage(
					"Couldn't configure client identity for calling Hawaii service",
					exception);
		}

		if (identity != null) {
			this.getBaseApplication().setClientIdentity(identity);
		} else {
			this.showErrorMessage(
					"Couldn't configure client identity for calling Hawaii service",
					null);
		}
	}

	/**
	 * 
	 * getAuthenticationTypeFromApp Return authentication type which assigned by
	 * application
	 * 
	 * @return AuthenticationType
	 */
	protected AuthenticationType getAuthenticationTypeFromApp() {
		return this.getBaseApplication().getAuthType();
	}

	/**
	 * Return a flag to indicate whether current Activity needs authentication
	 * 
	 * @return boolean
	 */
	protected boolean shouldAuthenticate() {
		return true;
	}

	/**
	 * show ErrorMessage using AlertDialog
	 * 
	 * @param title
	 *            title to display on the dialog
	 * @param exception
	 *            exception to display on the dialog
	 */
	protected void showErrorMessage(String title, Throwable exception) {
		if (exception != null) {
			exception.printStackTrace();
			System.out.println(exception.toString());
		}

		AlertDialog dialog = dialogToShow(title, exception).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * show ErrorMessage using AlertDialog
	 * 
	 * @param title
	 *            title to display on the dialog
	 * @param exception
	 *            exception to display on the dialog
	 */
	protected void showErrorMessage(AlertDialog.Builder builder) {
		if (builder == null) {
			return;
		}

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * show specified message in toast
	 * 
	 * @param message
	 *            specified message
	 */
	protected void showMessage(String message) {
		if (!Utility.isStringNullOrEmpty(message)) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * create a {@link AlertDialog.Builder} to show dialog
	 * 
	 * @param title
	 *            title to display on the dialog
	 * @param exception
	 *            exception to display on the dialog
	 * @return AlertDialog.Builder
	 */
	protected AlertDialog.Builder dialogToShow(String title, Throwable exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		if (exception != null) {
			builder.setMessage(exception.getLocalizedMessage());
		}

		builder.setCancelable(true);
		return builder;
	}

	/**
	 * gets current {@link HawaiiBaseApplication} object
	 * 
	 * @return HawaiiBaseApplication
	 */
	protected HawaiiBaseApplication getBaseApplication() {
		return (HawaiiBaseApplication) this.getApplication();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.configuration_error_title));
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);

		String errorMessage = this.getErrorMessage(id);
		dialog.setMessage(errorMessage);

		final Activity activity = this;
		dialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				activity.finish();
			}
		});

		return dialog;
	}

	protected String getErrorMessage(int id) {
		switch (id) {
		case MISSING_NETWORK_CONNECTION:
			return getString(R.string.error_missing_network_connection);
		case MISSING_AUTHENTICATION_TYPE:
			return getString(R.string.error_missing_authentication_type);
		case MISSING_AUTHENTICATION_VALUES:
			return getString(R.string.error_missing_authentication_value);
		default:
			return null;
		}
	}
}
