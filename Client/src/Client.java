import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client {

    public static void main(String[] args) {
        try {
            Callback stub = null;
            Callback callback = new CallbackImpl();
            try {
                stub = (Callback) UnicastRemoteObject.exportObject(callback, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            IDistributedRepository iDistributedRepository = Connector.connect("127.0.0.1", 4561,"RepositoryTwo");
            iDistributedRepository.set("a",15);
            iDistributedRepository = Connector.connect("127.0.0.1", 4562,"RepositoryThree");
            iDistributedRepository.set("a", 30);
            iDistributedRepository = Connector.connect("127.0.0.1", 4560,"RepositoryOne");
            iDistributedRepository.registerForCallback(stub);
            iDistributedRepository.set("a",10 );
            iDistributedRepository.add("a",15);
            iDistributedRepository.set("b",13);
            System.out.println(iDistributedRepository.listKeys());
            System.out.println(iDistributedRepository.aggregate(new String[]{"RepositoryTwo", "RepositoryThree"}).sum("a"));
            iDistributedRepository.enumKeys();
            iDistributedRepository.enumKeyValues("a");
        } catch (RepException rp) {
            rp.printStackTrace();
        }
        catch(RemoteException e) {
        e.printStackTrace();
    }
    }

}
