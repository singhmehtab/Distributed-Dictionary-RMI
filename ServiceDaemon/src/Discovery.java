import java.net.InetAddress;
import java.net.UnknownHostException;

public class Discovery implements IDirectory{

    Integer port = null;

    public Discovery(Integer port){
        this.port = port;
    }

    @Override
    public IRepository find(String s) {
        try {
            if(!Repository.getInstance(this).getAddressMap().containsKey(s)) {
                BroadcastSender.sendBroadCast(null, InetAddress.getLocalHost(), port, true, s);
            }
            Thread.sleep(500);
            if(Repository.getInstance(this).getAddressMap().containsKey(s)){
                String host = Repository.getInstance(this).getAddressMap().get(s)[0];
                Integer port = Integer.parseInt(Repository.getInstance(this).getAddressMap().get(s)[1]);
                return Connector.connect(host,port, s);
            }
            return null;
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String[] list() {
        return new String[0];
    }
}
