//
//  NetverifySDK.h
//  NetverifySDK
//
//  Created by Jumio Inc. on 07/01/2013.
//  Copyright (c) 2013 Jumio Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Netverify/JMNavigationController.h>
#import <Netverify/NetverifyDocumentData.h>
#import <JumioCore/JMSDK.h>

@class NetverifyViewController;

@protocol NetverifyViewControllerDelegate <NSObject>
@optional
- (void) netverifyViewControllerDidFinishInitializing: (NetverifyViewController*) netverifyViewController;

@required
- (void) netverifyViewController: (NetverifyViewController*) netverifyViewController didFinishWithDocumentData:(NetverifyDocumentData *)documentData scanReference: (NSString*) scanReference;
- (void) netverifyViewController: (NetverifyViewController*) netverifyViewController didCancelWithError: (NSError*) error scanReference: (NSString*) scanReference;

@end

/**
 @class NetverifySDK
 @brief Handle configuration and presentation of the Netverify Mobile SDK.
 */
@interface NetverifyViewController : JMNavigationController

@property (nonatomic, weak) id<NetverifyViewControllerDelegate> netverifyViewControllerDelegate;

@property (nonatomic, strong) NSString* preselectedCountry;             // Specify a country to skip selection by the user (format: ISO 3166-1 Alpha 3 code)
@property (nonatomic, assign) NVDocumentType preselectedDocumentType;   // Specify a document type to skip selection by the user
@property (nonatomic, assign) NVDocumentVariant preselectedDocumentVariant; // Specify a document variant to skip selection by the user
@property (nonatomic, assign) BOOL requireFaceMatch;                    // Enable a face match check between a camera still image and the document front side (default: NO)
@property (nonatomic, assign) BOOL requireVerification;                 // Enable verification of a scanned identity (default: NO)

@property (nonatomic, strong) NSString* merchantScanReference;          // Identify the scan in the Jumio merchant UI. (Maximum characters: 100)
@property (nonatomic, strong) NSString* merchantReportingCriteria;      // Identify the scan in your reports. Set it to nil if you don't use it. (Maximum characters: 100)
@property (nonatomic, strong) NSString* customerId;                     // Specifies how the user is registered on your system. For example, you can use an email address, user name, or account number. Optional. (Maximum characters: 100)

- (void) setFirstName: (NSString *) firstName lastName: (NSString *) lastName;    // Both are required to provide customers first- and lastname. (Maximum characters for each: 100)

@property (nonatomic, strong) NSString* name;                           // Optional name which the extracted name gets comared with. The value of name must be of format <firstname> <lastname>.

@property (nonatomic, assign) BOOL showFlagOnInfoBar;                   // Control the visibility of the flag image on the infobar (default: YES)

@property (nonatomic, assign) JumioCameraPosition cameraPosition;       // Set the default camera position


@property (nonatomic, assign) BOOL enableVisa;                          // Enable data extraction from visa documents

@property (nonatomic, strong) NSString *callbackUrl;                    // Callback URL (max. 255 characters) for the confirmation after the verification is completed.
                                                                        // This setting overrides your Jumio merchant settings.

/** Create an instance of the Netverify SDK.
 @param apiToken The API token of your Jumio merchant account
 @param apiSecret The corresponding API secret
 @param delegate A delegate implementing the NetverifySDKDelegate protocol
 @param dataCenter The data center your merchant account is created at
 @return An initialized NetverifyViewController instance */
- (id) initWithMerchantApiToken: (NSString*) apiToken apiSecret: (NSString*) apiSecret delegate: (id<NetverifyViewControllerDelegate>) delegate dataCenter: (JumioDataCenter) dataCenter;

/** Return the Netverify SDK version. */
- (NSString*) sdkVersion;

@end
