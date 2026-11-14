# 하드웨어 보안 · gem5 과제

컴퓨터 아키텍처 시뮬레이터 [gem5](https://github.com/gem5/gem5)를 사용해 작성하거나 수정한 과제 소스를 보관한다. 각 버전 폴더는 해당 gem5 소스 트리의 상대 경로를 따른다.

| 과제 | 보관한 코드 | 기준 버전 |
| --- | --- | --- |
| Lab 1 · L1 캐시 실험 | [C 실험 코드](v25.0.0.1/tests/test-progs/lab1) | [v25.0.0.1](https://github.com/gem5/gem5/tree/ddd4ae35adb0a3df1f1ba11e9a973a5c2f8c2944) |
| Lab 2 · 분기 예측기 실험 | [실험 프로그램과 Python 스크립트](v25.0.0.1/tests/test-progs/lab2), [수정한 시뮬레이션 설정](v25.0.0.1/configs/learning_gem5/part1/two_level.py) | v25.0.0.1 |
| Lab 3 · RISC-V 섀도 스택 | [ISA와 디코더 수정본](v23.0.0.1/src/arch/riscv), [과제 폴더의 설정·테스트 코드와 비교용 원본](v23.0.0.1/tests/lab3), [게스트 이미지에 보관했던 테스트용 C 소스](v23.0.0.1/guest-root/root/bof.c) | [v23.0.0.1](https://github.com/gem5/gem5/tree/af72b9ba580546ac12ce05bfaac3fd53fa8699f4) |

[VM용 설정 사본](vm-layout/riscv-fs.py)은 Lab 3 폴더의 동명 파일과 커널·디스크 경로가 다르다. 빌드 결과, 게스트 이미지, 실행 로그는 포함하지 않는다.
