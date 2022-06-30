import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        try {
            Callback stub = null;
            Callback callback = new CallbackImpl();
            try {
                stub = (Callback) UnicastRemoteObject.exportObject(callback, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            IDistributedRepository iDistributedRepository = Connector.connect("127.0.0.1", 4560, "Tom");
            iDistributedRepository.registerForCallback(stub);
            while (true) {
                try{
                String input = sc.nextLine();
                String[] clientCommand = input.split(" ");
                String operation = clientCommand[0];
                if (clientCommand[0].contains("RESET") && clientCommand[0].split("\\.").length > 1) {
                    iDistributedRepository.resetAll();
                }
                switch (operation) {
                    case "ADD": {
                        if (checkAndRespond(3, clientCommand)) {
                            continue;
                        }
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        if (clientCommandAdvancedSplit.length == 2) {
                            iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                            iDistributedRepository.add(clientCommandAdvancedSplit[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        } else {
                            iDistributedRepository.add(clientCommand[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "SET": {
                        if (checkAndRespond(3, clientCommand)) {
                            continue;
                        }
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        if (clientCommandAdvancedSplit.length == 2) {
                            iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                            iDistributedRepository.set(clientCommandAdvancedSplit[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        } else {
                            iDistributedRepository.set(clientCommand[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "DELETE": {
                        if (checkAndRespond(2, clientCommand)) {
                            continue;
                        }
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        if (clientCommandAdvancedSplit.length == 2) {
                            iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                            iDistributedRepository.delete(clientCommandAdvancedSplit[1]);
                            System.out.println("OK");
                        } else {
                            iDistributedRepository.delete(clientCommand[1]);
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "LIST": {
                        if (!clientCommand[1].contains("KEYS")) {
                            System.out.println(getCommandInvalidText());
                            continue;
                        }
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        if (clientCommandAdvancedSplit.length == 2) {
                            iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                            System.out.println(iDistributedRepository.listKeys());
                            System.out.println("OK");
                        } else {
                            System.out.println(iDistributedRepository.listKeys());
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "GET": {
                        if (checkAndRespond(3, clientCommand)) {
                            continue;
                        }
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        if (clientCommandAdvancedSplit.length == 2) {
                            if (clientCommandAdvancedSplit[1].equals("VALUE")) {
                                iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                                System.out.println(iDistributedRepository.getValue(clientCommand[2]));
                                System.out.println("OK");
                            } else if (clientCommandAdvancedSplit[1].equals("VALUES")) {
                                iDistributedRepository.aggregate(new String[]{clientCommandAdvancedSplit[0]});
                                System.out.println(iDistributedRepository.getValues(clientCommand[2]));
                                System.out.println("OK");
                            } else {
                                System.out.println(getCommandInvalidText());
                            }
                        } else {
                            if (clientCommand[1].equals("VALUE")) {
                                System.out.println(iDistributedRepository.getValue(clientCommand[2]));
                            } else if (clientCommand[1].equals("VALUES")) {
                                iDistributedRepository.getValues(clientCommand[2]);
                            } else System.out.println(getCommandInvalidText());
                        }
                        break;
                    }
                    case "SUM": {
                        if (checkAndRespond(2, clientCommand)) {
                            continue;
                        }
                        System.out.println(iDistributedRepository.sum(clientCommand[1]));
                        System.out.println("OK");
                        break;
                    }
                    case "DSUM": {
                        if (clientCommand.length == 2) {
                            System.out.println(iDistributedRepository.sum(clientCommand[1]));
                            System.out.println("OK");
                        } else if (clientCommand[2].equals("INCLUDING")) {
                            String[] array = new String[clientCommand.length - 3];
                            for (int i = 3; i < clientCommand.length; i++) {
                                array[i-3] = clientCommand[i];
                            }
                            System.out.println(iDistributedRepository.aggregate(array).sum(clientCommand[1]));

                        } else {
                            System.out.println(getCommandInvalidText());
                        }
                        break;
                    }
                    case "RESET": {
                        if (clientCommand.length == 2) {
                            iDistributedRepository.aggregate(new String[]{clientCommand[1]});
                            iDistributedRepository.resetAll();
                            System.out.println("OK");
                        } else {
                            iDistributedRepository.resetAll();
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "ENUM": {
                        if (clientCommand[1].equals("KEYS")) {
                            iDistributedRepository.enumKeys();
                        } else if (clientCommand[1].equals("KEY") && clientCommand[2].equals("VALUES")) {
                            iDistributedRepository.enumKeyValues(clientCommand[3]);
                        }
                    }
                    case "EXIT": {
                        return;
                    }
                    default: {
                        System.out.println(getCommandInvalidText());
                        break;
                    }
                }


                }
                catch (RepException e){
                    System.out.println(e.getMessage());
                }
                catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static boolean checkAndRespond(Integer requiredLength, String[] clientCommand){
        if(clientCommand.length != requiredLength){
            System.out.println(getCommandInvalidText());
            return true;
        }
        return false;
    }

    private static String[] advancedSplit(String command){
        return command.split("\\.");
    }

    private static  String getCommandInvalidText(){
        return "Command format not valid";
    }

}
