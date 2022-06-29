import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Callback extends Remote {

    void print(ArrayList<String> list) throws RemoteException;

}
