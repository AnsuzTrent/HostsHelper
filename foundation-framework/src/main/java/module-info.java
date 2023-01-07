module foundation.framework {
    requires java.compiler;
    requires java.scripting;

    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires micrometer.core;

    exports org.akvo.foundation.ioc;
    exports org.akvo.foundation.ioc.anontations;
    exports org.akvo.foundation.reactive;
    exports org.akvo.foundation.reactive.base;
    exports org.akvo.foundation.util.codec;
    exports org.akvo.foundation.util.collection;
    exports org.akvo.foundation.util.current;
    exports org.akvo.foundation.util.metric;
    exports org.akvo.foundation.util.serialize.datatype;
    exports org.akvo.foundation.util.throwing;
    exports org.akvo.foundation.util.throwing.function;
    exports org.akvo.foundation.util.time;
    exports org.akvo.foundation.util.tuple;

}
