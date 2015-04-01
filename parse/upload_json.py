import os, json, httplib, copy

#connect to Parse
connection = httplib.HTTPSConnection('api.parse.com', 443)
connection.connect()

#iterate through files in dir and upload to parse
url_data = {}
url_data["pk"] = "jsonObj"
url_data["Buildings"] = {}
marker_list_fields = ["shortName", "longName", "latitude", "longitude", "thumbnail"]

for root, dirs, files, in os.walk(os.getcwd()) :
	for name in files : 
		if(name.endswith(".txt")):

			#format: file name => building => GDC.txt
			#text in file => floor_URL => 01_www.imageURL.com
			#newline needed at end of file or a char will get cut off the last URL

			number_of_floors = 0
			building_name = name[0:-4]
			url_data["Buildings"][building_name] = {}

			file = open(name)
			for line in iter(file) :

				floor_and_url = line.split("_")
				if floor_and_url[0] not in marker_list_fields :
					url_data["Buildings"][building_name][floor_and_url[0]] = floor_and_url[1][0:-1]
					number_of_floors += 1

			#number of floors - 1 because one line for default_floor
			url_data["Buildings"][building_name]["numFloors"] = str(number_of_floors - 1)

print url_data

connection.request('POST', '/1/classes/BuildingJSON', json.dumps(url_data), {
 	"X-Parse-Application-Id": "xTzPEGb9UXNKHH6lEphikPyDpfXeSinJ9HoIqODU",
   	"X-Parse-REST-API-Key": "7LO1Je4sQxoSEWaWkKvssV12LbBHhnhoB1T4vUn6",
   	"Content-Type": "application/json"
})

result = json.loads(connection.getresponse().read())
print result
