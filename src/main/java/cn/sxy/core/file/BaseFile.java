package cn.sxy.core.file;

import java.io.File;
import java.util.List;

import cn.sxy.core.data.indexLinkedList.IndexList;
import cn.sxy.core.file.attach.BaseAttachSource;

/**
 * 描述一个文件
 */
public abstract class BaseFile {

    // 文件在硬盘里的体现
    private File file;

    // 文件附带资源
    private List<BaseAttachSource> baseAttachSources;

    // 维护文件信息的列表
    private IndexList indexList;

    // 上传
    public abstract void upload() throws Exception;

    // 读取相关资源进入内存
    public abstract void loadSources() throws Exception;

    public void addString(int offset, int length) {
        indexList.addString(offset, length);
    }

    public void removeString(int offset, int length) {
        indexList.removeString(offset, length);
    }
}
