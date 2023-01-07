package org.akvo.search.ui.swing.view;

import org.akvo.search.common.entity.Rule;
import org.akvo.search.ui.swing.api.FrontendSwingImpl;
import org.akvo.search.ui.swing.common.ButtonCommand;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingView extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(SwingView.class);
    private static final String TITLE = "Search Hosts in Website";
    private final JCheckBox enableTwice;
    private final JComboBox<Rule> ruleList;
    private final JTextField hostsTextFiled;
    private final JButton searchButton;
    private final JTextArea resultArea;
    private final JTextArea errorArea;
    private final JTextArea infoArea;
    private final JButton backupButton;
    private final JButton updateButton;
    private final JButton openFolderButton;
    private final JButton flushButton;
    private final FrontendSwingImpl listener;


    public SwingView(FrontendSwingImpl listener) {
        enableTwice = new JCheckBox("开启二次搜索", true);
        hostsTextFiled = new JTextField();
        searchButton = new JButton("搜索");
        ruleList = new JComboBox<>();

        resultArea = new JTextArea("请选择功能\n");
        errorArea = new JTextArea();
        infoArea = new JTextArea();

        backupButton = new JButton("备份");
        updateButton = new JButton("更新");
        openFolderButton = new JButton("打开hosts 所在文件夹");
        flushButton = new JButton("刷新DNS 配置");

        this.listener = listener;
        init();

        listener.setTwice(enableTwice::isSelected);
        listener.setEditableConsumer(editable -> {
            ruleList.setEnabled(editable);
            backupButton.setEnabled(editable);
            updateButton.setEnabled(editable);
            searchButton.setEnabled(editable);
            enableTwice.setEnabled(editable);
        });
        listener.setErrConsumer(errMsg -> {
            errorArea.append(errMsg);
            errorArea.append("\n\n");
        });
        listener.setInfoConsumer(infoMsg -> {
            infoArea.append(infoMsg);
            infoArea.append("\n\n");
        });
        listener.setResultConsumer(resultMsg -> {
            resultArea.append(resultMsg);
            resultArea.append("\n\n");
        });
        listener.setClearTextAreaRunnable(() -> {
            String msg = "\n\n\n-------------\n\n\n";
            errorArea.append(msg);
            infoArea.append(msg);
            resultArea.append(msg);
        });

        // 查验是否为Windows
        isWindows();
        loadRules();

        setVisible(true);
    }

    /**
     * 初始化
     */
    public void init() {
        // 设置顶栏、中栏、底栏
        setTop();
        setMiddle();
        setBottle();

        // 设置标题
        setTitle(TITLE);

        // 原生风格顶部透明
        setUndecorated(true);
        // 设置当前风格顶部
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        // 设置界面大小
        setSize(550, 550);
        // 设置是否可手动改变大小
        setResizable(false);
        // 居中
        setLocationRelativeTo(null);
        // 关闭窗口按钮可以关闭程序
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * 设置顶栏
     */
    private void setTop() {
        searchButton.setActionCommand(ButtonCommand.SEARCH);
        searchButton.addActionListener(listener);

        // 顶栏
        JPanel searchPanel = new JPanel(new GridLayout(1, 0));

        listener.setHostsTextFiled(hostsTextFiled);
        searchPanel.add(hostsTextFiled);
        searchPanel.add(searchButton);
        searchPanel.add(ruleList);
        searchPanel.add(enableTwice);

        add(searchPanel, BorderLayout.NORTH);
    }

    /**
     * 设置中栏
     */
    private void setMiddle() {
        // 设置只读
        resultArea.setEditable(false);
        errorArea.setEditable(false);
        infoArea.setEditable(false);

        // 设置自动换行
        resultArea.setLineWrap(true);
        errorArea.setLineWrap(true);
        infoArea.setLineWrap(true);

        // 创建滚动窗格，将文本域套进去
        JScrollPane resultPane = new JScrollPane(resultArea);
        JScrollPane errorPane = new JScrollPane(errorArea);
        JScrollPane infoPane = new JScrollPane(infoArea);

        // 中栏整体
        JPanel middlePanel = new JPanel(new GridLayout(0, 2));

        // 中栏左
        JPanel majorPanel = new JPanel(new BorderLayout());
        majorPanel.add(resultPane);

        // 中栏右上
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(infoPane);

        // 中栏右下
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorPane);

        // 中栏右
        JPanel secondPanel = new JPanel(new GridLayout(2, 0));

        secondPanel.add(infoPanel);
        secondPanel.add(errorPanel);

        middlePanel.add(majorPanel);
        middlePanel.add(secondPanel);

        add(middlePanel, BorderLayout.CENTER);
    }

    /**
     * 设置底栏
     */
    private void setBottle() {
        backupButton.setActionCommand(ButtonCommand.BACKUP);
        backupButton.addActionListener(listener);

        updateButton.setActionCommand(ButtonCommand.UPDATE);
        updateButton.addActionListener(listener);

        openFolderButton.setActionCommand(ButtonCommand.OPEN_FOLDER);
        openFolderButton.addActionListener(listener);

        flushButton.setActionCommand(ButtonCommand.FLUSH_DNS);
        flushButton.addActionListener(listener);


        // 底栏
        JPanel backup = new JPanel(new GridLayout(1, 0));

        backup.add(backupButton);
        backup.add(updateButton);
        backup.add(openFolderButton);
        backup.add(flushButton);

        add(backup, BorderLayout.SOUTH);
    }

    /**
     * 检查是否为Windows 系统
     */
    public void isWindows() {
        // 听说其在Win98,win me 中位于/Windows 下？
        if (SystemUtils.IS_OS_WINDOWS) {
            return;
        }
        infoArea.setText("目前仅支持Windows 2000/XP 及以上版本");

        openFolderButton.setEnabled(false);
        flushButton.setEnabled(false);
    }

    /**
     * 设置下拉条内容
     */
    public void loadRules() {
        List<Rule> rules = listener.loadRules();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("规则：\n");
        for (Rule r : rules) {
            stringBuilder.append("[")
                .append(r.name())
                .append("]");
            if (r.replaceRegex() == null) {
                stringBuilder.append(" (无清理正则)");
            }
            stringBuilder.append("\n");

            // 加入下拉条
            ruleList.addItem(r);
        }
        stringBuilder.append("\n");

        infoArea.append(stringBuilder.toString());
    }

}
