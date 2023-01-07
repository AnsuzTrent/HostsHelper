package org.akvo.search.core.adaptor;

import org.akvo.search.common.entity.Rule;
import org.akvo.search.core.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ParseResult {
    private static final Logger log = LoggerFactory.getLogger(ParseResult.class);
    private static final long TIMEOUT_MILLIS = 10 * 1000L;
    private static final String REPLACE_STR = "${website}";

    private ParseResult() {
    }

    public static List<String> parse(String site, Rule rule) throws IOException {
        return parse(site, rule, null);
    }

    public static List<String> parse(String site, Rule rule, String proxy) throws IOException {
        setProxy(proxy);
        if (rule == null) {
            log.warn("rule is null");
            return new ArrayList<>();
        }
        log.info("search {} with {}", site, rule.name());
        if (StringUtils.isBlank(site)) {
            log.warn("site is null");
            return new ArrayList<>();
        }
        String url = rule.url().replace(REPLACE_STR, site);
        return getResult(getWebPage(url), rule.cssQuery(), rule.replaceRegex());
    }

    private static List<String> getResult(String pageXml, String cssQuery, String replaceRegex) {
        String pageText = Jsoup.parse(pageXml)
            .select(cssQuery)
            .text();
        log.info("get str:{}", pageText);
        if (StringUtils.isNotBlank(replaceRegex)) {
            pageText = pageText.replaceAll(replaceRegex, "");
        }

        List<String> result = IpUtil.matchIp(pageText);
        log.info("get result:{}", result);
        return result;
    }

    /**
     * 获得网页内容
     *
     * @param url 目标网址
     * @return 网页
     */
    private static String getWebPage(String url) throws IOException {
        log.info("try access {}", url);
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
            return page.asXml();
        }
    }

    private static void setProxy(String proxy) {
        if (proxy == null) {
            return;
        }
        proxy = proxy.trim();
        String[] split = proxy.split(":");
        if (split.length != 2) {
            log.info("proxy format error: {}", proxy);
            return;
        }

        String host = split[0];
        String port = split[1];
        if (IpUtil.IpType.UNKNOWN == IpUtil.ipType(host)) {
            log.info("proxy host {} unknown", proxy);
            return;
        }
        if (!StringUtils.isNumeric(port)) {
            log.info("port {} error", port);
            return;
        }

        // 设置代理
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
        log.info("set proxy: {}", proxy);
    }

}
