package com.payfriendz.cordova.jumio;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import com.jumio.mobile.sdk.*;
import com.jumio.mobile.sdk.enums.*;
import com.jumio.netswipe.sdk.*;
import com.jumio.netverify.barcode.*;
import com.jumio.netverify.barcode.enums.BarcodeFormat;
import com.jumio.netverify.sdk.*;
import com.jumio.netverify.sdk.custom.*;
import com.jumio.netverify.sdk.enums.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import java.text.SimpleDateFormat;

public class JumioMobileCordovaPlugin extends CordovaPlugin {
    private CallbackContext callbackContext;
    private NetverifySDK netverifySDK;

     @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {        
        if("isSupportedPlatform".equals(action)) {
            if (NetverifySDK.isSupportedPlatform(cordova.getActivity())){
                callbackContext.success();
            }else{
                callbackContext.error("Device not supported");
            }
            return true;
        }
        if("sdkVersion".equals(action)) {
            String sdkVersion = NetverifySDK.getSDKVersion();
            callbackContext.success(sdkVersion);
            return true;
        }
        if("presentNetverifyController".equals(action)) {
            this.callbackContext = callbackContext;
            this.presentNetverifyController(args.getJSONObject(0), args.getJSONObject(1), callbackContext);
            return true;
        }
        if("configureNetverifyControllerAppearence".equals(action)){
            //THIS IS A NO-OP ON JAVA, APPEARENCE IS NOT CONFIGURED THROUGH CODE
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject result = new JSONObject();

        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (resultCode == NetverifySDK.RESULT_CODE_SUCCESS || resultCode == NetverifySDK.RESULT_CODE_BACK_WITH_SUCCESS) {
                try{
                    result.put("scanReference", data.getStringExtra(NetverifySDK.RESULT_DATA_SCAN_REFERENCE));

                    NetverifyDocumentData documentData = (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.RESULT_DATA_SCAN_DATA);
                    JSONObject documentDataResult = new JSONObject();

                    documentDataResult.put("selectedCountry", documentData.getSelectedCountry());
                    documentDataResult.put("idNumber", documentData.getIdNumber());
                    documentDataResult.put("personalNumber", documentData.getPersonalNumber());
                    documentDataResult.put("issuingCountry", documentData.getIssuingCountry());
                    documentDataResult.put("lastName", documentData.getLastName());
                    documentDataResult.put("firstName", documentData.getFirstName());
                    documentDataResult.put("middleName", documentData.getMiddleName());
                    documentDataResult.put("originatingCountry", documentData.getOriginatingCountry());
                    documentDataResult.put("street", documentData.getStreet());
                    documentDataResult.put("city", documentData.getCity());
                    documentDataResult.put("state", documentData.getState());
                    documentDataResult.put("postalCode", documentData.getPostalCode());
                    documentDataResult.put("optionalData1", documentData.getOptionalData1());
                    documentDataResult.put("optionalData2", documentData.getOptionalData2());
                    documentDataResult.put("nameMatch", documentData.isNameMatch());
                    documentDataResult.put("nameDistance", documentData.getNameDistance());
                    documentDataResult.put("livenessDetected", documentData.getLivenessDetected());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    documentDataResult.put("issuingDate", sdf.format(documentData.getIssuingDate()));
                    documentDataResult.put("expiryDate", sdf.format(documentData.getExpiryDate()));
                    documentDataResult.put("dob", sdf.format(documentData.getDob()));

                    documentDataResult.put("selectedDocumentType", documentData.getSelectedDocumentType() == null ? "UNKNOWN" : documentData.getSelectedDocumentType().name());
                    documentDataResult.put("gender", documentData.getGender() == null ? "UNKNOWN" : documentData.getGender().toString());

                    result.put("documentData", documentDataResult);
                }catch(JSONException ex){
                    this.callbackContext.error("Error reading scan results");
                }

                this.callbackContext.success(result);
            } else if (resultCode == NetverifySDK.RESULT_CODE_CANCEL) {
                try{
                    result.put("scanReference", data.getStringExtra(NetverifySDK.RESULT_DATA_SCAN_REFERENCE));
                    result.put("errorCode", data.getIntExtra(NetverifySDK.RESULT_DATA_ERROR_CODE, 0));
                    result.put("errorMessage", data.getStringExtra(NetverifySDK.RESULT_DATA_ERROR_MESSAGE));

                    this.callbackContext.error(result);
                }catch(JSONException ex){
                    this.callbackContext.error("Scan failed, but so did the error reading");
                }
                
            }
            this.netverifySDK.destroy();
        }
    }

