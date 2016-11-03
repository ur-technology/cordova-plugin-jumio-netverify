//
//  JMExtendedScanDebugging.h
//  JumioMobileSDK
//
//  Created by Christian Neudorfer on 24.07.14.
//  Copyright (c) 2014 Jumio Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
//#import "NVScanViewController.h"

@interface JMExtendedScanDebugging : NSObject
{
    NSString*               _requestTimingLogFilePath;
    BOOL                    _isScanStarted;
    NSMutableDictionary*    _imagesBufferDict;
    NSMutableDictionary*    _rawImagesBufferDict;
    NSString*               _scanLogFilePath;
    NSString*               _scanLogDirPath;
}

@property (nonatomic, assign) BOOL netswipeExtendedScanDebugging;
@property (nonatomic, assign) BOOL netverifyExtendedScanDebugging;
@property (nonatomic, assign) BOOL netverifyLiveScanDebugging;

@property (nonatomic, assign, readonly) BOOL                    isScanStarted;
@property (nonatomic, strong)           NSString*               scanReference;
@property (nonatomic, strong, readonly) NSMutableDictionary*    imagesBufferDict;
@property (nonatomic, strong, readonly) NSMutableDictionary*    rawImagesBufferDict;
@property (nonatomic, strong, readonly) NSString*               scanLogFilePath;
@property (nonatomic, strong, readonly) NSString*               scanLogDirPath;
@property (nonatomic, strong, readonly) NSString*               requestTimingLogFilePath;
@property (nonatomic, strong) NSDictionary* netswipeEndpoint;
@property (nonatomic, strong) NSDictionary* netverifyEndpoint;


+ (JMExtendedScanDebugging*) sharedInstance;

@end
