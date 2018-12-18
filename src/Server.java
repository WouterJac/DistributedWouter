public class Server {

    private static boolean running = true;
    private static Message mess;

    public static void main(String[] args){

        NodeData data = new NodeData(true);
        ServerMapHandler map = new ServerMapHandler(data);
        MulticastReceiver multi = new MulticastReceiver(data);


        while(running){

            mess=null;

            do{
                mess=multi.receiveMulticast();
            }while(mess==null);

            System.out.println("Received a message: "+mess);

            map.processMulticast(mess);


        }





    }
}
