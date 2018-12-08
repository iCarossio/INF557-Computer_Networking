#!/usr/bin/python3

import socket
import sys

UDP_IP = "127.0.0.1"
UDP_PORT = 4242

sock = socket.socket(socket.AF_INET,  # Internet
                     socket.SOCK_DGRAM)  # UDP

message = ("HELLO;Test;"+sys.argv[1]+";255;0").encode('utf-8')

sock.sendto(message, (UDP_IP, UDP_PORT))

