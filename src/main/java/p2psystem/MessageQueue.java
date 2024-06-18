package p2psystem;

import java.util.*;

public class MessageQueue {
    private PriorityQueue<Message> messageQueue;
    private Map<Long, Integer> idCounts;
    private Map<Long, Integer> broadCounts;

    public MessageQueue() {
        // ��ʼ�����ȶ��к�ID������
        messageQueue = new PriorityQueue<>(Comparator.comparingLong(Message::getseq));
        idCounts = new HashMap<>();
        broadCounts = new HashMap<>() ; 
    }

    // �����Ϣ������
    public void addMessage(Message message) {
        messageQueue.offer(message); // ��ӵ����ȶ���
    }
    
    public void incrementBroadCount ( Message message ) {
    	broadCounts.put(message.getId(), 1 ) ; 
    }
    
    public int getBroadCount ( long id ) { 
    	return broadCounts.getOrDefault(id, 0);
    }
    
    // ����id��ACK����
    public void incrementIdCount ( Message message ) {
    	idCounts.put(message.getId(), idCounts.getOrDefault(message.getId(), 0) + 1); // ����ID����
    }

    // ɾ��ָ��ID����Ϣ
    public void removeMessage(long id) {
        // ����һ���µ����ȶ������ڴ洢ʣ�����Ϣ
        PriorityQueue<Message> newQueue = new PriorityQueue<>(Comparator.comparingLong(Message::getseq));
        // ��ԭ�������Ƴ�����ָ��ID����Ϣ��������ID����
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            if (message.getId() != id) {
                newQueue.offer(message); // ����ָ��ID����Ϣ��ӵ��¶�����
            } else {
                idCounts.put(id, 0);
            }
        }
        // ���¶���Ϊ�¶���
        messageQueue = newQueue;
    }
    
    // ��ȡͷ����Ϣ
    public Message getHeadMessage ( ) {
    	return messageQueue.peek() ; 
    }

    // ��ȡ�����е�������Ϣ
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageQueue);
    }

    // ��ȡָ��ID����Ϣ����
    public int getMessageCount(long id) {
        return idCounts.getOrDefault(id, 0);
    }

    // ��ӡ�����е�������Ϣ
    public void printAllMessages() {
        System.out.println("All Messages in the Queue:");
        for (Message message : messageQueue) {
            System.out.println(message.toString());
        }
    }
}
