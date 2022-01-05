#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# 복수의 빈도 파일을 병합하는 프로그램

import sys
import heapq

###############################################################################
def merge_k_sorted_freq(input_files):
    '''
    input_files : list of input filenames (frequency files; 2 column format)
    '''
    fins = []
    k = len(input_files)
    heap = []
    finished = [False for _ in range(k)] # [False] * k

    for i in range(k):
        fins.append(open(input_files[i]))

    freq_list = []
    for i in range(k):
        line = fins[i].readline().rstrip().split('\t')
        heap.append((line[0], int(line[1]), i))
    heapq.heapify(heap)

    temp = [0, 0, 0]
    true_check = [True]*k

    while true_check != finished:
        temp[0], temp[1], temp[2] = heapq.heappop(heap)
        line = fins[temp[2]].readline().rstrip().split('\t')
        if not line[0]:
            finished[temp[2]] = True
        else:
            heapq.heappush(heap, (line[0], int(line[1]), temp[2]))

        while heap:
            if temp[0] != heap[0][0]:
                break
            word, freq, index = heapq.heappop(heap)
            temp[1] += freq
            line = fins[index].readline().rstrip().split('\t')
            if not line[0]:
                finished[index] = True
            else:
                heapq.heappush(heap, (line[0], int(line[1]), index))
        print(temp[0], temp[1], sep = '\t')

        
            
    for i in range(k):
        fins[i].close()

###############################################################################
if __name__ == "__main__":

    if len(sys.argv) < 2:
        print( "[Usage]", sys.argv[0], "in-file(s)", file=sys.stderr)
        sys.exit()

    merge_k_sorted_freq( sys.argv[1:])
