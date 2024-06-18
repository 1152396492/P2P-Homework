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
		// ����socket,�����������
		Socket socket = new Socket ( SEVERADDRESS , PORT ) ; 
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) ) ; 
		PrintWriter out = new PrintWriter ( socket.getOutputStream() , true ) ; 
		BufferedReader consoleInput = new BufferedReader ( new InputStreamReader ( System.in ) ) ; 
		// ����һ�����Ͻ��ܷ������Ϣ�Ľ���
		new Thread ( ( ) -> {
			try {
				String serverMessageJson ; 
				while ( ( serverMessageJson = in.readLine() ) != null ) {
					Message message = MessageUtils.deserialize(serverMessageJson) ; 
					// ���������Ϣ������ACK��Ϣ
					// ����Ӧ��Ϣm��ACK+1
					// �۲����ͷ������Ϣ��������ڸ���Ϣ�Ѿ��յ������нڵ��ACK��Ϣ������Ӷ�����ȡ���������Ͷ��
					if ( message.getseq() == ACKseq ) {
						//Mqueue.printAllMessages();
						Mqueue.incrementIdCount(message) ; 
						Message HeadMessage = Mqueue.getHeadMessage() ; 
						int count = Mqueue.getMessageCount(HeadMessage.getId()) ; 
						if ( count == clientNum ) {
							logicNum = logicNum + 1 ; 
							chat.setLogicNum(logicNum) ;
							Mqueue.removeMessage(HeadMessage.getId()) ; 
							//���ݸ�Ӧ�ò�
							chat.appendText(ClientName + ":" + HeadMessage.toString());
							System.out.println ( HeadMessage.toString() ) ; 
						}
					}
					// ���������Ŀͻ���
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
					// ���������Ϣ���ڷ��͵���Ϣ
					// ����Ϣ���߼�ʱ����Ⱥ�˳����뻺�����
					// �۲컺�����ͷ������Ϣ���������Ϣ��δ�㲥��ACK��Ϣ���������нڵ㷢�͹��ڸ���Ϣ��ACK��Ϣ
					else { 
						Mqueue.addMessage(message) ;
						//Mqueue.printAllMessages();
						//System.out.println(message.toString());
						Message HeadMessage = Mqueue.getHeadMessage() ; 
						int count = Mqueue.getBroadCount(HeadMessage.getId()) ;
						//System.out.println ( count ) ; 
						if ( count == 0 ) {
							// ����ACK��Ϣ
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
		// ���߷��������¿ͻ��˼���
		Message infoM = new Message( 0 , ACKnew , "Hello") ;
		String messageJso = MessageUtils.serialize(infoM) ; 
		out.println ( messageJso ) ; 
		// �������������ݴ���
		// ���ն���������ݽ��д���
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
