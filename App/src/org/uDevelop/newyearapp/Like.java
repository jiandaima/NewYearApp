package org.uDevelop.newyearapp;

public class Like {
	public final static byte NOT_LIKE = 0; //не залайкано
	public final static byte LIKE = 1; //залайкано
	public final static byte SEND_LIKE = 2; //залайкано, но не отправлено на сервер	
	
	public int count;
	public byte state;

}
