package com.datacollection.collect.wal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SimpleReader implements WalReader {

    private final BufferedReader reader;

    public SimpleReader(WalFile file) {
        try {
            this.reader = new BufferedReader(new FileReader(file.absolutePath()));
        } catch (IOException e) {
            throw new WalException(e);
        }
    }

    @Override
    public byte[] next() {
        try {
            String line = reader.readLine();
            return line != null ? line.getBytes("utf-8") : null;
        } catch (IOException e) {
            throw new WalException(e);
        }
    }

    @Override
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new WalException(e);
        }
    }
}
