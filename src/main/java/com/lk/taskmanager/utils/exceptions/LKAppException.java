package com.lk.taskmanager.utils.exceptions;

public class LKAppException extends RuntimeException
{

	public LKAppException() { }

	public LKAppException(String message)
	{
		super(message);
	}

	public LKAppException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LKAppException(Throwable cause)
	{
		super(cause);
	}
}
