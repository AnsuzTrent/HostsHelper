package org.akvo.search.common.entity;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Supplier;

public class Config {
    private String proxy;
    private String backupFilePath;
    private Charset charset;
    private String flushCommand;
    private String etcDirPath;
    private String hostsPath;
    private List<Rule> rules;
    private Supplier<Boolean> twiceSearch;

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getBackupFilePath() {
        return backupFilePath;
    }

    public void setBackupFilePath(String backupFilePath) {
        this.backupFilePath = backupFilePath;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getFlushCommand() {
        return flushCommand;
    }

    public void setFlushCommand(String flushCommand) {
        this.flushCommand = flushCommand;
    }

    public String getEtcDirPath() {
        return etcDirPath;
    }

    public void setEtcDirPath(String etcDirPath) {
        this.etcDirPath = etcDirPath;
    }

    public String getHostsPath() {
        return hostsPath;
    }

    public void setHostsPath(String hostsPath) {
        this.hostsPath = hostsPath;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Supplier<Boolean> getTwiceSearch() {
        return twiceSearch;
    }

    public void setTwiceSearch(Supplier<Boolean> twiceSearch) {
        this.twiceSearch = twiceSearch;
    }
}
