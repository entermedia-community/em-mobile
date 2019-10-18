package org.entermediadb.entermediaslide;

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
