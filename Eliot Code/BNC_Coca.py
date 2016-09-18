#This uses corpus text files for the BNC and COCA and sets up frequency tables for them
import sys
import numpy
import pickle
import string
import re
from stemming.porter2 import stem
from time import gmtime, strftime #Turns out this is not redundant
import time
from math import log, sqrt, exp
import nltk   

wnl = nltk.WordNetLemmatizer
total_COCA_words = 0

#   This is a good way to stip things out of a line
#   lis.append(x.rstrip('\n'))


#Build COCA frequency table for Metric 2
lis = []
full_COCA_list=[]
f = open('full_coca_freq_table.txt', 'r')
for x in f:
    lis.append(x.rstrip('\n'))
f.close()
full_coca_freq_table = {}
for line in lis:
    key, value = line.split('|')
    if key not in full_coca_freq_table:
        full_coca_freq_table[key] = int(value)
for key in full_coca_freq_table:
    full_COCA_list.append(full_coca_freq_table[key])
    
    total_COCA_words = total_COCA_words + full_coca_freq_table[key]

print total_COCA_words

COCA_list_mean=numpy.average(full_COCA_list)
COCA_list_stdev=numpy.std(full_COCA_list)

full_BNC_freq_table=pickle.load(open('bncrawgrams_1.p','rb'))
BNC_list_mean=float(84.28629)
BNC_list_stdev=float(928.5974)



