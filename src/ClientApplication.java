import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientApplication{

    private String name;
    private static boolean hasMessage = false;
    protected String message = "";
    protected static  boolean cont = true;


    public static void main(String[] args){

        NodeData data = new NodeData();
        ClientThread backgroundWorker = new ClientThread(data);
        backgroundWorker.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (cont) {
                System.out.println("What action should be performed?");
                System.out.println("1: Shut Down");
                String s = br.readLine();
                if (s.equals("1")) {
                    cont = false;
                    System.out.println("Shutting down this ClientThread..");
                    System.exit(0);

                    /*TODO
                    Find more elegant way to shut down the ClientThread, instead of System.exit.
                     */
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error has occured");

        }
    }
}