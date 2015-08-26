//
//  JMDownloadController.h
//  JumioMobileSDK
//
//  Created by Cosmin-Valentin Popescu on 10/02/15.
//  Copyright (c) 2015 Jumio Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JMOperationsController : NSObject {
    @protected
    NSOperationQueue* _operationsQueue;
    NSMutableArray*   _operations;
}

@property (nonatomic, assign, readonly) BOOL isStarted;

- (void)enqueueOperation:(NSOperation* const) operation;
- (void)dequeueOperation:(NSOperation* const) operation;
- (void)addOperation:(NSOperation* const) operation;
- (void)cancelAllOperations;
- (void)startAllOperations;

- (void)operationDidFinish:(NSOperation* const)operation;
- (void)allOperationsDidFinish;
@end
