package client;

import interfaces.GraphInterface;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import logger.Logger;

public class Client extends Thread {
    private final int BATCH_SIZE = 4;
    private final int READ_PERCENTAGE = 50;
    private final int WRITE_PERCENTAGE = 50;
    private final int ADD_PERCENTAGE = 50;
    private final int NUMBER_OF_VERTICES = 50;

    private int id;
    private int batchesCount = 0;

    public Client(int id){
        this.id = id;
        createFolders();
    }

    public void run(){
        GraphInterface graph = getGraph();
        Random random = new Random();
        while (true){
            try {
                String batch = generateRandomBatch();
                long start = System.currentTimeMillis();
                String response = graph.computeBatch(batch);
                long end = System.currentTimeMillis();
                long responseTime = end - start;

                logData(batch, response, responseTime);

                int secsToSleep = random.nextInt(11) * 1000;
                Thread.sleep(secsToSleep);
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }

    }

    private GraphInterface getGraph(){
        GraphInterface graph = null;
        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);

            // Looking up the registry for the remote object
            graph = (GraphInterface) registry.lookup("Update");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return graph;
    }

    private String generateRandomBatch(){
        int nOfReads = (int) (BATCH_SIZE * (READ_PERCENTAGE / 100.0));
        int nOfWrites = (int) (BATCH_SIZE * (WRITE_PERCENTAGE / 100.0));
        int nOfAddedReads = 0, nOfAddedWrites = 0;
        Random random = new Random();
        String batch = "";
        for(int i = 0; i < BATCH_SIZE; i++){
            int rand = random.nextInt(100);
            if((rand < WRITE_PERCENTAGE && nOfAddedWrites < nOfWrites) || nOfAddedReads == nOfReads){
                batch += getOperation(false);
                nOfAddedWrites++;
            }else{
                batch += getOperation(true);
                nOfAddedReads++;
            }
        }
        batch += "F";
        return batch;
    }

    private String getOperation(boolean op){
        Random random = new Random();
        int v1 = random.nextInt(NUMBER_OF_VERTICES);
        int v2 = random.nextInt(NUMBER_OF_VERTICES);
        // read operation
        if(op){
            return "Q "+ v1 + " " + v2 + "\n";
        }else{
            int rand = random.nextInt(100);
            String operation;
            if ( rand < ADD_PERCENTAGE )
                operation = "A ";
            else
                operation = "D ";
            return operation + v1 + " " + v2 + "\n";
        }
    }

    private void createFolders(){
        File directory = new File("Client "+ id);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File batchesDirectory = new File("Client "+ id + "/Batches");
        if (!batchesDirectory.exists()) {
            batchesDirectory.mkdirs();
        }

        File LogsDirectory = new File("Client "+ id + "/Logs");
        if (!LogsDirectory.exists()) {
            LogsDirectory.mkdirs();
        }

        File responseTimeFile = new File("Client "+ id + "/Response_times.txt");
        try {
            responseTimeFile.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    private void logData(String batch, String response, long responseTime){
        Logger.writeToFile("Client "+ id + "/Batches/Batch_" + batchesCount + ".txt", batch);
        String log = "Response:\n" + response + "\nResponse time =  " + responseTime;
        Logger.writeToFile("Client "+ id + "/Logs/Log_" + batchesCount + ".txt", log);
        String responseTimeLog = "Response time of batch_" + batchesCount + "  =  " + responseTime + "\n";
        Logger.writeToFile("Client "+ id + "/Response_times.txt", responseTimeLog);
        batchesCount++;
    }
}
