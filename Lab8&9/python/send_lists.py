#!/usr/bin/python3

import socket
import sys

# Params: seqnum [nbLists]

# The sample database full_database is used as testing reference

UDP_IP = "127.0.0.1"
UDP_PORT = 4242

database = []

with open("full_database") as fichier:  
    database = fichier.read().split("\n")

nbMessages = len(database)

if len(sys.argv) >= 3:
    nbMessages = min(nbMessages, int(sys.argv[2]))

sock = socket.socket(socket.AF_INET,  # Internet
                     socket.SOCK_DGRAM)  # UDP

if nbMessages == 0:
    message = "LIST;Test;Flavien;"+sys.argv[1]+";"+str(nbMessages)+";"+str(0)+";;"

    sock.sendto(message.encode('utf-8'), (UDP_IP, UDP_PORT))

else:
    for i in range(nbMessages):
        message = "LIST;Test;Flavien;"+sys.argv[1]+";"+str(nbMessages)+";"+str(i)+";"+database[i]+";"

        sock.sendto(message.encode('utf-8'), (UDP_IP, UDP_PORT))

