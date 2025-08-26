import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.*;


//Notes
//generate the keystore "keytool -genkeypair -alias xyz -keyalg RSA -validity 60 -keystore 3750keystore -storetype pkcs12"
//view keystore contents "keytool -list -v -keystore 3750keystore"
//export certificate "keytool -export -alias xyz -keystore 3750keystore -rfc -file xyz.cer"
//view certificate "cat xyz.cer"
//then download and move to server b

//compile the code "javac InfoCollectionServer.java"
//run the server with the port number "java InfoCollectionServer 5120"

public class InfoCollectionServer {
    private static final AtomicInteger userIdGenerator = new AtomicInteger(1);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java InfoCollectionServer <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } 
        catch (NumberFormatException e) {
            System.err.println("Invalid port number.");
            return;
        }

        // Configure keystore properties
        System.setProperty("javax.net.ssl.keyStore", "testkeystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
	    System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        try {
            SSLServerSocketFactory sslServerSocketFactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket =
                    (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("Server is listening on port " + port);

            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                new Thread(() -> handleClient(sslSocket)).start();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(SSLSocket sslSocket) {
        try (
            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()))
        ) {
            String response;
            do {
                int userId = userIdGenerator.getAndIncrement();
    
                // 1) Ask for user name and create file
                out.println("User Name:");
                out.flush();
                System.out.println("[server] Sent prompt: User Name:");
                String userName = in.readLine();
                System.out.println("[server] Received: " + userName);
                String fileName = (userName == null || userName.isEmpty()) ? "user_" + userId + ".txt" : userName + ".txt";
                try (PrintWriter fileWriter = new PrintWriter(new FileWriter(fileName))) {
                    fileWriter.println("User Name: " + userName);
    
                    // Full Name
                    out.println("Full Name:");
                    out.flush();
                    System.out.println("[server] Sent prompt: Full Name:");
                    String fullName = in.readLine();
                    System.out.println("[server] Received: " + fullName);
                    fileWriter.println("Full Name: " + fullName);
    
                    // Address
                    out.println("Address:");
                    out.flush();
                    System.out.println("[server] Sent prompt: Address:");
                    String address = in.readLine();
                    System.out.println("[server] Received: " + address);
                    fileWriter.println("Address: " + address);
    
                    // Phone number
                    out.println("Phone number:");
                    out.flush();
                    System.out.println("[server] Sent prompt: Phone number:");
                    String phoneNumber = in.readLine();
                    System.out.println("[server] Received: " + phoneNumber);
                    fileWriter.println("Phone number: " + phoneNumber);
    
                    // Email address
                    out.println("Email address:");
                    out.flush();
                    System.out.println("[server] Sent prompt: Email address:");
                    String email = in.readLine();
                    System.out.println("[server] Received: " + email);
                    fileWriter.println("Email address: " + email);
                }
    
                out.println("Add more users? (yes or any for no)");
                out.flush();
                System.out.println("[server] Sent prompt: Add more users?");
                response = in.readLine();
                System.out.println("[server] Received: " + response);
            } while ("yes".equalsIgnoreCase(response));
    
            out.println("Thank you! Connection will now close.");
            out.flush();
            System.out.println("[server] Sent: Thank you! Connection will now close.");
            sslSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}    
