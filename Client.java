import java.net.*;
import java.io.*;

public class Client{

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(Socket socket) throws IOException {
        try 
        {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } 
        catch (Exception e) 
        {
            closeClient(socket, out, in);
        }
    }

    private void closeClient(Socket socket, BufferedWriter writer, BufferedReader reader) 
    {
        try 
        {
            if (!socket.isClosed()) 
            {
                socket.close();
            }
            if (writer != null) 
            {
                writer.close();
            }
            {
                writer.close();
            }
            if (reader != null) 
            {
                reader.close();
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private void sendMessage()
    {
        try 
        {
            // Read from keyboard
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String line;

            // Send message to server
            while(!socket.isClosed()) 
            {
                line = consoleReader.readLine();

                // Leave chat if user types "exit"
                if(line.equals("exit")) 
                {
                    out.write(line);
                    out.newLine();
                    out.flush();
                    closeClient(socket, out, in);
                    System.exit(0);
                    break;
                }
                out.write(line);
                out.newLine();
                out.flush();
            }
        }
        catch(IOException e)
        {
            // Something went wrong -> close the client
            closeClient(socket, out, in);
        }
    }

    // Always listen for messages from the server in a separate thread
    private void listenForMessages()
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                String msgFromOthers;
                while(socket.isConnected())
                {
                    try 
                    {
                        msgFromOthers = in.readLine();

                        // Never print empty messages
                        if(msgFromOthers != null)
                        {
                            System.out.println(msgFromOthers);
                        }
                    } 
                    catch (IOException e) 
                    {
                        // Something went wrong -> close the client
                        closeClient(socket, out, in);
                    }
                }
                
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        int serverPort = 2000;
        InetAddress host = InetAddress.getByName("localhost"); 
        System.out.println("Connecting to server on port " + serverPort); 

        Socket socket = new Socket(host,serverPort); 
        Client client = new Client(socket);

		client.listenForMessages();
        client.sendMessage();
    }
}