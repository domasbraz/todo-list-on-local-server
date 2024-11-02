
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

            //reads incoming message from client and displays it on the server terminal
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
                //message returned here to avoid having to create another new PrintWriter in catch statement
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
                //adds task to list
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
                        //creates a new HashSet if there is no tasks yet for the given date
                        HashSet<String> map = new HashSet<>();
                        map.add(note);
                        //adds date as key and HashSet as value to HashMap
                        list.put(date, map);
                    }
                    //returns confirmation message to client
                    out.println("added " + note + " for " + date);
                    break;
                    
                //lists all tasks for a given date
                case "list":
                    //this command requires 2 tokens
                    if (tokens.length < 2)
                    {
                        out.println("Syntax Error!");
                        throw new IncorrectActionException();
                    }
                    //spaces out message to improve readability
                    String result = "\r\n";

                    //checks if their are tasks stored on the given date
                    if (list.size() == 0 || !list.containsKey(date))
                    {
                        result = "List is Empty!";
                    }
                    else
                    {
                        result += date + "\r\n";
                        //gets the HashSet stored on for the date key
                        HashSet<String> map = list.get(date);

                        //loops through the HashSet and adds it to the result string
                        for (String todo : map)
                        {
                            result += " + " + todo + "\r\n";
                        }
                        
                        result += "\r\n";
                    }
                    
                    out.println(result);
                    break;
                    
                //ends connection with client
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
                    //this is used to forcefully shutdown the server
                    //command: admin;SHUTDOWN;password
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

}
