package org.akvo.search.common.spi;

/**
 * 抽象的后端，提供功能接口
 */
public interface BackendFacade {
    /**
     * 备份hosts 文件
     */
    void backupHostsFile();

    /**
     * 刷新DNS 缓存
     */
    void flushDnsCache();

    /**
     * 刷新hosts 文件内容
     */
    void updateHosts();

    /**
     * 搜索URL 对应的IP
     *
     * @param url url
     */
    void searchIp(String url);
}
