import java.rmi.RemoteException;

public interface IDistributedRepository extends IRepository {

    IAggregate aggregate(String[] repIds)  throws RemoteException;
}
