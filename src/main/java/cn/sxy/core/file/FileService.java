package cn.sxy.core.file;

import java.util.List;

/**
 * 文件操作相关接口
 */

public interface FileService {
    // 上传文件
    void upload(BaseFile file);

    // 从硬盘读取文件列表
    List<BaseFile> localLoad(BaseFile file);

    // 从远程读取文件列表
    List<BaseFile> loadRemote();
}
