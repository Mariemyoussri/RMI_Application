package server;

import interfaces.GraphInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

public class Graph implements GraphInterface {

    private final HashMap<Integer, HashSet<Integer>> graph;

    public Graph(String filePath) {
        this.graph = new HashMap<>();
        constructGraph(filePath);
    }

    private void constructGraph(String filePath) {
        BufferedReader reader = getReader(filePath);
        String line;
        while ((line = readLine(reader)) != null && !line.equals("S")) {
            String[] tokens = line.split(" ");
            int v = Integer.parseInt(tokens[0]);
            int w = Integer.parseInt(tokens[1]);
            addEdge(v, w);
        }
    }

    private void addEdge(int v1, int v2) {
        if (graph.get(v1) == null)
            graph.put(v1, new HashSet<Integer>());
        if (graph.get(v2) == null)
            graph.put(v2, new HashSet<Integer>());
        this.graph.get(v1).add(v2);
    }

    private void deleteEdge(int v1, int v2) {
        if(this.graph.containsKey(v1))
            this.graph.get(v1).remove(v2);
    }

    private int query(int src, int dest) {
        Queue<Integer> queue = new LinkedList<>();
        if (!graph.containsKey(src) || !graph.containsKey(dest)) return -1;
        if (src == dest) return 0;

        HashSet<Integer> visited = new HashSet<>();
        HashMap<Integer, Integer> dist = new HashMap<>();

        // initializing distances array between src and the node
        for (int i : graph.keySet()) {
            dist.put(i, -1);
        }
        dist.put(src, 0);
        visited.add(src);
        queue.add(src);

        while (!queue.isEmpty()) {
            int node = queue.remove();
            HashSet<Integer> neighbors = graph.get(node);
            for (int neighbor : neighbors) {
                if (visited.add(neighbor)) {
                    int distance = dist.get(node) + 1;
                    dist.put(neighbor, distance);
                    queue.add(neighbor);

                    if (neighbor == dest)
                        return dist.get(neighbor);
                }
            }
        }

        return dist.get(dest);
    }

    @Override
    public synchronized String computeBatch(String batch) throws RemoteException{
        String ans = "";
        String[] operations = batch.split("\n");
        for (String operation : operations) {
            if (operation.equals("F") || operation.equals("f"))
                break;
            String[] parts = operation.split(" ", 3);
            ans += performOperation(parts[0].charAt(0), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
        if (ans.endsWith("\n")) {
            ans = ans.substring(0, ans.length() - 1);
        }
        return ans;
    }

    private String performOperation(char op, int v1, int v2){
        switch (op){
            case 'A':
                addEdge(v1, v2);
                return "";
            case 'D':
                deleteEdge(v1, v2);
                return "";
            case 'Q':
                int ans = query(v1, v2);
                return ans + "\n";
        }
        return null;
    }

    public HashMap<Integer, HashSet<Integer>> getGraph() {
        return graph;
    }

    private BufferedReader getReader(String filePath) {
        try {
            return new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printGraph() {
        System.out.println("Printing the graph:");
        System.out.println("=========================================================================");
        for (Map.Entry<Integer, HashSet<Integer>> entry : graph.entrySet()) {
            System.out.printf("vertex: %d \n", entry.getKey());
            System.out.printf("neighbors: %s \n", entry.getValue().toString());
        }
        System.out.println("=========================================================================");
    }
}
