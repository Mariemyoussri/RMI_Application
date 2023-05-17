package server;

import interfaces.GraphInterface;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends Thread {
    private boolean useBFS;

    public Server(boolean useBFS){
        this.useBFS = useBFS;
        File serverLog = new File("Server_Log.txt");
        try {
            serverLog.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    public void run(){
        try {
            Graph graph = new Graph("graph.txt", useBFS);

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            GraphInterface g = (GraphInterface) UnicastRemoteObject.exportObject(graph, 0);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.bind("Update", g);
            System.out.println("R");
        } catch (Exception e) {
            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
