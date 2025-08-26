#  SSL-Encrypted Client-Server Information Collector

It uses the SSL/TLS protocol to establish a private, encrypted channel for data exchange between the client and server. The primary function is to collect user data, such as contact information, from a client and write it to 
a file on the server's machine, all while ensuring the communication is protected from eavesdropping or tampering. The system showcases fundamental concepts of secure networking, including the use of digital certificates, keystores, 
and truststores to authenticate the server and guarantee data integrity.


## Contributors

- Riley McKenzie

## Description

InfoCollectionServer:

-  Uses a secure socket layer (SSL) to create an encrypted connection with the client
-  Prompts the user for personal information such as their name, address, and phone number, and then writes this data to a text file
-  The server is designed to handle multiple clients concurrently by using multithreading

EchoClient:

- It establishes a secure connection to the server and handles user input
-  It sends the user's responses to the server and displays the server's prompts in the console

## Installation

### Prerequisites
Before you begin, ensure you have Java 8+ installed on your computer

### 1. Clone the Repository
```bash
git clone https://github.com/rileymck/BabyTEA.git

```

### 2. Generate the server's keystore
``` bash
keytool -genkeypair -alias serveralias -keyalg RSA -validity 365 -keystore testkeystore.p12 -storepass password -keypass password -storetype PKCS12
```

### 3. Export the server's certificate
``` bash
keytool -export -alias serveralias -file server.cer -keystore testkeystore.p12 -storepass password
```

### 4. Create the client's truststore
``` bash 
keytool -import -trustcacerts -alias serveralias -file server.cer -keystore clienttruststore.p12 -storepass password -storetype PKCS12
```

### 5. Compile InfoCollectionServer
``` bash 
javac InfoCollectionServer.java
```

### 6. Compile EchoClient
``` bash 
javac EchoClient.java
```

### 7. Run InfoCollectionServer
``` bash 
java InfoCollectionServer 9999
```

### 8. Run EchoClient
``` bash 
java EchoClient
```

