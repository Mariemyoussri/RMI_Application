package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphInterface extends Remote {

    public  String computeBatch(String batch) throws RemoteException;
}
