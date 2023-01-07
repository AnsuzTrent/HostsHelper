import org.akvo.search.common.spi.FrontendFacade;

module search.core {
    requires foundation.framework;
    requires search.common;

    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.jsoup;
    requires org.slf4j;
    requires htmlunit;

    exports org.akvo.search.core.service;

    uses FrontendFacade;
}
