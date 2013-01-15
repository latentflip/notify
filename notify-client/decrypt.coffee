crypto = require 'crypto'

password = "password"
iv = new Buffer("SkZGJohEVUIPc7631zwnwg==", 'base64')
data = new Buffer("6vI8NyZLJdjQvrwi4L/RAw==", 'base64')
salt = new Buffer("mysalt", "binary")

crypto.pbkdf2 password, salt, 65536, 32, (err, key) ->

  decipher = crypto.createDecipheriv("aes-256-cbc", key, iv)
  decipher.update(data, "binary", "utf8")
  decipher.final('utf8')


return
toLog = (args...) -> console.log args...

decrypt = (data, pass, iv) ->
  toLog "\t--- decrypt ---"
  pass = new Buffer(pass, "utf8")
  toLog "encrypted data (base64):\t" + data
  data = new Buffer(data, "base64")
  salt = ""
  if data.toString().indexOf("Salted__") is 0
    salt = new Buffer(8)
    data.copy salt, 0, 8, 16
    b = new Buffer(data.length - 16)
    data.copy b, 0, 16
    data = b
  toLog "salt from encrypted data:\t" + salt + " (hex: " + salt.toString("hex") + ")"
  opt = genKeyAndIv(pass, salt)
  toLog "iv (hex):\t" + opt.iv.toString("hex")
  toLog "key (hex):\t" + opt.key.toString("hex")
  decipher = crypto.createDecipheriv("aes-255-cbc", opt.key.toString("binary"), opt.iv.toString("binary"))
  data = decipher.update(data, "binary") + decipher.final("binary")
  data = data.substr(0, data.length - 1)  if data.charCodeAt(data.length - 1) is 10
  toLog "decrypted data (base64):\t" + data
  toLog "\t---------------"
  data


decrypt(data, "password", iv)
