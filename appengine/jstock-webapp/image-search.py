import webapp2
import urllib
import hashlib
import json
import logging
import time
from google.appengine.api import memcache
from google.appengine.ext import ndb
from google.appengine.api import urlfetch



_MEMCACHE_DURATION = 12*24*60*60

# Image will stay in DB around 2 months.
_IMAGE_TOO_OLD_DURATION = 64*24*60*60

# Under jstock-api project.
GOOGLE_API_KEY = 'AIzaSyAwFTiFITAvx-VFUkz8sI-z98p903HcRDM'
GOOGLE_SEARCH_ENGINE_ID = '001592990107804432426:zcbxfb3jnr4'

BING_SUBSCRIPTION_KEY = '76697f8e32b048668b08bb1698e84cb7'
#BING_SUBSCRIPTION_KEY = 'd4bacbb7628749b98466b6b66681f70e'

SEARCH_RESULT_SIZE = 12

class Image(ndb.Model):
    q = ndb.StringProperty(required = True)
    links = ndb.StringProperty(repeated = True, indexed=False)
    thumbnail_links = ndb.StringProperty(repeated = True, indexed=False)
    source = ndb.StringProperty(required = True)
    modified_time = ndb.DateTimeProperty(auto_now = True)

    
class Search(webapp2.RequestHandler):
    def get(self):
        result = search(self)
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(result))

        
class ThumbDownload(webapp2.RequestHandler):
    def get(self):
        thumbnail_download(self)


class Download(webapp2.RequestHandler):
    def get(self):
        download(self)
        
        
def hash(x):
    salt = "@)(8"
    m = hashlib.md5()
    m.update((salt + x).encode('utf-8'))
    return m.hexdigest()

    
def search(self):
        q = self.request.get('q')
        h = self.request.get('h')
        
        if not q:
            return {}
            
        expected_h = hash(q)
        if h != expected_h:
            logging.error('q [{}] should hashed to [{}] but is given [{}]'.format(unicode(q).encode('utf-8'), expected_h, h))
            return {}
        
        q = q.strip().lower()       

        if not q:
            return {}
            
        current_timestamp = time.time()

        memcache_key = q
        image = memcache.get(memcache_key)
        if image is not None:
            if image.q != memcache_key:
                # Probably the key is too long.
                logging.error('Memcache key [{}] might be too long.'.format(unicode(memcache_key).encode('utf-8')))
                image = None
            else:
                # Is the result too old?
                modified_timestamp = int(time.mktime(image.modified_time.timetuple()))                
                if (current_timestamp - modified_timestamp) > _IMAGE_TOO_OLD_DURATION:
                    logging.info('Memcache image [{}] too old.'.format(unicode(memcache_key).encode('utf-8')))
                    image = None
                    
        if image is not None:
            dict = image.to_dict()
            del dict['modified_time']
            return dict
        else:
            image = Image.get_by_id(q)
            
            if image is not None:
                # Is the result too old?
                modified_timestamp = int(time.mktime(image.modified_time.timetuple()))
                if (current_timestamp - modified_timestamp) > _IMAGE_TOO_OLD_DURATION:
                    logging.info('NDB image [{}] too old.'.format(unicode(memcache_key).encode('utf-8')))
                    image.key.delete()
                    image = None
                
            if image is not None:
                memcache.add(memcache_key, image, _MEMCACHE_DURATION)
                
                dict = image.to_dict()
                del dict['modified_time']
                return dict
            else:
                result = google_image_search(q)
                if result is not None:
                    image = Image.get_or_insert(q, q=q, links=result['links'], thumbnail_links=result['thumbnail_links'], source='google')
                    memcache.add(memcache_key, image, _MEMCACHE_DURATION)
                    
                    dict = image.to_dict()
                    del dict['modified_time']
                    return dict
                else:            
                    result = bing_image_search(q)
                    if result is not None:
                        image = Image.get_or_insert(q, q=q, links=result['links'], thumbnail_links=result['thumbnail_links'], source='bing')
                        memcache.add(memcache_key, image, _MEMCACHE_DURATION)
                        
                        dict = image.to_dict()
                        del dict['modified_time']
                        return dict
                    else:
                        return {}
                        
 
