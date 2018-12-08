# TD 7 and 8

### Antoine Carossio, Flavien Solt, X2016

##### Design decision:

LIST part numbers range from 0 to nbParts-1.
To inform that the database is empty, just send a single LIST message with a zero nbParts.

##### Testing:

A python folder is here to help you run tests.

Usage:
    send_hello seqNb
    send_lists seqNb [nbElements] # else will send the full sample database. nbElements >= 0.
    send_raw # Will prompt raw messages in the console