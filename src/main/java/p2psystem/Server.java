package p2psystem;

import java.io.* ; 
import java.net.* ; 
import java.util.* ;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson; 

public class Server {
	// 定义需要的全局变量
	private static final int PORT = 8189 ; 
	private static int messageID = 0 ; 
	private static Map<Integer,PrintWriter> clientWriters = new ConcurrentHashMap<>() ; 
	private static final Gson gson = new Gson() ; 
	private static final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator ( 1 , 1 ) ;
	private static final int ACKseq = 1145141919 ;
	private static final int ACKnew = 1919810 ;
	private static int clientNum = 0 ;
	
	public static void main ( String[] args ) {
		System.out.println ( "Central server started!") ; 
		try ( ServerSocket serverSocket = new ServerSocket ( PORT ) ) {
			int count = 0 ; 
			while ( true ) {
				new ClientHandler( serverSocket.accept() ).start() ; 
			}
		} catch ( IOException e ) {
			e.printStackTrace(); 
		}
	}
	
	private static class ClientHandler extends Thread {
		private Socket socket ; 
		private int clientID ; 
		private BufferedReader in ; 
		private PrintWriter out ; 
		
		public ClientHandler ( Socket socket ) {
			this.socket = socket ; 
		}
		
		@Override
		public void run ( ) {
			try {
				BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) ) ; 
				PrintWriter out = new PrintWriter ( socket.getOutputStream() , true ) ; 
				clientID = socket.getPort() ; 
				clientWriters.put(clientID, out) ; 
				
				String messageJson ; 
				while ( (messageJson = in.readLine()) != null ) {
					// synchronized用来锁这个相同同步块的线程，因为messageID时唯一的，只能有一个来进行写操作
					synchronized ( Server.class ) {
						Message message = MessageUtils.deserialize(messageJson);
						if ( message.getId() == 0 )
							message.setId ( idGenerator.nextId() ) ; 
						if ( message.getseq() == ACKnew ) {
							message.setId((long)clientNum+(long)1);
							clientNum = clientNum + 1 ; 
						}
						String broadcastMessage = MessageUtils.serialize(message) ; 
						for ( PrintWriter writer : clientWriters.values() ) {
							writer.println ( broadcastMessage ) ; 
						}
					}
				}
			} catch ( IOException e ) {
				e.printStackTrace(); 
			} finally {
				if ( out != null ) {
					clientWriters.remove(clientID) ; 
				}
				try {
					socket.close(); 
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
