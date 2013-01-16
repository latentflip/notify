dgram = require("dgram")
server = dgram.createSocket("udp4")
growl = require("growl")
_ = require('underscore')
Encrypt = require('./encrypt')

Messages = []

growlPasscodeError = ->
  growl "It looks like your passcode is wrong", { title: "Notify" }

growlMessage = (msg) ->
  args = [
    msg.content,
    {
      title: msg.from,
      image: msg.icon,
      sticky: msg.sticky
    }
  ]

  console.log "Growling", args...

  growl args...


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

doIcon = (msg) ->
  if msg.icon
    image_hash = hash(msg.icon)
    decodedImage = new Buffer(msg.icon, 'base64');
    path = "icons/#{image_hash}.png"
    fs.writeFile(path, decodedImage, ((err)->));
    msg.icon = "#{__dirname}/#{path}"
  else
    msg.icon = ""
  msg

base64 = require('./base64')
fs = require('fs')
crypto = require('crypto')

hash = (str) -> crypto.createHash('md5').update(str).digest("hex");

server.on "message", (msgstr, rinfo) ->
  msgstr = msgstr.toString()
  msg = JSON.parse( msgstr.toString() )

  try
    if msg.iv
      decrypted = Encrypt.decryptWithKeyAndIV("1234567890", msg.iv, msg.encrypted)
      msg = JSON.parse(decrypted)

    msg.sticky = false
    msg = doIcon(msg)
    Messages.unshift(msg)
    growlMessage msg
  catch e
    console.log "Passcode is wrong"
    growlPasscodeError()
server.bind 2562


#####################################################

express = require('express')
app = express()

app.use express.static(__dirname + '/public')
app.use '/icons', express.static(__dirname + '/icons')

Templates =
  message: """
    <li>
      <img src="/icons/<%= _.last(icon.split("/")) %>" />
      <span class=app><%= app %></span>
      <span class=from><%= from %></span>
      <span class=message><%= content %></span>
    </li>
  """

  layout: """
    <html>
      <head>
        <link rel="stylesheet" href="style.css">
        <script>
          //setInterval(function() { document.location.reload() }, 5000);
        </script>
      
      </head>

      <body>
        <%= yield %>
      </body>
    </html>
  """

app.get '/', (req, res) ->
  if Messages.length == 0
    res.send _.template(Templates.layout, {yield: "No messages"})
  else
    msglist = _.reduce( Messages,
                        ((list, m) -> list + _.template(Templates.message, m))
                        ""
                      )

  
    res.send _.template(Templates.layout,
      {
        yield: "<ul> #{msglist} </ul> "
      }
    )

app.listen(2563)
