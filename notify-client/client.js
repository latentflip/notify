var dgram = require("dgram");
var server = dgram.createSocket("udp4");
var growl = require("growl");

function growlMessage(msgstr) {
  var msg = parseMessage(msgstr);

  console.log("Growling", msg);

  growl(msg.message, { title: msg.from, image: msg.icon})
}


function lookupIcon(appname) {
  {


  }
}

function parseMessage(msgstr) {
  msgstr = msgstr.replace( /\[|\]/g, '');
  var parts = msgstr.split(',');

  return {
    app: parts[0].trim(),
    from: parts[1].trim(),
    message: parts[2].trim(),
    time: parts[3].trim(),
    icon: __dirname + "/icon-android.png"
  }
}

server.on("message", function (msg, rinfo) {
  //rinfo.address + ":" + rinfo.port);
  growlMessage(msg.toString());
});

server.on("listening", function () {
  var address = server.address();
  console.log("server listening " + address.address + ":" + address.port);


});

server.bind(2562);
