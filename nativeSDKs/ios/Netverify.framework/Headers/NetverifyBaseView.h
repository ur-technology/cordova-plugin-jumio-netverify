//
//  NetverifyBaseView.h
//  Netverify
//
//  Created by Lukas Danzer on 20/04/16.
//  Copyright © 2016 Jumio Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NetverifyViewController.h"

@interface NetverifyBaseView : UIView <NetverifyAppearance>

@property (nonatomic, strong) NSNumber *disableBlur UI_APPEARANCE_SELECTOR;

@property (nonatomic, strong) UIColor *foregroundColor UI_APPEARANCE_SELECTOR;

@property (nonatomic, strong) NSString *customLightFontName UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) NSString *customRegularFontName UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) NSString *customMediumFontName UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) NSString *customBoldFontName UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) NSString *customItalicFontName UI_APPEARANCE_SELECTOR;

@end
