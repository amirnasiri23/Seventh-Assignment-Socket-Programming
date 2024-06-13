package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client Class
public class Client {
    // TODO: Implement the client-side operations
    // TODO: Add constructor and necessary methods

    private String[] files = {"all-of-me-john-legend", "a-man-without-love-ngelbert-Hmperdinck", "birds-imagine-dragons",
            "blinding-lights-the-weekend", "dont-matter-to-me-drake", "feeling-in-my-body-elvis",
            "out-of-time-the-weekend", "something-in-the-way-nirvana", "why-you-wanna-trip-on-me-michael-jackson",
            "you-put-a-spell-on-me-austin-giorgio"};
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = name;
        }
        catch (IOException i) {
            closingFunc(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        try {
            bufferedWriter.write(this.userName);
            bufferedWriter.newLine();
            bufferedWriter.flush(); ///

            while (socket.isConnected()) {
                String message = scanner.nextLine();
                if (!message.startsWith("Download") && !isMessageInFiles(message)) {
                    bufferedWriter.write("@" + this.userName + " %$#--->: " + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (message.equals("Download")) {
                    for (String item : files) {
                        System.out.println(item + "\n");
                    }
                }
                else {
                    // now message is the file name

                    try {
                        String data = null;
                        String sourcePath = "src/main/java/Server/data" + "/" + message + ".txt";
                        String goalPath = "src/main/java/Client";

                        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(goalPath));

                        while ((data = reader.readLine()) != null) {
                            writer.write(data);
                            writer.newLine();
                        }

                        writer.close();
                        reader.close();
                    }
                    catch (IOException i) {
                        System.out.println(i);
                    }
                }
            }
        }
        catch (IOException i) {
            closingFunc(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;

                while (socket.isConnected()) {
                    try {
                        messageFromChat = bufferedReader.readLine();
                        System.out.println(messageFromChat);
                    }
                    catch (IOException i) {
                        closingFunc(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closingFunc(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    private boolean isMessageInFiles(String message) {
        for (String item : files) {
            if (item.equals(message)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws IOException {
        // TODO: Implement the main method to start the client

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Your UserName %$#--->: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("localHost", 3000);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}