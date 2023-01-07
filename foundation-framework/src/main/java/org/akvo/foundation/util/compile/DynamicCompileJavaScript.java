package org.akvo.foundation.util.compile;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public interface DynamicCompileJavaScript {
    static Object compile(String jsCode) throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        return nashorn.eval(jsCode);
    }
}
