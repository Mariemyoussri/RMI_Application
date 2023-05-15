package server;

import interfaces.GraphInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends Thread {

    public void run(){
        try {
            Graph graph = new Graph("/home/ubuntu/graph.txt");

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
