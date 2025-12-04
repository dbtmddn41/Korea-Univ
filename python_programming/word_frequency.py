#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

###############################################################################
def word_count(filename):
    """ 단어 빈도 dictionary를 생성한다. (key: word, value: frequency)
    
    filename: input file
    return value: a sorted list of tuple (word, frequency) 
    """
    with open(sys.argv[1]) as fin:
        words = fin.readlines()
        freq = dict()
        for word in words:
            word = word.rstrip()
            freq[word] = freq.get(word, 0) + 1
        freq_items = list(freq.items())
        freq_items.sort()
    return freq_items
###############################################################################
if __name__ == "__main__":

    if len(sys.argv) != 2:
        print( "[Usage]", sys.argv[0], "in-file", file=sys.stderr)
        sys.exit()

    result = word_count(sys.argv[1])

     #list of tuples
    for w, freq in result:
        print( "%s\t%d" %(w, freq))
