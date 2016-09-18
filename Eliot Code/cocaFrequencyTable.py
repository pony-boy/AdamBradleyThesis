total_COCA_words = 0
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

f.close()

eliotUnique = ['alike', 'alms', 'bequeath', 'bin', 'bramble', 'chapman', 'crab', 'december', 'dem', 'drake', 'eft', 'fille', 'five-finger', 'font', 'forme', 'fortnight', 'friday', 'gall', 'garlic', 'goldsmith', 'ham', 'handbook', 'handful', 'handle', 'hart', 'horoscope', 'il', 'inborn', 'knave', 'latter', 'lifeless', 'midwinter', 'mit', 'one-eyed', 'p', 'pentecost', 'pose', 'prior', 'reader', 'seraphim', 'shorten', 'sickle', 'sine', 'songster', 'stonecrop', 'strawberry', 'swart', 'tar', 'unholy', 'virgo', 'weevil', 'yew']

for x in eliotUnique:
    if x in full_coca_freq_table:
        print x, full_coca_freq_table[x]
