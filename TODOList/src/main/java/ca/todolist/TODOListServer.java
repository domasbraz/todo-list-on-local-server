/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package ca.todolist;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;

public class TODOListServer
{

    private static ServerSocket servSock;
    private static final int PORT = 1234;
    private static int clientConnections = 0;
    private static HashMap<String, HashSet<String>> list = new HashMap<>();
    private static String password = "password";

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
            clientConnections++;
            String client_ID = "Client " + clientConnections;
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
