package cn.sxy.core.data.MQ;

import java.util.Queue;

import org.springframework.stereotype.Service;

import cn.sxy.core.file.BaseFile;

@Service
public class FileMessageHandlerImpl implements FileMessageHandler {

    // 消息队列
    // 生产者消费者模型
    // 用户输入生产信息，这里消费信息
    private volatile Queue<Message> MQ;

    // 这个文件所对应的
    private volatile BaseFile baseFile;

    @Override
    public void treat() {
        Message msg;

        // 循环处理信息，映射至文件
        while ((msg = MQ.peek()) != null) {
            treatMessage(msg, baseFile);
        }
    }

    @Override
    public void Receive(Message message) {
        // 在界面层产生信息，receive进队列，待处理
        MQ.add(message);
    }

    private void treatMessage(Message message, BaseFile baseFile) {
        switch (message.getType()) {
            case TEXT_INSERT:
                baseFile.addString(message.getOffset(), message.getLength());
                break;
            case TEXT_DELETE:
                baseFile.removeString(message.getOffset(), message.getLength());
                break;
            case UPLOAD:
                // todo 这里先不考虑本地存储的问题, 先强制和服务器联通
                break;
        }
    }
}
