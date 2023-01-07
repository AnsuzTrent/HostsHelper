package org.akvo.search.mini;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.akvo.search.mini.common.Factory;

/**
 * @author trent
 */
public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        Factory.getFactory().getView();
    }
}
