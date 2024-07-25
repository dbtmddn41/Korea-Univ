def make_ana_dict():
    ana_dict = {}
    file = open('words.txt')
    for word in file.readlines():
        letters = tuple(sorted(list(word.rstrip())))
        if letters not in ana_dict:
            ana_dict[letters] = []
        ana_dict[letters].append(word.rstrip())
    return ana_dict

if __name__ == '__main__':

	
	print(make_ana_dict())