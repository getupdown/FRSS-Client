import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cn.sxy.core.data.LST.LinkSegTree;
import cn.sxy.core.data.indexLinkedList.Page;

public class TestLST {

    LinkSegTree linkSegTree = new LinkSegTree();

    @Before
    public void constructTree() {
        List<Page> list = new ArrayList<>();
        int unit = 4;
        for (int i = 0; i < 4; i++) {
            Page page = new Page((i + 1) * 4 - 1, unit);
            list.add(page);
        }

        linkSegTree.build(list);
    }

    @Test
    public void test() {
//        linkSegTree.addString(3, 1);
//        linkSegTree.fortest();
//        linkSegTree.removeString(3, 1);
//        linkSegTree.fortest();
//        linkSegTree.removeString(3, 1);
//        linkSegTree.fortest();
//        linkSegTree.removeString(3, 1);
//        linkSegTree.fortest();
//        linkSegTree.removeString(3, 3);
//        linkSegTree.fortest();
//        linkSegTree.removeString(0, 7);
//        linkSegTree.fortest();

        add(3, 1);
        remove(3, 1);
        remove(3, 1);
        remove(3, 1);
        remove(3, 3);
        remove(0, 7);
        add(0,20);
        remove(0, 3);
        remove(0, 14);
        add(4,10);
    }

    private void add(int offset, int length) {
        linkSegTree.addString(offset, length);
        linkSegTree.fortest();
    }

    private void remove(int offset, int length) {
        linkSegTree.removeString(offset, length);
        linkSegTree.fortest();
    }

}
