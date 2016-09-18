#Currenly this script takes a txtfile, tokenizes it, removes puntuation, and
#outputs a concordances of the number of times a word is used. It preprocessing
#for dateTestDavidCodetotalwordsused.py

import string
#from nltk.stem import PorterStemmer
#stemmer=PorterStemmer()
#from nltk.corpus import gutenberg
#from nltk.corpus import brown

f = open('Eliot_post_conversion.txt', 'r')
g = open('WordcountlistEliotPostConversion.txt','w')
#whitman = gutenberg.words('whitman-leaves.txt')
#brown = brown.words(categories='news')

myDict = {}

linenum = 0
for line in f:
    line = (line.translate(None, r".;,!:?()\"'")).lower()

    for word in line.split():
        word2 = word
         #word = word without trailing hyphens
 #       word = stemmer.stem(word)
        #if not word in myDict:
 #           myDict[word] = []
            

        if not word2 in myDict:
            myDict[word2] = []
            
            
        else:
            myDict[word].append(linenum)


##print "%-15s %-15s" %("Word", "Frequency")
##for key in sorted(myDict):
##    print '%-15s: %-15d' % (key, (len(myDict[key]))+1)

for key in sorted(myDict):
    length = str(len(myDict[key])+1) #this accounts for the creation of the dict
    g.write(key)
    g.write(" ")
    g.write(length)
    g.write('\n')


g.close()





