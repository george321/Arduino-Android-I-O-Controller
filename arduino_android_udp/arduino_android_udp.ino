#include <SPI.h>         // needed for Arduino versions later than 0018
#include <Ethernet.h>
#include <EthernetUdp.h>         // UDP library from: bjoern@cs.stanford.edu 12/30/2008
#include <stdarg.h>
#include <stdio.h>
#include <string.h>

// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};

IPAddress ip(192, 168, 1, 105);
EthernetServer server(8032);

unsigned int localPort = 8032; // local port to listen on
boolean incoming = 0;
const int pResistor = A0;
int lightValue;
float tempC;
int reading;
int lm35Pin = 5;
const int buzzer = 9; //buzzer to arduino pin 9


// buffers for receiving and sending data
char packetBuffer[UDP_TX_PACKET_MAX_SIZE]; //buffer to hold incoming packet
char  ReplyBuffer[20];       // a string to send back
char  editedText[10];       // a string to send back
char PAULA = "-";
int light = 88;

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;

void setup() {
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);

  //Initalise sensors
  analogReference(INTERNAL);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(pResistor, INPUT);
  pinMode(buzzer, OUTPUT); // Set buzzer - pin 9 as an output
  Serial.begin(9600);
}

void loop() {

  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  checkLight();
  if (packetSize)
  {
    Serial.print("Received packet of size ");
    Serial.println(packetSize);
    Serial.print("From ");
    IPAddress remote = Udp.remoteIP();
    for (int i = 0; i < 4; i++)
    {
      Serial.print(remote[i], DEC);
      if (i < 3)
      {
        Serial.print(".");
      }
    }
    Serial.print(", port ");
    Serial.println(Udp.remotePort());
    for ( int i = 0; i < sizeof(UDP_TX_PACKET_MAX_SIZE);  ++i ) {
      packetBuffer[0] = (char)0;
    }
    Serial.println("cleaing packet buffer");
    Serial.println(packetBuffer);
    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    Serial.println("Contents:");
    Serial.println(packetBuffer);
    Serial.println("");

    if (packetBuffer[0] == '1') {
      digitalWrite(2, HIGH);
    } else if (packetBuffer[0] == '2') {
      digitalWrite(2, LOW);
    } else if (packetBuffer[0] == '3') {
      writeReply();
    } else if (packetBuffer[0] == '4') {
      tone(buzzer, 1000); // Send 1KHz sound signal...
      delay(900);        // ...for 1 sec
      noTone(buzzer);     // Stop sound...
    } else if (packetBuffer[0] == 's') {
      Serial.println("packet buffer");
      Serial.println(packetBuffer);
      Serial.println("editedText buffer");
      Serial.println(editedText);
      for ( int i = 0; i < sizeof(editedText);  ++i ) {
        editedText[0] = (char)0;
      }
      strcat(editedText, packetBuffer + 1);

      light = atoi(editedText);
    }



    delay(1);
  }
}

void writeReply() {
  //Get Light Value
  lightValue = analogRead(pResistor);
  String lightStr = String(lightValue);

  //Get temp Value
  reading = analogRead(lm35Pin);
  tempC = reading / 9.31;
  String tempSTR = String(tempC);

  //Get light limit Value
  String tempLightLimit = String(light);

  //Make packet
  String combinedString = lightStr + String("-") + tempSTR + String("-") + tempLightLimit;
  combinedString.toCharArray(ReplyBuffer, 20);

  // send a reply, to the IP address and port that sent us the packet we received
  Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
  Udp.write(ReplyBuffer);
  Udp.endPacket();
}

void checkLight() {
  lightValue = analogRead(pResistor);

  //You can change value "25"
  if (light == 0) {
    digitalWrite(3, LOW);  //Turn led off
  } else if (lightValue > light) {
    digitalWrite(3, LOW);  //Turn led off
  }
  else {
    digitalWrite(3, HIGH); //Turn led on
  }

  delay(10); //Small delay
}


