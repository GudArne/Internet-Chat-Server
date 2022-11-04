import java.net.*;
import java.io.*;
import java.awt.*;

public class Client{

    public void run() {
        try {
            int serverPort = 2000;
            InetAddress host = InetAddress.getByName("localhost"); 
            System.out.println("Connecting to server on port " + serverPort); 
    
            Socket socket = new Socket(host,serverPort); 

            // Connection established
            System.out.println("Just connected to " + socket.getRemoteSocketAddress()); 

            // Send data to server
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);

            // Receive data from server
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read from keyboard
            BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in));
    
            String line = "";
            while(line != "exit") {
                System.out.print("Send a message: ");
                line = consoleReader.readLine();
                toServer.println(line);
            }


            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
      }
    public static void main(String[] args) throws IOException {
        Client client = new Client();
		client.run();
    }
}