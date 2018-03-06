import webapp2
import time

class GetTimeHandler(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('id=ltsgi4R41y6x9GucPlNn5ls5HPDzKBIgRezXRcuYF/AjqV9XZCmyDJ9wl+KWaTed')
        self.response.out.write('\n')
        self.response.out.write('time=%.0f' % (time.time() * 1000))
        
app = webapp2.WSGIApplication([
    ('/get-time.py', GetTimeHandler)
], debug=True)