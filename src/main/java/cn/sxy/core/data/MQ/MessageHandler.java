package cn.sxy.core.data.MQ;

import java.util.Queue;

import cn.sxy.core.data.indexLinkedList.IndexList;

public class MessageHandler implements MessageTreater {

    // 消息队列
    // 生产者消费者模型
    // 用户输入生产信息，这里消费信息
    private Queue<MessageHandler> MQ;

    @Override
    public void treat(Message message, IndexList indexList) {
        // todo implement
    }
}
