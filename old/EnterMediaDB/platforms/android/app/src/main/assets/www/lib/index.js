var $output;


function log(msg, opts){
    opts = opts || {};

    opts.logLevel = opts.logLevel || "log";
    console[opts.logLevel](msg);

}

function logError(msg){
    log(msg, {
        logLevel: "error"
    });
}


// Init
function onDeviceReady(){
    $output = $('#log-output');
    log("deviceready");


//https://stackoverflow.com/questions/48363788/error-10-ionic-3cordova-cordova-plugin-googleplus-after-install-from-google

    window.plugins.googleplus.login(
    {
      'webClientId': '279466694094-cngqjn1rkp0gt6cg9ba7fp2hm5ik9n4t.apps.googleusercontent.com', // optional clientId of your Web application from Credentials settings of your project - On Android, this MUST be included to get an idToken. On iOS, it is not required.
      'offline': false // optional, but requires the webClientId - if set to true the plugin will also return a serverAuthCode, which can be used to grant offline access to a non-Google server
    },
    function (obj) {
      alert(JSON.stringify(obj)); // do something useful instead of alerting
    },
    function (msg) {
      log("Could not login " + msg);
      alert('error: ' + msg);
    }
);

}
$(document).on('deviceready', onDeviceReady);

var initIos = function(){};

var initAndroid = function(){

};

