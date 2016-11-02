package com.jumio.sample;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.jumio.MobileSDK;
import com.jumio.bam.*;
import com.jumio.bam.custom.*;
import com.jumio.core.enums.*;
import com.jumio.core.exceptions.*;
import com.jumio.md.MultiDocumentSDK;
import com.jumio.nv.*;

import java.util.ArrayList;

/**
 * Sample activity using the JumioSDK
 */
public class SampleActivity extends Activity implements BamCustomScanInterface {

    private final static String TAG = "JumioMobileSDKSample";
    private static final int PERMISSION_REQUEST_CODE_BAM = 300;
    private static final int PERMISSION_REQUEST_CODE_NETVERIFY = 301;
    private static final int PERMISSION_REQUEST_CODE_BAM_CUSTOM = 302;
	private static final int PERMISSION_REQUEST_CODE_MULTI_DOCUMENT = 303;

    /* PUT YOUR NETVERIFY API TOKEN AND SECRET HERE */
    private static String NETVERIFY_API_TOKEN = "";
    private static String NETVERIFY_API_SECRET = "";

    /* PUT YOUR BAM API TOKEN AND SECRET HERE */
    private static String BAM_API_TOKEN = "";
    private static String BAM_API_SECRET = "";

    private Button startBamButton;
    private Button startBamCustomButton;
    private Button stopBamCustomButton;
    private Button startNetverifyButton;
    private Button startMultiDocumentButton;

    private ImageView switchCameraImageView;
    private ImageView toggleFlashImageView;
    private RelativeLayout bamCustomContainer;


