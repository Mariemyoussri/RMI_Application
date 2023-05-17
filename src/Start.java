import client.Client;
import server.Server;

import java.io.*;
import java.util.Properties;

public class Start {

    public static void main(String[] args) {

        // Read properties
        Properties properties = getProperties();
        boolean use_BFS = Boolean.parseBoolean(properties.getProperty("USE_BFS"));
        // start the server
        Server server = new Server(use_BFS);
        server.start();
        waitForServerToStart();

        int batchSize = Integer.parseInt(properties.getProperty("BATCH_SIZE"));
        int readPercentage = Integer.parseInt(properties.getProperty("READ_PERCENTAGE"));
        int writePercentage = Integer.parseInt(properties.getProperty("WRITE_PERCENTAGE"));
        int addPercentage = Integer.parseInt(properties.getProperty("ADD_PERCENTAGE"));
        int numberOfVertices = Integer.parseInt(properties.getProperty("NUMBER_OF_VERTICES"));
        int numberOfClientNodes = Integer.parseInt(properties.getProperty("NUMBER_OF_CLIENT_NODES"));



//        String batch1 = "Q 1 3\nA 4 5\nQ 1 5\nQ 5 1\nF";
//        String batch2 = "A 5 3\nQ 1 3\nD 2 3\nQ 1 3\nF";
        //  start clients
        Client[] clients = new Client[numberOfClientNodes];
        for(int i = 0; i < numberOfClientNodes; i++){
            clients[i] = new Client(i, batchSize, readPercentage, writePercentage, addPercentage, numberOfVertices);
            clients[i].start();
        }
//
//        Client client1 = new Client(1, batchSize, readPercentage, writePercentage, addPercentage, numberOfVertices);
//        Client client2 = new Client(2, batchSize, readPercentage, writePercentage, addPercentage, numberOfVertices);
//        Client client2 = new Client();
//        Client client3 = new Client();


        try {
            Thread.sleep(3000); // sleep for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        client.start();
//        client1.start();
//        try {
//            Thread.sleep(3000); // sleep for 5 seconds
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        client2.start();
//        client3.start();

        try {
//            client.join();
//            client1.join();
//            client2.join();
//            client3.join();
            for(Client client : clients)
                client.join();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("system.properties")) {
            properties.load(fis);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void waitForServerToStart(){
        // Create a ByteArrayOutputStream to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create a custom PrintStream that writes to the ByteArrayOutputStream
        PrintStream interceptor = new PrintStream(outputStream);

        // Redirect the standard output to the interceptor
        PrintStream originalOut = System.out;
        System.setOut(interceptor);
        boolean foundOutput = false;
        while (!foundOutput){
            String capturedOutput = outputStream.toString();
            if (capturedOutput.contains("R")) {
                foundOutput = true;
            } else {
                // Sleep for a short duration before checking again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.setOut(originalOut);
        System.out.println("R\n");
    }
}
