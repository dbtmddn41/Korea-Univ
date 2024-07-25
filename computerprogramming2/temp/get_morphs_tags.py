#!/usr/bin/env python3
 #coding: utf-8

import sys

def get_morphs_tags(tagged):
    i = 4
    start = 1
    word_list = list()
    tagged_len = len(tagged)
    while i < tagged_len:
        
        if tagged[i] == '+':
            word_list.append(tagged[start-1:i])
            i += 1
            start = i+1
        i += 1
    word_list.append(tagged[start-1:])

    word_tuple_list = list()
    for word in word_list:
        i = 1
        while True:
            if word[i] == '/':
                word_tuple_list.append((word[:i], word[i+1:]))
                break
            i += 1

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
