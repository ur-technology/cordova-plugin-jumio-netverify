function JumioMobile() {}

JumioMobile.prototype.sdkVersion = function(jumioCredentials, completionCallback) {
  var failureCallback = function() {
    console.log("Could not retrieve Jumio library version");
  };

  cordova.exec(completionCallback, failureCallback, "JumioMobile", "sdkVersion", [jumioCredentials]);
};

JumioMobile.prototype.isSupportedPlatform = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, "JumioMobile", "isSupportedPlatform", []);
};

JumioMobile.prototype.presentNetverifyController = function(jumioCredentials, netverifyConfiguration, completionCallback, failureCallback) {
  cordova.exec(completionCallback, failureCallback, "JumioMobile", "presentNetverifyController", [jumioCredentials, netverifyConfiguration]);
}

JumioMobile.prototype.configureNetverifyControllerAppearence = function(appearenceConfig, completionCallback){
  var failureCallback = function() {
    console.log("Could not configure Netverify appearence");   
  };
  cordova.exec(completionCallback, failureCallback, "JumioMobile", "configureNetverifyControllerAppearence", [appearenceConfig]);
}

module.exports = new JumioMobile();