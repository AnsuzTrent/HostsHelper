package org.akvo.search.mini.common;

import javax.swing.*;
import java.util.List;

/**
 * @author trent
 */
public abstract class Base extends SwingWorker<Void, String> {
    protected Rule rule = null;

    @Override
    protected void process(List<String> chunks) {
        for (String s : chunks) {
            Factory.getFactory()
                .getController().printResult(s);
        }
    }

    @Override
    protected void done() {
        Factory.getFactory().getController()
            .setComponentStatusRunning(true);
    }

}
