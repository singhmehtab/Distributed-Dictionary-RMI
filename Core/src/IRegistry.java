import java.net.URI;

public interface IRegistry {

    void register(String id, URI uri);

    void unregister(String id);

}
