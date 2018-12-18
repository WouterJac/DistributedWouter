import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class FileHandler extends MulticastSender{

    boolean initialised = false;
    Login theServer = null;

    public FileHandler(NodeData d){
        super(d);
        try {
            theServer = (Login) Naming.lookup("rmi://"+d.getServerIP()+"/myserver");
            initialised = true;
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getOwner(String fileName){
        String owner="";
        try {
            owner = theServer.getOwner(fileName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return owner;
    }


    public boolean processMulticast(Message mess) {
        boolean processed = true;

        switch (mess.getCommand()) {
            case "Bootstrap":
                break;
            default:
                processed = false;
                break;
        }
        return processed;
    }
}
