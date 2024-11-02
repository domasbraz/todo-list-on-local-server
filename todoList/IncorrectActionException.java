
package todoList;


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
