import java.io.*;
import java.net.Socket;
import java.util.*;

public class Action implements Runnable{


    private String name;
    private final Scanner sc;
    private final Writer writer;
    private final Socket socket;
    private boolean isConnect;
    private EchoServer echoServer;

    private Action(Socket socket, EchoServer echoServer) throws IOException {
        this.name = giveName();
        this.sc = getReader(socket);
        this.writer = getWriter(socket);
        this.socket = socket;
        this.isConnect = true;
        this.echoServer = echoServer;
    }
    @Override
    public void run() {
        System.out.printf("Client %s has connected!%n", name);
        try (socket; sc; writer) {
            sendResponse("Hi " + name);
            mailingList("Client " + name + " has connected");
            while (!socket.isClosed() && isConnect) {
                String message = sc.nextLine();
                if (isQuitMsg(message)){
                    sendResponse("Client " + name + " has disconnected");
                    break;
                } else if (isEmptyMsg(message)) {
                    mailingList("Message is empty");
                } else mailingList(name + " : " + message);
            }
        } catch (NoSuchElementException e) {
            mailingList("Client " + name + " is disconnected\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String giveName() {
   String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
   java.util.Random rand = new java.util.Random();
   Set<String> identifiers = new HashSet<>();
    StringBuilder builder = new StringBuilder();
    while(builder.toString().length() == 0) {
        int length = rand.nextInt(5)+5;
        for(int i = 0; i < length; i++) {
            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
        }
        if(identifiers.contains(builder.toString())) {
            builder = new StringBuilder();
        }
    }
    return builder.toString();
}

    public static Action connectClient(Socket socket, EchoServer server) {
        try {
            return new Action(socket, server);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isQuitMsg(String message) {
        return "bye".equals(message.toLowerCase());
    }

    private static boolean isEmptyMsg(String message) {
        return message == null || message.isBlank();
    }

     private void sendResponse(String response) throws IOException {
        this.writer.write(response);
        this.writer.write(System.lineSeparator());
        this.writer.flush();
    }

    private void mailingList(String response){
        echoServer.getClients().forEach(c -> {
            if (c != this) {
                try {
                    c.sendResponse(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
                });
    }

    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }

    private static Scanner getReader(Socket socket)throws IOException {
        InputStream stream = socket.getInputStream();
        InputStreamReader input = new InputStreamReader(stream,"UTF-8");
        return new Scanner(input);
    }
}
