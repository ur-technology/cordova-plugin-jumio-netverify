//
//  NetverifyScanOptionButton.h
//  Netverify
//
//  Created by Lukas Danzer on 26/04/16.
//  Copyright © 2016 Jumio Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "NetverifyViewController.h"

@interface NetverifyScanOptionButton : UIButton <NetverifyAppearance>

- (void)setBackgroundColor:(UIColor *)backgroundColor forState:(UIControlState)state UI_APPEARANCE_SELECTOR;
- (UIColor *)backgroundColorForState:(UIControlState)state UI_APPEARANCE_SELECTOR;


@end
