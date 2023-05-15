import client.Client;
import server.Server;

public class Start {

    public static void main(String[] args){

        Server server = new Server();
//
//        String batch1 = "Q 1 3\nA 4 5\nQ 1 5\nQ 5 1\nF";
//        String batch2 = "A 5 3\nQ 1 3\nD 2 3\nQ 1 3\nF";
//
        Client client1 = new Client(1);
        Client client2 = new Client(2);
//        Client client2 = new Client();
//        Client client3 = new Client();

        server.start();

        try {
            Thread.sleep(3000); // sleep for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        client.start();
        client1.start();
        client2.start();
//        client3.start();

        try {
//            client.join();
            client1.join();
            client2.join();
//            client3.join();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
