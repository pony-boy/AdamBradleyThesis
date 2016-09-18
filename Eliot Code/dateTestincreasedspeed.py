import pickle
import re
import numpy

f = open ("2e.txt", "r")
g = open ("testlistEliot.txt","r")

def median(lst):
    return numpy.median(numpy.array(lst))


myDict = {}
numberOfUses = {}
dateList = []
the_whole_file = f.read()
wordCount = 0
hitCount = 0
century1=0
century2=0
century3=0
century4=0
century5=0
century6=0
century7=0
century8=0
century9=0
century10=0
century11=0
century12=0
century13=0
century14=0
century15=0
century16=0
century17=0
century18=0
century19=0
century20=0



for line in g:
    line = line[:-1]
    line = line.split()
    print line
    myDict[line[0]]=[]
    print myDict[line[0]]
    numberOfUses[line[0]] = [line[1]]
    print numberOfUses[line[0]]
print len (myDict)

##for key in myDict:
##    
##
##    start_position = 0
##    
##    start_position = the_whole_file.find('<LF>'+key, start_position)
##    #print start_position
##
##    end_position = the_whole_file.find('</D>', start_position)
##    #print start_position
##    date =  the_whole_file[start_position:end_position]
for date in re.findall("\<LF\>(.*)\<\/D\>",the_whole_file):
    key = date.split(" ",1)[0]
    print key
    if key in myDict:
       
    
        useCount = numberOfUses[key]
        useCount = int(useCount)
        print key
        print useCount
        wordCount = wordCount + 1
        print wordCount

        

        if date !="":
            hitCount = hitCount + 1
            date = date [-7:]
            date = re.sub(r'[A..]', '', date)
            date = re.sub(r'[\.\.]', '00', date)
            print date
            date = re.sub(r'-\.*',' ',date)
            print date
            date = re.findall(r'\d+', date)
            date = date[0]
            date = int(date)
            print date
            print "****************"
            print "\n"
            
            if date >0 and  date <100:
                century1 = century1 + 1
                print "FIRST CENTURY"
                print date
                print key
            elif date >99 and date <200:
                century2 = century2 + 1
            elif date >199 and date <300:
                century3 = century3 + 1
            elif date >299 and date <400:
                century4 = century4 + 1
            elif date >399 and date <500:
                century5 = century5 + 1    
            elif date >499 and date <600:
                century6 = century6 + 1
            elif date >599 and date <700:
                century7 = century7 + 1
            elif date >699 and date <800:
                century8 = century8 + 1
            elif date >799 and date <900:
                century9 = century9 + 1
            elif date >899 and date <1000:
                century10 = century10 + 1
            elif date >999 and date <1100:
                century11 = century11 + 1
            elif date >1099 and date <1200:
                century12 = century12 + 1
            elif date >1199 and date <1300:
                century13 = century13 + 1
            elif date >1299 and date <1400:
                century14 = century14 + 1
            elif date >1399 and date <1500:
                century15 = century15 + 1
            elif date >1499 and date <1600:
                century16 = century16 + 1
            elif date >1599 and date <1700:
                century17 = century17 + 1
            elif date >1699 and date <1800:
                century18 = century18 + 1
            elif date >1799 and date <1900:
                century19 = century19 + 1
            elif date >1899 and date <2000:
                century20 = century20 + 1
                
            #print key
            #print date # [-7:]
            #print "\n"
            myDict[key] = []
            myDict[key].append(date)
            dateList.append(date)
            #print dateList


    
#pickle.dump(myDict, open("georgianpoetry_worddates.p","wb"))


dateTotal = sum(dateList)
print "total number of words: ", len (myDict)
print "total number of headword hits: ", hitCount    
print "Average Word Date: ", dateTotal / len(dateList)
print "earliest word date: ", min(dateList)
print "latest word date: ", max (dateList)
print "mean word date: ", median(dateList)
print "1st century: ", century1
print "2nd century: ", century2
print "3rd century: ", century3
print "4th century: ", century4
print "5th century: ", century5
print "6th century: ", century6
print "7th century: ", century7
print "8th century: ", century8
print "9th century: ", century9
print "10th century: ", century10
print "11th century: ", century11
print "12th century: ", century12
print "13th century: ", century13
print "14th century: ", century14
print "14th century: ", century15
print "16th century: ", century16
print "17th century: ", century17
print "18th century: ", century18
print "19th century: ", century19
print "20th century: ", century20


