//
//  ResourceLoader.h
//  JumioMobileSDK
//
//  Created by Cosmin-Valentin Popescu on 12/05/14.
//  Copyright (c) 2014 Jumio Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <JumioCore/JMSDK.h>

#define SharedResourceLoader [JMResourceLoader sharedInstance]

@interface JMResourceLoader : NSObject

+ (JMResourceLoader *)sharedInstance;

- (UIImage*)getImageWithName:(NSString*)imageName bundle:(NSBundle*)bundleName;
- (NSData *)getFontWithName:(NSString *)name bundle:(NSBundle*)bundle;
- (NSString *)getOCRRootPathForBundle:(NSBundle*)bundle;
- (NSDictionary *) getPlistFromBundle:(NSBundle*)bundle;

+ (NSString*) suffixForImagesDependingOnDeviceScreenScale;

+ (UIImage*) imageResourceWithName: (NSString*) imageName;

@end
