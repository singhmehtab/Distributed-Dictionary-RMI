import java.rmi.RemoteException;
import java.util.ArrayList;

public class CallbackImpl implements Callback{
    @Override
    public void print(ArrayList<String> list) throws RemoteException {
        for(String s : list){
            System.out.println(s);
        }
    }
}
