package org.akvo.search.common.entity;

public record Rule(
    String name,
    String url,
    String cssQuery,
    String replaceRegex,
    Boolean enable
) {
    public static final Rule INNER_RULE = new Rule(
        "站长之家PC 版",
        "https://tool.chinaz.com/dns?type=a&host=${website}&ip=",
        "div.w60-0.tl",
        "(\\[(.+?)]|-)"
    );

    public Rule(String name, String url, String cssQuery) {
        this(name, url, cssQuery, null, true);
    }

    public Rule(String name, String url, String cssQuery, String replaceRegex) {
        this(name, url, cssQuery, replaceRegex, true);
    }

}
