# Python Programming - Korean Text Analysis and Search System

## Overview

This project implements a Korean text analysis and search system that processes morphologically tagged Korean text data. It includes functionality for word frequency analysis, indexing, and search capabilities including Hangul (Korean alphabet) character processing and chosung (initial consonant) search.

## Project Purpose

The main goal of this project is to:
- Process and analyze Korean text corpus data from multiple years (2000-2019)
- Extract morphological information from tagged Korean text
- Build inverted indexes for efficient text search
- Provide word frequency analysis capabilities
- Implement Korean-specific search features (chosung search, Jamo conversion)

## Data Structure

The project works with the following data directories:
- **`txt/`**: Raw text files (e.g., `2000.txt`)
- **`tagged/`**: Morphologically tagged text files in 2-column format (word + morpheme/tag pairs)
- **`freq/`**: Word frequency files (word + frequency count)
- **`index.pickle`**: Serialized inverted index and sentences for search

## Python Scripts Description

### Core Morphological Analysis

#### `get_morphs_tags.py`
Parses morphologically tagged text and extracts morpheme-tag pairs.

**Purpose**: Converts tagged format (`word/TAG+word/TAG`) into list of tuples.

**Usage**:
```bash
python get_morphs_tags.py <tagged-file>
```

**Input**: 2-column format file (word\ttagged_morphemes)
**Output**: Morpheme and tag pairs, one per line

---

#### `get_index_terms.py`
Extracts index terms (nouns and compound nouns) from morphologically analyzed text.

**Purpose**: Identifies indexable terms based on POS tags (NNG, NNP, SL, SN, SH).

**Usage**:
```bash
python get_index_terms.py <tagged-file>
```

**Input**: Tagged text file
**Output**: List of extracted index terms (nouns and compound nouns)

---

### Word Frequency Analysis

#### `word_frequency.py`
Counts word frequencies from a list of words.

**Purpose**: Generates word frequency statistics from input text.

**Usage**:
```bash
python word_frequency.py <word-list-file>
```

**Input**: File with one word per line
**Output**: Sorted list of words with their frequencies (word\tfrequency)

---

#### `word_frequency_by_year.py`
Analyzes word frequency trends across multiple years.

**Purpose**: Tracks how word frequencies change over time across different yearly datasets.

**Usage**:
```bash
python word_frequency_by_year.py <tagged-file-1> <tagged-file-2> ... <tagged-file-n>
```

**Input**: Multiple tagged files (one per year)
**Output**: Interactive search that shows frequency distribution by year for queried words

**Example**:
```bash
python word_frequency_by_year.py tagged/2000.tag tagged/2001.tag tagged/2002.tag
# Then enter search terms interactively
```

---

#### `merge_k_frequency.py`
Merges multiple sorted frequency files into a single sorted output.

**Purpose**: Efficiently combines frequency data from multiple sources using heap-based k-way merge.

**Usage**:
```bash
python merge_k_frequency.py <freq-file-1> <freq-file-2> ... <freq-file-n>
```

**Input**: Multiple sorted frequency files (2-column format: word\tfrequency)
**Output**: Merged frequency list with combined counts

---

### Indexing and Search

#### `indexer.py`
Builds an inverted index from tagged text files.

**Purpose**: Creates a searchable index mapping terms to sentences where they appear.

**Usage**:
```bash
python indexer.py <tagged-file-1> <tagged-file-2> ... <tagged-file-n>
```

**Input**: One or more tagged text files
**Output**: `index.pickle` file containing inverted index and sentence list

---

#### `search.py`
Performs concordance search using the inverted index.

**Purpose**: Searches for words in the indexed corpus and returns matching sentences with highlighting.

**Usage**:
```bash
python search.py
# Enter search query when prompted (use Ctrl+D to exit)
```

**Input**: 
- Reads from `index.pickle` (must run `indexer.py` first)
- Interactive query input (supports multi-word queries)

**Output**: HTML-formatted search results with highlighted query terms

---

### Korean Character Processing

#### `convert_jamo.py`
Converts between Hangul syllables and Jamo (Korean alphabet components).

**Purpose**: Advanced Jamo manipulation including composition and decomposition of Korean characters.

**Features**:
- Decomposes Hangul syllables into Jamo (초성/중성/종성)
- Converts Jamo to English keyboard input sequences
- Reconstructs Hangul syllables from Jamo
- Handles double consonants and vowels

