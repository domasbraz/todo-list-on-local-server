
package todoList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class ClientThread implements Runnable
{

    Socket client_link = null;
    String clientID;
    HashMap<String, HashSet<String>> list;
    final String password;
    ServerSocket server;

    public ClientThread(Socket connection, String cID, HashMap<String, HashSet<String>> list, ServerSocket server, String password)
    {
        this.client_link = connection;
        clientID = cID;
        this.list = list;
        this.password = password;
        this.server = server;
    }

    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(client_link.getInputStream()));
            PrintWriter out = new PrintWriter(client_link.getOutputStream(), true);

            String message = in.readLine();
            System.out.println("Message received from client: " + clientID + "  " + message);
            
            //split message into tokens action;date;task
            String[] tokens = message.split(";");
            //list of possible actions
            String[] actions = {"add", "list", "STOP", "admin"};
            String action = tokens[0];
            
            //check if command has a valid length and that one of the actions is used
            if (tokens.length < 1 || tokens.length > 3 || !checkIfExists(action, actions))
            {
                //message returned here to avoid having to create another new PrintWriter
                out.println("Syntax Error!");
                throw new IncorrectActionException();
            }

            String date = "";
            String note = "";
            //assign date if the message consists of more than 1 token
            if (tokens.length > 1)
            {
                date = tokens[1];
            }
            //assigns task if the message consists of more than 2 tokens
            if (tokens.length > 2)
            {
                note = tokens[2];
            }
            
            switch (action)
            {
                case "add":
                    //add command requires 3 tokens
                    if (tokens.length != 3)
                    {
                        out.println("Syntax Error!");
                        throw new IncorrectActionException();
                    }
                    //check if there is already task/s assigned for the chosen date
                    if (list.containsKey(date))
                    {
                        //get() returns value of the given key, the value type is a HashSet
                        HashSet<String> map = list.get(date);
                        //adds task to the HashSet
                        map.add(note);
                    }
                    else
                    {
                        //creates a new HashSet if there is tasks yet for the given date
                        HashSet<String> map = new HashSet<>();
                        map.add(note);
                        //adds date as key and HashSet as value to HashMap
                        list.put(date, map);
                    }
                    out.println("added " + note + " for " + date);
                    break;
                    
                case "list":
                    if (tokens.length < 2)
                    {
                        out.println("Syntax Error!");
                        throw new IncorrectActionException();
                    }
                    
                    String result = "\r\n";
                    if (list.size() == 0 || !list.containsKey(date))
                    {
                        result = "List is Empty!";
                    }
                    
                    result += date + "\r\n";
                    HashSet<String> map = list.get(date);

                    for (String todo : map)
                    {
                        result += " + " + todo + "\r\n";
                    }
                    
                    result += "\r\n";
                    
                    out.println(result);
                    break;
                    
                case "STOP":
                    out.println("TERMINATE");
                    try
                    {
                        System.out.println("\n* Closing connection with the client " + clientID + " ... *");
                        client_link.close();
                    }
                    catch (IOException e)
                    {
                        System.out.println("Unable to disconnect!");
                    }
                    break;
                    
                case "admin":
                     if (tokens.length != 3)
                    {
                        out.println("Syntax Error!");
                        throw new IncorrectActionException();
                    }
                    if (date.equals("SHUTDOWN") && note.equals(password))
                    {
                        //this crashes the app
                        System.out.println(clientID + " has shutdown the server!");
                        server.close();
                    }
                    break;
                    
            }
            

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (IncorrectActionException e)
        {
            System.out.println(e.getMessage());
        }
        
        finally
        {
            try
            {
                System.out.println("\n* Closing connection with the client " + clientID + " ... *");
                client_link.close();
            }
            catch (IOException e)
            {
                System.out.println("Unable to disconnect!");
            }
        }
        
    }
    
    //simple method for checking if a value exists in an array
    public boolean checkIfExists(String value, String[] array)
    {
        for (String word : array)
        {
            if (value.equals(word))
            {
                return true;
            }
        }
        return false;
    }

    public void addTask(String[] tokens, HashMap<String, HashSet<String>> list, PrintWriter out)
    {
        
    }
}
