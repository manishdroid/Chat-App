package models;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A client is online");
                ClientManager clientManager = new ClientManager(socket);

                Thread thread = new Thread(clientManager);
                thread.start();
            }
        } catch (IOException e){
            {
            throw new RuntimeException(e);
        }


    }
}
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1209);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
