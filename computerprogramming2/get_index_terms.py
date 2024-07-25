#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import get_morphs_tags as mf

###############################################################################
# 색인어 추출
def get_index_terms(mt_list):
    """ 형태소 분석 결과(형태소-품사 쌍)로부터 색인어를 추출
    색인어는 품사가 일반명사(NNG), 고유명사(NNP), 영어(SL), 숫자(SN), 한자(SH)이어야 함
    동일 어절 내에서 인접하여 결합된 경우도 색인어로 추출해야 함 (복합어)

    mt_list: a list of tuples (morpheme, tag)
    return value: a list of string (색인어 리스트)
    """
    nouns = []
    word_num = len(mt_list)

    i = 0
    while i < word_num:
        if mt_list[i][1] == 'NNG' or mt_list[i][1] == 'NNP' or mt_list[i][1] == 'SL' or mt_list[i][1]== 'SN' or mt_list[i][1] == 'SH':
            nouns.append(mt_list[i][0])
            j = i + 1
            if  i + 1 == word_num:
                break
            while mt_list[j][1] == 'NNG' or mt_list[j][1] == 'NNP' or mt_list[j][1] == 'SL' or mt_list[j][1]== 'SN' or mt_list[j][1] == 'SH':
                nouns.append(mt_list[j][0])
                j += 1
                if  j == word_num:
                    break
            if j != i + 1:
                mixed = ''
                for x in range(i, j):
                    mixed += mt_list[x][0]
                nouns.append(mixed)
            i = j
        i += 1    
    return nouns

###############################################################################
if __name__ == "__main__":

    if len(sys.argv) < 2:
        print( "[Usage]", sys.argv[0], "in-file", file=sys.stderr)
        sys.exit()

    with open( sys.argv[1]) as fin:

        mt_freq = {}
    
        # 2 column format
        for line in fin.readlines():

            segments = line.split('\t')

            if len(segments) < 2:
                continue

            # 형태소, 품사 추출
            # result : list of tuples
            result = mf.get_morphs_tags(segments[1].rstrip())
    
            # 색인어 추출 (명사 및 복합명사 등)
            terms = get_index_terms( result)
        
            # 색인어 출력
            for term in terms:
                print(term)
