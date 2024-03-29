import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Repository implements IDistributedRepository {

    private HashMap<String, ArrayList<Integer>> dictionary = new HashMap<>();

    private HashMap<String, String[]> addressMap = new HashMap<>();

    private static Repository instance = null;

    private static IDirectory directory = null;
    ArrayList<IDistributedRepository> aggregateList = new ArrayList<>();

    private Callback callbackObject = null;

    private Repository(){
    }

    public static Repository getInstance(IDirectory iDirectory){
        if(instance == null){
            instance = new Repository();}
        directory = iDirectory;
        return instance;
    }

    @Override
    public IAggregate aggregate(String[] ids) throws RepException {
        try {
            for(String id :  ids){
                IDistributedRepository idis = (IDistributedRepository) directory.find(id);
                if(idis==null) throw new RepException("Ambiguous Repository");
                aggregateList.add(idis);
            }
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }

        return this;
    }

    @Override
    public void add(String key, Integer value) throws RepException{
        try {
        if(aggregateList.size()>0){
            for (IDistributedRepository rep : aggregateList) {
                rep.add(key, value);
            }
            aggregateList.clear();
            return;
        }

            if (dictionary.containsKey(key)) {
                dictionary.get(key).add(value);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(value);
                dictionary.put(key, list);
            }
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void set(String key, Integer value)  throws RepException{
        try {
            if(aggregateList.size()>0){
                for (IDistributedRepository rep : aggregateList) {
                    rep.set(key, value);
                }
                aggregateList.clear();
                return;
            }

            ArrayList<Integer> list = new ArrayList<>();
            list.add(value);
            dictionary.put(key, list);
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void delete(String key) throws RepException{
        try {
            if(aggregateList.size()>0){
                aggregateList.get(0).delete(key);
                aggregateList.clear();
                return;
            }
            dictionary.remove(key);
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public String listKeys() throws RepException{
        try {

            if(aggregateList.size()==1){
               return  aggregateList.get(0).listKeys();
            }
            StringBuilder sb = new StringBuilder();
            ArrayList<String> list = new ArrayList<>(dictionary.keySet());
            if(list.size()==0) return "";
            sb.append(list.get(0));
            if(list.size()==1)return sb.toString();
            for (int i = 1; i < list.size(); i++) {
                sb.append(",").append(list.get(i));
            }
            return sb.toString();
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
        finally {
            aggregateList.clear();
        }
    }

    @Override
    public String getValue(String key) throws RepException{
        try {
            if(aggregateList.size()==1){
                return aggregateList.get(0).getValue(key);
            }
            if (dictionary.containsKey(key)) {
                return dictionary.get(key).get(0).toString();
            } else throw new RepException("error");
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
        finally {
            aggregateList.clear();
        }
    }

    @Override
    public String getValues(String key) throws RepException{
        try {
            if(aggregateList.size()==1){
                return aggregateList.get(0).getValues(key);
            }
            if (dictionary.containsKey(key)) {
                StringBuilder sb = new StringBuilder();
                ArrayList<Integer> list = dictionary.get(key);
                sb.append(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    sb.append(",").append(list.get(i));
                }
                return sb.toString();
            } else throw new RepException("error");
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
        finally {
            aggregateList.clear();
        }
    }

    @Override
    public String sum(String key) throws RepException{
        try {
            int sum = 0;
            if (dictionary.containsKey(key)) {
                sum = Integer.parseInt(String.valueOf(dictionary.get(key).stream().mapToInt(i -> i).sum()));
                for (IDistributedRepository rep : aggregateList) {
                    String response = rep.sum(key);
                    if (response.equals("error")) return "error";
                    sum += Integer.parseInt(response);
                }
                aggregateList.clear();
                return String.valueOf(sum);
            } else {
                aggregateList.clear();
                throw new RepException("error");
            }
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public String max(String key) throws RepException{
        try {
            if (dictionary.containsKey(key)) {
                return String.valueOf(Collections.max(dictionary.get(key)));
            } else throw new RepException("error");
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void resetAll() throws RepException{
        try {
            if(aggregateList.size()==1){
                aggregateList.get(0).resetAll();
                aggregateList.clear();
                return;
            }
            dictionary.clear();
        }catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void enumKeys() throws RepException{
        try {
            callbackObject.print(new ArrayList<>(dictionary.keySet()));
        } catch (Exception e) {
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void enumKeyValues(String s) throws RepException {
        try {
            ArrayList<String> list = new ArrayList<>();
            for (Integer i : dictionary.get(s)) {
                list.add(String.valueOf(i));
            }
            callbackObject.print(list);
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    @Override
    public void registerForCallback(Callback callback) throws RepException {
        try {
            callbackObject = callback;
        }
        catch (Exception e){
            throw new RepException(e.getMessage());
        }
    }

    public void addAddress(String name, String host, Integer port){
        addressMap.put(name, new String[]{host, String.valueOf(port)});
    }

    public HashMap<String, String[]> getAddressMap(){
        return addressMap;
    }
}
