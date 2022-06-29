import java.rmi.RemoteException;

public class RepException extends RemoteException {

    public RepException(){
        super();
    }

    public RepException(String message){
        super(message);
    }
}
