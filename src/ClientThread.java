public class ClientThread extends Thread{

    private volatile boolean running = true;
    private Message mess;
    private NodeData data;



    public ClientThread(NodeData data) {        //Works as a background thread, processing multicasts
        this.data = data;
    }

    public void run(){
        NetworkHandler net = new NetworkHandler(data);
        MulticastReceiver multi = new MulticastReceiver(data);
        MulticastSender multis = new MulticastSender(data);

        net.sendBootstrap();

        while(running){

            mess=null;

            do{
                mess=multi.receiveMulticast();
            }while(mess==null);

            System.out.println("Received a message: "+mess);
            running = net.processMulticast(mess);
        }

    }

    public void toggleRunning(){
        if(running){
            running=false;
        }else{
            running=true;
        }
    }

}