    private NetverifySDK netverifySDK;
    private BamSDK bamSDK;
    private BamCustomScanPresenter customScanPresenter;
    private MultiDocumentSDK multiDocumentSDK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        startBamButton = (Button) findViewById(R.id.startBamButton);
        startBamButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //Since the BamSDK is a singleton internally, a new instance is not
                //created here.
                initializeBamSDK();
                try {
                    checkPermissionsAndStart(bamSDK);
                } catch(IllegalArgumentException e) {
                    Toast.makeText(SampleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        startBamCustomButton = (Button) findViewById(R.id.startBamCustomButton);
        startBamCustomButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //Since the BamSDK is a singleton internally, a new instance is not
                //created here.
                if (!MobileSDK.hasAllRequiredPermissions(SampleActivity.this)) {
                    ActivityCompat.requestPermissions(SampleActivity.this, MobileSDK.getMissingPermissions(SampleActivity.this), PERMISSION_REQUEST_CODE_BAM_CUSTOM);
                } else
                    startBamCustom();
            }
        });

        stopBamCustomButton = (Button) findViewById(R.id.stopBamCustomButton);
        stopBamCustomButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //Do not just re-instantiate the SDK here because fast subsequent taps on the button
                //can cause two SDK instances to be created, which will result in undefined (and
                //most likely incorrect) behaviour. A suitable place for the re-instantiation of the SDK
                //would be onCreate().
                customScanPresenter.stopScan();
                customScanPresenter.clearSDK();
                bamCustomContainer.setVisibility(View.GONE);

                startBamButton.setVisibility(View.VISIBLE);
                startBamCustomButton.setVisibility(View.VISIBLE);
                startNetverifyButton.setVisibility(View.VISIBLE);
	            startMultiDocumentButton.setVisibility(View.VISIBLE);
            }
        });

        startNetverifyButton = (Button) findViewById(R.id.startNetverifyButton);
        startNetverifyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //Since the NetverifySDK is a singleton internally, a new instance is not
                //created here.
                initializeNetverifySDK();
                checkPermissionsAndStart(netverifySDK);
            }
        });

	    startMultiDocumentButton = (Button) findViewById(R.id.startMultiDocumentButton);
	    startMultiDocumentButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View view) {

			    //Since the MultiDocumentSDK is a singleton internally, a new instance is not
			    //created here.
			    initializeMultiDocumentSDK();
			    checkPermissionsAndStart(multiDocumentSDK);
		    }
	    });

        bamCustomContainer = (RelativeLayout) findViewById(R.id.bamCustomContainer);

        switchCameraImageView = (ImageView) findViewById(R.id.switchCameraImageView);
        switchCameraImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                if (customScanPresenter != null && customScanPresenter.hasMultipleCameras())
                    customScanPresenter.switchCamera();
            }
        });

        toggleFlashImageView = (ImageView) findViewById(R.id.toggleFlashImageView);
        toggleFlashImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (customScanPresenter != null && customScanPresenter.hasFlash()) {
                    customScanPresenter.toggleFlash();
                    v.setEnabled(true);
                }
                toggleFlashImageView.setImageResource(customScanPresenter.isFlashOn() ? R.drawable.ic_flash_off : R.drawable.ic_flash_on);
            }
        });

        initializeBamSDK();
        initializeNetverifySDK();
    }

    private void startBamCustom() {

        initializeBamSDK();
        try {
            customScanPresenter = bamSDK.start(SampleActivity.this, (BamCustomScanView) findViewById(R.id.bamCustomScanView));
            startBamButton.setVisibility(View.GONE);
            startBamCustomButton.setVisibility(View.GONE);
            startNetverifyButton.setVisibility(View.GONE);
	        startMultiDocumentButton.setVisibility(View.GONE);
            bamCustomContainer.setVisibility(View.VISIBLE);
        } catch(IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissionsAndStart(MobileSDK sdk) {
        if (!MobileSDK.hasAllRequiredPermissions(this)) {
            //Acquire missing permissions.
            String[] mp = MobileSDK.getMissingPermissions(this);

            int code;
            if (sdk instanceof BamSDK)
                code = PERMISSION_REQUEST_CODE_BAM;
            else if (sdk instanceof NetverifySDK)
                code = PERMISSION_REQUEST_CODE_NETVERIFY;
            else if (sdk instanceof MultiDocumentSDK)
	            code = PERMISSION_REQUEST_CODE_MULTI_DOCUMENT;
            else {
                Toast.makeText(this, "Invalid SDK instance", Toast.LENGTH_LONG).show();
                return;
            }

            ActivityCompat.requestPermissions(this, mp, code);
            //The result is received in onRequestPermissionsResult.
        } else {
            startSdk(sdk);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean allGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            if (requestCode == PERMISSION_REQUEST_CODE_BAM) {
                startSdk(bamSDK);
            } else if (requestCode == PERMISSION_REQUEST_CODE_NETVERIFY) {
                startSdk(netverifySDK);
            } else if (requestCode == PERMISSION_REQUEST_CODE_BAM_CUSTOM) {
                startBamCustom();
            } else if (requestCode == PERMISSION_REQUEST_CODE_MULTI_DOCUMENT) {
	            startSdk(multiDocumentSDK);
            }
        } else {
            Toast.makeText(this, "You need to grant all required permissions to start the Jumio SDK", Toast.LENGTH_LONG).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startSdk(MobileSDK sdk) {
        try {
            sdk.start();
        } catch (MissingPermissionException e) {
            Toast.makeText(SampleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        if (customScanPresenter != null)
            customScanPresenter.onActivityPause();
        super.onPause();
    }

    private void initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
            // NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            // NetverifySDK.isSupportedPlatform();

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(SampleActivity.this, NETVERIFY_API_TOKEN, NETVERIFY_API_SECRET, JumioDataCenter.US);

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setRequireVerification(true);

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
            // String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
            // netverifySDK.setPreselectedCountry("AUT");
            // ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
            // documentTypes.add(NVDocumentType.PASSPORT);
            // netverifySDK.setPreselectedDocumentTypes(documentTypes);
            // netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

            // The merchant scan reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // netverifySDK.setMerchantScanReference("YOURSCANREFERENCE");

            // Use the following property to identify the scan in your reports (max. 100 characters).
            // netverifySDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            // netverifySDK.setCustomerId("CUSTOMERID");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
            // netverifySDK.setCallbackUrl("YOURCALLBACKURL");

            // You can enable face match during the ID verification for a specific transaction.
            // netverifySDK.setRequireFaceMatch(true);

            // Use the following method to pass first and last name to Fastfill for name match.
            // netverifySDK.setName("FIRSTNAME LASTNAME");

            // Use the following method to disable ePassport scanning.
            // netverifySDK.setEnableEpassport(false);

            // Set the default camera position
            // netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to only support IDs where data can be extracted on mobile only.
            // netverifySDK.setDataExtractionOnMobileOnly(true);

            // Use the following method to disable showing help before scanning.
            // netverifySDK.setShowHelpBeforeScan(false);

            // Additional information for this scan should not contain sensitive data like PII (Personally Identifiable Information) or account login
            // netverifySDK.setAdditionalInformation("YOURADDITIONALINFORMATION");

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
			// Only set this property to true if you are asked by our Jumio support personnel.
			// netverifySDK.sendDebugInfoToJumio(true);

        } catch (PlatformNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeBamSDK() {
        try {
            // You can get the current SDK version using the method below.
            // BamSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported
            // BamSDK.isSupportedPlatform();

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (BamSDK.isRooted())
                Log.w(TAG, "Device is rooted");

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct, specify an instance
            // of your activity and provide a reference to identify the scans in your reports (max. 100
            // characters or null). If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            bamSDK = BamSDK.create(SampleActivity.this, BAM_API_TOKEN, BAM_API_SECRET, "YOURREPORTINGCRITERIA", JumioDataCenter.US);

            // Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).
            // bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // To restrict supported card types, pass an ArrayList of CreditCardTypes to the setSupportedCreditCardTypes method.
            // ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();
            // creditCardTypes.add(CreditCardType.VISA);
            // creditCardTypes.add(CreditCardType.MASTER_CARD);
            // creditCardTypes.add(CreditCardType.AMERICAN_EXPRESS);
            // creditCardTypes.add(CreditCardType.DINERS_CLUB);
            // creditCardTypes.add(CreditCardType.DISCOVER);
            // creditCardTypes.add(CreditCardType.CHINA_UNIONPAY);
            // creditCardTypes.add(CreditCardType.JCB);
            // bamSDK.setSupportedCreditCardTypes(creditCardTypes);

            // Expiry recognition, card holder name and CVV entry are enabled by default and can be disabled.
            // You can enable the recognition of sort code and account number.
            // bamSDK.setExpiryRequired(false);
            // bamSDK.setCardHolderNameRequired(false);
            // bamSDK.setCvvRequired(false);
            // bamSDK.setSortCodeAndAccountNumberRequired(true);

            // You can show the unmasked credit card number to the user during the workflow if setCardNumberMaskingEnabled is disabled.
            // bamSDK.setCardNumberMaskingEnabled(false);

            // The user can edit the recognized expiry date if setExpiryEditable is enabled.
            // bamSDK.setExpiryEditable(true);

            // Use setName to pass first and last name for name match if card holder recognition is enabled.
            // bamSDK.setName("FIRSTNAME LASTNAME");

            // The user can edit the recognized card holder name if setCardHolderNameEditable is enabled.
            // bamSDK.setCardHolderNameEditable(true);

            // You can set a short vibration and sound effect to notify the user that the card has been detected.
            // bamSDK.setVibrationEffectEnabled(true);
            // bamSDK.setSoundEffect(R.raw.shutter_sound);

            // Set the default camera position.
            // bamSDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Automatically enable flash when scan is started.
            // bamSDK.setEnableFlashOnScanStart(true);

            // Use the following method to provide the Adyen Public Key. This activates the generation
            // of an encrypted Adyen payment data object.
            // bamSDK.setAdyenPublicKey("YOUR ADYEN PUBLIC KEY");

            // You can add custom fields to the confirmation page (keyboard entry or predefined values).
            // bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), getString(R.string.zip_code_hint), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");
            // ArrayList<String> states = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.state_selection_values)));
            // bamSDK.addCustomField("stateId", getString(R.string.state), getString(R.string.state_hint), states, false, getString(R.string.state_reset_value));

        } catch (PlatformNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
        }
    }

	private void initializeMultiDocumentSDK() {
		try {
			// You can get the current SDK version using the method below.
			MultiDocumentSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported.
			MultiDocumentSDK.isSupportedPlatform();

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			multiDocumentSDK = MultiDocumentSDK.create(SampleActivity.this, NETVERIFY_API_TOKEN, NETVERIFY_API_SECRET, JumioDataCenter.US);

			// One of the configured DocumentTypeCodes: BC, BS, CAAP, CB, CCS, CRC, HCC, IC, LAG, LOAP,
			// MEDC, MOAP, PB, SEL, SENC, SS, STUC, TAC, TR, UB, SSC, USSS, VC, VT, WWCC, CUSTOM
			multiDocumentSDK.setType("BC");

			// ISO 3166-1 alpha-3 country code
			multiDocumentSDK.setCountry("USA");

			// The merchant scan reference allows you to identify the scan (max. 100 characters).
			// Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
			multiDocumentSDK.setMerchantScanReference("YOURSCANREFERENCE");

			// You can also set a customer identifier (max. 100 characters).
			// Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
			multiDocumentSDK.setCustomerId("CUSTOMERID");

			// One of the Custom Document Type Codes as configurable by Merchant in Merchant UI.
			// multiDocumentSDK.setCustomDocumentCode("YOURCUSTOMDOCUMENTCODE");

			// Overrides the label for the document name (on Help Screen below document icon)
			// multiDocumentSDK.setDocumentName("DOCUMENTNAME");

			// Use the following property to identify the scan in your reports (max. 255 characters).
			// multiDocumentSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

			// Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
			// multiDocumentSDK.setCallbackUrl("YOURCALLBACKURL");

			// Set the default camera position
			// multiDocumentSDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Use the following method to disable showing help before scanning.
			// multiDocumentSDK.setShowHelpBeforeScan(false);

			// Additional information for this scan should not contain sensitive data like PII (Personally Identifiable Information) or account login
			// multiDocumentSDK.setAdditionalInformation("YOURADDITIONALINFORMATION");

		} catch (PlatformNotSupportedException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null)
                return;
            if (resultCode == Activity.RESULT_OK) {
                String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
                NetverifyDocumentData documentData = (data == null) ? null : (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
                int errorCode = data.getIntExtra(NetverifySDK.EXTRA_ERROR_CODE, 0);
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            if (netverifySDK != null) {
                netverifySDK.destroy();
                netverifySDK = null;
            }

        } else if (requestCode == BamSDK.REQUEST_CODE) {
            if (data == null)
                return;
            ArrayList<String> scanAttempts = data.getStringArrayListExtra(BamSDK.EXTRA_SCAN_ATTEMPTS);

            if (resultCode == Activity.RESULT_OK) {
                BamCardInformation cardInformation = data.getParcelableExtra(BamSDK.EXTRA_CARD_INFORMATION);

                cardInformation.clear();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                String errorMessage = data.getStringExtra(BamSDK.EXTRA_ERROR_MESSAGE);
                int errorCode = data.getIntExtra(BamSDK.EXTRA_ERROR_CODE, 0);
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            if (bamSDK != null) {
                bamSDK.destroy();
                bamSDK = null;
            }
        }
    }

    //Called as soon as the camera is available for the custom scan. It is safe to check for flash and additional cameras here.
    @Override
    public void onBamCameraAvailable() {
        Log.d("BamCustomScan", "camera available");
        switchCameraImageView.setVisibility(customScanPresenter.hasMultipleCameras() ? View.VISIBLE : View.INVISIBLE);
        switchCameraImageView.setImageResource(customScanPresenter.isCameraFrontFacing() ? R.drawable.ic_camera_rear : R.drawable.ic_camera_front);
        toggleFlashImageView.setVisibility(customScanPresenter.hasFlash() ? View.VISIBLE : View.INVISIBLE);
        toggleFlashImageView.setImageResource(customScanPresenter.isFlashOn() ? R.drawable.ic_flash_off : R.drawable.ic_flash_on);
    }

    @Override
    public void onBamError(int errorCode, int detailedErrorCode, String errorMessage, boolean retryPossible, ArrayList<String> scanAttempts) {
        Log.d("BamCustomScan", "error occured");
        //Do not show error dialog when it is an error because of background execution not supported exception.
        if (errorCode == 310)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Scan error");
        alertDialogBuilder.setMessage(errorMessage);
        if (retryPossible) {
            alertDialogBuilder.setPositiveButton("retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        customScanPresenter.retryScan();
                    } catch (UnsupportedOperationException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopBamCustomButton.performClick();
            }
        });
        alertDialogBuilder.show();
    }

    //When extraction is started, the preview screen will be paused. A loading indicator can be displayed within this callback.
    @Override
    public void onBamExtractionStarted() {
        Log.d("BamCustomScan", "extraction started");
    }

    @Override
    public void onBamExtractionFinished(BamCardInformation bamCardInformation, ArrayList<String> scanAttempts) {
        Log.d("BamCustomScan", "extraction finished");
        bamCardInformation.clear();
    }
}