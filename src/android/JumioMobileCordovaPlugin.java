//package android.JumioMobileCordovaPlugin.java;
package com.cordova.plugin.android.jumiomobilecordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.*;

import com.jumio.MobileSDK;
import com.jumio.core.data.Strings;
import com.jumio.core.enums.*;
import com.jumio.core.exceptions.*;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.jumio.nv.*;

import java.util.ArrayList;

import android.content.Intent;
import android.app.Activity;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;

public class JumioMobileCordovaPlugin extends CordovaPlugin {
    
    private static final int PERMISSION_REQUEST_CODE_NETVERIFY = 301;
    private CallbackContext callbackContext;
    private NetverifySDK netverifySDK;
    private JSONArray arg;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        //NETVERIFY
        if("isSupportedPlatformForNetverify".equals(action)) {
            if (NetverifySDK.isSupportedPlatform()){
                callbackContext.success();
            }else{
                callbackContext.error("Device not supported");
            }
            return true;
        }
        if("netverifySdkVersion".equals(action)) {
            String sdkVersion = NetverifySDK.getSDKVersion();
            callbackContext.success(sdkVersion);
            return true;
        }
        if("presentNetverifyController".equals(action)) {
            this.callbackContext = callbackContext;
            //check permissions first
            this.arg = args;
            checkPermissionsAndStart();
            return true;
        }
        if("configureNetverifyControllerAppearence".equals(action)){
            //THIS IS A NO-OP ON ANDROID, APPEARENCE IS NOT CONFIGURED THROUGH CODE
            return true;
        }
        
        //NETSWIPE
        if("netswipeSdkVersion".equals(action)) {
            String sdkVersion = netverifySDK.getSDKVersion();
            callbackContext.success(sdkVersion);
            return true;
        }
        
        if("configureNetswipeControllerAppearence".equals(action)){
            //THIS IS A NO-OP ON ANDROID, APPEARENCE IS NOT CONFIGURED THROUGH CODE
            return true;
        }
        
        return false;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject result = new JSONObject();
        
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null)
                return;
            if (resultCode == Activity.RESULT_OK){
                //if (resultCode == NetverifySDK.RESULT_CODE_SUCCESS || resultCode == NetverifySDK.RESULT_CODE_BACK_WITH_SUCCESS) {
                try{
                    result.put("scanReference", data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE));
                    
                    NetverifyDocumentData documentData = (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                    JSONObject documentDataResult = new JSONObject();
                    
                    documentDataResult.put("selectedCountry", documentData.getSelectedCountry());
                    documentDataResult.put("idNumber", documentData.getIdNumber());
                    documentDataResult.put("personalNumber", documentData.getPersonalNumber());
                    documentDataResult.put("issuingCountry", documentData.getIssuingCountry());
                    documentDataResult.put("lastName", documentData.getLastName());
                    documentDataResult.put("firstName", documentData.getFirstName());
                    documentDataResult.put("middleName", documentData.getMiddleName());
                    documentDataResult.put("originatingCountry", documentData.getOriginatingCountry());
                    //documentDataResult.put("street", documentData.getStreet());
                    documentDataResult.put("city", documentData.getCity());
                    //documentDataResult.put("state", documentData.getState());
                    documentDataResult.put("postalCode", documentData.getPostCode());
                    documentDataResult.put("optionalData1", documentData.getOptionalData1());
                    documentDataResult.put("optionalData2", documentData.getOptionalData2());
                    documentDataResult.put("nameMatch", documentData.isNameMatch());
                    documentDataResult.put("nameDistance", documentData.getNameDistance());
                    
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                try{
                    result.put("scanReference", data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE));
                    result.put("errorCode", data.getIntExtra(NetverifySDK.EXTRA_ERROR_CODE, 0));
                    result.put("errorMessage", data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE));
                    
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
        }catch(com.jumio.core.exceptions.PlatformNotSupportedException exception){
            callbackContext.error("Device not supported");
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
            //if(configuration.has("enableVisa")){ this.netverifySDK.setEnableVisa(configuration.getBoolean("enableVisa")); }
            //if(configuration.has("showFlagOnInfoBar")){ this.netverifySDK.setShowFlagOnInfoBar(configuration.getBoolean("showFlagOnInfoBar")); }
            
            ArrayList<NVDocumentType> documentTypes = new ArrayList<NVDocumentType>();
            documentTypes.add(NVDocumentType.PASSPORT);
            documentTypes.add(NVDocumentType.DRIVER_LICENSE);
            documentTypes.add(NVDocumentType.IDENTITY_CARD);
            if(configuration.has("preselectedDocumentType")){ this.netverifySDK.setPreselectedDocumentTypes(documentTypes); }
            if(configuration.has("preselectedDocumentVariant")){ setUpDocumentVariantFromStringCode(configuration.getString("preselectedDocumentVariant")); }
            if(configuration.has("cameraPosition")){ this.netverifySDK.setCameraPosition(getCameraPositionFromStr(configuration.getString("cameraPosition"))); }
            
            String firstName = configuration.getString("firstName");
            String lastName = configuration.getString("lastName");
            if( firstName != null && lastName != null){
                //this.netverifySDK.setFirstAndLastName(firstName, lastName);
                this.netverifySDK.setName(firstName + " " + lastName);
            }
        }catch(org.json.JSONException exception){
            callbackContext.error("Invalid configuration");
        }
        
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    netverifySDK.start();
                } catch (MissingPermissionException e){
                    Toast.makeText(cordova.getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
                
            }
        };
        
        this.cordova.setActivityResultCallback(this);
        this.cordova.getActivity().runOnUiThread(runnable);
    }
    
    private void checkPermissionsAndStart() throws  JSONException
    {
        // MobileSDK.hasPermissionsFor(cordova.getActivity(),)
        if(!MobileSDK.hasAllRequiredPermissions(cordova.getActivity()))
        {
            String [] mp = MobileSDK.getMissingPermissions(cordova.getActivity());
            ActivityCompat.requestPermissions(cordova.getActivity(),mp,PERMISSION_REQUEST_CODE_NETVERIFY);
        }else
        {
            //start SDK
            startSDK();
        }
    }
    
    public void startSDK()throws JSONException
    {
        this.presentNetverifyController(this.arg.getJSONObject(0), this.arg.getJSONObject(1), callbackContext);
    }
    
    
    
    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException{
        boolean allGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        
        if (allGranted) {
            if (requestCode == PERMISSION_REQUEST_CODE_NETVERIFY) {
                // startSdk(netverifySDK);
                startSDK();
            }
        } else {
            Toast.makeText(cordova.getActivity(), "You need to grant all required permissions to start the Jumio SDK", Toast.LENGTH_LONG).show();
            super.onRequestPermissionResult(requestCode, permissions, grantResults);
        }
    }
    
    
    private void setUpDocumentVariantFromStringCode(String documentVariantStr){
        if(documentVariantStr == "PAPER"){
            this.netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PAPER);
        }
        if(documentVariantStr == "PLASTIC"){
            this.netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);
        }
    }
    
    private JumioCameraPosition getCameraPositionFromStr(String cameraPositionStr){
        if(cameraPositionStr == "FRONT"){
            return JumioCameraPosition.FRONT;
        }
        return JumioCameraPosition.BACK;
    }
}
