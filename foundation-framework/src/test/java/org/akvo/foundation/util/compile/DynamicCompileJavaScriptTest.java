package org.akvo.foundation.util.compile;

import org.junit.jupiter.api.Test;

class DynamicCompileJavaScriptTest {

    @Test
    void dynamicCompileJavaScript() throws Exception {
        // language=javascript
        String code = """
            var BigDecimal = Java.type('java.math.BigDecimal');

            function calculate(amount, percentage) {
                var result = new BigDecimal(amount).multiply(
                    new BigDecimal(percentage)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN);
                return result.toPlainString();
            }

            var result = calculate(568000000000000000023, 13.9);
            print(result);
            result""";
        Object o = DynamicCompileJavaScript.compile(code);
        System.out.println(o);
    }
}
