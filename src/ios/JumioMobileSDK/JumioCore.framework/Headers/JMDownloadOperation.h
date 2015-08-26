//
//  JMDownloadOperation.h
//  TemplateDownloadTest
//
//  Created by Cosmin-Valentin Popescu on 11/02/15.
//  Copyright (c) 2015 Jumio Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JMDownloadOperation : NSOperation {
    @protected
    BOOL _executing;
    BOOL _finished;
    NSInteger _httpStatusCode;
}
@property (nonatomic, strong, readonly) NSError*    		error;
@property (nonatomic, assign, readonly) NSInteger   		httpStatusCode;
@property (nonatomic, strong, readonly) NSData*         	data;
@property (nonatomic, assign, readonly) NSTimeInterval  	timeoutInterval;
@property (nonatomic, assign, readonly) long                executionTime;
@property (nonatomic, strong, readonly) NSURLConnection*    connection;
@property (nonatomic, strong, readonly) NSURL*              url;

- (instancetype)initWithURL:(NSURL* const)url timeoutInterval:(NSTimeInterval) timeoutInterval;


- (void)connectionDidStart;
- (void)connectionDidFailWithError:(NSError* const)error;
- (void)connectionDidReceiveResponse:(NSURLResponse* const)response;
- (void)connectionDidReceiveData:(NSData* const)data;
- (void)connectionDidFinishLoading;
- (void)connectionWillSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge* const)challenge;
@end
