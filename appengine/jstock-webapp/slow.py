import webapp2
import time

class AnyHandler(webapp2.RequestHandler):
    def get(self):
        time.sleep(1024)

    def post(self):
        time.sleep(1024)
        
app = webapp2.WSGIApplication([
    (r'/.*', AnyHandler)
], debug=True)