
#import "JumioMobileCordovaPlugin.h"

@interface JumioMobileCordovaPlugin ()

@property(nonatomic, strong, readwrite) CDVInvokedUrlCommand *command;

@end

@implementation JumioMobileCordovaPlugin

- (void)netverifySdkVersion:(CDVInvokedUrlCommand *)command {
    NSDictionary* credentials = [command.arguments objectAtIndex:0];

    NetverifyViewController* netverifyViewController = [self instantiateNetverifyViewController: credentials];
    NSString *sdkVersion = [netverifyViewController sdkVersion];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString: sdkVersion];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isSupportedPlatformForNetverify:(CDVInvokedUrlCommand *)command {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsBool: YES];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId]; 
}

- (void)presentNetverifyController:(CDVInvokedUrlCommand *)command {
  NSDictionary* credentials = [command.arguments objectAtIndex:0];
  NetverifyViewController* netverifyViewController = [self instantiateNetverifyViewController: credentials];

  NSDictionary* netverifyConfiguration = [command.arguments objectAtIndex:1];
  [self setUpProperty: @"preselectedCountry" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"merchantScanReference" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"merchantReportingCriteria" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"customerId" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"callbackUrl" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"requireVerification" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"requireFaceMatch" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"showFlagOnInfoBar" from: netverifyConfiguration on: netverifyViewController];
  [self setUpProperty: @"enableVisa" from: netverifyConfiguration on: netverifyViewController];

  [self setUpEnumProperty: @"preselectedDocumentType" withPossibleValues: [self allNVDocumentTypeToStringCode] from: netverifyConfiguration on: netverifyViewController];
  [self setUpEnumProperty: @"preselectedDocumentVariant" withPossibleValues: [self allNVDocumentVariantToStringCode] from: netverifyConfiguration on: netverifyViewController];
  [self setUpEnumProperty: @"cameraPosition" withPossibleValues: [self allJumioCameraPositionToStringCode] from: netverifyConfiguration on: netverifyViewController];

  if( [netverifyConfiguration objectForKey: @"firstName"] != [NSNull null] && [netverifyConfiguration objectForKey: @"lastName"] != [NSNull null]){
    NSString* firstName = [netverifyConfiguration objectForKey: @"firstName"];
    NSString* lastName = [netverifyConfiguration objectForKey: @"lastName"];
    [netverifyViewController setFirstName: firstName lastName: lastName];
    netverifyViewController.name = [[firstName stringByAppendingString: @" "] stringByAppendingString: lastName];
  }

  self.command = command;
  [self.viewController presentViewController: netverifyViewController animated: YES completion: nil];
}

