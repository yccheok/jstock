import webapp2
import time
import json

class AnyHandler(webapp2.RequestHandler):
    def get(self):
        #time.sleep(1024)
        obj = {
            'wlpID': 'DW',
            'tradingType': 'c',
            'userID': '02e57c7d-d071-4c63-b491-1194a9939ea5',
            'languageID': 'en_US',
            'usCitizen': 'true',
            'firstName': 'Bob',
            'lastName': 'Belcher',
            'emailAddress1': 'bob@belcher.com',
            'username': 'bobbelcher',
            'addressLine1': '123 Main St',
            'addressLine2': 'Apt 2',
            'city': 'Wicker',
            'stateProvince': 'New Jersey',
            'countryID': 'USA',
            'zipPostalCode': '08312',
            'dob': '1976-11-18',
            'gender': 'male',
            'maritalStatus': 'married',
            'phoneHome': '(212) 555-3212',
            'idNo': '113-43-1231',
            'citizenship': 'USA',
            'ownershipType': 'Individual',
            'employerIsBroker': 'false',
            'director': 'false',
            'employmentStatus': 'Employed / Self-Employed',
            'politicallyExposed': 'false',
            'employerCompany': 'Bob\'s Burgers',
            'employerBusiness': 'Food and Beverage',
            'employerCountryID': 'USA',
            'directorOf': '',
            'politicallyExposedNames': '',
            'investmentObjectives': 'Income',
            'investmentExperience': 'Limited',
            'annualIncome': '$25,000 - $99,999',
            'networthLiquid': '$5,000 - $99,999',
            'networthTotal': '$25,000 - $99,999',
            'riskTolerance': 'Low',
            'ackSigned': 'true',
            'disclosureAck': 'true',
            'disclosureRule14b': 'true',
            'ackCustomerAgreement': 'true',
            'ackSweep': 'true',
            'ackMarketData': 'true',
            'ackSignedBy': 'Bob Belcher',
            'ackSignedWhen': '2015-11-03T18:25:02.622Z',
            'ackFindersFee': 'true'
        }
        
        self.response.headers['Content-Type'] = 'application/json'   
        self.response.out.write(json.dumps(obj))
        
    def post(self):
        #time.sleep(1024)
        obj = {
            'wlpID': 'DW',
            'tradingType': 'c',
            'userID': '02e57c7d-d071-4c63-b491-1194a9939ea5',
            'languageID': 'en_US',
            'usCitizen': 'true',
            'firstName': 'Bob',
            'lastName': 'Belcher',
            'emailAddress1': 'bob@belcher.com',
            'username': 'bobbelcher',
            'addressLine1': '123 Main St',
            'addressLine2': 'Apt 2',
            'city': 'Wicker',
            'stateProvince': 'New Jersey',
            'countryID': 'USA',
            'zipPostalCode': '08312',
            'dob': '1976-11-18',
            'gender': 'male',
            'maritalStatus': 'married',
            'phoneHome': '(212) 555-3212',
            'idNo': '113-43-1231',
            'citizenship': 'USA',
            'ownershipType': 'Individual',
            'employerIsBroker': 'false',
            'director': 'false',
            'employmentStatus': 'Employed / Self-Employed',
            'politicallyExposed': 'false',
            'employerCompany': 'Bob\'s Burgers',
            'employerBusiness': 'Food and Beverage',
            'employerCountryID': 'USA',
            'directorOf': '',
            'politicallyExposedNames': '',
            'investmentObjectives': 'Income',
            'investmentExperience': 'Limited',
            'annualIncome': '$25,000 - $99,999',
            'networthLiquid': '$5,000 - $99,999',
            'networthTotal': '$25,000 - $99,999',
            'riskTolerance': 'Low',
            'ackSigned': 'true',
            'disclosureAck': 'true',
            'disclosureRule14b': 'true',
            'ackCustomerAgreement': 'true',
            'ackSweep': 'true',
            'ackMarketData': 'true',
            'ackSignedBy': 'Bob Belcher',
            'ackSignedWhen': '2015-11-03T18:25:02.622Z',
            'ackFindersFee': 'true'
        }
        
        self.response.headers['Content-Type'] = 'application/json'   
        self.response.out.write(json.dumps(obj))

    def put(self):
        time.sleep(1024)
        
app = webapp2.WSGIApplication([
    (r'/.*', AnyHandler)
], debug=True)