package org.entermediadb.chat2;

public class EMException extends RuntimeException
{

    public EMException(Throwable ex)
    {
        super(ex);
    }

    public EMException(String ex)
    {
        super(ex);
    }
}
