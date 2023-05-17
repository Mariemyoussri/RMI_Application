import client.Client;
import server.Graph;

import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws RemoteException {
        Graph g = new Graph("graph.txt", false);
        String batch1 = "Q 1 3\nA 4 5\nQ 1 5\nQ 5 1\nF";
        String batch2 = "A 5 3\nQ 1 3\nD 2 3\nQ 1 3\nF";

        System.out.println("1\n=========\n"+g.computeBatch(0, batch2)+"\n===========\n");
        System.out.println("2\n========\n"+g.computeBatch(1, batch1));

        Client c = new Client(1, 4, 50, 50, 50, 50);
        c.run();
    }
}