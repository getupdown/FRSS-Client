package cn.sxy.core.data.LST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected static class Node {

        private static int DONT_SET = -10;

        private static int DONT_ADD = 0;

        // 线段树左右孩子，父亲节点
        Node left;
        Node right;
        Node par;
        // 所包含叶子个数
        int leafCnt;
        // 右偏移量
        int tailOffset;

        // 链表next指针
        Node next;

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
            setOffset = DONT_SET;
            addFlag = DONT_ADD;
        }

        public int getTailOffset() {
            return tailOffset;
        }

        public void setTailOffset(int tailOffset) {
            this.tailOffset = tailOffset;
        }

        public boolean isLeaf() {
            return !(ele == null);
        }

        // 下推标记
        public void pushDown() {

            if (setOffset != DONT_SET) {
                // 如果说是叶子,记录一下前后差值
                int cha = setOffset - tailOffset;

                tailOffset = setOffset;
                if (left != null) {
                    left.setOffset = setOffset;
                    // 原本有add的标记, add标记被覆盖
                    left.addFlag = 0;
                }
                if (right != null) {
                    right.setOffset = setOffset;
                    // 原本有add的标记, add标记被覆盖
                    right.addFlag = 0;
                }
                // set操作只会操作在被删除的那几段上,所以可以利用这个来更新
                if (isLeaf()) {
                    int save = (ele.getLength() + cha);
                    // 如果长度小于0了 就说明这段完全没了
                    ele.setLength(save >= 0 ? save : 0);
                    ele.setEndOffset(tailOffset);
                }
                setOffset = DONT_SET;
            }

            if (addFlag != DONT_ADD) {
                tailOffset += addFlag;
                if (left != null) {
                    left.addFlag += addFlag;
                }
                if (right != null) {
                    right.addFlag += addFlag;
                }
                if (isLeaf()) {
                    ele.setEndOffset(tailOffset);
                }
                addFlag = DONT_ADD;
            }
        }

        // 反向更新时的pushup操作
        public void pushUp() {
            if (isLeaf()) {
                return;
            }
            left.pushDown();
            right.pushDown();
            int cnt = left.leafCnt + right.leafCnt;
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

        FindDto findDto = new FindDto();
        findDto.offset = offset;

        // 当前页是第几页
        int rk = findByOffset(findDto);
        assert findDto.node != null;

        Node tar = findDto.node;

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
            // 叶子总数
            int sum = treeRoot.leafCnt;
            // 把[rk+1, sum]所有增加length
            updateAdd(1, sum, rk + 1, sum, length, treeRoot);

        } else if (nextLen > PAGE_MAX_SIZE) {
            // 需要分裂
            // 如果碰到用户大量复制的情况，这里会退化
            // 可以考虑重构策略

            logger.info("需要分裂");
            // todo 通过next指针判断下一页是否是空页，利用空页
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
    public void removeString(int offset, int length) {
        // 首先找到首尾所在的页
        // 设首为head, 尾为tail
        logger.info("删掉从{}以后{}个字符", offset, length);

        FindDto dto1 = new FindDto();
        dto1.offset = offset;
        int headrk = findByOffset(dto1);
        // 这里指的是被删除的最后一个字符, offset + length 开始的字符都是存在的
        FindDto dto2 = new FindDto();
        dto2.offset = offset + length - 1;
        int tailrk = findByOffset(dto2);

        // 如果两页是同一页
        // 那么只要改这一页的endoffset和length
        // update(tailrk + 1, end, -length)即可
        if (headrk == tailrk) {
            int preOffset = dto1.node.ele.getEndOffset();
            int preLen = dto1.node.ele.getLength();

            dto1.node.tailOffset = preOffset - length;
            dto1.node.ele.setEndOffset(preOffset - length);
            dto1.node.ele.setLength(preLen - length);
            //todo 建立一个page到node的反向指针
            setDirtyPage(dto1.node.ele);

            reverseUpdateFromBottom(dto1.node);

            updateAdd(1, treeRoot.leafCnt, tailrk + 1, treeRoot.leafCnt, -length, treeRoot);
        } else {
            // 如果两页不是同一页
            // 即中间有些页被完全置空
            // 首先把尾页分裂,左边是被删除的部分，右边是保留的部分
            // 那么tailrk就是原来的被删除的最后一页
            // tailrk + 1是新页
            // 那么就相当于 [headrk~tailrk]整体一次set成offset-1操作, 他们的长度会在过程中被更新
            // [tailrk + 1, end] 就是整体的add操作, 尾偏移量减去length即可,长度不用变

            // 1.尾页分裂
            Node now = dto2.node;
            int nowLen = dto2.node.ele.getLength();

            int nowLeftOffset = now.ele.getEndOffset() - now.ele.getLength() + 1;

            int newLen = dto2.offset - nowLeftOffset + 1;

            // 如果新页不为空，再分裂
            if (nowLen - newLen > 0) {
                now.left = new Node(now, true);
                now.right = new Node(now, true);
                // 设置左边
                now.left.ele.setEndOffset(dto2.offset);
                now.left.tailOffset = dto2.offset;
                now.left.ele.setLength(newLen);
                now.left.leafCnt = 1;
                // 设置右边
                now.right.ele.setEndOffset(dto2.node.tailOffset);
                now.right.tailOffset = dto2.node.tailOffset;
                now.right.ele.setLength(nowLen - newLen);
                now.right.leafCnt = 1;

                setDirtyPage(now.left.ele);
                setDirtyPage(now.right.ele);
                now.ele = null;
            } else {
                setDirtyPage(now.ele);
            }
            reverseUpdateFromBottom(now);

            // 2.分裂完之后，两次操作分解
            // todo 空页回收机制
            updateSet(1, treeRoot.leafCnt, headrk, tailrk, offset - 1, treeRoot);

            updateAdd(1, treeRoot.leafCnt, tailrk + 1, treeRoot.leafCnt, -length, treeRoot);
        }

    }

    private void segTreeSearch(int nl, int nr, int tl, int tr, int value, Node o, Function function) {
        o.pushDown();
        if (tl > tr) {
            return;
        }

        if (nl > tr || nr < tl) {
            return;
        }
        if (nl >= tl && nr <= tr) {
            function.apply(o);
            return;
        }
        segTreeSearch(nl, nr - o.right.leafCnt, tl, tr, value, o.left, function);
        segTreeSearch(nl + o.left.leafCnt, nr, tl, tr, value, o.right, function);
        o.pushUp();
    }

    // 线段树常规操作
    private void updateSet(int nl, int nr, int tl, int tr, int value, Node o) {
        segTreeSearch(nl, nr, tl, tr, value, o, (tar) -> {
            tar.setOffset = value;
            tar.pushDown();
        });
    }

    // 线段树常规操作
    private void updateAdd(int nl, int nr, int tl, int tr, int value, Node o) {
        segTreeSearch(nl, nr, tl, tr, value, o, (tar) -> {
            tar.addFlag += value;
            tar.pushDown();
        });
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

    // 在一些特殊情况下需要强制更新某个叶子的值
    private void reverseUpdateForce(Node node) {
        List<Node> nodes = new ArrayList<>();

        Node tmp = node;
        while (tmp != null) {
            nodes.add(tmp);
            tmp = tmp.par;
        }

        // 自顶向下更新
        for (int i = nodes.size() - 1; i >= 0; i--) {
            nodes.get(i).pushDown();
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
    public IndexList build(List<Page> list) {
        // 从1开始
        Node start = new Node(null, false);
        __build(list, 1, list.size(), start, null);
        treeRoot = start;
        return this;
    }

    int testa = 0;
    int fuck = 0;

    public void fortestReverse(Node o) {
        o.pushDown();
        if (o.isLeaf()) {
            // logger.info("第{}页,尾偏移量{},长度为{}", testa, o.tailOffset, o.ele.getLength());
            testa++;
            return;
        }
        int tmp = fuck;
        fuck++;
        System.out.println(tmp + " -> " + (fuck));
        fortestReverse(o.left);
        fuck++;
        System.out.println(tmp + " -> " + (fuck));
        fortestReverse(o.right);
        o.pushUp();
    }

    public void fortest() {
        testa = 1;
        fuck = 1;
        fortestReverse(treeRoot);
        logger.info("————————————————————");
    }

    @FunctionalInterface
    private interface Function {
        void apply(Node o);
    }

    // 测试
    public static void main(String args[]) {

    }
}
