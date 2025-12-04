import make_ana_dict as mkanad
import shelve
import sys
import pickle


def store_anagrams(filename, anagram_map):
    shelf = shelve.open(filename, 'c')
    for alpha, word_list in anagram_map.items():
        shelf[str(alpha)] = word_list
    shelf.close()
    
def read_anagrams(filename, word):
    shelf = shelve.open(filename)
    word = str(tuple(sorted(word)))
    try:
        return shelf[word]
    except:
        return []

def main(script, command='make_db', ver='not_pickle'):
    if command == 'make_db' and ver != 'pickle':
        anagram_map = mkanad.make_ana_dict()
        store_anagrams('anagrams.db', anagram_map)
    elif command == 'make_db' and ver == 'pickle':
        fp = open('anagrams.pickle', 'wb')
        pickle.dump(mkanad.make_ana_dict(), fp)
        fp.close()

    elif ver != 'pickle':
        print(read_anagrams('anagrams.db', command))
    else:
        fp = open('anagrams.pickle', 'rb')
        anagram_map = pickle.load(fp)

        try:
            print(anagram_map[tuple(sorted(command))])
        except:
            print([])
if __name__ == '__main__':
    main(*sys.argv)