package cn.sxy.core.data.LST;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sxy.core.data.MQ.Message;
import cn.sxy.core.data.indexLinkedList.IndexList;
import cn.sxy.core.data.indexLinkedList.Page;

/**
 * 链式线段树
 * 专门可以用于解决这个问题
 * 效率起见大多都写成循环式的
 */

public class LinkSegTree implements IndexList {

    private static Logger logger = LoggerFactory.getLogger(LinkSegTree.class);

    // 树根节点
    private Node treeRoot;

    // 链表头节点
    private Node linkHead;

    // 脏页列表
    private Set<Page> dirtyPageSet = new HashSet<>();

    // 节点类
    private static class Node {

        // 线段树左右孩子，父亲节点
        Node left;
        Node right;
        Node par;
        // 所包含叶子个数
        int leafCnt;
        // 右偏移量
        int tailOffset;

        // 链表前后
        Node next;
        Node prev;

        // 页元素
        Page ele;

        // 偏移量增加标记(在增加文字之后)
        int addFlag;

        // 偏移量设置标记(在删除文字之后)
        int setOffset;

        Node(Node par, boolean flag) {
            if (flag) {
                ele = new Page();
            }
            left = right = null;
            this.par = par;
            leafCnt = 0;
        }

        boolean isLeaf() {
            return !(ele == null);
        }

        // 下推标记
        void pushDown() {

            if (addFlag != 0) {
                tailOffset += addFlag;
                if (left != null) {
                    left.addFlag += addFlag;
                }
                if (right != null) {
                    right.addFlag += addFlag;
                }
                addFlag = 0;
            }

        }

        // 反向更新时的pushup操作
        void pushUp() {
            if (isLeaf()) {
                return;
            }
            left.pushDown();
            right.pushDown();
            int cnt = (left == null ? 0 : left.leafCnt) + (right == null ? 0 : right.leafCnt);
            int offset = right.tailOffset;
            this.leafCnt = cnt;
            this.tailOffset = offset;
        }
    }

    private static class FindDto {
        int offset;
        Node node;
    }

    // 根据偏移量找到页所在的编号和页本身
    private int findByOffset(FindDto dto) {
        Node now = treeRoot;
        now.pushDown();
        int acc = 0;
        int res = 0;

        while (!now.isLeaf()) {
            now.pushDown();

            now.left.pushDown();
            now.right.pushDown();

            int lo = now.left.tailOffset;
            if (dto.offset <= lo) {
                now = now.left;
            } else {
                acc += now.left.leafCnt;
                now = now.right;
            }
        }
        // 找到页
        dto.node = now;
        res = acc + 1;

        return res;
    }

