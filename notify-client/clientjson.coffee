dgram = require("dgram")
server = dgram.createSocket("udp4")
growl = require("growl")
_ = require('underscore')

Messages = []

growlMessage = (msg) ->
  #console.log "Growling", msg
  growl msg.message,
    {
      title: msg.from
      image: msg.icon
      sticky: msg.sticky
    }


lookupAppName = (appname) ->
  map = {
    "Google": /google/i,
    "Kik": /kik/i
  }

  for realname, regex of map
    return realname if appname.match(regex)
  return appname


lookupIcon = (appname) ->
  getIconPath = (icon) -> "#{__dirname}/icons/#{icon}.png"
  defaultIconPath = getIconPath('android')
  map = {
    google: /google/i,
    kik: /kik/i
  }

  for icon, regex of map
    return getIconPath(icon) if appname.match(regex)
  
  return defaultIconPath

lookupSticky = (appname) ->
  sticky = [ /kik/i ]

  for regex in sticky
    return true if appname.match(regex)
  
  return false

parseMessage = (msgstr) ->
  msgstr = msgstr.replace(/\[|\]/g, "")
  parts = msgstr.split(",")

  appname = parts[0].trim()

  {
    app: lookupAppName(appname)
    from: parts[1].trim()
    message: parts[2].trim()
    time: parts[3].trim()
    icon: lookupIcon(appname)
    sticky: lookupSticky(appname)
  }


server.on "listening", ->
  address = server.address()
  console.log "server listening " + address.address + ":" + address.port

base64 = require('./base64')
fs = require('fs')
crypto = require('crypto')

hash = (str) -> crypto.createHash('md5').update(str).digest("hex");

server.on "message", (msgstr, rinfo) ->
  console.log msgstr.toString()
  msg = JSON.parse(msgstr.toString())

  image_hash = hash(msg.icon)
  decodedImage = new Buffer(msg.icon, 'base64');
  fs.writeFile("icons/#{image_hash}.png", decodedImage, ((err)->));
  #msg = parseMessage(msgstr.toString())
  #Messages.unshift(msg)
  #growlMessage msg

server.bind 2562


