import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Connector {

    public static IDistributedRepository connect(String host, Integer port, String serverName) {

        try {
            Registry registry = LocateRegistry.getRegistry(host,port);
            return (IDistributedRepository) registry.lookup(serverName);
        }
        catch (RepException e) {
            return null;
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
