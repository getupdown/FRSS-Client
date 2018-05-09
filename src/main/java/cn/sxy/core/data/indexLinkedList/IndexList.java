package cn.sxy.core.data.indexLinkedList;

import java.util.List;

import cn.sxy.core.data.MQ.Message;

/**
 * 索引链表
 * 用户的客户端上会维护一个链表，里面会维护每个页的信息以及指针情况
 * 不仅仅是链表，还可以通过index索引至所在页
 */

public interface IndexList {

    // 每一页从服务器上down下来的默认大小
    Integer PAGE_DEFAULT_SIZE = 4;

    // 每一页的最大大小
    Integer PAGE_MAX_SIZE = 8;

    // 根据list构造IndexList
    IndexList build(List<Page> list);

    // 获取当前的Index快照, 用于上传
    IndexList getSnapshot();

    // 根据偏移量获取所在的页
    Page getPageByOffset(int offset);

    // 根据消息改变链表状态
    void updateByMessage(Message message);
}
