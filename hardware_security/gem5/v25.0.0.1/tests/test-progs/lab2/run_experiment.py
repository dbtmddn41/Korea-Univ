#!/usr/bin/env python3
"""
Branch Predictor Experiment Script
localPredictorSize와 localCtrBits를 변경하며 gem5 시뮬레이션을 실행하고 결과를 수집합니다.
멀티프로세싱을 사용하여 여러 실험을 병렬로 실행합니다.
"""

import subprocess
import re
import os
import sys
import shutil
from datetime import datetime
from multiprocessing import Pool, cpu_count
import tempfile

# 실험 파라미터
localPredictorSizes = [1, 2, 4, 8, 16]
localCtrBits_values = [1, 2, 4, 8]

# 경로 설정
gem5_root = "/gem5"
gem5_binary = f"{gem5_root}/build/X86/gem5.opt"
config_script_template = f"{gem5_root}/configs/learning_gem5/part1/two_level.py"
test_dir = "/gem5/tests/test-progs/lab2"
binary_path = f"{test_dir}/a.out"

# 결과를 저장할 리스트
results = []

def create_temp_config(predictor_size, ctr_bits):
    """
    임시 설정 파일을 생성합니다 (멀티프로세싱 안전).
    """
    with open(config_script_template, 'r') as f:
        content = f.read()
    
    # 패턴 매칭으로 O3CPU 라인 찾아서 수정
    # 원본: system.cpu = O3CPU(branchPred = LocalBP(localPredictorSize=X, localCtrBits=Y))
    # 더 유연한 패턴: 공백과 = 주변 처리  
    pattern = r'system\.cpu\s*=\s*O3CPU\s*\(\s*branchPred\s*=\s*LocalBP\s*\([^)]*\)\s*\)'
    replacement = f'system.cpu = O3CPU(branchPred = LocalBP(localPredictorSize={predictor_size}, localCtrBits={ctr_bits}))'
    
    # 패턴이 매칭되는지 먼저 확인
    if not re.search(pattern, content):
        raise ValueError("Could not find O3CPU configuration line to modify")
    
    # 내용 치환 (같은 값으로 바뀔 수도 있음)
    modified_content = re.sub(pattern, replacement, content)
    
    # 임시 파일 생성 (원본과 같은 디렉토리에 생성하여 상대 import가 동작하도록)
    config_dir = os.path.dirname(config_script_template)
    temp_file = tempfile.NamedTemporaryFile(mode='w', suffix='.py', delete=False, dir=config_dir)
    temp_file.write(modified_content)
    temp_file.close()
    
    return temp_file.name

def run_simulation(config_file):
    """
    gem5 시뮬레이션을 실행하고 결과를 파싱합니다.
    """
    # 각 프로세스마다 고유한 출력 디렉토리 생성
    output_dir = tempfile.mkdtemp(prefix='gem5_', dir='/tmp')
    
    cmd = [gem5_binary, '--outdir', output_dir, config_file]
    
    try:
        result = subprocess.run(
            cmd,
            cwd=test_dir,
            capture_output=True,
            text=True,
            timeout=600  # 10분 타임아웃
        )
        
        # "Exiting @ tick XXXXX because ..." 패턴 찾기
        match = re.search(r'Exiting @ tick (\d+) because', result.stdout + result.stderr)
        
        if match:
            tick_count = int(match.group(1))
            return tick_count
        else:
            print("    Warning: Could not find tick count in output")
            print("    Output:", (result.stdout + result.stderr)[-500:])
            return None
            
    except subprocess.TimeoutExpired:
        print("    Error: Simulation timed out")
        return None
    except Exception as e:
        print(f"    Error running simulation: {e}")
        return None
    finally:
        # 임시 출력 디렉토리 정리
        try:
            shutil.rmtree(output_dir)
        except:
            pass

def run_single_experiment(params):
    """
    단일 실험을 실행합니다 (멀티프로세싱용 래퍼 함수).
    """
    predictor_size, ctr_bits, exp_num, total = params
    
    print(f"[Experiment {exp_num}/{total}] localPredictorSize={predictor_size}, localCtrBits={ctr_bits} - Starting...")
    
    temp_config = None
    try:
        # 임시 설정 파일 생성
        temp_config = create_temp_config(predictor_size, ctr_bits)
        
        # 시뮬레이션 실행
        tick_count = run_simulation(temp_config)
        
        result = {
            'predictor_size': predictor_size,
            'ctr_bits': ctr_bits,
            'tick_count': tick_count if tick_count is not None else 'ERROR'
        }
        
        if tick_count is not None:
            print(f"[Experiment {exp_num}/{total}] ✓ Complete: {tick_count:,} ticks")
        else:
            print(f"[Experiment {exp_num}/{total}] ✗ Failed")
        
        return result
        
    except Exception as e:
        print(f"[Experiment {exp_num}/{total}] ✗ Error: {e}")
        return {
            'predictor_size': predictor_size,
            'ctr_bits': ctr_bits,
            'tick_count': 'ERROR'
        }
    finally:
        # 임시 설정 파일 삭제 (반드시 삭제)
        if temp_config and os.path.exists(temp_config):
            try:
                os.unlink(temp_config)
            except Exception as e:
                print(f"    Warning: Failed to delete temp config {temp_config}: {e}")

