import os, json, httplib, copy  

# connect to Parse ------------------------------------------------------------

connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()

wrapper = {}
search_dict = {}

# iterate through files in current dir get search file -------------------------

for root, dirs, files, in os.walk(os.getcwd()) :
	for name in files : 
		if name.startswith("Search"):
	
			file = open(name)
			for line in iter(file) :	

				split = line.split("_")
				search_dict[split[0]] = split[1][0:-1]


wrapper["SearchMap"] = search_dict
print wrapper

# update column in BuildingJSON table via PUT ---------------------------------- 

connection.request('PUT', '/1/classes/BuildingJSON/KYOpwgby96', json.dumps(wrapper), {
 	"X-Parse-Application-Id": "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
   	"X-Parse-REST-API-Key": "7LO1Je4sQxoSEWaWkKvssV12LbBHhnhoB1T4vUn6",
   	"Content-Type": "application/json"
})

result = json.loads(connection.getresponse().read())
print result
