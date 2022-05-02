import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class ScrapingApi {

    private Set<String> keywordsList;
    private Set<String> checkingAccountsSet;
    private Set<String> scrapingAccountsSet;
    private Set<String> alreadyCheckedAccountsSet;
    private ArrayList<String> checkingAccountsList;
    private Set<String> statsCheckingSet;
    private String apiKey;
    private File outFile;
    private File newaccsFile;
    private File accstocheckFile;
    private File freeaccsFile;
    private File keywordsScraperFile;
    private File alreadycheckedFile;
    private int threadsCount;
    private int checkingThreadsCount;
    private int newresultsCounter = 0;
    private int checkedAccs = 0;
    private int freeAccs = 0;
    private int resultscounter = 0;
    private boolean checkAutomatically = false;
    private boolean stoppedScraping = false;
    private boolean check404 = false;
    private final boolean using_proxy = true;
    private ProxyService scrapingProxyService;
    private ProxyService checkingProxyService;
    private ProxyService keywordsScraperProxyService;
    private ProxyService statsProxyService;
    private ArrayList<Thread> checkingThreads;
    private ArrayBlockingQueue<String> checkedAccountsQueue;
    private LinkedBlockingQueue<String> accountsToCheckQueue;
    private boolean checkingDone = false;
    private Thread scrapingThread;
    private Thread checkingThread;
    private JPanel mainPanel;
    private JScrollPane scrapingProxyScrollPanel;
    private JScrollPane keywordsScrollPanel;
    private JScrollPane scrapingOutputScrollPane1;
    private JLabel scrapingProxyLabel;
    private JLabel scrapingKeywordsLabel;
    private JPanel scrapingSettingPanel;
    private JLabel scrapingThreadsLabel;
    private JLabel scrapingOutputFileLabel;
    private JLabel scrapingResultsFileLabel;
    private JTabbedPane TabbelPanel1;
    private JPanel scrapingPanel;
    private JTextArea scrapingPoxyArea;
    private JTextArea keywordsArea;
    private JTextArea scrapingResultsArea1;
    private JButton scrapingProxiesFromFileButton;
    private JButton keywordsFromFileButton;
    private JTextField threadsField;
    private JButton scrapingoutFile;
    private JButton scrapingResultsFile;
    private JPanel checkingPanel;
    private JPanel settingsPanel;
    private JButton scrapeButton;
    private JButton stopScrapeButton;
    private JTextArea scrapingResultsArea2;
    private JScrollPane scrapingOutputScrollPane2;
    private JButton newAccountsButton;
    private JLabel newAccountsLabel;
    private JPanel checkngSettingsPanel;
    private JTextField checkingThreadsArea;
    private JButton freeAccountsfileButton;
    private JLabel freeAccountsFileLabel;
    private JButton checkAccountsButton;
    private JButton stopCheckingButton;
    private JLabel accountstoCheckFileLabel;
    private JButton accountsToCheckFileButton;
    private JScrollPane checkingOutputScrollPanel1;
    private JTextArea checkingResultsArea1;
    private JScrollPane checkingOutputScrollPanel2;
    private JTextArea checkingResultsArea2;
    private JScrollPane checkingProxyPanel;
    private JTextArea checkingProxyArea;
    private JButton checkingProxiesFileButton;
    private JCheckBox scrapeAndCheckAutomaticallyCheckBox;
    private JTextArea keywordsScraperResultsArea;
    private JTextArea keywordsScraperKeywordsArea;
    private JTextArea keywordsScraperProxyArea;
    private JButton keywordsScraperScrapeButton;
    private JCheckBox keywordsScraperProxiesCheckBox;
    private JButton keywordsScraperResultsFileButton;
    private JScrollPane keywordsScraperResultsPanel;
    private JScrollPane keywordsScraperKeywordsPanel;
    private JScrollPane keywordsScraperProxyPanel;
    private JPanel keywordsScraperPanel;
    private JTextField keywordsScraperThreadsCountField;
    private JLabel keywordsScraperResultsNumberField;
    private JButton alreadyCheckedFileButton;
    private JCheckBox check404CheckBox;
    JFrame jFrame;
//    JTabbedPane tabs;
//    JPanel scrapingPanel;
//    JPanel checkingPanel;
//    JPanel settingsPanel;
//    JTextArea scrapingProxyArea;
//    JTextArea keywordsArea;

    public ScrapingApi() {
        Authenticator.setDefault(new MyAuthenticator());
        scrapingProxiesFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    try {
                        scrapingPoxyArea.setText("");
                        Scanner tempscanner = new Scanner(chooser.getSelectedFile());
                        while (tempscanner.hasNext()) {
                            scrapingPoxyArea.append(tempscanner.nextLine() + "\n");
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        scrapingResultsArea2.setText("");
                        scrapingResultsArea2.append(ex.getMessage() + "\n");
                    }
                }
            }
        });
        keywordsFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    loadKeywords(chooser.getSelectedFile());
                }
            }
        });
        scrapingoutFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    outFile = chooser.getSelectedFile();
                    scrapingoutFile.setText(chooser.getSelectedFile().getName());
                }
            }
        });
