package org.akvo.search.mini.constant;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author trent
 * @date 2020年08月08日
 * @since JDK 1.8
 */
public interface PropertyConstant {
    String TITLE = "Search Hosts in Website";
    String SYSTEM_PROPERTY = "os.name";
    String WINDOWS_PROPERTY = "indows";
    String NONE_FLAG = "none";
    String DNS_FLUSH = "ipconfig /flushDNS";
    String INNER_RULE_STR = "[{\"name\": \"站长之家PC 版\",\"url\": \"http://tool.chinaz.com/dns/?type=1&host=${website}&ip=\",\"cssQuery\": \"div.w60-0.tl\",\"replaceRegex\": \"(\\\\[(.+?)]|-)\"}]";
    String ETC_PATH = "C:\\Windows\\System32\\drivers\\etc";
    String REPLACE_SITE = "${website}";
    String RECODE_FORMAT = "\n%s %s";
    /**
     * 系统host
     */
    File HOSTS_PATH = new File(ETC_PATH + "\\hosts");
    /**
     * 生成host
     */
    File OBTAIN_FILE = new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\hosts");
}
