import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FileHandler extends MulticastSender{

    boolean initialised = false;
    Login theServer = null;
    TCPHandler tcp;
    Queue<String> q = new LinkedList<>();

    public FileHandler(NodeData d){
        super(d);
        rmiStartup();
        fileStartup();
        tcp = new TCPHandler();
    }

    public boolean processMulticast(Message mess) {
        boolean processed = true;

        switch (mess.getCommand()) {
            case "Replicate":
                if(mess.getParameters()[1].equals(data.getNodeID())){
                    System.out.println("Request received : opening TCP socket");
                    sendMulticast("TCPopen "+mess.getParameters()[0]+" "+mess.getSenderID());
                    tcp.getFromTCP(mess.getParameters()[0]);
                }
                break;
            case "TCPopen":
                System.out.println("Receiver opened socket : send file");
                tcp.sendToTCP(mess.getSenderID(), "Files\\"+mess.getParameters()[0]);
                if(!q.isEmpty()){
                    String fileToReplicate = q.poll();
                    sendMulticast("Replicate " + fileToReplicate + " " + whereToReplicate(fileToReplicate));
                }
                break;
            default:
                processed = false;
                break;
        }
        return processed;
    }

    private void rmiStartup() {
        try {
            theServer = (Login) Naming.lookup("rmi://"+data.getServerIP()+"/myserver");
            initialised = true;
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void fileStartup() {
        listFiles();
        if(!q.isEmpty()){
            String firstFile=q.poll();
            sendMulticast("Replicate " + firstFile + " " + whereToReplicate(firstFile));
        }

    }

    private void listFiles() {
        File folder = new File("Files");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
        System.out.println("Listing local files..");

        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println("Local File: " + listOfFiles[i].getName());
        }

        for(File f : listOfFiles){
            q.add(f.getName());
        }
        System.out.println();



    }

    private String whereToReplicate(String name) {
        String replicateNode="";
        boolean send = true;

        String owner = getOwner(name);
        if(owner.equals(data.getNodeID())){
            replicateNode = getIDFromHash(data.getPreviousNode());
            if(replicateNode.equals(data.getNodeID())){
                send = false;
            }
        }else {
            replicateNode = owner;
        }
        if(send==true) {
            return null;
        }else {
            return replicateNode;
        }
        /*
            boolean received = false;
            sendMulticast("Replicate " + name + " " + replicateNode);
            while(!received)
            {
                Message mess=receiveMulticast();
                if(mess.getContent().contains("TCPopen")){
                    System.out.println("Receiver opened socket : send file");
                    //sendToTCP(mess.getSender(), mess.getContent().split(" ")[1]);
                    tcp.sendToTCP(replicateNode, "Files\\"+name);
                    received=true;
                }
            }
        }*/
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

    public String getIDFromHash(int hash){
        String ID="";
        try {
            ID = theServer.getIDFromHash(hash);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ID;
    }


}
