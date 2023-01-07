package org.akvo.foundation.util.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GzipUtilTest {
    private static final Logger log = LoggerFactory.getLogger(GzipUtilTest.class);

    @Test
    void testGzip() {
        String s = "123456789aqwertyuiopsdfghjklzxcvbnm";
        String compress = GzipUtil.compress(s);
        log.info(compress);
        System.out.println(compress);
        String decompress = GzipUtil.decompress(compress);
        log.info(decompress);
        System.out.println(decompress);
        Assertions.assertEquals(s, decompress);
    }

}
