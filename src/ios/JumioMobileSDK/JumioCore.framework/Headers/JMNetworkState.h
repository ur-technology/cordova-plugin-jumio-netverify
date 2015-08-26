//
//  JMNetworkState.h
//  JumioCore
//
//  Created by Cosmin-Valentin Popescu on 18/06/15.
//  Copyright (c) 2015 Jumio Inc. All rights reserved.
//

#import "JMState.h"

@interface JMNetworkState : JMState <NSCopying>

@property (nonatomic, assign, readonly) BOOL isCanceled;

- (void)executeTask;

- (void)didFinishTaskSuccessfully;

- (void)didFailTask;

- (void)cancel;

@end
