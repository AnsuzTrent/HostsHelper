package org.akvo.search.core.service;

import org.akvo.foundation.ioc.anontations.Autowired;
import org.akvo.foundation.ioc.anontations.Component;
import org.akvo.foundation.util.collection.StreamUtil;
import org.akvo.foundation.util.throwing.Throwing;
import org.akvo.search.common.entity.Config;
import org.akvo.search.common.entity.ExecResult;
import org.akvo.search.common.entity.Rule;
import org.akvo.search.common.spi.BackendFacade;
import org.akvo.search.common.spi.FrontendFacade;
import org.akvo.search.core.adaptor.ParseResult;
import org.akvo.search.core.util.IpUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Component
public class SearchBackend implements BackendFacade {
    private final FrontendFacade frontendFacade;

    @Autowired
    public SearchBackend() {
        var facade = ServiceLoader.load(FrontendFacade.class)
            .findFirst();
        if (facade.isEmpty()) {
            throw new IllegalArgumentException("");
        }
        frontendFacade = facade.get();
        frontendFacade.backend(this);
        frontendFacade.start();
    }

    @Override
    public void backupHostsFile() {
        Config config = frontendFacade.config();
        try {
            var hostsPath = new File(config.getHostsPath()).toPath();
            var backupPath = new File(config.getBackupFilePath()).toPath();
            Path copy = Files.copy(hostsPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            long size = Files.size(copy);
            frontendFacade.publish(ExecResult.success("已备份hosts 文件至: %s, 大小%sBytes".formatted(copy, size)));
        } catch (Exception e) {
            frontendFacade.publish(ExecResult.exception(e));
        } finally {
            frontendFacade.publish(ExecResult.terminal());
        }
    }

    @Override
    public void flushDnsCache() {
        Config config = frontendFacade.config();
        String flushCommand = config.getFlushCommand();
        try {
            Process process = Runtime.getRuntime().exec(flushCommand);
            String trim = new String(process.getInputStream().readAllBytes(), config.getCharset()).trim();
            frontendFacade.publish(ExecResult.success(trim));
        } catch (Exception e) {
            frontendFacade.publish(ExecResult.exception(e));
        } finally {
            frontendFacade.publish(ExecResult.terminal());
        }
    }

    @Override
    public void updateHosts() {
        Config config = frontendFacade.config();
        try {
            List<Rule> rules = loadRule(config);
            var hostsPath = new File(config.getHostsPath()).toPath();
            List<String> list = StreamUtil.streamNullable(Files.readAllLines(hostsPath))
                .filter(StringUtils::isNoneBlank)
                .filter(s -> !s.startsWith("#"))
                .filter(s -> IpUtil.IpType.EXTERNAL_V4 == IpUtil.ipType(s.split(" ")[0]))
                .map(s -> s.split(" ")[1])
                .distinct()
                .toList();
            List<String> failSite = new ArrayList<>();
            for (Rule rule : rules) {
                (failSite.isEmpty() ? list : failSite)
                    .stream()
                    .flatMap(Throwing.sneakyFunction(site -> {
                        List<String> parse = ParseResult.parse(site, rule);
                        if (!parse.isEmpty()) {
                            failSite.add(site);
                        }
                        return parse.stream()
                            .map(ip -> ip + " " + site);
                    }))
                    .forEach(msg -> frontendFacade.publish(ExecResult.success(msg)));

                if (failSite.isEmpty() || !config.getTwiceSearch().get()) {
                    break;
                }
            }

        } catch (Exception e) {
            frontendFacade.publish(ExecResult.exception(e));
        } finally {
            frontendFacade.publish(ExecResult.terminal());
        }
    }

    @Override
    public void searchIp(String site) {
        Config config = frontendFacade.config();
        try {
            List<Rule> rules = loadRule(config);
            for (Rule rule : rules) {
                List<String> parse = ParseResult.parse(site, rule);

                if (parse.isEmpty() || !config.getTwiceSearch().get()) {
                    break;
                }
                parse.stream()
                    .map(ip -> ip + " " + site)
                    .forEach(msg -> frontendFacade.publish(ExecResult.success(msg)));
            }
        } catch (IOException e) {
            frontendFacade.publish(ExecResult.exception(e));
        } finally {
            frontendFacade.publish(ExecResult.terminal());
        }
    }

    private List<Rule> loadRule(Config config) {
        return config.getRules();
    }
}
