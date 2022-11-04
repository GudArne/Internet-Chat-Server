import java.net.*;
import java.io.*;
import java.awt.*;


public class Server {
    private int userId = 0;

    public void run() {
        try {
            int serverPort = 2000;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            // serverSocket.setSoTimeout(10000); 
            while(true){
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 
    
                // Accept connection from client
                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress()); 
    
                // Send data to client
                PrintWriter toClient = new PrintWriter(server.getOutputStream(),true);
                // Receive data from client
                
                BufferedReader fromClient = new BufferedReader( new InputStreamReader(server.getInputStream()));
                userId++;
                
                // while(true){
                    String line = fromClient.readLine();
                    if(line != null)
                    {
                        System.out.println("User "+userId+" sent: " + line); 
                    } 
                // }
                    // toClient.println("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!"); 
            }
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
      }
    public static void main(String[] args) throws IOException {
        Server srv = new Server();
		srv.run();
    }
}


