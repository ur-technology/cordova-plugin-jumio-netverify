//
//  NetswipeScanOverlay.h
//  Netswipe
//
//  Created by Cosmin-Valentin Popescu on 10/08/16.
//  Copyright © 2016 Jumio Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NetswipeViewController.h"

@interface NetswipeScanOverlay : UIView<NetswipeAppearance>

@property (nonatomic, strong) UIColor *borderColor UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) UIColor *textColor UI_APPEARANCE_SELECTOR;

@end
