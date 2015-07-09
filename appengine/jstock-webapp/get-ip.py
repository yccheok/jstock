import time
import os

ip = os.environ.get("HTTP_X_FORWARDED_FOR") or os.environ.get("REMOTE_ADDR")
ip = ip.split(',')[0]

print 'Content-Type: text/plain'
print ''
print ip 