- (void)configureNetverifyControllerAppearence:(CDVInvokedUrlCommand *)command{
  NSDictionary* appearenceConfig = [command.arguments objectAtIndex:0];

  if([appearenceConfig objectForKey: @"submitButtonBackgroundColorNormal"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setBackgroundColor: [self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonBackgroundColorNormal"]] forState:UIControlStateNormal];
  if([appearenceConfig objectForKey: @"submitButtonBackgroundColorHighlighted"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setBackgroundColor: [self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonBackgroundColorHighlighted"]] forState:UIControlStateHighlighted];
  if([appearenceConfig objectForKey: @"submitButtonBackgroundColorDisabled"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setBackgroundColor: [self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonBackgroundColorDisabled"]] forState:UIControlStateDisabled];
    
  if([appearenceConfig objectForKey: @"submitButtonTitleColorNormal"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonTitleColorNormal"]] forState:UIControlStateNormal];
  if([appearenceConfig objectForKey: @"submitButtonTitleColorHighlighted"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonTitleColorHighlighted"]] forState:UIControlStateHighlighted];
  if([appearenceConfig objectForKey: @"submitButtonTitleColorDisabled"])
    [[NetverifySubmitButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"submitButtonTitleColorDisabled"]] forState:UIControlStateDisabled];
    
  if([appearenceConfig objectForKey: @"navigationBarTintColor"])  
    [[UINavigationBar appearanceWhenContainedIn:[NetverifyViewController class], nil] setTintColor:[self colorFromHexString: [appearenceConfig objectForKey: @"navigationBarTintColor"]]];
  if([appearenceConfig objectForKey: @"navigationBarTitleTextForegroundColor"])  
    [[UINavigationBar appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleTextAttributes: @{NSForegroundColorAttributeName:[self colorFromHexString: [appearenceConfig objectForKey: @"navigationBarTitleTextForegroundColor"]]}];
  if([appearenceConfig objectForKey: @"navigationBarBarTintColor"])  
    [[UINavigationBar appearanceWhenContainedIn:[NetverifyViewController class], nil] setBarTintColor:[self colorFromHexString: [appearenceConfig objectForKey: @"navigationBarBarTintColor"]]];
    
  if([appearenceConfig objectForKey: @"infoBarLabelTitleColor"])  
    [[NetverifyInfoBarLabel appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"infoBarLabelTitleColor"]]];
    
  if([appearenceConfig objectForKey: @"submissionTextViewTextTintColor"])  
    [[NetverifySubmissionTextView appearanceWhenContainedIn:[NetverifyViewController class], nil] setTextTintColor:[self colorFromHexString: [appearenceConfig objectForKey: @"submissionTextViewTextTintColor"]]];
    
  if([appearenceConfig objectForKey: @"helpButtonTitleColorNormal"])
    [[NetverifyHelpButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"helpButtonTitleColorNormal"]] forState:UIControlStateNormal];
  if([appearenceConfig objectForKey: @"helpButtonTitleColorHighlighted"])
    [[NetverifyHelpButton appearanceWhenContainedIn:[NetverifyViewController class], nil] setTitleColor:[self colorFromHexString: [appearenceConfig objectForKey: @"helpButtonTitleColorHighlighted"]] forState:UIControlStateHighlighted];

  CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#pragma mark - NetverifyViewControllerDelegate
- (void) netverifyViewControllerDidFinishInitializing: (NetverifyViewController *) netverifyViewController {
}

- (void) netverifyViewController: (NetverifyViewController *) netverifyViewController didFinishWithDocumentData: (NetverifyDocumentData *)documentData scanReference: (NSString *) scanReference {
  NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
  if(scanReference)[dictionary setObject: scanReference forKey: @"scanReference"];

  NSMutableDictionary *documentDataDictionary = [[NSMutableDictionary alloc] init];
  [dictionary setObject: documentDataDictionary forKey: @"documentData"];

  if(documentData.selectedDocumentType)[documentDataDictionary setObject: [[self allNVDocumentTypeToStringCode] objectForKey: [NSNumber numberWithInt: documentData.selectedDocumentType]] forKey: @"selectedDocumentType"]; 
  if(documentData.selectedCountry)[documentDataDictionary setObject: documentData.selectedCountry forKey: @"selectedCountry"];
  if(documentData.idNumber)[documentDataDictionary setObject: documentData.idNumber forKey: @"idNumber"];
  if(documentData.personalNumber)[documentDataDictionary setObject: documentData.personalNumber forKey: @"personalNumber"];
  if(documentData.gender)[documentDataDictionary setObject: [[self allNVGenderToStringCode] objectForKey: [NSNumber numberWithInt: documentData.gender]] forKey: @"gender"];
  if(documentData.issuingCountry)[documentDataDictionary setObject: documentData.issuingCountry forKey: @"issuingCountry"];
  if(documentData.lastName)[documentDataDictionary setObject: documentData.lastName forKey: @"lastName"];
  if(documentData.firstName)[documentDataDictionary setObject: documentData.firstName forKey: @"firstName"];
  if(documentData.middleName)[documentDataDictionary setObject: documentData.middleName forKey: @"middleName"];
  if(documentData.originatingCountry)[documentDataDictionary setObject: documentData.originatingCountry forKey: @"originatingCountry"];
  if(documentData.street)[documentDataDictionary setObject: documentData.street forKey: @"street"];
  if(documentData.city)[documentDataDictionary setObject: documentData.city forKey: @"city"];
  if(documentData.state)[documentDataDictionary setObject: documentData.state forKey: @"state"];
  if(documentData.postalCode)[documentDataDictionary setObject: documentData.postalCode forKey: @"postalCode"];
  if(documentData.optionalData1)[documentDataDictionary setObject: documentData.optionalData1 forKey: @"optionalData1"];  
  if(documentData.optionalData2)[documentDataDictionary setObject: documentData.optionalData2 forKey: @"optionalData2"];
  [documentDataDictionary setObject: [NSNumber numberWithBool: documentData.nameMatch] forKey: @"nameMatch"];
  if(documentData.nameDistance)[documentDataDictionary setObject: [NSNumber numberWithInteger: documentData.nameDistance] forKey: @"nameDistance"];
  [documentDataDictionary setObject: [NSNumber numberWithBool: documentData.livenessDetected] forKey: @"livenessDetected"];
  if(documentData.issuingDate)[documentDataDictionary setObject: [self convertNSDateToNSString: documentData.issuingDate] forKey: @"issuingDate"];
  if(documentData.expiryDate)[documentDataDictionary setObject: [self convertNSDateToNSString: documentData.expiryDate] forKey: @"expiryDate"];
  if(documentData.dob) [documentDataDictionary setObject: [self convertNSDateToNSString: documentData.dob] forKey: @"dob"];

  [netverifyViewController dismissViewControllerAnimated:YES completion:^{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary: dictionary];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
  }];
}

- (void) netverifyViewController: (NetverifyViewController *)netverifyViewController didCancelWithError: (NSError *) error scanReference:(NSString *) scanReference {
  NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
  if(scanReference) [dictionary setObject: scanReference forKey: @"scanReference"];
  if(error.code) [dictionary setObject: [NSNumber numberWithInteger: error.code] forKey: @"errorCode"];
  if(error.localizedDescription)[dictionary setObject: error.localizedDescription forKey: @"errorMessage"];

  [netverifyViewController dismissViewControllerAnimated:YES completion:^{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                      messageAsDictionary: dictionary];

    [self.commandDelegate sendPluginResult: pluginResult callbackId:self.command.callbackId];
  }];
}

#pragma mark - Netswipe SDK related methods

- (void)netswipeSdkVersion:(CDVInvokedUrlCommand *)command {
    NSDictionary* credentials = [command.arguments objectAtIndex:0];

    NetswipeViewController* netswipeViewController =  [self instantiateNetswipeViewController: credentials merchantReportingCriteria: nil];
    NSString *sdkVersion = [netswipeViewController sdkVersion];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString: sdkVersion];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)isSupportedPlatformForNetswipe:(CDVInvokedUrlCommand *)command {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsBool: YES];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId]; 
}

- (void) isRootedDevice: (CDVInvokedUrlCommand *)command {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsBool: false];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId]; 
}

- (void)presentNetswipeController:(CDVInvokedUrlCommand *)command{
  NSDictionary* credentials = [command.arguments objectAtIndex:0];
  NSString* merchantReportingCriteria = [command.arguments objectAtIndex:1];
  NetswipeViewController* netswipeViewController = [self instantiateNetswipeViewController: credentials merchantReportingCriteria: merchantReportingCriteria];

  NSDictionary* netswipeConfiguration = [command.arguments objectAtIndex:2];
  [self setUpProperty: @"merchantReportingCriteria" from: netswipeConfiguration on: netswipeConfiguration];
  [self setUpProperty: @"cardHolderNameRequired" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"sortCodeAndAccountNumberRequired" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"manualEntryEnabled" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"expiryRequired" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"cvvRequired" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"expiryEditable" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"cardHolderNameEditable" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"soundEffect" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"vibrationEffectEnabled" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"enableFlashOnScanStart" from: netswipeConfiguration on: netswipeViewController];
  [self setUpProperty: @"cardNumberMaskingEnabled" from: netswipeConfiguration on: netswipeViewController];

  [self setUpEnumProperty: @"cameraPosition" withPossibleValues: [self allJumioCameraPositionToStringCode] from: netswipeConfiguration on: netswipeViewController];  

  if([netswipeConfiguration objectForKey: @"supportedCreditCardTypes"]){
    NetswipeCreditCardTypes cardTypes;
    NSArray* cardTypesStr = [netswipeConfiguration objectForKey: @"supportedCreditCardTypes"];
    for (NSString* cardTypeStr in cardTypesStr) {
      NSArray *temp = [[self allNetswipeCreditCardTypeToStringCode] allKeysForObject: cardTypeStr];
      cardTypes = cardTypes | [[temp objectAtIndex:0] intValue];
    }    
    netswipeViewController.supportedCreditCardTypes = cardTypes;
  }

  if( [netswipeConfiguration objectForKey: @"firstName"] != [NSNull null] && [netswipeConfiguration objectForKey: @"lastName"] != [NSNull null]){
    NSString* firstName = [netswipeConfiguration objectForKey: @"firstName"];
    NSString* lastName = [netswipeConfiguration objectForKey: @"lastName"];
    netswipeViewController.name = [[firstName stringByAppendingString: @" "] stringByAppendingString: lastName];
  }

  self.command = command;
  [self.viewController presentViewController: netswipeViewController animated: YES completion: nil];
}

#pragma mark - NetswipeViewControllerDelegate
- (void) netswipeViewController: (NetswipeViewController *) controller didStartScanAttemptWithScanReference: (NSString *) scanReference {

}

- (void) netswipeViewController: (NetswipeViewController *) controller didFinishScanWithCardInformation: (NetswipeCardInformation *) cardInformation scanReference: (NSString *) scanReference {
  NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];

  if(cardInformation.cardType)[dictionary setObject: [[self allNetswipeCreditCardTypeToStringCode] objectForKey: [NSNumber numberWithInt: cardInformation.cardType]] forKey: @"cardType"]; 
  if(cardInformation.cardNumber)[dictionary setObject: cardInformation.cardNumber forKey: @"cardNumber"]; 
  if(cardInformation.cardNumberGrouped)[dictionary setObject: cardInformation.cardNumberGrouped forKey: @"cardNumberGrouped"];
  if(cardInformation.cardNumberMasked)[dictionary setObject: cardInformation.cardNumberMasked forKey: @"cardNumberMasked"];
  if(cardInformation.cardNumberManuallyEntered)[dictionary setObject: [NSNumber numberWithBool: cardInformation.cardNumberManuallyEntered] forKey: @"cardNumberManuallyEntered"];
  if(cardInformation.cardExpiryMonth)[dictionary setObject: cardInformation.cardExpiryMonth forKey: @"cardExpiryMonth"];
  if(cardInformation.cardExpiryYear)[dictionary setObject: cardInformation.cardExpiryYear forKey: @"cardExpiryYear"];
  if(cardInformation.cardExpiryDate)[dictionary setObject: cardInformation.cardExpiryDate forKey: @"cardExpiryDate"];
  if(cardInformation.cardCVV)[dictionary setObject: cardInformation.cardCVV forKey: @"cardCVV"];
  if(cardInformation.cardHolderName)[dictionary setObject: cardInformation.cardHolderName forKey: @"cardHolderName"];
  if(cardInformation.cardSortCode)[dictionary setObject: cardInformation.cardSortCode forKey: @"cardSortCode"];
  if(cardInformation.cardAccountNumber)[dictionary setObject: cardInformation.cardAccountNumber forKey: @"cardAccountNumber"];
  [dictionary setObject: [NSNumber numberWithBool: cardInformation.cardSortCodeValid] forKey: @"sortCodeValid"];
  [dictionary setObject: [NSNumber numberWithBool: cardInformation.cardAccountNumberValid] forKey: @"accountNumberValid"];
  [dictionary setObject: [NSNumber numberWithBool: cardInformation.nameMatch] forKey: @"nameMatch"];
  if(cardInformation.nameDistance)[dictionary setObject: [NSNumber numberWithInteger: cardInformation.nameDistance] forKey: @"nameDistance"];

  [controller dismissViewControllerAnimated:YES completion:^{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary: dictionary];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];

    [cardInformation clear];
  }];
}

- (void) netswipeViewController: (NetswipeViewController *) controller didCancelWithError: (NSError *) error {
  NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
  if(error.code) [dictionary setObject: [NSNumber numberWithInteger: error.code] forKey: @"errorCode"];
  if(error.localizedDescription)[dictionary setObject: error.localizedDescription forKey: @"errorMessage"];

  [controller dismissViewControllerAnimated:YES completion:^{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                      messageAsDictionary: dictionary];

    [self.commandDelegate sendPluginResult: pluginResult callbackId: self.command.callbackId];
  }];
}

#pragma mark - Internal Jumio helpers

- (void)setUpProperty: (NSString* ) propertyName from: (NSDictionary*) config on: (NSObject*) controller {
  NSObject* propertyValue = [config objectForKey: propertyName];
  if(propertyValue != [NSNull null]){
    [controller setValue: propertyValue forKey: propertyName];
  }
}

- (void)setUpEnumProperty: (NSString* ) propertyName withPossibleValues: (NSDictionary*) enumToStringCodeDictionary from: (NSDictionary*) config on: (NSObject*) controller {
  NSObject* propertyValue = [config objectForKey: propertyName];
  if(propertyValue != [NSNull null]){
    NSLog(@"I'm setting %@ with %@", propertyName, (NSString*)propertyValue);
    [controller setValue: [[enumToStringCodeDictionary allKeysForObject: propertyValue] lastObject] forKey: propertyName];
  }
}

- (NSDictionary*) allNVDocumentTypeToStringCode {
  return [[NSDictionary alloc] initWithObjectsAndKeys: 
    @"UNKNOWN", [NSNumber numberWithInt: NVDocumentTypeUnknown],
    @"DRIVER_LICENSE", [NSNumber numberWithInt: NVDocumentTypeDriverLicense],
    @"ID_CARD", [NSNumber numberWithInt: NVDocumentTypeIdentityCard],
    @"PASSPORT", [NSNumber numberWithInt: NVDocumentTypePassport],
    @"VISA", [NSNumber numberWithInt: NVDocumentTypeVisa],
    nil];
}

- (NSDictionary*) allNVDocumentVariantToStringCode {
  return [[NSDictionary alloc] initWithObjectsAndKeys: 
    @"UNKNOWN", [NSNumber numberWithInt: NVDocumentVariantUnknown],
    @"PAPER", [NSNumber numberWithInt: NVDocumentVariantPaper],
    @"PLASTIC", [NSNumber numberWithInt: NVDocumentVariantPlastic],
    nil];
}

- (NSDictionary*) allNVGenderToStringCode {
  return [[NSDictionary alloc] initWithObjectsAndKeys: 
    @"UNKNOWN", [NSNumber numberWithInt: NVGenderUnknown],
    @"M", [NSNumber numberWithInt: NVGenderM],
    @"F", [NSNumber numberWithInt: NVGenderF],
    nil];
}

- (NSDictionary*) allJumioCameraPositionToStringCode {
  return [[NSDictionary alloc] initWithObjectsAndKeys: 
    @"BACK", [NSNumber numberWithInt: JumioCameraPositionBack],
    @"FRONT", [NSNumber numberWithInt: JumioCameraPositionFront],
    nil];
}

- (NSDictionary*) allNetswipeCreditCardTypeToStringCode {
  return [[NSDictionary alloc] initWithObjectsAndKeys: 
    @"VISA", [NSNumber numberWithInt: NetswipeCreditCardTypeVisa],
    @"MASTER_CARD", [NSNumber numberWithInt: NetswipeCreditCardTypeMasterCard],
    @"AMERICAN_EXPRESS", [NSNumber numberWithInt: NetswipeCreditCardTypeAmericanExpress],
    @"DINERS_CLUB", [NSNumber numberWithInt: NetswipeCreditCardTypeDiners],
    @"DISCOVER", [NSNumber numberWithInt: NetswipeCreditCardTypeDiscover],
    @"CHINA_UNIONPAY", [NSNumber numberWithInt: NetswipeCreditCardTypeChinaUnionPay],
    @"JCB", [NSNumber numberWithInt: NetswipeCreditCardTypeJCB],
    @"PRIVATE_LABEL", [NSNumber numberWithInt: NetswipeCreditCardTypePrivateLabel],
    nil];
}

- (NSString*) convertNSDateToNSString: (NSDate*) date{
  NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
  dateFormatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ssZ";

  NSString *formattedDateString = [dateFormatter stringFromDate: date];
  return formattedDateString;
}

- (NetverifyViewController*) instantiateNetverifyViewController: (NSDictionary*) credentials {
  return [[NetverifyViewController alloc] initWithMerchantApiToken: credentials[@"apiToken"] apiSecret: credentials[@"apiSecret"] delegate: self dataCenter: JumioDataCenterEU];
}

- (NetswipeViewController*) instantiateNetswipeViewController: (NSDictionary*) credentials merchantReportingCriteria: (NSString*) merchantReportingCriteria {
  return [[NetswipeViewController alloc] initWithMerchantApiToken: credentials[@"apiToken"] apiSecret: credentials[@"apiSecret"] merchantReportingCriteria: nil delegate: self dataCenter: JumioDataCenterEU customOverlay: nil];
}

// Assumes input like "#00FF00" (#RRGGBB).
- (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1]; // bypass '#' character
    [scanner scanHexInt:&rgbValue];
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}
@end