package org.uDevelop.newyearapp;

public class Like {
	public final static int NOT_LIKE = 0; //не залайкано
	public final static int LIKE = 1; //залайкано
	public final static int SEND_LIKE = 2; //залайкано, но не отправлено на сервер	
	
	public int count;
	public int state;

}
