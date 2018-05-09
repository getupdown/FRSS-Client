package cn.sxy.core.data.indexLinkedList;

/**
 * 1个文件会被分成若干个页，每个页的信息在数据结构
 * 用户的输入可以被看做一个巨大的数组
 */

public class Page {

    // 这一页的末尾所在的偏移量
    private int endOffset;

    // 这一页的大小
    private int length;

    // 是否是脏页
    private boolean dirty;

    public int getEndOffset() {
        return endOffset;
    }

    public Page() {

    }

    public Page(int endOffset, int length) {
        this.endOffset = endOffset;
        this.length = length;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
