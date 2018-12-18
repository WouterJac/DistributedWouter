import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Login extends Remote {
    String getOwner(String fileName) throws RemoteException;
    String getIpFromHash(int hash) throws RemoteException;
    //Boolean  remove(String ip) throws RemoteException;
}