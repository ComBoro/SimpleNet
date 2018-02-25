/*
 *   SimpleNet
 *   Copyright (C) 2018  ComBoro
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.comboro.server.ui;

import net.comboro.server.Application;
import net.comboro.server.Loader;
import net.comboro.server.Server;
import net.comboro.server.command.CommandMap;
import net.comboro.server.command.CommandSender;
import net.comboro.server.command.defaults.ThisCommand;
import net.comboro.server.networking.TCPServerImpl;
import net.comboro.server.plugin.Plugin;
import net.comboro.server.plugin.PluginException;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

public class BetterUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final AttributeSet timeAset = StyleContext
            .getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY,
                    StyleConstants.Foreground, Color.GRAY);
    private static final List<String> lastCommands = new ArrayList<>();
    private JTextPane consoleTextPane;
    private JCheckBox debuggingCheckBox;
    private JTabbedPane consoleTabbedPane;
    private JTextField commandLine;
    private JMenu plugin;

    private boolean debugging = false, skipDate = false;

    private int UPpressed = 0;

    // License stuff
    private boolean licenseOpened = false;

    private JScrollPane licenseScrollPane;

    private JList<String> clientsList;

    // Creating plugin help page stuff
    private boolean pluginsTabOpened = false;

    private JScrollPane pluginHelpTabScrollPane;

    public BetterUI() {
        initComponents();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> Application.shutdown(false)));
    }

    public void append(final String str, final Color c){
        this.append(str,c,false,true);
    }

    public void append(final String str, Color color, boolean bold, boolean endLine) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color);
        StyleConstants.setBold(attributeSet, true);
        append(str,attributeSet,endLine);
    }

    public void append(final String str, AttributeSet attributeSet, boolean endLine) {
        if (str == null || str.trim().length() == 0)
            return;

        SwingUtilities.invokeLater(() -> {
            try {
                if (consoleTextPane == null)
                    return;

                Document doc = consoleTextPane.getDocument();
                String time = "";
                if(!skipDate){
                    time = "[ "
                            + DateFormat.getTimeInstance().format(
                            new Date(System.currentTimeMillis()))
                            + " ] ";

                    doc.insertString(doc.getLength(), time, timeAset);
                } else skipDate = false;

                String line = str;
                if(endLine && !line.endsWith(System.lineSeparator()))
                    line +=  System.lineSeparator();
                else
                    skipDate = true;

                doc.insertString(doc.getLength(), line, attributeSet);

                Application.log(time + line);

                consoleTextPane.setCaretPosition(consoleTextPane
                        .getDocument().getLength());
            } catch (BadLocationException e) {
                append(str, attributeSet, endLine);
            }
        });
    }

    public void clearCommandLine() {
        commandLine.setText("");
    }

    private void debuggingCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        setDebugging(debuggingCheckBox.isSelected());
    }

    public JTabbedPane getConsoleTabbedPane() {
        return consoleTabbedPane;
    }

    private void initComponents() {

        // Out & Err
        try {
            PrintStream out = new PrintStream(new ConsoleOutputStream(
                    Color.black), true, "UTF-8");
            System.setOut(out);

            PrintStream err = new PrintStream(new ConsoleOutputStream(
                    Application.error), true, "UTF-8");
            System.setErr(err);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image logoBlue = Loader.loadImage("/res/logoNoBG25x25.png");
        setIconImage(logoBlue);

        JTabbedPane clientsTabbedPane = new JTabbedPane();
        JScrollPane clientsScrollPane = new JScrollPane();
        clientsList = new JList<>();
        consoleTabbedPane = new JTabbedPane();
        JScrollPane jScrollPane1 = new JScrollPane();
        consoleTextPane = new JTextPane();
        JButton jButton1 = new JButton();
        commandLine = new JTextField();
        debuggingCheckBox = new JCheckBox();
        JMenuBar menuBar = new JMenuBar();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("SimpleNet Server");
        setMinimumSize(new java.awt.Dimension(880, 660));
        setPreferredSize(new java.awt.Dimension(880, 660));

        consoleTextPane.setEditable(false);

        clientsList.setToolTipText("Shows all the connected clients");
        clientsScrollPane.setViewportView(clientsList);

        clientsList.setModel(new AbstractListModel<>(){
            @Override
            public int getSize() {
                TCPServerImpl impl = Application.getTCPImpl();
                return impl == null ? 0 : impl.getClientList().size();
            }

            @Override
            public String getElementAt(int index) {
                TCPServerImpl impl = Application.getTCPImpl();
                return impl == null ? "[NULL IMPL]" : impl.getClientList().get(index).getThreadName();
            }
        });

        clientsTabbedPane.addTab("Clients", clientsScrollPane);

        jScrollPane1.setViewportView(consoleTextPane);

        consoleTabbedPane.addTab("Console", jScrollPane1);

        jButton1.setText("Execute");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        debuggingCheckBox.setText("Debugging");
        debuggingCheckBox
                .addActionListener(this::debuggingCheckBoxActionPerformed);

        commandLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_ENTER:
                        onButtonClick();
                        break;
                    case KeyEvent.VK_UP:
                        commandLine.setText(onArrowClick(true));
                        break;
                    case KeyEvent.VK_DOWN:
                        commandLine.setText(onArrowClick(false));
                        break;
                }
            }
        });

        menuBar = new JMenuBar();

        JMenu thisMenu = new JMenu("Server");

        JMenuItem internalRestart = new JMenuItem("Internal Restart");
        internalRestart.addActionListener(ae -> {
            Application.getPluginLoader().reloadAll();
            ThisCommand.clear();
        });
        thisMenu.add(internalRestart);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(ae -> Application.shutdown(true));
        thisMenu.add(exit);

        menuBar.add(thisMenu);

        // Plugin Menu
        plugin = new JMenu("Plugin");

        JMenuItem plugin_import = new JMenuItem("Import");
        plugin_import.addActionListener(ae -> {
            if (Application.getPluginLoader() == null)
                return;
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.showOpenDialog(null);
            File file = fc.getSelectedFile();
            try {
                if (file != null)
                    Application.getPluginLoader().load(file);
            } catch (PluginException e1) {
                Server.error(e1.getMessage());
            }
        });
        plugin.add(plugin_import);

        JMenuItem plugin_importAll = new JMenuItem("Import directory");
        plugin_importAll.addActionListener(ae -> {
            if (Application.getPluginLoader() == null)
                return;
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.showOpenDialog(null);
            File file = fc.getSelectedFile();
            if (file != null)
                Application.getPluginLoader().loadAll(file);
        });
        plugin.add(plugin_importAll);

        plugin.addSeparator();

        JMenuItem plugin_loadAll = new JMenuItem("Load all");
        plugin_loadAll.addActionListener(ae -> {
            if (Application.getPluginLoader() != null)
                Application.getPluginLoader().loadAll();
        });
        plugin.add(plugin_loadAll);

        JMenuItem plugin_reloadAll = new JMenuItem("Reload all");
        plugin_reloadAll.addActionListener(ae -> {
            if (Application.getPluginLoader() != null)
                Application.getPluginLoader().reloadAll();
        });
        plugin.add(plugin_reloadAll);

        JMenuItem plugin_unloadAll = new JMenuItem("Unload all");
        plugin_unloadAll.addActionListener(ae -> {
            if (Application.getPluginLoader() != null) {
                Application.getPluginLoader().unloadAll();
                BetterUI.this.updatePluginsPane();
            }
        });
        plugin.add(plugin_unloadAll);

        plugin.addSeparator();

        updatePluginsPane();

        menuBar.add(plugin);

        JMenu about = new JMenu("About");

        JMenuItem help = new JMenuItem("Help");
        help.addActionListener(event ->
                CommandMap.dispatch(CommandSender.UI, "help all")
        );
        about.add(help);

        JMenuItem legal = new JMenuItem("Legal");
        legal.addActionListener(ae -> openLicense());
        about.add(legal);

        menuBar.add(about);

        setJMenuBar(menuBar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(clientsTabbedPane,
                                        GroupLayout.PREFERRED_SIZE, 190,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(consoleTabbedPane)
                                                .addGroup(
                                                        GroupLayout.Alignment.TRAILING,
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        debuggingCheckBox)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED,
                                                                        488,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(
                                                                        jButton1))
                                                .addComponent(commandLine))
                                .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        consoleTabbedPane)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(
                                                                        commandLine,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        30,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(
                                                                                        jButton1)
                                                                                .addComponent(
                                                                                        debuggingCheckBox)))
                                                .addComponent(
                                                        clientsTabbedPane,
                                                        GroupLayout.Alignment.TRAILING,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        621, Short.MAX_VALUE))
                                .addContainerGap()));

        pack();
        setVisible(true);
    }

    public void updatePluginsPane() {
        if(plugin == null || Application.getPluginMap() == null) {
            return;
        }
        Set<Plugin> pset = new HashSet(Application.getPluginMap().getPlugins());
            while(plugin.getItemCount() > 7)
                plugin.remove(7);
            for (Plugin plg : pset)
                plugin.add(genMenu(plg));

    }

    private JMenu genMenu(Plugin plg) {
        JMenu temp = new JMenu(plg.getName());

        JMenuItem reload = new JMenuItem("Reload");
        reload.addActionListener(ae -> {
            if (Application.getPluginMap().getPlugins().contains(plg))
                Application.getPluginLoader().reload(plg);

            plugin.remove(temp);
        });
        temp.add(reload);

        JMenuItem unload = new JMenuItem("Unload");
        unload.addActionListener(ae -> {
            if (Application.getPluginMap().getPlugins().contains(plg))
                Application.getPluginLoader().unload(plg);

             plugin.remove(temp);

        });
        temp.add(unload);

        return temp;
    }

    public boolean isDebugging() {
        return debugging;
    }

    private void setDebugging(boolean debugging) {
        debuggingCheckBox.setSelected(debugging);
        Application.setDebugging(debugging);
        if (debugging) {
            append("Debugging is turned on.", Color.GREEN);
        } else if (this.debugging) {
            append("Debugging is turned off.", Color.RED);
        }
        this.debugging = debugging;

    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        onButtonClick();
    }

    private String onArrowClick(boolean isUParrow) {
        int size = lastCommands.size();
        if (size == 0)
            return "";
        if (isUParrow) {
            UPpressed++;

            int result = size - UPpressed;

            if (result < 0) {
                UPpressed--;
                try {
                    return lastCommands.get(0);
                } catch (IndexOutOfBoundsException e) {
                    return "";
                }
            }

            return lastCommands.get(result);
        } else {
            UPpressed--;
            int arraySize = size - 1;

            int result = size - UPpressed;

            if (result > arraySize) {
                UPpressed++;
                try {
                    return lastCommands.get(arraySize);
                } catch (IndexOutOfBoundsException e) {
                    return "";
                }
            }

            try {
                return lastCommands.get(result);
            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        }
    }

    private void onButtonClick() {
        String command = commandLine.getText().trim();
        CommandMap.dispatch(CommandSender.CONSOLE, command);
        clearCommandLine();
        if (!command.equals(""))
            lastCommands.add(command);
    }

    private void openLicense() {
        SwingUtilities
                .invokeLater(() -> {
                    try {
                        if (!licenseOpened) {
                            if (licenseScrollPane == null) {
                                JTextPane tf = new JTextPane();
                                tf.setEditable(false);
                                licenseScrollPane = new JScrollPane(tf);
                                Document doc = tf.getDocument();

                                AttributeSet simple = new SimpleAttributeSet();

                                File licenseFile = new File("resources/license.txt");
                                BufferedReader buff = new BufferedReader(new FileReader(licenseFile));

                                String line;

                                while ((line = buff.readLine()) != null) {
                                    doc.insertString(doc.getLength(), line
                                            + System.lineSeparator(), simple);
                                }

                                StyledDocument docm = tf.getStyledDocument();
                                SimpleAttributeSet center = new SimpleAttributeSet();
                                StyleConstants.setAlignment(center,
                                        StyleConstants.ALIGN_CENTER);
                                docm.setParagraphAttributes(0, doc.getLength(),
                                        center, false);

                                tf.setCaretPosition(0);

                                licenseScrollPane = new JScrollPane(tf);
                            }

                            consoleTabbedPane.add("Legal", licenseScrollPane);
                        } else {
                            // Close license
                            consoleTabbedPane.remove(licenseScrollPane);
                        }

                        licenseOpened = !licenseOpened;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void clearConsole(){
        consoleTextPane.setText("");
    }

    public void updateUI(){
        clientsList.updateUI();
    }

}
