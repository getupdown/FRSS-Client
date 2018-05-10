package cn.sxy.core.data.MQ;

import cn.sxy.core.data.indexLinkedList.IndexList;

public interface MessageTreater {
    // 处理消息，把消息映射到indexList上
    void treat(Message message, IndexList indexList);

    void Receive(Message message);
}
