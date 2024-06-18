package p2psystem;

import java.util.*;

public class MessageQueue {
    private PriorityQueue<Message> messageQueue;
    private Map<Long, Integer> idCounts;
    private Map<Long, Integer> broadCounts;

    public MessageQueue() {
        // 初始化优先队列和ID计数器
        messageQueue = new PriorityQueue<>(Comparator.comparingLong(Message::getseq));
        idCounts = new HashMap<>();
        broadCounts = new HashMap<>() ; 
    }

    // 添加消息到队列
    public void addMessage(Message message) {
        messageQueue.offer(message); // 添加到优先队列
    }
    
    public void incrementBroadCount ( Message message ) {
    	broadCounts.put(message.getId(), 1 ) ; 
    }
    
    public int getBroadCount ( long id ) { 
    	return broadCounts.getOrDefault(id, 0);
    }
    
    // 增加id的ACK计数
    public void incrementIdCount ( Message message ) {
    	idCounts.put(message.getId(), idCounts.getOrDefault(message.getId(), 0) + 1); // 增加ID计数
    }

    // 删除指定ID的消息
    public void removeMessage(long id) {
        // 创建一个新的优先队列用于存储剩余的消息
        PriorityQueue<Message> newQueue = new PriorityQueue<>(Comparator.comparingLong(Message::getseq));
        // 从原队列中移除所有指定ID的消息，并更新ID计数
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            if (message.getId() != id) {
                newQueue.offer(message); // 将非指定ID的消息添加到新队列中
            } else {
                idCounts.put(id, 0);
            }
        }
        // 更新队列为新队列
        messageQueue = newQueue;
    }
    
    // 获取头部信息
    public Message getHeadMessage ( ) {
    	return messageQueue.peek() ; 
    }

    // 获取队列中的所有消息
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageQueue);
    }

    // 获取指定ID的消息数量
    public int getMessageCount(long id) {
        return idCounts.getOrDefault(id, 0);
    }

    // 打印队列中的所有消息
    public void printAllMessages() {
        System.out.println("All Messages in the Queue:");
        for (Message message : messageQueue) {
            System.out.println(message.toString());
        }
    }
}
