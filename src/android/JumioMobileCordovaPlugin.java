package com.payfriendz.cordova.jumio;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import com.jumio.mobile.sdk.*;
import com.jumio.mobile.sdk.enums.*;
import com.jumio.netswipe.sdk.*;
import com.jumio.netswipe.sdk.enums.*;
import com.jumio.netverify.barcode.*;
import com.jumio.netverify.barcode.enums.BarcodeFormat;
import com.jumio.netverify.sdk.*;
import com.jumio.netverify.sdk.custom.*;
import com.jumio.netverify.sdk.enums.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.Intent;
import android.app.Activity;

import java.text.SimpleDateFormat;

public class JumioMobileCordovaPlugin extends CordovaPlugin {
    private CallbackContext callbackContext;
    private NetverifySDK netverifySDK;
    private NetswipeSDK netswipeSDK;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {        

        //NETVERIFY
        if("isSupportedPlatformForNetverify".equals(action)) {
            if (NetverifySDK.isSupportedPlatform(cordova.getActivity())){
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
            this.presentNetverifyController(args.getJSONObject(0), args.getJSONObject(1), callbackContext);
            return true;
        }
        if("configureNetverifyControllerAppearence".equals(action)){
            //THIS IS A NO-OP ON JAVA, APPEARENCE IS NOT CONFIGURED THROUGH CODE
            return true;
        }

        //NETSWIPE
        if("netswipeSdkVersion".equals(action)) {
            String sdkVersion = NetswipeSDK.getSDKVersion();
            callbackContext.success(sdkVersion);
            return true;
        }
        if("isSupportedPlatformForNetswipe".equals(action)) {
            if (NetswipeSDK.isSupportedPlatform(cordova.getActivity())){
                callbackContext.success();
            }else{
                callbackContext.error("Device not supported");
            }
            return true;
        }
        if("isRootedDevice".equals(action)) {
            boolean isRooted = NetswipeSDK.isRooted();
            callbackContext.success(isRooted ? 1 : 0);
            return true;
        }

        if("presentNetswipeController".equals(action)) {
            this.callbackContext = callbackContext;
            this.presentNetswipeController(args.getJSONObject(0), args.getString(1), args.getJSONObject(2), callbackContext);
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

        if (requestCode == NetswipeSDK.REQUEST_CODE) {
            ArrayList<String> scanReferences = data.getStringArrayListExtra(NetswipeSDK.EXTRA_SCAN_ATTEMPTS);
            if (resultCode == Activity.RESULT_OK) {
                NetswipeCardInformation cardInformation = data.getParcelableExtra(NetswipeSDK.EXTRA_CARD_INFORMATION);
                CreditCardType cardType = cardInformation.getCardType();

                try{
                    result.put("cardType", cardType.name());
                    result.put("cardNumber", new String(cardInformation.getCardNumber()));
                    result.put("cardNumberGrouped", new String(cardInformation.getCardNumberGrouped()));
                    result.put("cardNumberMasked", new String(cardInformation.getCardNumberMasked()));
                    result.put("cardNumberManuallyEntered", cardInformation.isCardNumberManuallyEntered());
                    result.put("cardExpiryMonth", new String(cardInformation.getCardExpiryDateMonth()));
                    result.put("cardExpiryYear", new String(cardInformation.getCardExpiryDateYear()));
                    result.put("cardExpiryDate", new String(cardInformation.getCardExpiryDate()));
                    result.put("cardCVV", new String(cardInformation.getCardCvvCode()));
                    result.put("cardHolderName", new String(cardInformation.getCardHolderName()));

                    result.put("cardSortCode", new String(cardInformation.getCardSortCode()));
                    result.put("cardAccountNumber", new String(cardInformation.getCardAccountNumber()));
                    result.put("sortCodeValid", cardInformation.isCardSortCodeValid());
                    result.put("accountNumberValid", cardInformation.isCardAccountNumberValid());
                    result.put("nameMatch", cardInformation.isNameMatch());
                    result.put("nameDistance", cardInformation.getNameDistance());

                }catch(JSONException ex){
                    this.callbackContext.error("Error reading scan results");
                }

                this.callbackContext.success(result);
                cardInformation.clear();
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                try{
                    result.put("errorCode", data.getIntExtra(NetswipeSDK.EXTRA_ERROR_CODE, 0));
                    result.put("errorMessage", data.getStringExtra(NetswipeSDK.EXTRA_ERROR_MESSAGE));

                    this.callbackContext.error(result);
                }catch(JSONException ex){
                    this.callbackContext.error("Scan failed, but so did the error reading");
                }
            }
            netswipeSDK.destroy();
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
            if(configuration.has("cameraPosition")){ this.netverifySDK.setCameraPosition(getCameraPositionFromStr(configuration.getString("cameraPosition"))); }

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

    private void presentNetswipeController(JSONObject crendentials, String merchantReportingCriteria, JSONObject configuration, CallbackContext callbackContext) throws JSONException{
        try{
            this.netswipeSDK = NetswipeSDK.create(cordova.getActivity(), crendentials.getString("apiToken"), crendentials.getString("apiSecret"), merchantReportingCriteria, JumioDataCenter.EU);
        }catch(com.jumio.mobile.sdk.PlatformNotSupportedException exception){
            callbackContext.error("Device not supported");
        }catch(com.jumio.mobile.sdk.ResourceNotFoundException ex){
            callbackContext.error("Resource not found");
        }

        if(this.netswipeSDK == null) return;

        if(configuration.has("merchantReportingCriteria")){ this.netswipeSDK.setMerchantReportingCriteria(configuration.getString("merchantReportingCriteria")); }
        if(configuration.has("cardHolderNameRequired")){ this.netswipeSDK.setCardHolderNameRequired(configuration.getBoolean("cardHolderNameRequired")); }
        if(configuration.has("sortCodeAndAccountNumberRequired")){ this.netswipeSDK.setSortCodeAndAccountNumberRequired(configuration.getBoolean("sortCodeAndAccountNumberRequired")); }
        if(configuration.has("manualEntryEnabled")){ this.netswipeSDK.setManualEntryEnabled(configuration.getBoolean("manualEntryEnabled")); }
        if(configuration.has("expiryRequired")){ this.netswipeSDK.setExpiryRequired(configuration.getBoolean("expiryRequired")); }
        if(configuration.has("cvvRequired")){ this.netswipeSDK.setCvvRequired(configuration.getBoolean("cvvRequired")); }
        if(configuration.has("expiryEditable")){ this.netswipeSDK.setExpiryEditable(configuration.getBoolean("expiryEditable")); }
        if(configuration.has("cardHolderNameEditable")){ this.netswipeSDK.setCardHolderNameEditable(configuration.getBoolean("cardHolderNameEditable")); }

        if(configuration.has("vibrationEffectEnabled")){ this.netswipeSDK.setVibrationEffectEnabled(configuration.getBoolean("vibrationEffectEnabled")); }
        if(configuration.has("enableFlashOnScanStart")){ this.netswipeSDK.setEnableFlashOnScanStart(configuration.getBoolean("enableFlashOnScanStart")); }
        if(configuration.has("cardNumberMaskingEnabled")){ this.netswipeSDK.setCardNumberMaskingEnabled(configuration.getBoolean("cardNumberMaskingEnabled")); }

        if(configuration.has("cameraPosition")){ this.netswipeSDK.setCameraPosition(getCameraPositionFromStr(configuration.getString("cameraPosition"))); }

        if(configuration.has("supportedCreditCardTypes")){
            ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();

            JSONArray cardTypesStr =configuration.getJSONArray("supportedCreditCardTypes");
            for(int i = 0; i < cardTypesStr.length(); ++i) {
                String cardTypeStr = cardTypesStr.getString(i);
                creditCardTypes.add(getCreditCardTypeFromStr(cardTypeStr));                
            }
            netswipeSDK.setSupportedCreditCardTypes(creditCardTypes);
        }

        String firstName = configuration.getString("firstName");
        String lastName = configuration.getString("lastName");
        if( firstName != null && lastName != null){
            this.netswipeSDK.setName(firstName + " " + lastName);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                netswipeSDK.start();
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

    private JumioCameraPosition getCameraPositionFromStr(String cameraPositionStr){
        if(cameraPositionStr == "FRONT"){
            return JumioCameraPosition.FRONT;
        }
        return JumioCameraPosition.BACK;
    }

    private CreditCardType getCreditCardTypeFromStr(String creditCardTypeStr){
        if(creditCardTypeStr == "VISA"){ return CreditCardType.VISA; }
        if(creditCardTypeStr == "MASTER_CARD"){ return CreditCardType.MASTER_CARD; }
        if(creditCardTypeStr == "AMERICAN_EXPRESS"){ return CreditCardType.AMERICAN_EXPRESS; }
        if(creditCardTypeStr == "CHINA_UNIONPAY"){ return CreditCardType.CHINA_UNIONPAY; }
        if(creditCardTypeStr == "DISCOVER"){ return CreditCardType.DISCOVER; }
        if(creditCardTypeStr == "DINERS_CLUB"){ return CreditCardType.DINERS_CLUB; }
        if(creditCardTypeStr == "JCB"){ return CreditCardType.JCB; }
        if(creditCardTypeStr == "PRIVATE_LABEL"){ return CreditCardType.PRIVATE_LABEL; }
        return CreditCardType.UNKNOWN;
    }
}