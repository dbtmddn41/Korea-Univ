#!/usr/bin/env python3
 #coding: utf-8

import sys

def get_morphs_tags(tagged):
	word_list = tagged.split('+')
	word_num = len(word_list)
	i=0
	while i < word_num:
		if not word_list[i]:
			word_list.pop(i)
			word_list[i] = '+/SP'
			word_num -= 1
		i += 1
	
	word_tuple_list = list()
	for word in word_list:
		word_tuple = word.split('/')
		if len(word_tuple) == 2:
			word_tuple_list.append(word_tuple)
		else:
			word_tuple_list.append(('/','SS'))
	
	return word_tuple_list
###############################################################################
if __name__ == "__main__":

    if len(sys.argv) != 2:
        print( "[Usage]", sys.argv[0], "in-file", file=sys.stderr)
        sys.exit()

    with open(sys.argv[1]) as fin:

        for line in fin.readlines():

            # 2 column format
            segments = line.split('\t')

            if len(segments) < 2: 
                continue

            # result : list of tuples
            result = get_morphs_tags(segments[1].rstrip())
        
            for morph, tag in result:
                print(morph, tag, sep='\t')
