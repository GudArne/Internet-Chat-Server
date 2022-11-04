import java.net.*;
import java.io.*;
import java.awt.*;


public class Server extends Thread {
    private int userId = 0;

    public void run() {
        try {
            int serverPort = 2000;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            // serverSocket.setSoTimeout(10000); 
            while(true)
            {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 
    
                // Accept connection from client
                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                // Create a new thread for the client
                ClientHandler client = new ClientHandler(server, userId);
                
                synchronized(this) {
                    userId++;
                }
                client.start();
                client.run();
            }
        }
        catch(UnknownHostException ex) 
        {
            ex.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
      }

      static class ClientHandler extends Thread {
        private volatile Socket clientSocket;
        private volatile PrintWriter out;
        private volatile BufferedReader in;
        private volatile int userId;

        public ClientHandler(Socket socket, int userId) {
            this.clientSocket = socket;
            this.userId = userId;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) 
                {
                    System.out.println("User "+userId+" sent: " + inputLine);
                    break;
                }
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server srv = new Server();

		srv.run();
    }
}


