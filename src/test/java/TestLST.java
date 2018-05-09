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
        linkSegTree.addString(3, 1);
        linkSegTree.fortest();
        linkSegTree.removeString(3, 1);
        linkSegTree.fortest();
    }

}
