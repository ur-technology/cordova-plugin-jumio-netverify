//
//  NVNavigationController.h
//  NetverifySDK
//
//  Created by Philipp Rakuschan on 30/01/2013.
//  Copyright (c) 2013 Jumio Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum : NSUInteger {
    JMNavigationControllerTransitionTypePushPop,
    JMNavigationControllerTransitionTypeCustom,
} JMNavigationControllerTransitionType;

@class AVCaptureVideoPreviewLayer;

@protocol JMAnimator <NSObject>

@required
- (JMNavigationControllerTransitionType)transitionType;
- (NSTimeInterval)offTransitionDuration;
- (NSTimeInterval) viewOffTransitionDuration;
- (void) performOffTransition:(BOOL)isPop toFullScreenBlur:(BOOL)toFullScreen;
- (NSTimeInterval)onTransitionDuration;
- (NSTimeInterval) viewOnTransitionDuration;
- (void) performOnTransition:(BOOL)isPop;
- (void) prepareForCustomTransition:(BOOL)isPop;
- (BOOL)startsWithFullScreenBlur;
@end


@interface JMNavigationController : UINavigationController{
    
}

@property (nonatomic, strong) AVCaptureVideoPreviewLayer* previewLayer;

@end
