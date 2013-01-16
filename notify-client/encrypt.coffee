crypto = require("crypto")
Encrypt = module.exports = (->
  makeKey = (password) ->
    crypto.createHash("sha256").update(password).digest()

  key = makeKey("RwcmlVpg")
  iv = '4e5Wa71fYoT7MFEX'

  cipherWithKeyIV = (mode, key, iv, data) ->
    encipher = crypto[mode]("aes-256-cbc", key, iv)
    encoded = encipher.update(data)
    encoded += encipher.final()
    return encoded

  cipher = (mode, data) ->
    encipher = crypto[mode]("aes-256-cbc", key, iv)
    encoded = encipher.update(data)
    encoded += encipher.final()
    return encoded

  encrypt = (data) ->
    return b64enc(cipher "createCipheriv", data)

  decrypt = (data) ->
    return cipher "createDecipheriv", b64dec(data)

  decryptWithKeyAndIV = (key, iv, data) ->
    return cipherWithKeyIV "createDecipheriv", makeKey(key), iv, b64dec(data)

  b64enc = (data) ->
    b = new Buffer(data, "binary")
    b.toString "base64"

  b64dec = (data) ->
    b = new Buffer(data, "base64")
    b.toString "binary"

  return { 
    encrypt: encrypt
    decrypt: decrypt
    decryptWithKeyAndIV: decryptWithKeyAndIV
  }

)()
