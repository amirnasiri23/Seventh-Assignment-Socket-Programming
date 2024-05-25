package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> messages = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = bufferedReader.readLine();
            clientHandlers.add(this);
            ServerMessage(this.userName + " Has Entered The Chat");
            showAllMessagesForNewClient(this);
        }
        catch (IOException i) {
            closingFunc(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                ServerMessage(message);
            }
            catch (IOException i) {
                System.out.println(i);
                closingFunc(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void ServerMessage(String message) {
        messages.add(message);
        for (ClientHandler clientHandler : clientHandlers) {
            try {
//                if (!clientHandler.userName.equals(this.userName)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); ///
                }
//            }
            catch (IOException i) {
                closingFunc(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void deleteClientHandler() {
        clientHandlers.remove(this);
        ServerMessage(userName + " Has Left The Chat");
    }

    public void closingFunc(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        deleteClientHandler();
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
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    public void showAllMessagesForNewClient(ClientHandler clientHandler) throws IOException {
        for (String message : messages) {
            try {
                clientHandler.bufferedWriter.write(message);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            }
            catch (IOException i) {
                System.out.println(i);
                bufferedWriter.close();
            }
        }
    }
}
