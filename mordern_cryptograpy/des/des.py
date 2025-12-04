from Crypto.Cipher import DES

def encrypt_des(plaintext_int, key_int):
    # 64비트 정수를 8바이트 big-endian 바이트로 변환합니다.
    plaintext_bytes = plaintext_int.to_bytes(8, byteorder='big')
    key_bytes = key_int.to_bytes(8, byteorder='big')
    # DES 암호 객체를 생성합니다.
    cipher = DES.new(key_bytes, DES.MODE_ECB)
    # 암호화를 수행합니다.
    ciphertext_bytes = cipher.encrypt(plaintext_bytes)
    # 결과를 64비트 정수로 변환합니다.
    ciphertext_int = int.from_bytes(ciphertext_bytes, byteorder='big')
    return ciphertext_int

def decrypt_des(ciphertext_int, key_int):
    # 64비트 정수를 8바이트 big-endian 바이트로 변환합니다.
    ciphertext_bytes = ciphertext_int.to_bytes(8, byteorder='big')
    key_bytes = key_int.to_bytes(8, byteorder='big')
    # DES 암호 객체를 생성합니다.
    cipher = DES.new(key_bytes, DES.MODE_ECB)
    # 복호화를 수행합니다.
    plaintext_bytes = cipher.decrypt(ciphertext_bytes)
    # 결과를 64비트 정수로 변환합니다.
    plaintext_int = int.from_bytes(plaintext_bytes, byteorder='big')
    return plaintext_int


if __name__ == "__main__":
# 사용 예시:
    plaintext_int = 0x0123456789abcdef  # 암호화할 64비트 정수
    key_int = 0x0       # 64비트 DES 키

    # 암호화
    ciphertext_int = encrypt_des(plaintext_int, key_int)
    print(f"암호문: 0x{ciphertext_int:016X}")

    # 복호화
    decrypted_int = decrypt_des(ciphertext_int, key_int)
    print(f"복호화된 평문: 0x{decrypted_int:016X}")
