import webapp2
import json
import hashlib
from google.appengine.ext import ndb

class MissingStockCode(ndb.Model):
    code = ndb.StringProperty(required = True)
    counter = ndb.IntegerProperty(required = True)
    timestamp = ndb.DateTimeProperty(auto_now = True)
    
def hash(x):
    salt = "b^*("
    m = hashlib.md5()
    m.update((salt + x).encode('utf-8'))
    return m.hexdigest()
    
class ReportHandler(webapp2.RequestHandler):
    def post(self):
        body = self.request.body
        
        missing_stock_code = json.loads(body)
        code = missing_stock_code['code']
        _hash = missing_stock_code['hash']

        expected_hash = hash(code)
        
        if _hash != expected_hash:
            logging.debug('ReportHandler, body = ' + body + ', _hash = ' + _hash + ', expected_hash = ' + expected_hash)
            self.response.set_status(400)
            return
            
        try:
            counter = MissingStockCode.get_or_insert(code, code=code, counter=0)
            counter.counter = counter.counter + 1
            counter.put()
        except Exception as e:
            logging.error(e, exc_info=True)
            
        self.response.set_status(200)
        
app = webapp2.WSGIApplication([
    ('/missing-stock-code/report', ReportHandler)
], debug=True)