package server;

import interfaces.GraphInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static logger.Logger.writeToFile;

public class Graph implements GraphInterface {

    private final HashMap<Integer, HashSet<Integer>> graph;
    private boolean useBFS;

    public Graph(String filePath, boolean useBFS) {
        this.graph = new HashMap<>();
        this.useBFS = useBFS;
        constructGraph(filePath);
    }

    private void constructGraph(String filePath) {
        writeToLog("Graph Construction");
        BufferedReader reader = getReader(filePath);
        String line;
        while ((line = readLine(reader)) != null && !line.equals("S")) {
            String[] tokens = line.split(" ");
            int v = Integer.parseInt(tokens[0]);
            int w = Integer.parseInt(tokens[1]);
            addEdge(v, w);
            writeToLog("A", v, w, 0);
        }
        writeToLog("------------------------------------------------------------------------------------------------");
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

    private int query(int src, int dest){
        if(useBFS)
            return BFSQuery(src, dest);
        else
            return djikstraQuery(src, dest);
    }

    private int BFSQuery(int src, int dest) {
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

    private int djikstraQuery(int src,int dest){
        int n = graph.size();
        if (!graph.containsKey(src) || !graph.containsKey(dest)) return -1;
        if (src == dest) return 0;

        HashSet<Integer> visited = new HashSet<>();
        HashMap<Integer, Integer> dist = new HashMap<>();

        // initializing distances array between src and the node
        for (int i : graph.keySet()) {
            dist.put(i, Integer.MAX_VALUE);
        }
        dist.put(src, 0);

        for (int i = 0; i < n - 1; i++) {
            // Find the vertex with the minimum distance from the source among the unvisited vertices
            int minVertex = -1;
            for (int j : dist.keySet()) {
                if (!visited.contains(j) && (minVertex == -1 || dist.get(j) < dist.get(minVertex))) {
                    minVertex = j;
                }
            }
            // Mark the minimum distance vertex as visited
            visited.add(minVertex);
            // Update the distances of the adjacent vertices of the minimum distance vertex
            HashSet<Integer> adjacentVertices = graph.get(minVertex);

            for (int adjacentVertex : adjacentVertices) {
                if (!visited.contains(adjacentVertex)) {
                    int newDistance = dist.get(minVertex) + 1; // Assuming unweighted graph
                    if (newDistance < dist.get(adjacentVertex)) {
                        dist.put(adjacentVertex,newDistance);
                    }
                }
            }
        }

        int distance = dist.get(dest);
        return distance == Integer.MAX_VALUE ? -1 : distance;
    }

    @Override
    public synchronized String computeBatch(int clientID, String batch) throws RemoteException{
        writeToLog("Client " + clientID);
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
        writeToLog("------------------------------------------------------------------------------------------------");
        return ans;
    }

    private String performOperation(char op, int v1, int v2){
        switch (op){
            case 'A':
                addEdge(v1, v2);
                writeToLog("A", v1, v2, 0);
                return "";
            case 'D':
                deleteEdge(v1, v2);
                writeToLog("D", v1, v2, 0);
                return "";
            case 'Q':
                int ans = query(v1, v2);
                writeToLog("Q", v1, v2, ans);
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

    private void writeToLog(String op, int v1, int v2, int result){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String toWrite = "";
        switch (op){
            case "A":
                toWrite += "Add edge " + v1 + " -> " + v2 + " at " + currentDateTime.format(formatter) + "\n";
                break;
            case "D":
                toWrite += "Remove edge between " + v1 + " and " + v2 + " at " + currentDateTime.format(formatter) + "\n";
                break;
            case "Q":
                toWrite += "Query for shortest path between " + v1 + " and " + v2 + " and the result is " + result + " at " + currentDateTime.format(formatter) + "\n";
                break;
        }
        writeToFile("Server_Log.txt", toWrite);
    }

    private void writeToLog(String title){
        writeToFile("Server_Log.txt", title + "\n");
    }
}
