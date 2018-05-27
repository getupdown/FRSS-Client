package cn.sxy.core.data.MQ;

import org.springframework.stereotype.Service;

@Service
public interface FileMessageHandler {
    // 处理消息，把消息映射到indexList上
    void treat();

    void Receive(Message message);
}
