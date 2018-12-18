import java.io.*;
import java.util.HashMap;

public class ServerMapHandler extends MulticastSender{
    private HashMap<Integer, String> ipMap;
    private File ipMapFile;


    public ServerMapHandler(NodeData d){
        super(d);
        ipMap = new HashMap<Integer, String>();
        ipMapFile = new File("IpMap.xml");
        saveFile(ipMapFile);

    }

    public boolean processMulticast(Message mess){         //Decides what to do when certain messages arrive

        boolean processed = true;

        switch (mess.getCommand()) {
            case "Bootstrap":
                sendMulticast("BootReplyServer "+ipMap.size());
                addNode(mess.getSenderID());
                break;
            case "Shut":
                removeNode(mess.getSenderID());
                break;
            default:
                processed = false;
                //System.out.println("Mapcommand not found");
        }
        return processed;
    }

    public void addNode(String sender) {
        int hash = data.hash(sender);
        ipMap.put(hash, sender);
        saveFile(ipMapFile);
        System.out.println("Added Hash: " + hash + "\tHost: " + sender);
    }

    public boolean removeNode(String ip) {
        int hash = data.hash(ip);
        ipMap.remove(hash);
        saveFile(ipMapFile);
        System.out.println("Removed: Hash: " + hash + "\tHost: " + ip);
        return true;
    }

    private void saveFile(File saveFile){                   //Saves the IPmap to a file
        try {
            Writer writer = new BufferedWriter(new FileWriter(saveFile));

            for (Integer hash : ipMap.keySet()) {
                String key = hash.toString();
                String value = ipMap.get(hash).toString();
                writer.write(key + " " + value + "\n");
            }
            System.out.println("Saved xml-file..");
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
