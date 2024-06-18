package p2psystem;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L ; 
	
	private long id ; 
	private String content ; 
	private int seq ; 
	
	public Message ( long id , int seq , String content ) {
		this.id = id ; 
		this.seq = seq ; 
		this.content = content ; 
	}
	
	public long getId ( ) { return id ; } 
	public void setId ( long id ) { this.id = id ; } 
	public int getseq ( ) { return seq ; } 
	public void setseq ( int seq ) { this.seq = seq ; } 
	public String getContent ( ) { return content ; } 
	public void setContent ( String content ) { this.content = content ; } 
	
	public String toString ( ) {
		return "Message{"+
				"id="+id+
				",seq="+seq+
				",content='"+content+'\''+
				'}';
	}
}
