import socket
import time
import subprocess
import sys
import os
import signal

def wait_for_port(port, timeout=300):
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            with socket.create_connection(("localhost", port), timeout=1):
                return True
        except (ConnectionRefusedError, OSError):
            time.sleep(1)
    return False

def read_until(sock, pattern, timeout=600):
    buffer = b""
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            chunk = sock.recv(1024)
            if not chunk:
                break
            buffer += chunk
            sys.stdout.write(chunk.decode(errors='ignore'))
            sys.stdout.flush()
            if pattern.encode() in buffer:
                return True
        except socket.timeout:
            continue
    return False

def main():
    # Start gem5
    gem5_cmd = ["../../build/RISCV/gem5.opt", "--listener-mode=on", "riscv-fs.py"]
    print(f"Starting gem5: {' '.join(gem5_cmd)}")
    
    # Open output file
    with open("output.log", "w") as outfile:
        process = subprocess.Popen(
            gem5_cmd,
            stdout=outfile,
            stderr=subprocess.STDOUT,
            cwd="/gem5/gem5-23/tests/lab3",
            preexec_fn=os.setsid
        )

    try:
        print("Waiting for m5term port 3456...")
        if not wait_for_port(3456):
            print("Timeout waiting for port 3456")
            return

        print("Connecting to m5term...")
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(("localhost", 3456))
        s.settimeout(1.0)

        print("Waiting for login prompt...")
        time.sleep(5) 
        s.sendall(b"\n")
        
        if read_until(s, "login:", timeout=600):
            print("Found login prompt!")
            s.sendall(b"root\n")
            time.sleep(1)
            s.sendall(b"root\n")
            time.sleep(1)
            
            read_until(s, "#", timeout=30)
            print("Logged in. Disabling ASLR and running bof...")
            
            s.sendall(b"echo 0 > /proc/sys/kernel/randomize_va_space\n")
            time.sleep(1)

            s.sendall(b"./bof\n")
            
            time.sleep(5)
            print("bof executed. Exiting...")
        else:
            print("Login prompt not found (timeout).")

    finally:
        print("Terminating gem5...")
        os.killpg(os.getpgid(process.pid), signal.SIGTERM)
        process.wait()

if __name__ == "__main__":
    main()
