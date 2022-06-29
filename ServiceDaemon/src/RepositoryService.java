import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RepositoryService {

    public static void main(String[] args) {
        Integer myPort = 4562;
        Discovery discovery = new Discovery(myPort);
        Repository repository = Repository.getInstance(discovery);
        String myName = "RepositoryThree";
        try {
            BroadcastListener listener = new BroadcastListener(repository.getAddressMap(), myName, InetAddress.getByName("localhost"), myPort);
            listener.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        IDistributedRepository stub = null;
        try {
            stub = (IDistributedRepository) UnicastRemoteObject.exportObject(repository, 0);
            Registry registry = LocateRegistry.createRegistry(myPort);
            registry.rebind(myName, stub);
            System.out.println("stub registered");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
