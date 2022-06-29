import java.rmi.RemoteException;

public interface IRepository extends IAggregate{

    void add(String key, Integer value) throws RemoteException;

    void set(String key, Integer value)  throws RemoteException;

    void delete(String key)  throws RemoteException;

    String listKeys()  throws RemoteException;

    String getValue(String key)  throws RemoteException;

    String getValues(String key)  throws RemoteException;

    String sum(String key)  throws RemoteException;

    String max(String key)  throws RemoteException;

    void resetAll()  throws RemoteException;

    void enumKeys() throws RemoteException;

    void enumKeyValues(String key) throws RemoteException;

    void registerForCallback(Callback callback) throws RemoteException;

}
