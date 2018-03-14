package com.vcc.bigdata.common.io;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FileHelper {

    public static String readAsString(String path) throws IOException {
        return readAsString(new FileInputStream(path));
    }

    public static String readAsString(InputStream is) throws IOException {
        try (Reader reader = new InputStreamReader(is)) {
            StringBuilder sb = new StringBuilder();
            char[] buff = new char[8192];
            int nReads;
            while ((nReads = reader.read(buff)) != -1) {
                sb.append(buff, 0, nReads);
            }
            return sb.toString();
        }
    }

    public static void unsafeWrite(String path, String content) {
        unsafeWrite(path, content, true);
    }

    public static void unsafeWrite(String path, String content, boolean append) {
        try {
            write(path, content, append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String path, String content) throws IOException {
        write(path, content, true);
    }

    public static void write(String path, String content, boolean append) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path, append))) {
            writer.println(content);
        }
    }

    public static void writeAsync(String path, String content, boolean append) throws IOException {
        File file = new File(path);
        StandardOpenOption openOption = append && file.exists()
                ? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW;

        AsynchronousFileChannel channel =
                AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.allocate(content.length());
        buffer.put(content.getBytes());
        channel.write(buffer, 0);
        channel.close();
    }

    public static String[] readLastLines(File file, int line) throws IOException {
        StringBuilder builder = new StringBuilder();
        int count = 0;

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = file.length() - 1;
            // Set the pointer at the last of the file
            randomAccessFile.seek(fileLength);
            for (long pointer = fileLength; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c;
                // read from the last one char at the time
                c = (char) randomAccessFile.read();
                // break when end of the line
                if (c == '\n' && pointer < fileLength) {
                    if (++count >= line) break;
                }
                builder.append(c);
            }
            // Since line is read from the last so it
            // is in reverse so use reverse method to make it right
            builder.reverse();

            return builder.toString().split("\n");
        }
    }

    public static String readLastLine(File file) throws IOException {
        String[] strings = readLastLines(file, 1);
        return strings.length > 0 ? strings[0] : "";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean checkCreateNewFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            final long lastModifiedMillis = file.lastModified();
            DateTime lastModified = new DateTime(lastModifiedMillis);
            DateTime now = DateTime.now();

            // if current day different last modified day then rename old file
            if (now.getYear() > lastModified.getYear() || now.getDayOfYear() > lastModified.getDayOfYear()) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String newPath = path + "." + df.format(new Date(lastModifiedMillis));
                file.renameTo(new File(newPath));
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkCreateDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
    }

    public static String getFileName(String absolutePath) {
        int index = absolutePath.lastIndexOf("/");
        return absolutePath.substring(index + 1);
    }
}
