package org.akvo.foundation.util.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GzipUtil {
    private static final Logger log = LoggerFactory.getLogger(GzipUtil.class);

    private GzipUtil() {
    }

    /**
     * 使用gzip进行压缩
     */
    public static String compress(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 不对其使用try-with-resource 的方式，是因为会压缩出错误的字符串，
            // 这种字符串在解压时会导致"java.io.EOFException: Unexpected end of ZLIB input stream"
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.warn("Gzip Compress Error", e);
        }
        return primStr;
    }

    /**
     * 使用gzip进行解压缩
     */
    public static String decompress(String compressedStr) {
        if (compressedStr == null || compressedStr.length() == 0) {
            return compressedStr;
        }

        final int bufferSize = 1024;
        final int endFlag = -1;
        final int offset = 0;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(compressedStr));
             GZIPInputStream gzipInputStream = new GZIPInputStream(in)) {
            int len;
            byte[] buffer = new byte[bufferSize];
            while ((len = gzipInputStream.read(buffer)) != endFlag) {
                out.write(buffer, offset, len);
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Gzip decompress Error", e);
            return compressedStr;
        }
    }

}
