package com.vcc.bigdata.collect.wal;

import com.vcc.bigdata.common.types.IdGenerator;
import com.vcc.bigdata.common.types.SequenceIdGenerator;

/**
 * Identify an WAL (write-ahead-log) file
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface WalFile {

    String absolutePath();

    boolean exists();

    boolean isReachedLimit();

    WalReader openForRead();

    WalWriter openForWrite();

    boolean delete();

    IdGenerator ID_GENERATOR = new SequenceIdGenerator();

    /**
     * Create new empty wal file
     * @return absolute path of new created file
     */
    static String createNew() {
        return "wal-" + ID_GENERATOR.generate() + ".log";
    }

    static WalWriter getWriter(WalFile file, String codec) {
        switch (codec) {
            case "simple":
                return new SimpleWriter(file);
            default:
                throw new IllegalArgumentException("Invalid wal codec");
        }
    }

    static WalReader getReader(WalFile file, String codec) {
        switch (codec) {
            case "simple":
                return new SimpleReader(file);
            default:
                throw new IllegalArgumentException("Invalid wal codec");
        }
    }
}
