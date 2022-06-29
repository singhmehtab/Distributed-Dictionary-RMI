import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAggregate extends Remote {

    String sum(String key) throws RemoteException;

}
