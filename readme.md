﻿


## ARDUINO ANDROID CONTROLLER

Μια android εφαρμογή η οποια συνδεεται μεσω ιντερνετ με ενα arduino και ανάλογα με την επιλογη του χρηστη στην εφαρμογή το arduino στέλνει τα καταλληλα δεδομενα και αυτα παρουσιαζονται στην εφαρμογη.


## ABOUT ARDUINO IO CONTROL
Το project μας αποτελείται απο 2 μερη. Το κομματι του arduino και το κομματι της android εφαρμογης.

### Arduino Schematic
![arduino schematic](http://www.cs.uoi.gr/~cs122312/arduino/img/Arduino_schematic.png)

Στο κομμάτι του arduino έχουμε συνδέσει πάνω ένα buzzer, ένα θερμόμετρο και έναν αισθητήρα φωτός και 2 λαμπάκια(κόκκινο και μπλε). Συνδέουμε το arduino με ethernet στο δίκτυο και αυτό ανοίγει έναν Server στην παρακάτω ip:port 192.168.1.105:8032. Έπειτα με μνήματα udp επικοινωνεί με την android εφαρμογή και απαντάει ανάλογα με το τι ρωτάμε στην εφαρμογή.

### Android App
Στο κομμάτι του android έχουμε αναπτύξει μια εφαρμογή η οποία έχει το παρακάτω GUI. Έναν διακόπτη on/off για το κόκκινο λαμπάκι. Ένα κουμπί για να λάβουμε πληροφορίες για την θερμοκρασία του χώρου, την ένταση του φωτός στο χώρο και το όριο που θέτουμε στο φως για να ανάβει η να σβήνει το μπλε φως. Παράδειγμα μόλις νυχτώσει ανάβει αυτόματα και την ημέρα σβήνει αυτόματα. Επίσης υπάρχει και εάν κουμπάκι για να ενεργοποιείται το buzzer. Τέλος δίνεται η δυνατότητα να ρυθμίσει το όριο στο οποίο θα σβήνει/ανάβει το μπλε φως. Καθώς επίσης έχουμε ενσωματώσει και φωνητικές εντολές για να χρησιμοποιεί κάνεις την εφαρμογη.

![App Gui](http://www.cs.uoi.gr/~cs122312/arduino/img/appGui1.png)


## Για να λειτουργήσει το προγραμμα πρέπει.
1. Συνδεουμε το arduino με ethernet στο τοπικό δίκτυο. ΠΡΟΣΟΧΗ το arduino
θα πάρει την ip:port >> 192.168.1.105:8032
2. Εγκαθιστούμε στο android smartphone μας το apk: arduinoControler.apk
3. Συνδεουμε το smartphone μας με wifi στο τοπικο δικτυο που ειναι και το arduino
4. Ανοιγουμε την εφαρμογή Arduino IO control που εγκαταστησαμε στο βημα 2
5. Ειμαστε ετοιμοι να χρησιμοποιησουμε την εφαρμογή μας.

### Πληρεις οδηγίες χρήσεις υπαρχουν [εδώ](ArduinoIOcontrolReport.pdf).
