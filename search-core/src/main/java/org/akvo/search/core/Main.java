package org.akvo.search.core;

import org.akvo.foundation.ioc.Initiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fehu
 * @date 2023-06-16
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Initiation.run(Main.class, () -> log.info("init success"));
    }
}
