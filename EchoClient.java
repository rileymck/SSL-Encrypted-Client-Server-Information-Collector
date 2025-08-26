import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class EchoClient {
    // simple flag to stop threads
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "clienttruststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 9999);

            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            // Thread: print whatever server sends
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                        // stop when server says goodbye
                        if (line.toLowerCase().contains("thank you")) {
                            running = false;
                            try { socket.close(); } catch (Exception ignored) {}
                            break;
                        }
                    }
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                } finally {
                    running = false;
                }
            }, "server-reader");
            readerThread.setDaemon(true);
            readerThread.start();

            // Main thread: read user input and send to server
            while (running) {
                String userLine = userIn.readLine();
                if (userLine == null) break; // EOF
                serverOut.println(userLine);
                // optional sensible stops
                if (userLine.equalsIgnoreCase("Bye!") || userLine.equalsIgnoreCase("quit")) {
                    break;
                }
            }

            running = false;
            try { socket.close(); } catch (Exception ignored) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
