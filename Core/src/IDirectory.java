public interface IDirectory {

    IRepository find(String id);

    String[] list();

}
