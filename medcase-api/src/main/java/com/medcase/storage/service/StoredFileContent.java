package com.medcase.storage.service;

import com.medcase.storage.pojo.FileAttachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 已存储文件的流式内容。
 *
 * @author suyh
 */
@RequiredArgsConstructor
@Getter
public class StoredFileContent implements Closeable {
    private final FileAttachment file;
    private final InputStream inputStream;

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