def thumbnail_download(self):
    result = search(self)
    if 'thumbnail_links' in result:
        self.redirect(str(result['thumbnail_links'][0]))
 

def download(self):
    result = search(self)
    if 'links' in result:
        self.redirect(str(result['links'][0]))

        
def google_image_search(q):
    data = {
        'links' : [],
        'thumbnail_links' : []
    }
    
    params = {
        'key' : GOOGLE_API_KEY,
        'cx' : GOOGLE_SEARCH_ENGINE_ID,
        'searchType' : 'image',
        'q' : unicode(q).encode('utf-8')
    }
    
    url = 'https://www.googleapis.com/customsearch/v1?' + urllib.urlencode(params)

    try:
        result = urlfetch.fetch(url)
        
        if result.status_code == 200:
            try:
                json_data = json.loads(result.content)
                
                if not json_data or 'items' not in json_data:
                    return None
                    
                items = json_data['items']

                if type(items) is not list:
                    return None
                    
                for i in range(len(items)):
                    if i >= SEARCH_RESULT_SIZE:
                        break
                        
                    item = items[i]
                    if not item or 'link' not in item:
                        continue
                    link = item['link']
                    
                    if not item or 'image' not in item:
                        continue
                    image = item['image']
                    if not image or 'thumbnailLink' not in image:
                        continue
                    thumbnail_link = image['thumbnailLink']
                    
                    if not link or not link.strip():
                        continue
                    if not thumbnail_link or not thumbnail_link.strip():
                        continue
                        
                    data['links'].append(link)
                    data['thumbnail_links'].append(thumbnail_link)
            except ValueError as e:
                logging.error(e, exc_info=True)
        else:
            logging.error('{} with status code {}.'.format(url, result.status_code))
    except urlfetch.Error as e:
        logging.error(e, exc_info=True)

    if not data['links'] or len(data['links']) != len(data['thumbnail_links']):
        logging.error('No good result from {}.'.format(url))
        return None
        
    return data
    
    
def bing_image_search(q):
    data = {
        'links' : [],
        'thumbnail_links' : []
    }
    
    headers = {
        'Ocp-Apim-Subscription-Key' : BING_SUBSCRIPTION_KEY
    }
    
    params = {
        'count' : SEARCH_RESULT_SIZE,
        'q' : unicode(q).encode('utf-8')
    }
    
    url = 'https://api.cognitive.microsoft.com/bing/v7.0/images/search?' + urllib.urlencode(params)
    
    try:
        result = urlfetch.fetch(
            url=url,
            headers=headers
        )
        
        if result.status_code == 200:
            try:
                json_data = json.loads(result.content)
                
                if not json_data or 'value' not in json_data:
                    return None
                    
                values = json_data['value']
                
                if type(values) is not list:
                    return None
                    
                for i in range(len(values)):
                    if i >= SEARCH_RESULT_SIZE:
                        break
                        
                    value = values[i]
                    
                    if not value or 'contentUrl' not in value:
                        continue
                    link = value['contentUrl']
                    if not value or 'thumbnailUrl' not in value:
                        continue
                    thumbnail_link = value['thumbnailUrl']
                    
                    if not link or not link.strip():
                        continue
                    if not thumbnail_link or not thumbnail_link.strip():
                        continue
                        
                    data['links'].append(link)
                    data['thumbnail_links'].append(thumbnail_link)
            except ValueError as e:
                logging.error(e, exc_info=True)
        else:
            logging.error('{} with status code {}.'.format(url, result.status_code))
    except urlfetch.Error as e:
        logging.error(e, exc_info=True)
        
    if not data['links'] or len(data['links']) != len(data['thumbnail_links']):
        logging.error('No good result from {}.'.format(url))
        return None
        
    return data
    
    
app = webapp2.WSGIApplication([
    ('/image-search/search', Search),
    ('/image-search/thumbnail-download', ThumbDownload),
    ('/image-search/download', Download),
], debug=True)
