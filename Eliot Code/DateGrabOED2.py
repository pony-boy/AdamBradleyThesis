import pickle

f = open ("2e.txt", "r")
g = open ("HeadwordsOED2Edited.txt","r")
          


myDict = {}
the_whole_file = f.read()


##for line in g:
##    
##    #line = line[:-2]
##    print line
##    myDict[line] = []
##    #print myDict


for x in g: 
    
    start_position = 0
    
    start_position = the_whole_file.find('<LF>'+x, start_position)
    print start_position

    end_position = the_whole_file.find('</D>', start_position)
    print start_position
    date =  the_whole_file[start_position:end_position]

    print date

    myDict[x] = []
    myDict[x].append(date)

    start_position = end_position

pickle.dump(myDict, open("headword_dates.p","wb"))


# Load the dictionary back from the pickle file.
#import pickle

#favorite_color = pickle.load( open( "save.p", "rb" ) )
# favorite_color is now { "lion": "yellow", "kitty": "red" }

   
    