//      add exception handling
        scrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!checkAutomatically) {
                    try {
                        threadsCount = Integer.parseInt(threadsField.getText());
                        if ((keywordsList != null) & !(scrapingPoxyArea.getText().equals("")) & !(outFile == null) & !(newaccsFile == null)) {
                            scrapingThread = new Thread(() -> {
                                scrapingProxyService = new ProxyService(30000);
                                loadProxies(scrapingProxyService, scrapingPoxyArea.getText().split("\n"));
                                loadScrapingAccounts(outFile);
                                System.out.println("scraping");
                                scrape();
                            });
                            scrapingThread.start();
                            scrapeButton.setEnabled(false);

                        } else {
                            scrapingResultsArea2.setText("");
                            scrapingResultsArea2.append("Something went wrong\n" +
                                    "keywords loaded: " + (keywordsList != null) + "\n" +
                                    "Proxies loaded: " + !(scrapingPoxyArea.getText().equals("")) + "\n" +
                                    "All accounts file picked: " + !(outFile == null) + "\n" +
                                    "New accounts file picked: " + !(newaccsFile == null) + "\n");
                        }
                    } catch (NumberFormatException ex) {
                        scrapingResultsArea2.setText("");
                        scrapingResultsArea2.append("Invalid threads number" + "\n");
                    } catch (Exception ex) {
                        scrapingResultsArea2.append(ex.getMessage() + "\n");
                    }
                } else { // check automatically
                    try {
                        threadsCount = Integer.parseInt(threadsField.getText());
                        checkingThreadsCount = Integer.parseInt(checkingThreadsArea.getText());
                        if ((keywordsList != null) & !(scrapingPoxyArea.getText().equals("")) & !(outFile == null) & !(newaccsFile == null) & !(checkingProxyArea.getText().equals("")) & (freeaccsFile != null)) {
                            scrapingThread = new Thread(() -> {
                                scrapingProxyService = new ProxyService(30000);
                                loadProxies(scrapingProxyService, scrapingPoxyArea.getText().split("\n"));
                                loadScrapingAccounts(outFile);
                                System.out.println("scraping");
                                scrape();
                            });
                            scrapingThread.start();
                            scrapeButton.setEnabled(false);

                            checkingThread = new Thread(() -> {
                                checkingProxyService = new ProxyService(10000);
                                loadProxies(checkingProxyService, checkingProxyArea.getText().split("\n"));
                                System.out.println("cecking");
                                checkAccounts(checkingThreadsCount);
                            });
                            checkingThread.start();
                            checkAccountsButton.setEnabled(false);

                        } else {
                            scrapingResultsArea2.setText("");
                            scrapingResultsArea2.append("Something went wrong\n" +
                                    "keywords loaded: " + (keywordsList != null) + "\n" +
                                    "Proxies loaded: " + !(scrapingPoxyArea.getText().equals("")) + "\n" +
                                    "All accounts file picked: " + !(outFile == null) + "\n" +
                                    "New accounts file picked: " + !(newaccsFile == null) + "\n" +
                                    "Proxies loaded: " + !(checkingProxyArea.getText().equals("")) + "\n" +
                                    "Free accounts file picked: " + !(freeaccsFile == null) + "\n");
                        }
                    } catch (NumberFormatException ex) {
                        scrapingResultsArea2.setText("");
                        scrapingResultsArea2.append("Invalid threads number" + "\n");
                    } catch (Exception ex) {
                        scrapingResultsArea2.append(ex.getMessage() + "\n");
                    }
                }
            }
        });
        newAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    newaccsFile = chooser.getSelectedFile();
                    newAccountsButton.setText(chooser.getSelectedFile().getName());
                }
            }
        });
        stopScrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stoppedScraping = true;
            }
        });
        accountsToCheckFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    accstocheckFile = chooser.getSelectedFile();
                    accountsToCheckFileButton.setText(chooser.getSelectedFile().getName());
                }

            }
        });
        freeAccountsfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    freeaccsFile = chooser.getSelectedFile();
                    freeAccountsfileButton.setText(chooser.getSelectedFile().getName());
                }
            }
        });
        checkingProxiesFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    try {
                        Scanner tempscanner = new Scanner(chooser.getSelectedFile());
                        checkingProxyArea.setText("");
                        while (tempscanner.hasNext()) {
                            checkingProxyArea.append(tempscanner.nextLine() + "\n");
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        checkingResultsArea2.setText("");
                        checkingResultsArea2.append(ex.getMessage() + "\n");
                    }
                }
            }
        });
        checkAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    checkingThreadsCount = Integer.parseInt(checkingThreadsArea.getText());
                    if (!(checkingProxyArea.getText().equals("")) & (accstocheckFile != null) & (freeaccsFile != null)) {
                        System.out.println("xd");
                        checkingThread = new Thread(() -> {
                            loadCheckingAccounts(accstocheckFile);
                            checkingProxyService = new ProxyService(10000);
                            loadProxies(checkingProxyService, checkingProxyArea.getText().split("\n"));
                            System.out.println("cecking");
                            checkAccounts(checkingThreadsCount);
                        });
                        checkingThread.start();
                        checkAccountsButton.setEnabled(false);

                    } else {
                        checkingResultsArea2.setText("");
                        checkingResultsArea2.append("Something went wrong\n" +
                                "Proxies loaded: " + !(checkingProxyArea.getText().equals("")) + "\n" +
                                "Accounts to check file picked: " + !(accstocheckFile == null) + "\n" +
                                "Free accounts file picked: " + !(freeaccsFile == null) + "\n");
                    }
                } catch (NumberFormatException ex) {
                    checkingResultsArea2.setText("");
                    checkingResultsArea2.append("Invalid threads number" + "\n");
                }
            }
        });
        scrapeAndCheckAutomaticallyCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    scrapeButton.setText("Scrape and check");
                    checkAccountsButton.setEnabled(false);
                    checkAutomatically = true;
                } else {
                    scrapeButton.setText("Scrape");
                    checkAccountsButton.setEnabled(true);
                    checkAutomatically = false;
                }
            }
        });
        stopScrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrapingThread.interrupt();
                if (checkAutomatically)
                    checkingThread.interrupt();
            }
        });
        keywordsScraperScrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (keywordsScraperFile != null) {
                        int threads = Integer.parseInt(keywordsScraperThreadsCountField.getText());
                        Thread scrapeKeywordsThread = new Thread(() -> {
                            try {
                                scrapeKeywords(threads);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
                        scrapeKeywordsThread.start();
                    } else {
                        textareaPrintln(keywordsScraperResultsArea, "choose file");
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
        keywordsScraperResultsFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    keywordsScraperFile = chooser.getSelectedFile();
                    keywordsScraperResultsFileButton.setText("Result: " + chooser.getSelectedFile().getName());
                }
            }
        });
        alreadyCheckedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnval = chooser.showDialog(mainPanel, "Select");
                if (returnval == chooser.APPROVE_OPTION) {
                    alreadycheckedFile = chooser.getSelectedFile();
                    alreadyCheckedFileButton.setText(chooser.getSelectedFile().getName());
                }
            }
        });
        stopCheckingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        check404CheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        check404CheckBox.addComponentListener(new ComponentAdapter() {
        });
        check404CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    check404 = true;
                } else {
                    check404 = false;
                }
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        ScrapingApi scrapingApi = new ScrapingApi();
        scrapingApi.loadGui();