    // 增加字符的逻辑
    public void addString(int offset, int length) {

        logger.info("在{}位置加入{}个字符", offset, length);

        Node tar = null;
        // 叶子总数
        int sum = treeRoot.leafCnt;

        FindDto findDto = new FindDto();
        findDto.offset = offset;

        // 当前页是第几页
        int rk = findByOffset(findDto);
        assert findDto.node != null;

        tar = findDto.node;

        // 分两种情况
        // 如果增加了这么多之后，还不用分裂的
        int nextLen = tar.ele.getLength() + length;
        if (nextLen <= PAGE_MAX_SIZE) {
            // 标记为脏页
            setDirtyPage(tar.ele);

            tar.ele.setLength(nextLen);
            tar.ele.setEndOffset(tar.ele.getEndOffset() + length);
            tar.tailOffset = tar.ele.getEndOffset();

            logger.info("不需要分裂");

            // 反向更新
            reverseUpdateFromBottom(tar);
            // 把[rk+1, sum]所有增加length
            updateAdd(1, sum, rk + 1, sum, length, treeRoot);

        } else if (nextLen > PAGE_MAX_SIZE) {
            // 需要分裂
            // 如果碰到用户大量复制的情况，这里会退化
            // 可以考虑重构策略

            logger.info("需要分裂");

            tar.left = new Node(tar, true);
            tar.right = new Node(tar, true);

            // 左边offset赋值到当前页最大
            int pageRem = PAGE_MAX_SIZE - tar.ele.getLength();

            // 设置左孩子新值
            tar.left.ele.setEndOffset(tar.ele.getEndOffset() + pageRem);
            tar.left.ele.setLength(PAGE_MAX_SIZE);
            tar.left.tailOffset = tar.left.ele.getEndOffset();
            tar.left.leafCnt = 1;
            setDirtyPage(tar.left.ele);

            tar.ele = null;

            // 偏移量累加器
            int saveOffset = tar.left.ele.getEndOffset();
            // 剩余未解决长度
            int rem = length - pageRem;

            tar = tar.right;

            // 不断分裂
            while (rem > PAGE_MAX_SIZE) {
                rk++;

                tar.left = new Node(tar, true);
                tar.right = new Node(tar, true);

                tar.ele = null;

                setDirtyPage(tar.left.ele);
                tar.left.ele.setEndOffset(saveOffset + PAGE_MAX_SIZE);
                tar.left.ele.setLength(PAGE_MAX_SIZE);
                tar.left.tailOffset = tar.left.ele.getEndOffset();
                tar.left.leafCnt = 1;

                saveOffset += PAGE_MAX_SIZE;

                rem -= PAGE_MAX_SIZE;
                tar = tar.right;
            }

            // 最后停留在一定是一个右孩子上
            setDirtyPage(tar.ele);
            tar.ele.setEndOffset(saveOffset + rem);
            tar.ele.setLength(rem);
            tar.tailOffset = tar.ele.getEndOffset();
            tar.leafCnt = 1;

            // 反向更新
            reverseUpdateFromBottom(tar);
            // 把[rk+2, sum]所有增加length
            updateAdd(1, treeRoot.leafCnt, rk + 2, treeRoot.leafCnt, length, treeRoot);
        }
    }

    // 删除字符的逻辑
    private void removeString(int offset, int length) {

    }

    private void updateAdd(int nl, int nr, int tl, int tr, int value, Node o) {

        o.pushDown();

        if (tl > tr) {
            return;
        }

        if (nl > tr || nr < tl) {
            return;
        }
        if (nl >= tl && nr <= tr) {
            o.addFlag += value;
            o.pushDown();
            return;
        }

        updateAdd(nl, nr - o.right.leafCnt, tl, tr, value, o.left);
        updateAdd(nl + o.left.leafCnt, nr, tl, tr, value, o.right);
        o.pushUp();
    }

    private void __build(List<Page> list, int lt, int rt, Node o, Node p) {

        int mid = (lt + rt) / 2;

        if (lt == rt) {
            // 为了让下表从1开始
            o.ele = list.get(lt - 1);
            o.tailOffset = o.ele.getEndOffset();
            o.leafCnt = 1;
            return;
        }
        o.left = new Node(o, false);
        __build(list, lt, mid, o.left, o);
        o.right = new Node(o, false);
        __build(list, mid + 1, rt, o.right, o);
        o.pushUp();
    }

    private void setDirtyPage(Page page) {
        page.setDirty(true);
        dirtyPageSet.add(page);
    }

    // 自底部反向更新
    private void reverseUpdateFromBottom(Node node) {
        while (node != null) {
            node.pushUp();
            node = node.par;
        }
    }

    @Override
    public IndexList getSnapshot() {
        return null;
    }

    @Override
    public Page getPageByOffset(int offset) {
        return null;
    }

    @Override
    public void updateByMessage(Message message) {

    }

    @Override
    public IndexList build(List<Page> list) {
        // 从1开始
        Node start = new Node(null, false);
        __build(list, 1, list.size(), start, null);
        treeRoot = start;
        return this;
    }

    int testa = 0;

    public void fortestReverse(Node o) {
        o.pushDown();
        if (o.isLeaf()) {
            logger.info("第{}页,尾偏移量{},长度为{}", testa, o.tailOffset, o.ele.getLength());
            testa++;
            return;
        }
        fortestReverse(o.left);
        fortestReverse(o.right);
        o.pushUp();
    }

    public void fortest() {
        testa = 1;
        fortestReverse(treeRoot);
    }

    // 测试
    public static void main(String args[]) {

    }
}
