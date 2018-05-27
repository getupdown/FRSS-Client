package cn.sxy.core.data.MQ;

/**
 * 消息工厂
 */
public class MessageFactory {

    public static Message newInsertMessage(int offset, int length) {
        Message message = new Message();
        message.setType(Message.MessageType.TEXT_INSERT);
        message.setOffset(offset);
        message.setLength(length);

        return message;
    }

    public static Message newRemoveMessage(int offset, int length) {
        Message message = new Message();
        message.setType(Message.MessageType.TEXT_DELETE);
        message.setOffset(offset);
        message.setLength(length);

        return message;
    }

    public static Message newUploadMessage() {
        Message message = new Message();
        message.setType(Message.MessageType.UPLOAD);

        return message;
    }
}
