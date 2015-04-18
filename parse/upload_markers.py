import os, json, httplib, copy  

# connect to Parse ------------------------------------------------------------

connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()

# iterate through files in dir and upload to parse ----------------------------
# format: file name => building => GDC.txt ------------------------------------

# set up dictionary and list to build json ------------------------------------

marker_dict = {}
marker_list = []
marker_list_fields = ["shortName", "longName", "latitude", "longitude", "thumbnail"]

# iterate through files in current dir and load json --------------------------

for root, dirs, files, in os.walk(os.getcwd()) :
	for name in files : 
		if(not name.startswith("Search") and name.endswith(".txt")):


			marker_dictionary = {}
			file = open(name)
			for line in iter(file) :

				floor_and_url = line.split("_")
				if floor_and_url[0] in marker_list_fields :
					marker_dictionary[floor_and_url[0]] = floor_and_url[1][0:-1]

			marker_list.append(marker_dictionary)


marker_dict["Markers"] = marker_list
print marker_dict

# update column in BuildingJSON table via PUT ---------------------------------- 

connection.request('PUT', '/1/classes/BuildingJSON/e2QYtasAgi', json.dumps(marker_dict), {
 	"X-Parse-Application-Id": "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
   	"X-Parse-REST-API-Key": "7LO1Je4sQxoSEWaWkKvssV12LbBHhnhoB1T4vUn6",
   	"Content-Type": "application/json"
})

result = json.loads(connection.getresponse().read())
print result
