#include "des.h"
#include <stdint.h>
#include <openssl/des.h>

// 64비트 정수를 바이트 배열로 변환
void uint64_to_bytes(uint64_t val, unsigned char* bytes) {
    for (int i = 0; i < 8; i++) {
        bytes[i] = (val >> (8 * (7 - i))) & 0xFF;
    }
}

// 바이트 배열을 64비트 정수로 변환
uint64_t bytes_to_uint64(unsigned char* bytes) {
    uint64_t val = 0;
    for (int i = 0; i < 8; i++) {
        val = (val << 8) | bytes[i];
    }
    return val;
}

// DES 암호화 함수
void DES_enc(DES_STATE_t * C, uint64_t plaintext, uint64_t key) {
    DES_key_schedule ks;
    DES_cblock key_block;
    DES_cblock input_block;
    DES_cblock output_block;

    // 64비트 정수 key를 8바이트로 변환
    uint64_to_bytes(key, key_block);
    
    // DES 키 스케줄 설정
    DES_set_key_unchecked(&key_block, &ks);
    
    // 64비트 정수 plaintext를 8바이트로 변환
    uint64_to_bytes(plaintext, input_block);
    
    // DES 암호화
    DES_ecb_encrypt(&input_block, &output_block, &ks, DES_ENCRYPT);

    // 8바이트 암호문을 64비트 정수로 변환
    *C =  bytes_to_uint64(output_block);
}

// DES 복호화 함수
void DES_dec(DES_STATE_t * P, uint64_t ciphertext, uint64_t key) {
    DES_key_schedule ks;
    DES_cblock key_block;
    DES_cblock input_block;
    DES_cblock output_block;

    // 64비트 정수 key를 8바이트로 변환
    uint64_to_bytes(key, key_block);
    
    // DES 키 스케줄 설정
    DES_set_key_unchecked(&key_block, &ks);
    
    // 64비트 정수 ciphertext를 8바이트로 변환
    uint64_to_bytes(ciphertext, input_block);
    
    // DES 복호화
    DES_ecb_encrypt(&input_block, &output_block, &ks, DES_DECRYPT);

    // 8바이트 평문을 64비트 정수로 변환
    *P = bytes_to_uint64(output_block);
}