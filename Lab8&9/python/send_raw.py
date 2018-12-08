#!/usr/bin/python3

import socket

UDP_IP = "127.0.0.1"
UDP_PORT = 4242

sock = socket.socket(socket.AF_INET,  # Internet
                     socket.SOCK_DGRAM)  # UDP

while True:
    message = input().encode('utf-8')

    sock.sendto(message, (UDP_IP, UDP_PORT))

