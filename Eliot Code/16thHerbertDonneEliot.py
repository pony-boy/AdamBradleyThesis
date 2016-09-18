Eliot16th = ['alike', 'alms', 'bequeath', 'bin', 'bramble', 'chapman',
             'crab', 'december', 'dem', 'drake', 'eft', 'fille', 'five-finger',
             'font', 'forme', 'fortnight', 'friday', 'gall', 'garlic', 'goldsmith',
             'ham', 'handbook', 'handful', 'handle', 'hart', 'horoscope', 'il', 'inborn',
             'knave', 'latter', 'lifeless', 'midwinter', 'mit', 'one-eyed', 'p', 'pentecost',
             'pose', 'prior', 'reader', 'seraphim', 'shorten', 'sickle', 'sine', 'songster', 'stonecrop',
             'strawberry', 'swart', 'tar', 'unholy', 'virgo', 'weevil', 'yew']


DonneVocab=[]
HerbertVocab = []

f = open('WordcountlistDonnevocab.txt','rb')
g = open ('WordcountlistHerbertvocab.txt','rb')


for line in f:
    DonneVocab.append(line[:-1])
    

for word in g:
    HerbertVocab.append(word[:-1])
    


for x in Eliot16th:
    if x in DonneVocab and x in HerbertVocab:
        print x


f.close()
g.close()
    
