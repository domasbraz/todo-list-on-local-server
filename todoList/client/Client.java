
package todoList.client;

import java.io.*;
import java.net.*;

public class Client
{

    private static InetAddress host;
    private static final int PORT = 1234;
    private static boolean connected;
    //assigns unique id using unix timestamp
    private static final long timeStamp = System.currentTimeMillis();

    public static void main(String[] args)
    {
        try
        {
            host = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        run();
    }

    private static void run()
    {
        Socket link = null;
        try
        {

            connected = true;
            
            do
            {
                link = new Socket(host, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
                String message = null;
                String response = null;

                message = Long.toString(timeStamp);
                out.println(message);

                System.out.println("Enter message to be sent to server: ");
                message = userEntry.readLine();
                out.println(message);
                response = in.readLine();
                if (response.equals("TERMINATE"))
                {
                    connected = false;
                }
                System.out.println("\nSERVER RESPONSE> " + response);
                while ((response = in.readLine()) != null)
                {
                    System.out.println(response);
                }
            }
            while (connected);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                System.out.println("\n* Closing connection... *");
                link.close();
            }
            catch (IOException e)
            {
                System.out.println("Unable to disconnect/close!");
                System.exit(1);
            }
        }
    }
    
    
}
