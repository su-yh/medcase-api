package com.medcase.storage.service;

import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 已存储文件的流式内容。
 *
 * @author suyh
 */
@Data
public class StoredFileContent implements Closeable {
    private final FileAttachment file;
    private final InputStream inputStream;

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
