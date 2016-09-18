import pickle
import re


f = open ("2e.txt", "rb")
g = open ("GeorgianWordlist.txt","r")


oed2=f.read()
myDict = {}


wordCount = 0
hitCount = 0
sensesReturn = 0
overallObs = 0

entries=re.findall(r'<E>.+?</E>',oed2)
firstislowest=0
firstisnot=0
maxSense = 0

for e in entries:
    
    
    if '<ST>' in e:
        headword=re.findall(r'<LF>.+?</LF>',e)
        #print headword

        numberOfObs = re.findall(r'Obs.',e)
        
        
        
        TotalSenses=len(numberOfObs)
        overallObs = overallObs + TotalSenses
        
        
        

        headword = headword[0]
        headword = headword[4:-5].lower()

       
        
        if headword not in myDict:
            
            myDict[headword] = TotalSenses
            
            if TotalSenses > maxSense:
                
                 maxSense = TotalSenses
                 maxHeadword = headword
            





            
for line in g:
    line = line[:-1]
                
   
    wordCount = wordCount + 1
    

    if line in myDict:
        hitCount = hitCount + 1
        

        sensesReturn = sensesReturn + myDict[line]
        #print date

averageSense = sensesReturn / hitCount


print "The Total Obs is: ", overallObs    
print "The average number of Obs per word is: ", averageSense
print "The word used with the most Obs is: ", maxHeadword
print "That number of senses: ",  maxSense
print sorted
