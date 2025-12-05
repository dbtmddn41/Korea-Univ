#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import get_morphs_tags as mf
import get_index_terms as term
import pickle

###############################################################################
def indexing_tagged(indexing, sentences, filename):
    """ 형태소 분석 파일로부터 색인 정보를 생성 (역색인, 문장)
    indexing : 역색인 dictionary (key : index term, value : set of sentences)
    sentences : 색인된 문장 리스트
    filename : 형태소 분석 파일
    """

    file = open(filename)
    prev_sentences = len(sentences)
    string = []
    i = 0
    
    for line in file.readlines():
        segments = line.split('\t')
        if len(segments) < 2:
            sentences.append(' '.join(string))
            string = []
            i += 1
            continue
        
        string.append(segments[0])
        result = mf.get_morphs_tags(segments[1].rstrip())

        terms = term.get_index_terms(result)
        for t in terms:
            if t not in indexing:
                indexing[t] = set()
            indexing[t] |= {prev_sentences +i}
        

###############################################################################
if __name__ == "__main__":

    if len(sys.argv) < 2:
        print( "[Usage]", sys.argv[0], "in-file(s)", file=sys.stderr)
        sys.exit()

    inverted_indexing = {}
    sentences = []
    
    for filename in sys.argv[1:]:
        indexing_tagged( inverted_indexing, sentences, filename)

    with open("index.pickle","wb") as fout:
        pickle.dump((inverted_indexing, sentences), fout)

