import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server {
    private int userId = 0;
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) 
    {
        this.serverSocket = serverSocket;
    }

    public void run() 
    {
        try 
        {            
            while(!serverSocket.isClosed())
            {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 
    
                // Accept connection from client
                Socket server = serverSocket.accept();
                System.out.println("User " +userId+ " just connected to " + server.getRemoteSocketAddress());

                // Create a new thread for the client
                ClientHandler client = new ClientHandler(server, userId);
                userId++;

                // Start the thread for the client
                Thread t = new Thread(client);
                t.start();
            }
        }
        catch(UnknownHostException ex) 
        {
            ex.printStackTrace();
        } catch (IOException e) {
            // Something went wrong with the connection -> close the server
            closeServerSocket();
        }

    }

    // Close the server socket
    private void closeServerSocket()
    {
        try 
        {
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable 
    {
        public static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
        volatile Socket clientSocket;
        private BufferedWriter out;
        private BufferedReader in;
        private int userId;

        public ClientHandler(Socket socket, int userId) 
        {
            try 
            {
                this.clientSocket = socket;
                this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.userId = userId;
                clients.add(this);

                notifyClients("User " + userId + " has joined the chat");

            } 
            catch (Exception e) 
            {
                // Something went wrong with the connection -> close the clienet
                closeClient(socket, out, in);
            }
            this.userId = userId;
        }

        private void closeClient(Socket socket, BufferedWriter writer, BufferedReader reader) 
        {
            notifyClients("User " + userId + " has left the chat");

            System.out.println("We are in closeClient and the user id is " + userId);
            
            try 
            {
                if(clients.contains(this))
                {
                    clients.remove(this);
                }
                if (socket != null) 
                {
                    socket.close();
                }
                if (writer != null) 
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

        // Broadcast message to all clients except the sender
        private void notifyClients(String message) 
        {
            System.out.println(message);

            for (ClientHandler client : clients) 
            {
                if(client != null)
                {
                    try 
                    {
                        if(client.userId != userId)
                        {
                            client.out.write(message);
                            client.out.newLine();
                            client.out.flush();
                        }
                    } 
                    catch (Exception e) 
                    {
                        closeClient(clientSocket, out, in);
                    }
                }
            }
        }

        @Override
        public void run() 
        {
            String message;
            while(clientSocket.isConnected())
            {
                try 
                {
                    message = in.readLine();

                    if(message.equals("exit"))
                    {
                        closeClient(clientSocket, out, in);
                        break;
                    }

                    // Never send empty messages
                    if(message != null)
                    {
                        notifyClients("User " + userId + " sent: " + message);
                    }
                } 
                catch (IOException e) 
                {
                    // Something went wrong with the connection -> close the client
                    closeClient(clientSocket, out, in);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException 
    {
        Server server = new Server(new ServerSocket(2000));

		server.run();
    }
}