//        scrapingApi.checkStats(new File("src/urls.txt"), new File("src/CheckingProxy.txt"));
//        scrapingApi.configureScraping("src/keywords.txt", "src/urls.txt", 10, "src/urls_no_duplicates.txt", "src/freeAccounts.txt");
//        scrapingApi.checkAccounts(25);
//        scrapingApi.scrape();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        TabbelPanel1 = new JTabbedPane();
        mainPanel.add(TabbelPanel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 7, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        scrapingPanel = new JPanel();
        scrapingPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        TabbelPanel1.addTab("Scraping", scrapingPanel);
        scrapingProxyScrollPanel = new JScrollPane();
        scrapingPanel.add(scrapingProxyScrollPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(0, 147), null, 0, false));
        scrapingPoxyArea = new JTextArea();
        scrapingPoxyArea.setMaximumSize(new Dimension(100, 200));
        scrapingPoxyArea.setRows(10);
        scrapingPoxyArea.setText("");
        scrapingProxyScrollPanel.setViewportView(scrapingPoxyArea);
        keywordsScrollPanel = new JScrollPane();
        scrapingPanel.add(keywordsScrollPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        keywordsArea = new JTextArea();
        keywordsArea.setRows(10);
        keywordsScrollPanel.setViewportView(keywordsArea);
        scrapingOutputScrollPane1 = new JScrollPane();
        scrapingOutputScrollPane1.setVerticalScrollBarPolicy(20);
        scrapingPanel.add(scrapingOutputScrollPane1, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrapingResultsArea1 = new JTextArea();
        scrapingOutputScrollPane1.setViewportView(scrapingResultsArea1);
        scrapingProxyLabel = new JLabel();
        scrapingProxyLabel.setText("Proxy");
        scrapingPanel.add(scrapingProxyLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapingKeywordsLabel = new JLabel();
        scrapingKeywordsLabel.setText("Keywords");
        scrapingPanel.add(scrapingKeywordsLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapingProxiesFromFileButton = new JButton();
        scrapingProxiesFromFileButton.setText("Load from file");
        scrapingPanel.add(scrapingProxiesFromFileButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keywordsFromFileButton = new JButton();
        keywordsFromFileButton.setText("Load from file");
        scrapingPanel.add(keywordsFromFileButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapingSettingPanel = new JPanel();
        scrapingSettingPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        scrapingPanel.add(scrapingSettingPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrapingThreadsLabel = new JLabel();
        scrapingThreadsLabel.setText("Threads");
        scrapingSettingPanel.add(scrapingThreadsLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        threadsField = new JTextField();
        threadsField.setText("");
        scrapingSettingPanel.add(threadsField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        scrapingoutFile = new JButton();
        scrapingoutFile.setText("Choose file");
        scrapingSettingPanel.add(scrapingoutFile, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapingOutputFileLabel = new JLabel();
        scrapingOutputFileLabel.setText("All accounts");
        scrapingSettingPanel.add(scrapingOutputFileLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapeButton = new JButton();
        scrapeButton.setText("Scrape");
        scrapingSettingPanel.add(scrapeButton, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopScrapeButton = new JButton();
        stopScrapeButton.setText("Stop scraping");
        scrapingSettingPanel.add(stopScrapeButton, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        scrapingSettingPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        newAccountsButton = new JButton();
        newAccountsButton.setText("Choose file");
        scrapingSettingPanel.add(newAccountsButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newAccountsLabel = new JLabel();
        newAccountsLabel.setText("New accounts");
        scrapingSettingPanel.add(newAccountsLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapeAndCheckAutomaticallyCheckBox = new JCheckBox();
        scrapeAndCheckAutomaticallyCheckBox.setText("Scrape and check automatically");
        scrapingSettingPanel.add(scrapeAndCheckAutomaticallyCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrapingOutputScrollPane2 = new JScrollPane();
        scrapingOutputScrollPane2.setVerticalScrollBarPolicy(20);
        scrapingPanel.add(scrapingOutputScrollPane2, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrapingResultsArea2 = new JTextArea();
        scrapingResultsArea2.setRows(5);
        scrapingOutputScrollPane2.setViewportView(scrapingResultsArea2);
        checkingPanel = new JPanel();
        checkingPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        TabbelPanel1.addTab("Checking", checkingPanel);
        checkngSettingsPanel = new JPanel();
        checkngSettingsPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        checkingPanel.add(checkngSettingsPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Threads");
        checkngSettingsPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkingThreadsArea = new JTextField();
        checkingThreadsArea.setText("");
        checkngSettingsPanel.add(checkingThreadsArea, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        freeAccountsfileButton = new JButton();
        freeAccountsfileButton.setText("Choose file");
        checkngSettingsPanel.add(freeAccountsfileButton, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        freeAccountsFileLabel = new JLabel();
        freeAccountsFileLabel.setText("Free accounts file");
        checkngSettingsPanel.add(freeAccountsFileLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkAccountsButton = new JButton();
        checkAccountsButton.setText("Check");
        checkngSettingsPanel.add(checkAccountsButton, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopCheckingButton = new JButton();
        stopCheckingButton.setText("Stop checking");
        checkngSettingsPanel.add(stopCheckingButton, new com.intellij.uiDesigner.core.GridConstraints(1, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        checkngSettingsPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        accountstoCheckFileLabel = new JLabel();
        accountstoCheckFileLabel.setText("Accounts to check");
        checkngSettingsPanel.add(accountstoCheckFileLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        accountsToCheckFileButton = new JButton();
        accountsToCheckFileButton.setText("Choose file");
        checkngSettingsPanel.add(accountsToCheckFileButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Already checked");
        checkngSettingsPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alreadyCheckedFileButton = new JButton();
        alreadyCheckedFileButton.setText("Choose file");
        checkngSettingsPanel.add(alreadyCheckedFileButton, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        check404CheckBox = new JCheckBox();
        check404CheckBox.setText("check404");
        checkngSettingsPanel.add(check404CheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Proxy");
        checkingPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkingOutputScrollPanel1 = new JScrollPane();
        checkingOutputScrollPanel1.setVerticalScrollBarPolicy(20);
        checkingPanel.add(checkingOutputScrollPanel1, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        checkingResultsArea1 = new JTextArea();
        checkingResultsArea1.setText("");
        checkingOutputScrollPanel1.setViewportView(checkingResultsArea1);
        checkingOutputScrollPanel2 = new JScrollPane();
        checkingOutputScrollPanel2.setVerticalScrollBarPolicy(20);
        checkingPanel.add(checkingOutputScrollPanel2, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        checkingResultsArea2 = new JTextArea();
        checkingResultsArea2.setRows(5);
        checkingResultsArea2.setText("");
        checkingOutputScrollPanel2.setViewportView(checkingResultsArea2);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setVerticalScrollBarPolicy(20);
        checkingPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JTextArea textArea1 = new JTextArea();
        textArea1.setRows(5);
        textArea1.setText("");
        scrollPane1.setViewportView(textArea1);
        checkingProxyPanel = new JScrollPane();
        checkingPanel.add(checkingProxyPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        checkingProxyArea = new JTextArea();
        checkingProxyArea.setMaximumSize(new Dimension(-1, -1));
        checkingProxyArea.setRows(10);
        checkingProxyArea.setText("");
        checkingProxyPanel.setViewportView(checkingProxyArea);
        checkingProxiesFileButton = new JButton();
        checkingProxiesFileButton.setText("Load from file");
        checkingPanel.add(checkingProxiesFileButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JButton button1 = new JButton();
        button1.setText("Load from file");
        checkingPanel.add(button1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keywordsScraperPanel = new JPanel();
        keywordsScraperPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 7, new Insets(0, 0, 0, 0), -1, -1));
        TabbelPanel1.addTab("Keywords Scraper", keywordsScraperPanel);
        keywordsScraperResultsPanel = new JScrollPane();
        keywordsScraperPanel.add(keywordsScraperResultsPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        keywordsScraperResultsArea = new JTextArea();
        keywordsScraperResultsPanel.setViewportView(keywordsScraperResultsArea);
        keywordsScraperKeywordsPanel = new JScrollPane();
        keywordsScraperPanel.add(keywordsScraperKeywordsPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        keywordsScraperKeywordsArea = new JTextArea();
        keywordsScraperKeywordsPanel.setViewportView(keywordsScraperKeywordsArea);
        keywordsScraperProxyPanel = new JScrollPane();
        keywordsScraperPanel.add(keywordsScraperProxyPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        keywordsScraperProxyArea = new JTextArea();
        keywordsScraperProxyPanel.setViewportView(keywordsScraperProxyArea);
        final JLabel label4 = new JLabel();
        label4.setText("Proxies");
        keywordsScraperPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Keywords");
        keywordsScraperPanel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Results");
        keywordsScraperPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keywordsScraperProxiesCheckBox = new JCheckBox();
        keywordsScraperProxiesCheckBox.setText("Use Proxies");
        keywordsScraperPanel.add(keywordsScraperProxiesCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(99, 37), null, 0, false));
        keywordsScraperScrapeButton = new JButton();
        keywordsScraperScrapeButton.setText("Scrape");
        keywordsScraperPanel.add(keywordsScraperScrapeButton, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(98, 37), null, 0, false));
        keywordsScraperResultsNumberField = new JLabel();
        keywordsScraperResultsNumberField.setText("0");
        keywordsScraperPanel.add(keywordsScraperResultsNumberField, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(8, 37), null, 0, false));
        keywordsScraperResultsFileButton = new JButton();
        keywordsScraperResultsFileButton.setText("Results File");
        keywordsScraperPanel.add(keywordsScraperResultsFileButton, new com.intellij.uiDesigner.core.GridConstraints(2, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(111, 37), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Number of results");
        keywordsScraperPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(110, 37), null, 0, false));
        keywordsScraperThreadsCountField = new JTextField();
        keywordsScraperThreadsCountField.setText("");
        keywordsScraperPanel.add(keywordsScraperThreadsCountField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, 37), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Threads count");
        keywordsScraperPanel.add(label8, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(87, 37), null, 0, false));
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        TabbelPanel1.addTab("Settings", settingsPanel);
        scrapingProxyLabel.setLabelFor(scrapingProxyScrollPanel);
        label3.setLabelFor(scrapingProxyScrollPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


    class MyAuthenticator extends Authenticator {

        protected PasswordAuthentication getPasswordAuthentication() {
            PasswordAuthentication passwordAuthentication = null;
            if (scrapingProxyService != null) {
                for (ScrapingProxy sp : scrapingProxyService.getProxyList()) {
                    if (sp.getAddress().equals(getRequestingHost()) & Integer.parseInt(sp.getPort()) == getRequestingPort()) {
                        passwordAuthentication = new PasswordAuthentication(sp.getUsername(), sp.getPassword().toCharArray());
                        break;
                    }
                }
            }


            if (passwordAuthentication == null & checkingProxyService != null) {
                for (ScrapingProxy sp : checkingProxyService.getProxyList()) {
                    if (sp.getAddress().equals(getRequestingHost()) & Integer.parseInt(sp.getPort()) == getRequestingPort()) {
                        passwordAuthentication = new PasswordAuthentication(sp.getUsername(), sp.getPassword().toCharArray());
                        break;
                    }
                }
            }

            if (passwordAuthentication == null & keywordsScraperProxyService != null) {
                for (ScrapingProxy sp : keywordsScraperProxyService.getProxyList()) {
                    if (sp.getAddress().equals(getRequestingHost()) & Integer.parseInt(sp.getPort()) == getRequestingPort()) {
                        passwordAuthentication = new PasswordAuthentication(sp.getUsername(), sp.getPassword().toCharArray());
                        break;
                    }
                }
            }

            if (passwordAuthentication == null & statsProxyService != null) {
                for (ScrapingProxy sp : statsProxyService.getProxyList()) {
                    if (sp.getAddress().equals(getRequestingHost()) & Integer.parseInt(sp.getPort()) == getRequestingPort()) {
                        passwordAuthentication = new PasswordAuthentication(sp.getUsername(), sp.getPassword().toCharArray());
                        break;
                    }
                }
            }

            return passwordAuthentication;
        }
    }

    private void textareaPrintln(JTextArea jt, String s) {
        SwingUtilities.invokeLater(() -> {
            jt.append(s + "\n");
        });
    }

    private void loadProxies(ProxyService ps, String[] s) {
        for (int i = 0; i < s.length; i++) {
            ps.addProxy(s[i]);
        }
    }

    public void loadGui() {
        jFrame = new JFrame("ScrapingApi");
        jFrame.setSize(900, 900);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.add(mainPanel, BorderLayout.CENTER);
//        tabs = new JTabbedPane();
//        scrapingPanel = new JPanel();
//        checkingPanel = new JPanel();
//        settingsPanel = new JPanel();
//        tabs.addTab("Scraping", scrapingPanel);
//        tabs.addTab("Checking", checkingPanel);
//        tabs.addTab("Settings", settingsPanel);
//        scrapingProxyArea = new JTextArea(60,30);
//        keywordsArea = new JTextArea(60,30);
//        JPanel scrapingGrid = new JPanel(new GridLayout(1, 3));
//        scrapingGrid.add(new JScrollPane(scrapingProxyArea));
//        scrapingGrid.add(new JScrollPane(keywordsArea));
//        scrapingPanel.add(scrapingGrid, BorderLayout.CENTER);
//        jFrame.add(tabs, BorderLayout.CENTER);
        jFrame.setVisible(true);

        Thread t = new Thread(() -> {
            while (true) {
                SwingUtilities.invokeLater(() -> {
                    checkingResultsArea1.setText("");
                    checkingResultsArea2.setText("");
                    scrapingResultsArea1.setText("");
                    scrapingResultsArea2.setText("");
                });
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void checkStats(File infile, File outfile) {

        class StatsChecker implements Callable<String[]> {

            String url;

            public StatsChecker(String url) {
                this.url = url;
            }

            @Override
            public String[] call() throws Exception {
                ScrapingProxy scrapingProxy = statsProxyService.getProxy();
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(scrapingProxy.getAddress(), Integer.parseInt(scrapingProxy.getPort())));
                Connection.Response response = Jsoup.connect("https://www.checkmoz.com/bulktool").method(Connection.Method.POST)
                        .header("authority", "www.checkmoz.com")
                        .header("accept", "*/*")
                        .header("x-requested-with", "XMLHttpRequest")
                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36")
                        .header("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .header("origin", "https://www.checkmoz.com")
                        .header("sec-fetch-site", "same-origin")
                        .header("sec-fetch-mode", "cors")
                        .header("sec-fetch-dest", "empty")
                        .header("referer", "https://www.checkmoz.com/")
                        .header("accept-language", "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7")
                        .data("getStatus", "1")
                        .data("siteID", "1")
                        .data("sitelink", url)
                        .data("da", "1")
                        .data("pa", "1")
                        .data("ml", "1")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36")
                        .proxy(proxy)
                        .execute();
//        System.out.println(response.body());
                Document doc = Jsoup.parse("<table>" + response.body() + "</table>");
//        System.out.println(doc);
                Elements elements = doc.getElementsByTag("td");
//        System.out.println(elements);
                if (elements.size() > 0) {
                    if (elements.size() == 5) {
                        String url1 = doc.getElementsByTag("a").get(0).attr("href").replace("http://", "");
                        String da = elements.get(2).text();
                        String pa = elements.get(3).text();
                        String backlinks = elements.get(4).text();
                        String[] stats = new String[2];
                        stats[0] = url;
                        stats[1] = url1 + "," + da + "," + pa + "," + backlinks;
                        return stats;
                    } else {
                        System.out.println("too few elements");
                        return null;
                    }
                } else {
                    System.out.println("0 elements");
                    return null;
                }
            }
        }
        statsProxyService = new ProxyService(new File("src/Proxy.txt"));
        ExecutorService statsExecutor = Executors.newFixedThreadPool(30);
        ExecutorCompletionService<String[]> statsCheckerCompletionService = new ExecutorCompletionService<String[]>(statsExecutor);
        BufferedWriter statsWriter = null;
        try {
            statsWriter = new BufferedWriter(new FileWriter(outfile, true));
            statsWriter.write("URL,DA,PA,Backlinks");
            statsWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            statsCheckingSet = new HashSet<String>();
            Scanner scanner = new Scanner(infile);
            while (scanner.hasNext()) {
                statsCheckingSet.add(scanner.nextLine());
            }
            scanner.close();
//            textareaPrintln(checkingResultsArea2, checkingAccountsList.size() + " accounts loaded");
            System.out.println(statsCheckingSet.size());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        while (!statsCheckingSet.isEmpty()) {
            for (String acc : statsCheckingSet) {
                statsCheckerCompletionService.submit(new StatsChecker(acc));
            }
            int setSize = statsCheckingSet.size();
            for (int i = 0; i < setSize; i++) {
                try {
                    String[] result = statsCheckerCompletionService.take().get();
                    if (!(result == null)) {
                        System.out.println(result[0]);
                        statsWriter.write(result[1]);
                        statsWriter.newLine();
                        statsCheckingSet.remove(result[0]);
                    } else {
                        System.out.println("result null");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            statsWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("stats checking ended");
    }

    private void checkMOZ(String url) throws IOException, Exception {

    }

    private void loadKeywords(File file) {

        keywordsList = new HashSet<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("")) {
                    keywordsList.add(line);
                }
            }
            bufferedReader.close();
            textareaPrintln(scrapingResultsArea2, keywordsList.size() + " keywords loaded");
        } catch (FileNotFoundException ex) {
            textareaPrintln(scrapingResultsArea2, "");
            textareaPrintln(scrapingResultsArea2, ex.getMessage() + "\n");
        } catch (IOException ex) {
            textareaPrintln(scrapingResultsArea2, "");
            textareaPrintln(scrapingResultsArea2, ex.getMessage() + "\n");
        }

    }

    private void loadKeywords() {
        String keywordsstring = keywordsArea.getText();
        String[] xd = keywordsstring.split("\n");
        keywordsList = new LinkedHashSet<>();
        keywordsList.addAll(Arrays.asList(xd));
//        System.out.println(keywordsList);
    }

    private void loadScrapingAccounts(File file) {
        try {

            scrapingAccountsSet = new HashSet<String>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                scrapingAccountsSet.add(scanner.nextLine());
            }
            scanner.close();
            textareaPrintln(scrapingResultsArea2, scrapingAccountsSet.size() + " accounts loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void loadCheckingAccounts(File file) {
        try {

            checkingAccountsList = new ArrayList<String>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                checkingAccountsList.add(scanner.nextLine());
            }
            scanner.close();
            textareaPrintln(checkingResultsArea2, checkingAccountsList.size() + " accounts loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void configureScraping(String keywordspath, int threadsCount, String accounts_set_path, String freeOutPath) {
//        this.freeOutPath = freeOutPath;
//        this.threadsCount = threadsCount;
//        loadKeywords(keywordspath);
//        loadAccounts(accounts_set_path);
//        Authenticator.setDefault(new MyAuthenticator());
    }

    private void scrapeKeywords(int threads) throws IOException {
        class keywordResults implements Callable<Set> {
            String keyword;

            keywordResults(String keyword) {
                this.keyword = keyword;
            }

            @Override
            public Set call() throws Exception {
                Document doc = null;
                if (keywordsScraperProxiesCheckBox.isSelected()) {
                    ScrapingProxy checkingProxy = keywordsScraperProxyService.getProxy();
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(checkingProxy.getAddress(), Integer.parseInt(checkingProxy.getPort())));
                    doc = Jsoup.connect("http://suggestqueries.google.com/complete/search?output=toolbar&hl=en&q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)).ignoreContentType(true).proxy(proxy).get();
                } else {
                    doc = Jsoup.connect("http://suggestqueries.google.com/complete/search?output=toolbar&hl=en&q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)).ignoreContentType(true).get();
                    Thread.sleep(500);
                }
                Elements elements = doc.select("[data]");
                Set<String> tempset = new HashSet<String>();
                for (Element e : elements) {
                    tempset.add(e.attr("data"));
                }
                return tempset;
            }
        }
        if (keywordsScraperProxiesCheckBox.isSelected() & !keywordsScraperProxyArea.getText().equals("")) {
            keywordsScraperProxyService = new ProxyService(5000);
            loadProxies(keywordsScraperProxyService, keywordsScraperProxyArea.getText().split("\n"));
        }
        BufferedWriter keywordsWriter = new BufferedWriter(new FileWriter(keywordsScraperFile));
        Set<String> keywordsScrapingSet = new HashSet<String>();
        Executor keywordsexecutor = Executors.newFixedThreadPool(threads);
        CompletionService<Set> keywordCompletionService = new ExecutorCompletionService<>(keywordsexecutor);
        String[] keywordsscraperarray = keywordsScraperKeywordsArea.getText().split("\n");
        resultscounter = 0;
        int keywordscounter = 0;
        for (String s : keywordsscraperarray) {
            keywordscounter++;
            keywordCompletionService.submit(new keywordResults(s));
            for (char a = 'a'; a <= 'z'; a++) {
                keywordCompletionService.submit(new keywordResults(s + " " + a));
                keywordscounter++;
            }
        }
        for (int i = 0; i < keywordscounter; i++) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    keywordsScraperResultsNumberField.setText(Integer.toString(resultscounter));
                }
            });
            try {
                Set<String> tempSet = keywordCompletionService.take().get();
                for (String keyword : tempSet) {
                    if (!keywordsScrapingSet.contains(keyword)) {
//                        textareaPrintln(keywordsScraperResultsArea, keyword);
                        keywordsWriter.write(keyword);
                        keywordsWriter.newLine();
                        keywordsScrapingSet.add(keyword);
                        resultscounter++;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        keywordsWriter.flush();
        System.out.println("Scraping ended");
    }

    private void scrape() {
//        scrapingProxyService = new ProxyService("src/ScrapingProxy.txt");
        newresultsCounter = 0;
        boolean a = false;
        boolean interrupted = false;
        BufferedWriter bufferedWriter = null;
        BufferedWriter bufferedWriter1 = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outFile, true));
            bufferedWriter1 = new BufferedWriter(new FileWriter(newaccsFile, true));
        } catch (IOException ex) {
            textareaPrintln(scrapingResultsArea2, ex.getMessage());
        }
        ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
        CompletionService<ScrapingResult> completionService = new ExecutorCompletionService<ScrapingResult>(exec);
        while (!keywordsList.isEmpty()) {
            int keywordsListsize = keywordsList.size();
            for (String keyword : keywordsList) {
                completionService.submit(new apiRequest(keyword));
            }
            textareaPrintln(scrapingResultsArea2, "Starting scrape, keywords to scrape " + keywordsListsize);
            for (int i = 0; i < keywordsListsize; i++) {
                try {
                    ScrapingResult scrapingResult = completionService.take().get();
                    int resultsNumber = scrapingResult.getUrls().size();
                    int newresultsNumber = 0;
                    for (String account : scrapingResult.getUrls()) {
                        try {
                            if (!(scrapingAccountsSet.contains(account))) {
                                scrapingAccountsSet.add(account);
                                bufferedWriter.write(account);
                                bufferedWriter.newLine();
                                bufferedWriter1.write(account);
                                bufferedWriter1.newLine();
                                if (checkAutomatically) {
                                    accountsToCheckQueue.put(account);
                                }
                                newresultsNumber++;
                                newresultsCounter++;
                            }
                        } catch (IOException ex) {
                            textareaPrintln(scrapingResultsArea2, "problems with writing urls to file" + "\n" + ex.getMessage());
                            System.out.println(ex.getMessage());
                        }
                    }
                    keywordsList.remove(scrapingResult.getKeyword());
                    int setSize1 = scrapingAccountsSet.size();
                    textareaPrintln(scrapingResultsArea1, resultsNumber + " new accounts scraped | " + newresultsNumber + " unique accounts scraped | " + newresultsCounter + " accounts in total" + " | " + scrapingResult.getKeyword() + " | scraping num " + i);
//                    System.out.println("Keyword: " + scrapingResult.getKeyword() + " | " + scrapingResult.getUrls().size() + " accounts scraped");
                } catch (InterruptedException ex) {
                    textareaPrintln(scrapingResultsArea2, ex.getMessage());
                    interrupted = true;
                } catch (ExecutionException ex) {
                    textareaPrintln(scrapingResultsArea2, ex.getMessage());
                    Throwable t = ex.getCause();
                    if (t instanceof SocketTimeoutException) {
                        textareaPrintln(scrapingResultsArea2, t.getMessage());
                    } else if (t instanceof HttpStatusException) {
                        HttpStatusException httpex = (HttpStatusException) t;
                        boolean b = false;
                        switch (httpex.getStatusCode()) {
                            case 500:
                                textareaPrintln(scrapingResultsArea2, "Timeout code 500");
                                break;
                            case 429:
                                textareaPrintln(scrapingResultsArea2, "code 429");
                                break;
                            case 404:
                                textareaPrintln(scrapingResultsArea2, "404 error");
                                break;
                            case 403:
                                textareaPrintln(scrapingResultsArea2, "requests limit reached 403");
                                b = true;
                                a = true;
                                break;
                            default:
                                textareaPrintln(scrapingResultsArea2, "HttpStatusException" + httpex.getStatusCode());
                        }
                    } else if (t instanceof IOException) {
                        IOException ioex = (IOException) t;
                        textareaPrintln(scrapingResultsArea2, ioex.getMessage());
                    } else {
                        textareaPrintln(scrapingResultsArea2, t.getMessage());
                    }
                }
                if (interrupted) {
                    break;
                }
            }
            if (interrupted)
                break;
        }
        textareaPrintln(scrapingResultsArea2, "Scraping ended");
        exec.shutdownNow();
        scrapingProxyService.end();
        try {
            bufferedWriter.close();
            bufferedWriter1.close();
//            BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter(resultsFile));
//            for (String acc : accountsArraylist) {
//                bufferedWriter1.write(acc);
//                bufferedWriter1.newLine();
//            }
//            bufferedWriter1.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            scrapeButton.setEnabled(true);
        });
    }

    synchronized private String getAccount() {
        String acc = null;
        if (!checkingAccountsList.isEmpty())
            acc = checkingAccountsList.get(0);
        checkingAccountsList.remove(acc);
        return acc;
    }

    private void checkAccounts(int threadsCount) {
        checkedAccountsQueue = new ArrayBlockingQueue<String>(1024);
        accountsToCheckQueue = new LinkedBlockingQueue<String>();
        checkedAccs = 0;
        freeAccs = 0;
        alreadyCheckedAccountsSet = Collections.synchronizedSet(new HashSet<String>());
        if (alreadycheckedFile != null) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(alreadycheckedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (scanner.hasNext()) {
                alreadyCheckedAccountsSet.add(scanner.nextLine());
            }
        }
        Thread checkedWriter = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!checkAutomatically)
                    textareaPrintln(checkingResultsArea2, "starting checking, " + checkingAccountsList.size() + " accounts to check");
                BufferedWriter bufferedWriter2 = null;
                try {
                    bufferedWriter2 = new BufferedWriter(new FileWriter(freeaccsFile, true));
                    while (true) {
                        bufferedWriter2.write(checkedAccountsQueue.take());
                        bufferedWriter2.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    textareaPrintln(checkingResultsArea2, "Writing accounts ended");
                    try {
                        bufferedWriter2.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        checkedWriter.start();
        checkingThreads = new ArrayList<Thread>();
        for (int i = 0; i < threadsCount; i++) {
            if (check404) {
                checkingThreads.add(new Thread(new Checker4040()));
            } else {
                checkingThreads.add(new Thread(new AccountCheck()));
            }
        }
        for (Thread t : checkingThreads) {
            t.start();
        }
        try {
            for (Thread t : checkingThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            textareaPrintln(checkingResultsArea2, "Stopping checking");
        }
        checkedWriter.interrupt();
        checkingProxyService.end();
        for (Thread t : checkingThreads) {
            t.interrupt();
        }
        if (!checkAutomatically) {
            SwingUtilities.invokeLater(() -> {
                checkAccountsButton.setEnabled(true);
            });
        }
        textareaPrintln(checkingResultsArea2, "checking ended, " + checkedAccs + " accounts checked");
    }

    class apiRequest implements Callable<ScrapingResult> {

        String keyword;

        public apiRequest(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public ScrapingResult call() throws Exception {
            Document doc;
            StringBuilder sb = new StringBuilder();
            sb.append("https://www.bing.com/search?q=")
                    .append(URLEncoder.encode(keyword, StandardCharsets.UTF_8))
                    .append("+site%3a.tumblr.com&count=50");
            ScrapingProxy scrapingProxy = scrapingProxyService.getProxy();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(scrapingProxy.getAddress(), Integer.parseInt(scrapingProxy.getPort())));
//                System.out.println(proxy);
            int urlNum = 0;
            int counter = 0;
            boolean cookiesChecker = false;
            HashSet<String> tempSet = new HashSet<String>();
            String newUrl = sb.toString();
            Connection.Response res = null;
            Map<String, String> cookies = null;
            while (counter < 20) {
                Connection connection = Jsoup.connect(newUrl)
                        .proxy(proxy)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0")
                        .timeout(70000)
                        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                        .header("accept-language", "en-US;q=0.8,en;q=0.7")
                        .header("sec-fetch-dest", "document")
                        .header("sec-fetch-mode", "navigate")
                        .header("sec-fetch-site", "none")
                        .header("upgrade-insecure-requests", "1")
                        .method(Connection.Method.POST);
                if (cookiesChecker) {
                    res = connection.cookies(res.cookies()).execute();
                } else {
                    res = connection.execute();
                    cookies = res.cookies();
                }
                doc = Jsoup.parse(res.body());
                Elements elements = doc.select("h2 a");
                for (Element el : elements) {
                    String url = null;
                    try {
                        url = new URL(el.attr("href")).getHost();
                        if (url.contains(".tumblr.com")) {
                            if (url.startsWith("www.")) {
                                tempSet.add(url.replaceFirst("www.", ""));
                            } else {
                                tempSet.add(url);
                            }
                        }
                        urlNum++;
                    } catch (MalformedURLException e) {
                    }
                }
//                    System.out.print(urlNum + " urls | ");
//                    System.out.println(tempSet.size() + " unique accounts | ");
                Element nextPage = doc.selectFirst(".sb_pagN");
                if (nextPage == null) {
//                        System.out.println("last page reached");
                    break;
                } else {
                    newUrl = "https://bing.com" + nextPage.attr("href");
//                        System.out.println(newUrl);
//                        System.out.println(res.headers());
                    cookiesChecker = true;
                }
                if (counter >= 20) {
//                        System.out.println("20 pages checked");
                }
                counter++;
            }
            return new ScrapingResult(tempSet, keyword);
        }
    }

    class AccountCheck implements Runnable {

        @Override
        public void run() {
            try {
                String account = null;
                boolean interrupted = false;
                if (checkAutomatically)
                    account = accountsToCheckQueue.take();
                else
                    account = getAccount();
                while (!(account == null) & !Thread.interrupted() & !interrupted) {
                    Random random = new Random();
                    int mailInt = random.nextInt(7000) + 1223;
                    ScrapingProxy checkingProxy = checkingProxyService.getProxy();
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(checkingProxy.getAddress(), Integer.parseInt(checkingProxy.getPort())));
                    Document doc = null;
                    String formKey = null;
                    try {
                        doc = Jsoup
                                .connect("https://www.tumblr.com/register")
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0")
                                .proxy(proxy)
                                .timeout(60000).get();
                        formKey = doc.getElementById("tumblr_form_key").attr("content");
                    } catch (IOException e) {
                        textareaPrintln(checkingResultsArea2, e.getMessage() + " during getting form key");
                    }
                    if (formKey == null) {
                        textareaPrintln(checkingResultsArea2, "null form key");
                        formKey = "123";
                    }
                    for (int i = 0; i < 100; i++) {
                        try {
                            if (!alreadyCheckedAccountsSet.contains(account)) {
                                String name = account.replace(".tumblr.com", "");
                                Connection.Response response = Jsoup
                                        .connect("https://www.tumblr.com/svc/account/register")
                                        .timeout(60000)
                                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0")
                                        .header("Accept", "application/json, text/javascript, */*; q=0.01")
                                        .header("Accept-Encoding", "gzip, deflate")
                                        .header("Accept-Language", "en-US,en;q=0.9")
                                        .header("Origin", "https://www.tumblr.com")
                                        .header("Referer", "https://www.tumblr.com/")
                                        .header("X-Requested-With", "XMLHttpRequest")
                                        .data("determine_email", "")
                                        .data("user[email]", "xd" + mailInt + "abcd@gmail.com")
                                        .data("user[password]", "Pk25hxPdhjvsskkrkawc9cp3")
                                        .data("tumblelog[name]", name)
                                        .data("user[age]=", "")
                                        .data("context", "other")
                                        .data("version", "STANDARD")
                                        .data("follow", "")
                                        .data("http_referer", "https://www.tumblr.com/privacy/consent?redirect=/register")
                                        .data("section", "signup")
                                        .data("form_key", formKey)
                                        .data("seen_suggestion", "1")
                                        .data("used_suggestion", "0")
                                        .data("used_auto_suggestion", "0")
                                        .data("about_tumblr_slide", "")
                                        .data("random_username_suggestions", "")
                                        .data("eu_resident", "1")
                                        .data("gdpr_is_acceptable_age", "1")
                                        .data("gdpr_consent_core", "1")
                                        .data("gdpr_consent_first_party_ads", "1")
                                        .data("gdpr_consent_search_history", "1")
                                        .data("vendor_consents", "")
                                        .data("action", "signup_determine")
                                        .data("action", "signup_account")
                                        .data("tracking_url", "")
                                        .data("/&tracking_version", "modal").ignoreContentType(true).ignoreHttpErrors(true)
                                        .proxy(proxy)
                                        .method(Connection.Method.POST)
                                        .execute();
                                if (response.statusCode() == 200) { //if account is free
                                    freeAccs++;
                                    if (checkAutomatically)
                                        textareaPrintln(checkingResultsArea1, "free " + name + " | " + freeAccs + " free accounts | " + checkedAccs + " accounts checked" + " | " + accountsToCheckQueue.size() + " accounts to check");
                                    else
                                        textareaPrintln(checkingResultsArea1, "free " + name + " | " + freeAccs + " free accounts | " + checkedAccs + " accounts checked");
                                    checkedAccountsQueue.put(name + ".tumblr.com");
                                } else if (response.statusCode() == 403) { //if account is taken or error
                                    if (response.body().contains("That's a good one") | response.body().contains("Someone beat you to") | response.body().contains("Try something else")) {
                                        //                                System.out.println("taken " + name);
                                    } else if (response.body().contains("There was a problem logging in")) {
                                        System.out.println("switching proxy");
                                        break;
                                    } else if (response.body().contains("email")) {
                                        mailInt = random.nextInt(7000) + 1223;
                                        break;
                                    } else {
                                        System.out.println(response.statusCode() + " " + response.body());
                                    }
                                } else {
                                    System.out.println(response.statusCode());
                                }
                            }
                            if (checkAutomatically)
                                account = accountsToCheckQueue.take();
                            else
                                account = getAccount();
                            checkedAccs++;
                            if (account == null)
                                break;
                            if (Thread.interrupted()) {
                                interrupted = true;
                                break;
                            }

                        } catch (ConnectException ex) {
                            textareaPrintln(checkingResultsArea2, ex.getMessage());
                            break;
                        } catch (IOException ex) {
                            textareaPrintln(checkingResultsArea2, ex.getMessage());
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                textareaPrintln(checkingResultsArea2, "Checking thread interrupted");
            }
            textareaPrintln(checkingResultsArea2, "Checking thread ended");
        }
    }

    class Checker4040 implements Runnable {
        @Override
        public void run() {
            String account = null;
            account = getAccount();
            try {
                while (account != null) {
                    ScrapingProxy checkingProxy = checkingProxyService.getProxy();
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(checkingProxy.getAddress(), Integer.parseInt(checkingProxy.getPort())));
                    Connection.Response response = null;
                    try {
                        response = Jsoup.connect("http://" + account).proxy(proxy).ignoreHttpErrors(true).method(Connection.Method.GET).execute();
                    } catch (IOException e) {
                        textareaPrintln(checkingResultsArea2, e.getMessage());
                    }

                    if (response != null) {
                        if (response.statusCode() == 404) {
                            checkedAccountsQueue.put(account);
                            textareaPrintln(checkingResultsArea1, "free " + account + " | " + freeAccs + " free accounts | " + checkedAccs + " accounts checked" + " | " + accountsToCheckQueue.size() + " accounts to check");
                            account = getAccount();
                            freeAccs++;
                        } else if (response.statusCode() == 200) {
                            account = getAccount();
                        } else if (response.statusCode() == 429) {
                            System.out.println("429");
                            textareaPrintln(checkingResultsArea2, "429");
                        } else {
                            System.out.println(response.statusCode());
                            account = getAccount();
                        }
                        checkedAccs++;
                    } else {
                        System.out.println("null response");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Clearner implements Runnable {
        @Override
        public void run() {

        }

    }


}
