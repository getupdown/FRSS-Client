package cn.sxy.core.data.MQ;

/**
 * 消息队列处理的消息实体
 */

public class Message {

    private MessageType type;

    private int offset;

    private int length;

    enum MessageType {
        INSERT(1, "插入"),
        DELETE(2, "删除");

        private final int num;
        private final String actionName;

        MessageType(int num, String actionName) {
            this.num = num;
            this.actionName = actionName;
        }
    }
}
