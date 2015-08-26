#import <Cordova/CDV.h>
#import <Netverify/Netverify.h>

@interface JumioMobileCordovaPlugin : CDVPlugin <NetverifyViewControllerDelegate>

- (void)sdkVersion:(CDVInvokedUrlCommand *)command;

- (void)isSupportedPlatform:(CDVInvokedUrlCommand *)command;

- (void)presentNetverifyController:(CDVInvokedUrlCommand *)command;

- (void)configureNetverifyControllerAppearence:(CDVInvokedUrlCommand *)command;

@end
