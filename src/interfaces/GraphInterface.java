package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphInterface extends Remote {

    public  String computeBatch(int clientID, String batch) throws RemoteException;
}
