package cn.sxy.core.data.MQ;

/**
 * 消息队列处理的消息实体
 */

public class Message {

    private MessageType type;

    private int offset;

    private int length;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    enum MessageType {
        TEXT_INSERT(1, "插入"),
        TEXT_DELETE(2, "删除"),
        UPLOAD(3, "上传");

        private final int num;
        private final String actionName;

        MessageType(int num, String actionName) {
            this.num = num;
            this.actionName = actionName;
        }
    }
}
