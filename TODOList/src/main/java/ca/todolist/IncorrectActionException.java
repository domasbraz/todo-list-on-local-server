/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.todolist;


public class IncorrectActionException extends Exception
{

    String msg = "Syntax Error!";

    public IncorrectActionException()
    {
    }

    public IncorrectActionException(String msg)
    {
        super(msg);
        this.msg = msg;
    }

    @Override
    public String getMessage()
    {
        return this.msg;
    }
}