def backup_original_config():
    """
    원본 설정 파일을 백업합니다.
    """
    backup_path = f"{config_script_template}.backup"
    if not os.path.exists(backup_path):
        shutil.copy2(config_script_template, backup_path)
        print(f"Original config backed up to {backup_path}")

def restore_original_config():
    """
    원본 설정 파일을 복원합니다.
    """
    backup_path = f"{config_script_template}.backup"
    if os.path.exists(backup_path):
        shutil.copy2(backup_path, config_script_template)
        print(f"\nOriginal config restored from {backup_path}")

def cleanup_temp_files():
    """
    남아있는 임시 파일들을 정리합니다.
    """
    config_dir = os.path.dirname(config_script_template)
    
    # 임시 설정 파일 정리
    temp_configs = [f for f in os.listdir(config_dir) if f.startswith('tmp') and f.endswith('.py')]
    for temp_file in temp_configs:
        try:
            os.unlink(os.path.join(config_dir, temp_file))
        except:
            pass
    
    # 임시 출력 디렉토리 정리
    import glob
    for temp_dir in glob.glob('/tmp/gem5_*'):
        try:
            shutil.rmtree(temp_dir)
        except:
            pass
    
    if temp_configs or glob.glob('/tmp/gem5_*'):
        print(f"Cleaned up {len(temp_configs)} temp config files and temp output directories")

