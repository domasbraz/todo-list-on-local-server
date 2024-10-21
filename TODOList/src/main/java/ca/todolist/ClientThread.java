/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.todolist;

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
    String password;
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
            
            String[] tokens = message.split(";");
            String[] actions = {"add", "list", "STOP", "admin"};
            String action = tokens[0];
            
            if (tokens.length < 1 || tokens.length > 3 || !checkIfExists(action, actions))
            {
                out.println("Syntax Error!");
                throw new IncorrectActionException();
            }
            String date = "";
            String note = "";
            if (tokens.length > 1)
            {
                date = tokens[1];
            }
            if (tokens.length > 2)
            {
                note = tokens[2];
            }
            
            switch (action)
            {
                case "add":
                    if (tokens.length != 3)
                    {
                        out.println("Syntax Error!");
                        throw new IncorrectActionException();
                    }
                    if (list.containsKey(date))
                    {
                        HashSet<String> map = list.get(date);
                        map.add(note);
                    }
                    else
                    {
                        HashSet<String> map = new HashSet<>();
                        map.add(note);
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
