import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private Set<Action> clients;

    private EchoServer(int port) {
        this.port = port;
        clients = new HashSet<>();
    }
    static EchoServer bindToPort(int port){
        return new EchoServer(port);
    }


    public void run() {
        try(ServerSocket server = new ServerSocket(port)){
            while (!server.isClosed()){
                Socket clientSocket = server.accept();
                var client = Action.connectClient(clientSocket, this);
                        if(client != null){
                            clients.add(client);
                            pool.submit(client);
                        }
                }
        } catch (IOException e){
            System.out.printf("Port %s is busy!%n", port);
            e.printStackTrace();
        }
    }

    public Set<Action> getClients() {
        return clients;
    }

    public void listOfClients(){

    }

}