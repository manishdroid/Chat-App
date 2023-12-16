package models;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private static final ArrayList<ClientManager> CLIENT_MANAGERS = new ArrayList<>();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final String clientUserName;

    public ClientManager(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientUserName = bufferedReader.readLine();
        CLIENT_MANAGERS.add(this);
        broadcastMessage("SERVER: " + clientUserName + " is Online");
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) {
                    // Client has disconnected
                    break;
                }

                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                // Client has disconnected or there was an error
                break;
            }
        }

        // Client has disconnected
        closeEverything();
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientManager clientManager : CLIENT_MANAGERS) {
            if (!clientManager.clientUserName.equals(clientUserName)) {
                try {
                    clientManager.bufferedWriter.write(messageToSend);
                    clientManager.bufferedWriter.newLine();
                    clientManager.bufferedWriter.flush();
                } catch (IOException e) {
                    // Client has disconnected or there was an error
                }
            }
        }
    }

    public void removeClientManager() {
        CLIENT_MANAGERS.remove(this);
        broadcastMessage("SERVER: " + clientUserName + ": has left the chat! ");
    }

    public void closeEverything() {
        removeClientManager();

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

