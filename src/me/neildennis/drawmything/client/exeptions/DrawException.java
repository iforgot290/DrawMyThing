package me.neildennis.drawmything.client.exeptions;

public abstract class DrawException extends RuntimeException{
	
	private static final long serialVersionUID = -7888287836256133978L;
	
	public DrawException(String str){
		super(str);
	}

}
