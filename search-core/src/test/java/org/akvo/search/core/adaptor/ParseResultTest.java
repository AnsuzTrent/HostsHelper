package org.akvo.search.core.adaptor;

import org.akvo.search.common.entity.Rule;
import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


class ParseResultTest {
    private static final Logger log = LoggerFactory.getLogger(ParseResultTest.class);
    private static final long TIMEOUT_MILLIS = 10 * 1000L;

    @Test
    void testNoRule() throws IOException {
        var parse = ParseResult.parse("github.com", null);
        System.out.println(parse);
        Assertions.assertNotNull(parse);
    }

    @Test
    void testGetResult() throws IOException {
        List<String> parse = ParseResult.parse("github.com", Rule.INNER_RULE);
        System.out.println(parse);
        Assertions.assertNotNull(parse);
    }

    @Test
    void name() throws Exception {
        String url = "github.com";
        String url1 = "https://tool.chinaz.com/dns?type=a&host=" + url + "&ip=";

        htmlunit(url1);
    }

    void htmlunit(String url) throws Exception {
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            WebClientOptions webClientOptions = webClient.getOptions();

            webClientOptions.setUseInsecureSSL(true);
            // 禁用CSS
            webClientOptions.setCssEnabled(false);
            // 启用JS 解释器
            webClientOptions.setJavaScriptEnabled(true);
            // JS 错误时不抛出异常
            webClientOptions.setThrowExceptionOnScriptError(false);
            webClientOptions.setThrowExceptionOnFailingStatusCode(false);
            webClientOptions.setDoNotTrackEnabled(true);
            // 连接超时时间
            webClientOptions.setTimeout(5 * 1000);
            // 支持ajax
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.setJavaScriptTimeout(5 * 1000);
            // 等待后台运行 todo 这里卡一下
            webClient.waitForBackgroundJavaScript(600 * 1000);
            // todo 这里卡很久
            HtmlPage page = webClient.getPage(url);


            String xml = page.asXml();
            System.out.println(xml);
        }

    }
}