**Usage**:
```bash
python convert_jamo.py < input.txt
```

**Input**: Korean text via stdin
**Output**: Multiple representations showing conversion steps

---

#### `convert_jamo_cp.py`
Similar to `convert_jamo.py` with enhanced composition/decomposition logic.

**Purpose**: Provides Jamo-to-syllable conversion with improved handling of complex Jamo combinations.

**Usage**:
```bash
python convert_jamo_cp.py < input.txt
```

---

#### `chosung_search.py`
Implements chosung (initial consonant) search functionality.

**Purpose**: Enables searching Korean words by their initial consonants only (e.g., "ㄱㅅ" to find "감사", "고속", etc.).

**Usage**:
```bash
python chosung_search.py <frequency-file>
# Enter chosung pattern when prompted
```

**Input**: Frequency file (word\tfrequency format)
**Output**: Interactive search showing words matching the chosung pattern, sorted by frequency

---

## Complete Workflow Example

Here's how to process Korean text data from start to finish:

### 1. Extract Index Terms from Tagged Files
```bash
python get_index_terms.py tagged/2000.tag > terms_2000.txt
```

### 2. Generate Word Frequencies
```bash
python word_frequency.py terms_2000.txt > freq/2000.freq
```

### 3. Merge Multiple Frequency Files
```bash
python merge_k_frequency.py freq/2000.freq freq/2001.freq freq/2002.freq > merged_freq.txt
```

### 4. Build Inverted Index
```bash
python indexer.py tagged/2000.tag tagged/2001.tag tagged/2002.tag
# Creates index.pickle
```

### 5. Search the Index
```bash
python search.py
# Enter search terms interactively
# Output is HTML format - redirect to file or view in browser
python search.py > results.html
```

### 6. Perform Chosung Search
```bash
python chosung_search.py merged_freq.txt
# Enter chosung pattern (e.g., "ㄱㅅ")
```

### 7. Analyze Temporal Trends
```bash
python word_frequency_by_year.py tagged/2000.tag tagged/2001.tag tagged/2002.tag
# Enter words to see frequency changes over years
```

### 8. Test Jamo Conversion
```bash
echo "한글" | python convert_jamo.py
# Shows conversion steps: syllable → jamo → keyboard → jamo → syllable
```

---

## Requirements

- Python 3.x
- Standard library modules only (no external dependencies)
  - `sys`, `pickle`, `heapq`

## Data Format Notes

### Tagged File Format (2-column)
```
word<TAB>morpheme/TAG+morpheme/TAG+...
```

### Frequency File Format
```
word<TAB>frequency
```

### POS Tags Used
- **NNG**: General noun
- **NNP**: Proper noun
- **SL**: Foreign language
- **SN**: Number
- **SH**: Chinese character
- **VV**: Verb
- **JX, JKB, JKO, etc.**: Various particles
- **SS**: Symbols
- **SF**: Sentence final marks

## Notes

- The project is designed for Korean natural language processing
- All scripts use UTF-8 encoding
- The `tagged/` directory contains pre-processed morphologically analyzed text
- Some scripts require interactive input (search.py, chosung_search.py, word_frequency_by_year.py)
- HTML output from search.py can be viewed in a web browser

## Project Structure Summary

```
python_programming/
├── Core Analysis Scripts
│   ├── get_morphs_tags.py       # Parse morpheme-tag pairs
│   ├── get_index_terms.py       # Extract indexable terms
│   └── word_frequency.py        # Count word frequencies
│
├── Advanced Analysis
│   ├── word_frequency_by_year.py # Temporal frequency analysis
│   └── merge_k_frequency.py      # Merge frequency files
│
├── Search and Indexing
│   ├── indexer.py               # Build inverted index
│   └── search.py                # Search indexed corpus
│
├── Korean Character Processing
│   ├── convert_jamo.py          # Jamo conversion utilities
│   ├── convert_jamo_cp.py       # Enhanced Jamo conversion
│   └── chosung_search.py        # Initial consonant search
│
└── Data Directories
    ├── txt/                     # Raw text files
    ├── tagged/                  # Morphologically tagged files
    ├── freq/                    # Frequency files
    └── index.pickle             # Serialized search index
```
