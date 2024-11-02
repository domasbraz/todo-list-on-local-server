
package todoList;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;

public class TODOListServer
{

    private static ServerSocket servSock;
    private static final int PORT = 1234;
    //key value used for unique dates, HashSet used to avoid duplicate tasks
    private static HashMap<String, HashSet<String>> list = new HashMap<>();
    private static final String password = "password";

    public static void main(String[] args)
    {
        System.out.println("Opening port...\n");
        try
        {
            servSock = new ServerSocket(PORT);     
        }
        catch (IOException e)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        do
        {
            run();
        }
        while (true);

    }

    private static void run()
    {
        Socket link = null;                      
        try
        {
            link = servSock.accept();
            //clientConnections++;
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));

            String client_ID = "Client " + in.readLine();
            Runnable resource = new ClientThread(link, client_ID, list, servSock, password);
            Thread t = new Thread(resource);
            t.start();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            try
            {

                System.out.println("\n* Closing connection... *");
                link.close();	
                
            }
            catch (IOException e2)
            {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
            
        }
    }
}
