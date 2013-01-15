
b64 = ARGV[0]

require 'base64'
File.open('icon.png', 'wb') do|f|
    f.write(Base64.urlsafe_decode64(b64))
end
