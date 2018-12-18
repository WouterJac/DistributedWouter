public class NetworkHandler extends MulticastSender{

    private boolean setup = false;
    private boolean bootSent = false;

    public NetworkHandler(NodeData d){
        super(d);
    }

    public boolean processMulticast(Message mess){

        boolean processed = true;

        if(setup) {

            switch (mess.getCommand()) {
                case "Bootstrap":
                    welcomeNewNode(mess.getSenderID());     //A new node wants to bootstrap, let's see who he is
                    break;
                case "BootReplyServer":
                    System.out.println("This node has already been setup, ignoring BootReplyServer");

                    /*TODO
                    When the user shuts down, send neighbours (fake a message to self?)
                    Aplicationthread to interface with user, so he can shut down
                     */

                default:
                    processed = false;
                    //System.out.println("Networkcommand not found (setup): "+mess);
            }

        }else{

            switch (mess.getCommand()) {
                case "BootReplyServer":
                    data.setServerIP(mess.getSenderIp());

                    /*TODO
                    Initialise RMI with server IP
                     */

                    if(Integer.parseInt(mess.getParameters()[0])<1){        //If i'm the first node, i am my own neighbours
                        setup = true;
                        data.setPreviousNode(data.getMyHash());
                        data.setNextNode(data.getMyHash());
                    }
                    break;
                case "BootReplyNode":
                    data.setPreviousNode(data.hash(mess.getSenderID()));
                    data.setNextNode(Integer.parseInt(mess.getParameters()[0]));
                    setup = true;
                    break;
                default:
                    processed = false;
                    //System.out.println("Networkcommand not found (not setup): "+mess.toString());
            }
            if(setup){
                System.out.println("This Client has entered the network.\n\nYour previous node is: "+data.getPreviousNode()+"\tYour next node is: "+data.getNextNode());
            }
        }
        System.out.println();
        return processed;

    }

    public void sendBootstrap(){
        sendMulticast("Bootstrap");
        bootSent = true;
    }

    public void welcomeNewNode(String senderID)                                       //Welcomes a new node to the network
    {
        int hash = data.hash(senderID);
        if (isPrevious(hash)) {                                             //If the new node is my previous, set it
            data.setPreviousNode(hash);
        }
        if (isNext(hash)) {                                                 //If the new node is my next, set it
            sendMulticast("BootReplyNode " + data.getNextNode());   //If so, send him our last next
            data.setNextNode(hash);
        }
    }

    public boolean isNext(int hash) {

        if(data.getMyHash() == data.getNextNode()){                 //Indien dit naar zichzelf verwijst passen we zowiezo aan.
            return true;
        }else if(((data.getMyHash() < hash) || data.getNextNode() < data.getMyHash()) && (hash < data.getNextNode())){
            return true;
        }else{
            return false;
        }

    }

    public boolean isPrevious(int hash) {

        if(data.getMyHash() == data.getNextNode()){                 //Indien dit naar zichzelf verwijst passen we zowiezo aan.
            return true;
        }else if((hash < data.getMyHash() || data.getPreviousNode() > data.getMyHash()) && (data.getPreviousNode() < hash)){
            data.setPreviousNode(hash);
            return true;
        }else{
            return false;
        }

    }
}
