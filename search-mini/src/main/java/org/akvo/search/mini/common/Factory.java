package org.akvo.search.mini.common;

import org.akvo.search.mini.controller.ActionController;
import org.akvo.search.mini.features.Backend;
import org.akvo.search.mini.view.MainView;

import java.awt.*;

/**
 * @author trent
 * @ClassName: ViewFactory
 * @Description:
 * @date 2020年08月18日
 * @since JDK 1.8
 */
public class Factory {
    private static Factory factory = null;

    private MainView view = null;
    private ActionController controller = null;
    private Backend backend = null;

    private Factory() {
    }

    public static Factory getFactory() {
        if (factory == null) {
            synchronized (Factory.class) {
                if (factory == null) {
                    factory = new Factory();
                }
            }
        }
        return factory;
    }

    public MainView getView() {
        if (view == null) {
            synchronized (Factory.class) {
                if (view == null) {
                    EventQueue.invokeLater(() -> view = new MainView());
                }
            }
        }
        return view;
    }

    public ActionController getListener() {
        if (controller == null) {
            synchronized (Factory.class) {
                if (controller == null) {
                    controller = new ActionController();
                }
            }
        }
        return controller;
    }

    public Backend getController() {
        if (backend == null) {
            synchronized (Factory.class) {
                if (backend == null) {
                    backend = new Backend();
                }
            }
        }
        return backend;
    }

}
