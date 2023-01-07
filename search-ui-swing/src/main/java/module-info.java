import org.akvo.search.common.spi.FrontendFacade;

module search.ui.swing {
    requires java.desktop;
    requires foundation.framework;

    requires org.slf4j;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires com.formdev.flatlaf;
    requires search.common;

    provides FrontendFacade
        with org.akvo.search.ui.swing.api.FrontendSwingImpl;
}