    private void presentNetverifyController(JSONObject crendentials, JSONObject configuration, CallbackContext callbackContext){
        try{
            this.netverifySDK = NetverifySDK.create(cordova.getActivity(), crendentials.getString("apiToken"), crendentials.getString("apiSecret"), JumioDataCenter.EU);
        }catch(org.json.JSONException exception){
            callbackContext.error("Invalid credentials");
        }catch(com.jumio.mobile.sdk.PlatformNotSupportedException exception){
            callbackContext.error("Device not supported");
        }catch(com.jumio.mobile.sdk.ResourceNotFoundException ex){
            callbackContext.error("Resource not found");
        }

        if(this.netverifySDK == null) return;

        try{
            if(configuration.has("preselectedCountry")) this.netverifySDK.setPreselectedCountry(configuration.getString("preselectedCountry"));
            if(configuration.has("merchantScanReference")){ this.netverifySDK.setMerchantScanReference(configuration.getString("merchantScanReference")); }
            if(configuration.has("merchantReportingCriteria")){ this.netverifySDK.setMerchantReportingCriteria(configuration.getString("merchantReportingCriteria")); }
            if(configuration.has("customerId")){ this.netverifySDK.setCustomerId(configuration.getString("customerId")); }
            if(configuration.has("callbackUrl")){ this.netverifySDK.setCallbackUrl(configuration.getString("callbackUrl")); }
            if(configuration.has("requireVerification")){ this.netverifySDK.setRequireVerification(configuration.getBoolean("requireVerification")); }
            if(configuration.has("requireFaceMatch")){ this.netverifySDK.setRequireFaceMatch(configuration.getBoolean("requireFaceMatch")); }
            if(configuration.has("enableVisa")){ this.netverifySDK.setEnableVisa(configuration.getBoolean("enableVisa")); }
            if(configuration.has("showFlagOnInfoBar")){ this.netverifySDK.setShowFlagOnInfoBar(configuration.getBoolean("showFlagOnInfoBar")); }
            
            if(configuration.has("preselectedDocumentType")){ this.netverifySDK.setPreselectedDocumentType(NVDocumentType.fromString(configuration.getString("preselectedDocumentType"))); }
            if(configuration.has("preselectedDocumentVariant")){ setUpDocumentVariantFromStringCode(configuration.getString("preselectedDocumentVariant")); }
            if(configuration.has("cameraPosition")){ setUpCameraPositionFromStringCode(configuration.getString("cameraPosition")); }

            String firstName = configuration.getString("firstName");
            String lastName = configuration.getString("lastName");
            if( firstName != null && lastName != null){
                this.netverifySDK.setFirstAndLastName(firstName, lastName);
                this.netverifySDK.setName(firstName + " " + lastName);
            }
        }catch(org.json.JSONException exception){
            callbackContext.error("Invalid configuration");
        }

        Runnable runnable = new Runnable() {
            public void run() {
                netverifySDK.start();
            }
        };

        this.cordova.setActivityResultCallback(this);
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    private void setUpDocumentVariantFromStringCode(String documentVariantStr){
        if(documentVariantStr == "PAPER"){
            this.netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PAPER);
        }
        if(documentVariantStr == "PLASTIC"){
            this.netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);
        }
    }

    private void setUpCameraPositionFromStringCode(String cameraPositionStr){
        if(cameraPositionStr == "FRONT"){
            this.netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);
        }
        if(cameraPositionStr == "BACK"){
            this.netverifySDK.setCameraPosition(JumioCameraPosition.BACK);
        }
        
    }
}