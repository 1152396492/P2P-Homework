package p2psystem;

import java.io.* ;
import java.net.Socket;
import com.google.gson.Gson; 

public class Client {
	
	private static final String SEVERADDRESS = "127.0.0.1" ; 
	private static final int PORT = 8189 ; 
	private static int clientNum = 1 ; 
	private static int logicNum = 1 ; 
	private static final Gson gson = new Gson() ;
	private static final int ACKseq = 1145141919 ; 
	private static final int ACKnew = 1919810 ; 
	private static final MessageQueue Mqueue = new MessageQueue ( ) ; 
	private static TextSaverApp chat = new TextSaverApp("chatgroup") ; 
	private static int ClientName ;
	private static int ACKname = 0 ; 
	
	public static void main ( String [] args ) throws Exception {
		// 定义socket,输入输出变量
		Socket socket = new Socket ( SEVERADDRESS , PORT ) ; 
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) ) ; 
		PrintWriter out = new PrintWriter ( socket.getOutputStream() , true ) ; 
		BufferedReader consoleInput = new BufferedReader ( new InputStreamReader ( System.in ) ) ; 
		// 创造一个不断接受服务端信息的进程
		new Thread ( ( ) -> {
			try {
				String serverMessageJson ; 
				while ( ( serverMessageJson = in.readLine() ) != null ) {
					Message message = MessageUtils.deserialize(serverMessageJson) ; 
					// 如果这条信息是属于ACK信息
					// 将对应消息m的ACK+1
					// 观察队列头部的消息，如果关于该消息已经收到了所有节点的ACK消息，则将其从队列中取出，并完成投递
					if ( message.getseq() == ACKseq ) {
						//Mqueue.printAllMessages();
						Mqueue.incrementIdCount(message) ; 
						Message HeadMessage = Mqueue.getHeadMessage() ; 
						int count = Mqueue.getMessageCount(HeadMessage.getId()) ; 
						if ( count == clientNum ) {
							logicNum = logicNum + 1 ; 
							chat.setLogicNum(logicNum) ;
							Mqueue.removeMessage(HeadMessage.getId()) ; 
							//传递给应用层
							chat.appendText(ClientName + ":" + HeadMessage.toString());
							System.out.println ( HeadMessage.toString() ) ; 
						}
					}
					// 处理新来的客户端
					else if ( message.getseq() == ACKnew ) {
						logicNum = logicNum + 1 ; 
						chat.setLogicNum(logicNum) ;
						clientNum = (int)message.getId() ; 
						if ( ACKname == 0 ) {
							ACKname = 1 ; 
							ClientName = clientNum ; 
						}
						System.out.println ( clientNum ) ; 
					}
					// 如果这条信息属于发送的信息
					// 将消息按逻辑时间戳先后顺序放入缓存队列
					// 观察缓存队列头部的消息，如果该消息尚未广播过ACK消息，则向所有节点发送关于该消息的ACK信息
					else { 
						Mqueue.addMessage(message) ;
						//Mqueue.printAllMessages();
						//System.out.println(message.toString());
						Message HeadMessage = Mqueue.getHeadMessage() ; 
						int count = Mqueue.getBroadCount(HeadMessage.getId()) ;
						//System.out.println ( count ) ; 
						if ( count == 0 ) {
							// 发送ACK信息
							Mqueue.incrementBroadCount(message) ; 
							Message Hmessage = new Message ( message.getId() , ACKseq , message.getContent() )  ; 
							String messageJson = MessageUtils.serialize(Hmessage) ; 
							logicNum = Math.max(logicNum, message.getseq()) ; 
							logicNum = logicNum + 1 ; 
							chat.setLogicNum(logicNum) ; 
							out.println(messageJson);
						}
					}
				}
			} catch ( IOException e ) {
				e.printStackTrace() ; 
			}
		}).start ( ) ; 
		// 告诉服务器有新客户端加入
		Message infoM = new Message( 0 , ACKnew , "Hello") ;
		String messageJso = MessageUtils.serialize(infoM) ; 
		out.println ( messageJso ) ; 
		// 对面板输入的数据处理
		// 对终端输入的数据进行处理
		String userInput ; 
		while ( (userInput = consoleInput.readLine()) != null ) {
			Message message = new Message ( 0 , logicNum , userInput ) ;
			String messageJson = MessageUtils.serialize(message) ; 
			logicNum = logicNum + 1 ; 
			chat.setLogicNum(logicNum) ;
			out.println(messageJson);
		}
	}

}
