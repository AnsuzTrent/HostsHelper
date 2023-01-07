package org.akvo.search.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class IpUtil {
    private static final Logger log = LoggerFactory.getLogger(IpUtil.class);

    /**
     * 0  -  255
     * <pre>
     *     \d        0-9
     *     [1-9]\d   10-99
     *     1\d{2}    100-199
     *     2[0-4]\d  200-249
     *     25[0-5]   250-255
     * </pre>
     */
    private static final String IPV4_PART_PATTERN_STRING = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
    /**
     * 0.0.0.0-255.255.255.255
     */
    private static final String IPV4_PATTERN_STRING = IPV4_PART_PATTERN_STRING +
        "(\\." + IPV4_PART_PATTERN_STRING + "){3}";
    /**
     * A类地址范围：10.0.0.0—10.255.255.255
     */
    private static final String A_TYPE_IPV4_STRING = String.join("\\.",
        "10", IPV4_PART_PATTERN_STRING, IPV4_PART_PATTERN_STRING, IPV4_PART_PATTERN_STRING);
    /**
     * B类地址范围: 172.16.0.0---172.31.255.255  191.255.255.255
     */
    private static final String B_TYPE_IPV4_STRING = String.join("\\.",
        "172", "(1[6789]|2\\d|3[01])", IPV4_PART_PATTERN_STRING, IPV4_PART_PATTERN_STRING)
        + "|191.255.255.255";
    /**
     * C类地址范围: 192.168.0.0---192.168.255.255
     */
    private static final String C_TYPE_IPV4_STRING = String.join("\\.",
        "192", "168", IPV4_PART_PATTERN_STRING, IPV4_PART_PATTERN_STRING);
    private static final String LOCALHOST_IPV4_STRING = "127.0.0.1|0.0.0.0";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_PATTERN_STRING);
    private static final Pattern A_TYPE_IPV4 = Pattern.compile(A_TYPE_IPV4_STRING);
    private static final Pattern B_TYPE_IPV4 = Pattern.compile(B_TYPE_IPV4_STRING);
    private static final Pattern C_TYPE_IPV4 = Pattern.compile(C_TYPE_IPV4_STRING);
    private static final Pattern LOCALHOST_IPV4 = Pattern.compile(LOCALHOST_IPV4_STRING);

    /**
     * 0 - ffff
     */
    private static final String IPV6_PART_PATTERN_STRING = "[\\dA-Fa-f]{1,4}";
    /**
     * <pre>
     * // full form of IPv6
     * fe80:0000:0000:0000:0204:61ff:fe9d:f156
     *
     * // drop leading zeroes
     * fe80:0:0:0:204:61ff:fe9d:f156
     *
     * // collapse multiple zeroes to :: in the IPv6 address
     * fe80::204:61ff:fe9d:f156
     *
     *  // IPv4 dotted quad at the end
     * fe80:0000:0000:0000:0204:61ff:254.157.241.86
     *
     * // drop leading zeroes, IPv4 dotted quad at the end
     * fe80:0:0:0:0204:61ff:254.157.241.86
     *
     *  // dotted quad at the end, multiple zeroes collapsed
     * fe80::204:61ff:254.157.241.86
     *
     * // localhost
     * ::1
     *
     *  // link-local prefix
     * fe80::
     *
     * // global unicast prefix
     * 2001::
     * </pre>
     */
    private static final String IPV6_PATTERN_STRING = "\\s*(" +
        // fe80:0000:0000:0000:0204:61ff:fe9d:f156
        "((" + IPV6_PART_PATTERN_STRING + ":){7}(" + IPV6_PART_PATTERN_STRING + "|:))" +
        // fe80:0000:0000:0000:0204:61ff:254.157.241.86
        "|((" + IPV6_PART_PATTERN_STRING + ":){6}(:" + IPV6_PART_PATTERN_STRING + "|(" + IPV4_PATTERN_STRING + ")|:))" +
        "|((" + IPV6_PART_PATTERN_STRING + ":){5}(((:" + IPV6_PART_PATTERN_STRING + "){1,2})|:(" + IPV4_PATTERN_STRING + ")|:))" +
        "|((" + IPV6_PART_PATTERN_STRING + ":){4}(((:" + IPV6_PART_PATTERN_STRING + "){1,3})|((:" + IPV6_PART_PATTERN_STRING + ")?:(" + IPV4_PATTERN_STRING + "))|:))" +
        "|((" + IPV6_PART_PATTERN_STRING + ":){3}(((:" + IPV6_PART_PATTERN_STRING + "){1,4})|((:" + IPV6_PART_PATTERN_STRING + "){0,2}:(" + IPV4_PATTERN_STRING + "))|:))" +
        "|((" + IPV6_PART_PATTERN_STRING + ":){2}(((:" + IPV6_PART_PATTERN_STRING + "){1,5})|((:" + IPV6_PART_PATTERN_STRING + "){0,3}:(" + IPV4_PATTERN_STRING + "))|:))" +
        "|((" + IPV6_PART_PATTERN_STRING + ":){1}(((:" + IPV6_PART_PATTERN_STRING + "){1,6})|((:" + IPV6_PART_PATTERN_STRING + "){0,4}:(" + IPV4_PATTERN_STRING + "))|:))" +
        "|(:(((:" + IPV6_PART_PATTERN_STRING + "){1,7})|((:" + IPV6_PART_PATTERN_STRING + "){0,5}:(" + IPV4_PATTERN_STRING + "))|:))" +
        ")(%.+)?\\s*";
    private static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_PATTERN_STRING);

    private IpUtil() {
    }

    public static List<String> matchIp(String text) {
        Set<String> result = new HashSet<>(16);
        Matcher ipv4Matcher = IPV4_PATTERN.matcher(text);
        while (ipv4Matcher.find()) {
            // 匹配获得ip 字符串
            result.add(ipv4Matcher.group(0));
        }
        Matcher ipv6Matcher = IPV6_PATTERN.matcher(text);
        while (ipv6Matcher.find()) {
            // 匹配获得ip 字符串
            result.add(ipv6Matcher.group(0));
        }

        return result.stream()
            // 判断是否为内网ip ，不是则添加
            .filter(ip -> {
                var type = ipType(ip);
                return type == IpType.EXTERNAL_V4 || type == IpType.EXTERNAL_V6;
            })
            .toList();
    }

    /**
     * 目标IP 类型
     */
    public static IpType ipType(String str) {
        if (StringUtils.isBlank(str)) {
            log.info("string is blank");
            return IpType.UNKNOWN;
        }
        Matcher ipv4Matcher = IPV4_PATTERN.matcher(str);
        Matcher ipv6Matcher = IPV6_PATTERN.matcher(str);
        boolean isIpv4 = ipv4Matcher.find();
        boolean isIpv6 = ipv6Matcher.find();

        if (isIpv4 && !isIpv6) {
            IpType ipType = ipv4Type(ipv4Matcher.group(0));
            log.info("{} type is: {}", str, ipType);
            return ipType;
        }
        if (isIpv6) {
            IpType ipType = ipv6Type(ipv6Matcher.group(0));
            log.info("{} type is: {}", str, ipType);
            return ipType;
        }

        log.info("unknown type: {}", str);
        return IpType.UNKNOWN;
    }

    private static IpType ipv4Type(String str) {
        // 过滤内网，返回类型
        return Stream.of(A_TYPE_IPV4, B_TYPE_IPV4, C_TYPE_IPV4, LOCALHOST_IPV4)
            .anyMatch(tmp -> tmp.matcher(str).find()) ?
            IpType.INTERNAL_V4 : IpType.EXTERNAL_V4;
    }

    private static IpType ipv6Type(String str) {
        return Stream.of(A_TYPE_IPV4, B_TYPE_IPV4, C_TYPE_IPV4, LOCALHOST_IPV4)
            .anyMatch(tmp -> tmp.matcher(str).find()) ?
            IpType.INTERNAL_V6 : IpType.EXTERNAL_V6;
    }

    public enum IpType {
        /**
         * 内网IPv4
         */
        INTERNAL_V4,
        /**
         * 外网IPv4
         */
        EXTERNAL_V4,
        /**
         * 内网IPv6
         */
        INTERNAL_V6,
        /**
         * 外网IPv6
         */
        EXTERNAL_V6,

        /**
         * 未知
         */
        UNKNOWN,

    }

}
