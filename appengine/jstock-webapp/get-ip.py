import webapp2
import time
import os

class GetIPHandler(webapp2.RequestHandler):
    def get(self):
        ip = os.environ.get("HTTP_X_FORWARDED_FOR") or os.environ.get("REMOTE_ADDR")
        ip = ip.split(',')[0]
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write(ip)
        
app = webapp2.WSGIApplication([
    ('/get-ip.py', GetIPHandler)
], debug=True)