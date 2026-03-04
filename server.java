import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

public class server implements Runnable {
    private ServerSocket serverSocket = null;
    private RecordRepository repo= null;
    private static int numConnectedClients = 0;

    public server(ServerSocket ss, RecordRepository repository) throws IOException {
        serverSocket = ss;
        repo = repository;
        newListener();
    }

    public void run() {
        try {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            Certificate[] cert = session.getPeerCertificates();
            String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
            
            //String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName(); // Added in question 4
            //String serial = ((X509Certificate) cert[0]).getSerialNumber().toString(); // Added in question 5
            numConnectedClients++;
            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            //System.out.println("Issuer: " + issuer); // Added in question 4
            //System.out.println("Serial: " + serial); // Added in question 5
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
            

            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            while ((clientMsg = in.readLine()) != null) {
                //clientMsg = clientMsg.toUpperCase();
                Request req = RequestParsing.parse(clientMsg);
                String requestResponse = repo.handleRequest(subject.substring(3), req);
                out.println(requestResponse);
                String logMessage = LocalDateTime.now().toString() + "---" + subject.substring(3) + " --- " + req.action().name() + " " + req.information() + " --- " + 
                requestResponse;

                ReadSavedFiles.log(logMessage);

                //System.out.println(logMessage);

                // String rev = new StringBuilder(clientMsg).reverse().toString();
                // System.out.println("received '" + clientMsg + "' from client");
                // System.out.print("sending '" + rev + "' to client...");
                //out.println(rev);
                out.flush();
                System.out.println("done\n");
            }
            
            in.close();
            out.close();
            socket.close();
            numConnectedClients--;
            System.out.println("client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
        } catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private void newListener() {
        (new Thread(this)).start();
    } // calls run()

    public static void main(String args[]) {
        Set<MedicalRecord> medicalRecords = ReadSavedFiles.readSavedRecords("records.csv");
        Set<User> users = ReadSavedFiles.readSavedUsers("users.csv");
        RecordRepository repo = new RecordRepository(medicalRecords, users);
        
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLSv1.2";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port, 0, InetAddress.getByName(null));
            ((SSLServerSocket) ss).setNeedClientAuth(true); // enables client authentication
            new server(ss, repo);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
        new Thread(() -> {
            try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

                String line;
                while ((line = console.readLine()) != null) {
                    if (line.equalsIgnoreCase("save")) {
                        System.out.println("Saving data...");
                        ReadSavedFiles.writeRecords(repo.getAllRecords());
                        System.out.println("All records saved.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLSv1.2")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLSv1.2");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();
                // keystore password (storepass)
                ks.load(new FileInputStream("serverkeystore"), password);
                // truststore password (storepass)
                ts.load(new FileInputStream("servertruststore"), password);
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts); // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