def main():
    print("=" * 70)
    print("Branch Predictor Parameter Experiment (with Multiprocessing)")
    print("=" * 70)
    print(f"Start time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # CPU 코어 수 확인
    num_cores = cpu_count()
    num_workers = num_cores#min(num_cores, 4)  # 최대 4개의 워커 사용 (메모리 고려)
    print(f"Available CPU cores: {num_cores}")
    print(f"Using {num_workers} parallel workers")
    print()
    
    # 원본 백업 (필요시)
    backup_original_config()
    
    # 실험 파라미터 생성
    experiments = []
    experiment_count = 0
    
    for predictor_size in localPredictorSizes:
        for ctr_bits in localCtrBits_values:
            # localPredictorSize >= localCtrBits 조건 확인
            if predictor_size >= ctr_bits:
                experiment_count += 1
                experiments.append((predictor_size, ctr_bits, experiment_count, 0))
    
    # total 값 업데이트
    total_experiments = len(experiments)
    experiments = [(ps, cb, num, total_experiments) for ps, cb, num, _ in experiments]
    
    print(f"Total experiments to run: {total_experiments}")
    print()
    
    try:
        # 멀티프로세싱으로 실험 실행
        with Pool(processes=num_workers) as pool:
            results_list = pool.map(run_single_experiment, experiments)
        
        # 결과를 predictor_size, ctr_bits 순으로 정렬
        results_list.sort(key=lambda x: (x['predictor_size'], x['ctr_bits']))
    
    finally:
        # 임시 파일 정리
        cleanup_temp_files()
    
    # 결과 출력
    print()
    print("=" * 70)
    print("EXPERIMENT RESULTS")
    print("=" * 70)
    print()
    
    # 테이블 형식으로 출력
    print("localPredictorSize | localCtrBits | Tick Count")
    print("-" * 70)
    for r in results_list:
        if isinstance(r['tick_count'], int):
            print(f"{r['predictor_size']:18d} | {r['ctr_bits']:12d} | {r['tick_count']:,}")
        else:
            print(f"{r['predictor_size']:18d} | {r['ctr_bits']:12d} | {r['tick_count']}")
    
    print()
    
    # CSV 파일로 저장
    csv_path = f"{test_dir}/experiment_results.csv"
    with open(csv_path, 'w') as f:
        f.write("localPredictorSize,localCtrBits,tick_count\n")
        for r in results_list:
            f.write(f"{r['predictor_size']},{r['ctr_bits']},{r['tick_count']}\n")
    
    print(f"Results saved to: {csv_path}")
    
    # 보고서 생성
    generate_report(results_list)
    
    print()
    print(f"End time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)

def generate_report(results):
    """
    실험 결과를 바탕으로 보고서를 생성합니다.
    """
    report_path = f"{test_dir}/experiment_report.md"
    
    with open(report_path, 'w') as f:
        f.write("# Branch Predictor 실험 보고서\n\n")
        f.write(f"**실험 날짜**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
        
        f.write("## 1. 실험 개요\n\n")
        f.write("### 실험 목적\n")
        f.write("LocalBP(Local Branch Predictor)의 두 가지 파라미터인 `localPredictorSize`와 `localCtrBits`를 변경하면서 ")
        f.write("프로그램 실행 시간(tick count)에 미치는 영향을 분석합니다.\n\n")
        
        f.write("### 실험 대상 프로그램\n")
        f.write("- **파일**: `base.c`\n")
        f.write("- **내용**: 100x100 행렬 곱셈 연산\n")
        f.write("- **특징**: 중첩된 루프 구조로 분기 예측 성능이 중요\n\n")
        
        f.write("### 실험 파라미터\n")
        f.write("- **localPredictorSize**: [1, 2, 4, 8, 16]\n")
        f.write("- **localCtrBits**: [1, 2, 4, 8]\n")
        f.write("- **제약 조건**: localPredictorSize ≥ localCtrBits\n\n")
        
        f.write("## 2. 실험 결과\n\n")
        f.write("### 결과 테이블\n\n")
        f.write("| localPredictorSize | localCtrBits | Tick Count | 상대 성능 (%) |\n")
        f.write("|:------------------:|:------------:|:----------:|:-------------:|\n")
        
        # 최소값 찾기 (상대 성능 계산용)
        valid_results = [r for r in results if isinstance(r['tick_count'], int)]
        if valid_results:
            min_ticks = min(r['tick_count'] for r in valid_results)
            
            for r in results:
                if isinstance(r['tick_count'], int):
                    relative_perf = (r['tick_count'] / min_ticks) * 100
                    f.write(f"| {r['predictor_size']:18d} | {r['ctr_bits']:12d} | {r['tick_count']:10,d} | {relative_perf:6.2f} |\n")
                else:
                    f.write(f"| {r['predictor_size']:18d} | {r['ctr_bits']:12d} | {r['tick_count']:>10} | N/A |\n")
        
        f.write("\n> **상대 성능**: 가장 빠른 설정 대비 백분율 (100%에 가까울수록 빠름)\n\n")
        
        f.write("## 3. 분석\n\n")
        
        if valid_results:
            best_result = min(valid_results, key=lambda x: x['tick_count'])
            worst_result = max(valid_results, key=lambda x: x['tick_count'])
            
            f.write("### 최고 성능 설정\n")
            f.write(f"- **localPredictorSize**: {best_result['predictor_size']}\n")
            f.write(f"- **localCtrBits**: {best_result['ctr_bits']}\n")
            f.write(f"- **Tick Count**: {best_result['tick_count']:,}\n\n")
            
            f.write("### 최저 성능 설정\n")
            f.write(f"- **localPredictorSize**: {worst_result['predictor_size']}\n")
            f.write(f"- **localCtrBits**: {worst_result['ctr_bits']}\n")
            f.write(f"- **Tick Count**: {worst_result['tick_count']:,}\n\n")
            
            improvement = ((worst_result['tick_count'] - best_result['tick_count']) / 
                          worst_result['tick_count'] * 100)
            f.write(f"### 성능 개선\n")
            f.write(f"최적 설정 사용 시 최악 설정 대비 **{improvement:.2f}%** 성능 향상\n\n")
        
        f.write("### localPredictorSize의 영향\n")
        f.write("- **정의**: 분기 히스토리 테이블의 크기 (엔트리 수)\n")
        f.write("- **영향**: 크기가 클수록 더 많은 분기를 추적할 수 있어 예측 정확도가 향상될 수 있습니다.\n")
        f.write("- **트레이드오프**: 크기가 클수록 하드웨어 비용이 증가합니다.\n\n")
        
        f.write("### localCtrBits의 영향\n")
        f.write("- **정의**: 각 예측기 카운터의 비트 수\n")
        f.write("- **영향**: 비트 수가 많을수록 더 강한 예측을 할 수 있어 잡음에 강합니다.\n")
        f.write("- **트레이드오프**: 비트 수가 많을수록 하드웨어 비용이 증가하고, 적응 속도가 느려질 수 있습니다.\n\n")
        
        f.write("## 4. 결론\n\n")
        f.write("이 실험을 통해 분기 예측기의 파라미터가 프로그램 성능에 미치는 영향을 확인할 수 있습니다. ")
        f.write("적절한 predictor size와 counter bits의 조합을 선택하는 것이 중요하며, ")
        f.write("프로그램의 특성(분기 패턴)에 따라 최적의 설정이 달라질 수 있습니다.\n\n")
        
        f.write("## 5. 참고사항\n\n")
        f.write("- **시뮬레이터**: gem5 (X86 ISA)\n")
        f.write("- **CPU 모델**: O3CPU (Out-of-Order)\n")
        f.write("- **분기 예측기**: LocalBP\n")
        f.write("- **캐시 구성**: L1 I/D cache + L2 cache\n")
    
    print(f"Report saved to: {report_path}")

if __name__ == "__main__":
    main()
