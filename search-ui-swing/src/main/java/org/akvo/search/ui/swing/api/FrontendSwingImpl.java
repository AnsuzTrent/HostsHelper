package org.akvo.search.ui.swing.api;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.akvo.foundation.util.serialize.datatype.JsonSerializer;
import org.akvo.foundation.util.throwing.Try;
import org.akvo.search.common.entity.Config;
import org.akvo.search.common.entity.ExecResult;
import org.akvo.search.common.entity.Rule;
import org.akvo.search.common.spi.BackendFacade;
import org.akvo.search.common.spi.FrontendFacade;
import org.akvo.search.ui.swing.annotations.ButtonAction;
import org.akvo.search.ui.swing.common.ButtonCommand;
import org.akvo.search.ui.swing.view.SwingView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FrontendSwingImpl implements FrontendFacade, ActionListener {
    private static final Logger log = LoggerFactory.getLogger(FrontendSwingImpl.class);
    private static final HashMap<String, Method> classMap = new HashMap<>(8);
    private static final String currentDirPath = System.getProperty("user.dir");
    private static final String desktopDirPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
    private static final String configFileName = "config.json";
    private static final String rulesFileName = "rules.json";
    private static final String hostsFileName = "hosts";
    private static final String backupFileNameSuffix = ".bak";
    private static final String WINDOWS_ETC = String.join(File.separator,
        "C:", "Windows", "System32", "drivers", "etc");
    private static final String LINUX_ETC = String.join(File.separator,
        "", "etc");
    private static final String MAC_ETC = String.join(File.separator,
        "", "etc");
    private SwingView view;
    private Charset charset = Charset.defaultCharset();
    private BackendFacade backend;
    private JTextField hostsTextFiled;
    private Supplier<Boolean> twice;
    private Consumer<Boolean> editableConsumer;
    private Consumer<String> infoConsumer;
    private Consumer<String> errConsumer;
    private Consumer<String> resultConsumer;
    private Runnable clearTextAreaRunnable;

    public FrontendSwingImpl() {
        List.of(this.getClass().getMethods())
            .forEach(method -> {
                ButtonAction annotation = method.getAnnotation(ButtonAction.class);
                if (annotation == null) {
                    return;
                }
                String value = annotation.value();
                classMap.put(value, method);
            });
    }

    @Override
    public void backend(BackendFacade backend) {
        this.backend = backend;
    }

    @Override
    public void publish(ExecResult result) {
        final String msg = result.msg();
        switch (result.level()) {
            case SUCCESS -> {
                log.info(msg);
                infoConsumer.accept(msg);
                finish();
            }
            case FAIL -> {
                log.warn(msg);
                infoConsumer.accept("warn: " + msg);
                finish();
            }
            case EXCEPTION -> {
                log.error(msg);
                errConsumer.accept(msg);
                finish();
            }
            case RESULT -> {
                log.info(msg);
                resultConsumer.accept(msg);
                finish();
            }
            case TERMINAL -> finish();
        }
    }

    @Override
    public Config config() {
        String etcPath = "";
        String flushCommand = "";

        if (SystemUtils.IS_OS_LINUX) {
            charset = StandardCharsets.UTF_8;
            etcPath = LINUX_ETC;
        }
        if (SystemUtils.IS_OS_MAC) {
            charset = StandardCharsets.UTF_8;
            etcPath = MAC_ETC;
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            charset = Charset.forName("GBK");
            etcPath = WINDOWS_ETC;
            flushCommand = "ipconfig /flushDNS";
        }

        Config config = Try.of(() -> String.join(File.separator, currentDirPath, configFileName))
            .map(File::new)
            .map(file -> FileUtils.readFileToString(file, charset))
            .map(str -> JsonSerializer.INSTANCE.deserialize(str, Config.class))
            .onErrorResume(t -> {
                publish(ExecResult.exception(t));
                return new Config();
            })
            .orEmptyGet(Config::new);
        config.setCharset(charset);
        config.setFlushCommand(flushCommand);
        config.setEtcDirPath(etcPath);
        config.setTwiceSearch(twice);
        config.setHostsPath(String.join(File.separator, etcPath, hostsFileName));
        if (StringUtils.isBlank(config.getBackupFilePath())) {
            config.setBackupFilePath(String.join(File.separator, desktopDirPath, hostsFileName + backupFileNameSuffix));
        }
        config.setRules(loadRules());
        return config;

    }

    public List<Rule> loadRules() {
        List<Rule> rules;
        try {
            String rulesStr = FileUtils.readFileToString(
                new File(String.join(File.separator, currentDirPath, rulesFileName)), charset);
            rules = JsonSerializer.INSTANCE.deserialize(rulesStr, List.class, Rule.class);
            return (rules == null || rules.isEmpty()
                ? List.of(Rule.INNER_RULE)
                : rules);
        } catch (IOException e) {
            publish(ExecResult.exception(e));
            return List.of(Rule.INNER_RULE);
        }
    }

    @Override
    public String name() {
        return "swing";
    }

    @Override
    public void start() {
        FlatDarculaLaf.setup();
        EventQueue.invokeLater(() -> view = new SwingView(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        exec(e.getActionCommand());
    }

    private void exec(String command) {
        log.info("button command is: {}", command);
        begin();
        CompletableFuture.runAsync(() -> {
            try {
                classMap.get(command).invoke(this);
            } catch (Exception e) {
                log.error("error occur: ", e);
            }
        }, ForkJoinPool.commonPool());
    }

    @ButtonAction(ButtonCommand.BACKUP)
    public void backup() {
        backend.backupHostsFile();
    }

    @ButtonAction(ButtonCommand.UPDATE)
    public void update() {
        backend.updateHosts();
    }

    @ButtonAction(ButtonCommand.SEARCH)
    public void query() {
        String s = hostsTextFiled.getText().trim();
        String[] tmp = s.split("/");
        String uri = (s.startsWith("http:") | s.startsWith("https:"))
            ? tmp[2]
            : tmp[0];

        hostsTextFiled.setText(uri);
        backend.searchIp(uri);
    }

    @ButtonAction(ButtonCommand.FLUSH_DNS)
    public void flushDns() {
        backend.flushDnsCache();
    }

    @ButtonAction(ButtonCommand.OPEN_FOLDER)
    public void openHostsFolder() {
        try {
            Desktop.getDesktop()
                .open(new File(config().getEtcDirPath()));
        } catch (IOException e) {
            publish(ExecResult.exception(e));
        }
    }

    private void begin() {
        editableConsumer.accept(false);
        clearTextAreaRunnable.run();
    }

    private void finish() {
        editableConsumer.accept(true);
    }

    public void setInfoConsumer(Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
    }

    public void setErrConsumer(Consumer<String> errConsumer) {
        this.errConsumer = errConsumer;
    }

    public void setResultConsumer(Consumer<String> resultConsumer) {
        this.resultConsumer = resultConsumer;
    }

    public void setEditableConsumer(Consumer<Boolean> editableConsumer) {
        this.editableConsumer = editableConsumer;
    }

    public void setTwice(Supplier<Boolean> twice) {
        this.twice = twice;
    }

    public void setClearTextAreaRunnable(Runnable clearTextAreaRunnable) {
        this.clearTextAreaRunnable = clearTextAreaRunnable;
    }

    public void setHostsTextFiled(JTextField hostsTextFiled) {
        this.hostsTextFiled = hostsTextFiled;
    }
}
