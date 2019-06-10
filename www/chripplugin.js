function ChripPlugin() {
}
//Send Data Plugin
ChripPlugin.prototype.sendData = function (dataToSend, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, 'ChripPlugin', 'sendData',[dataToSend]);
};
 
  //Register as Listners
  ChripPlugin.prototype.registerAsReceiver= function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ChripPlugin', 'registerAsReceiver');
  }

// Stop Chirp
ChripPlugin.prototype.stop= function(successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, 'ChripPlugin', 'stop');
}
ChripPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.chripPlugin = new ChripPlugin();
  return window.plugins.chripPlugin;
};

cordova.addConstructor(ChripPlugin.install);