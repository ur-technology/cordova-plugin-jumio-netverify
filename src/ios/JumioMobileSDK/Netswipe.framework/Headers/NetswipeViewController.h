//
//  NetswipeViewController.h
//  NetswipeMobileSDK
//
//  Created by Jumio Inc. on 17/07/2013.
//  Copyright (c) 2013 Jumio Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NetswipeCardInformation.h"
#import "NetswipeCustomScanOverlayViewController.h"
#import <JumioCore/JumioCore.h>

@class NetswipeViewController;

@protocol NetswipeViewControllerDelegate <NSObject>

- (void) netswipeViewController: (NetswipeViewController *) controller didCancelWithError:(NSError *) error;
- (void) netswipeViewController:(NetswipeViewController *) controller didFinishScanWithCardInformation: (NetswipeCardInformation *)cardInformation scanReference:(NSString *) scanReference;

@optional
- (void) netswipeViewController:(NetswipeViewController *) controller didStartScanAttemptWithScanReference:(NSString *) scanReference;

@end

@interface NetswipeViewController : UINavigationController

@property (nonatomic, weak) id<NetswipeViewControllerDelegate> netswipeDelegate;

@property (nonatomic, assign) NetswipeCreditCardTypes supportedCreditCardTypes;   // Specify which card types your app supports by combining NetswipeCreditCardType constants using the C bitwise OR operator
@property (nonatomic, assign) BOOL expiryRequired;                          // Enable scanning of expiry date (default: YES)
@property (nonatomic, assign) BOOL expiryEditable;                          // Set the expiry field editable (default: NO)
@property (nonatomic, assign) BOOL cvvRequired;                             // Require cvv input by the user (default: YES)
@property (nonatomic, assign) BOOL manualEntryEnabled;                      // User is allowed to manually enter card details (default: YES)
@property (nonatomic, assign) BOOL cardNumberMaskingEnabled;                // The card number is displayed masked on every view (default: YES)

@property (nonatomic, assign) BOOL cardHolderNameRequired;                  // Enable scanning of card holder name (default: NO)
@property (nonatomic, assign) BOOL cardHolderNameEditable;                  // User may edit the scanned card holder name (default: NO)
@property (nonatomic, assign) BOOL sortCodeAndAccountNumberRequired;        // Enable scanning of sort code and account number (default: NO)
@property (nonatomic, strong) NSString* name;                      // Optional name which the card holder name gets comared with in case property cardHolderNameRequired is set to YES. The value of name must be of format <firstname> <lastname>.

@property (nonatomic, strong) NSString *merchantReportingCriteria;          // Identify the scan in your reports. Set it to nil if you don't use it. (Maximum characters: 100)

@property (nonatomic, assign) BOOL vibrationEffectEnabled;                  // The device will vibrate shortly if a card is detected.
@property (nonatomic, strong) NSString* soundEffect;                        // Set the file name to a sound file to give a short audio feedback to the user when the card is detected.
@property (nonatomic, assign) BOOL enableFlashOnScanStart;                  // Automatically enable flash when scan is started
@property (nonatomic, assign) JumioCameraPosition cameraPosition;           // Set the default camera position

/** Create an instance of NetswipeViewController.
 @param apiToken The API token of your Jumio merchant account
 @param apiSecret The corresponding API secret
 @param reportingCriteria Identify the scan in your reports (Maximum characters: 100)
 @param netswipeDelegate The delegate implementing the NetswipeViewControllerDelegate protocol
 @param dataCenter The data center your merchant account is created at
 @param customOverlay The optional implementation of a custom overlay view controller, pass nil to use default behavior
 @return An initialized NetswipeViewController instance */
- (id) initWithMerchantApiToken:(NSString*)apiToken apiSecret:(NSString*)apiSecret merchantReportingCriteria:(NSString*)reportingCriteria delegate:(id<NetswipeViewControllerDelegate>)netswipeDelegate dataCenter: (JumioDataCenter) dataCenter customOverlay:(NetswipeCustomScanOverlayViewController *)overlayViewController;

/** Custom fields the user is asked to fill in to finish a scan. Retrievable via the fieldId in NetswipeCardInformation.
 @param fieldId Unique ID of your field
 @param title Label text describing the field
 @param hint A placeholder text for the field
 @param keyboardType Configure a keyboard type for input
 @param regex Validate field input with a regular expression */
- (void) addCustomField: (NSString*) fieldId title: (NSString*) title hint: (NSString*) hint keyboardType: (UIKeyboardType) keyboardType regularExpression: (NSString*) regex;

/** Custom fields the user is asked to fill in to finish a scan. Retrievable via the fieldId in NetswipeCardInformation.
 @param fieldId Unique ID of your field
 @param title Label text describing the field
 @param hint A placeholder text for the field
 @param values An array from which the user can select one value
 @param required If YES it cannot be nil. resetValueText will not available
 @param resetValueText value within the UIPickerView that resets the value to nil */
- (void) addCustomField: (NSString*) fieldId title: (NSString*) title hint: (NSString*) hint values: (NSArray*) values required: (BOOL) required resetValueText: (NSString *) resetValueText;

/** Returns the Netswipe Mobile SDK version. */
- (NSString*) sdkVersion;

@end
