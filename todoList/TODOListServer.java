
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
    /*
    more info: hashmap keys are inherently made unique and therefore overwrites existing keys with new values
    thus, can be given as a String type.
    The value set for the key can have duplicate values, to avoid this, the list is stored on a HashSet as it
    does not accept duplicate values.
    */
    private static HashMap<String, HashSet<String>> list = new HashMap<>();
    //password used to forcefully shut down server
    private static final String password = "password";

    public static void main(String[] args)
    {
        System.out.println("Opening port...\n");
        try
        {
            //creates the server
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
            //creates a link with client
            link = servSock.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));

            //retrieves id value from client via input stream
            String client_ID = "Client " + in.readLine();
            //creates new thread with client
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
