package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame  {
    static Pattern pattern ;
    static Matcher matcherNext;
    static int indexStart = 0;
    static int searchLength = 0;
    static int  index = 0;

    public TextEditor() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 400);
        setTitle("Prima Pagina");

        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");

        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");

        JPanel upperPanel = new JPanel();
        upperPanel.setPreferredSize(new Dimension(600,50));

        JPanel upperPanelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperPanelLeft.setPreferredSize(new Dimension(400,30));

        JPanel upperPanelRight = new  JPanel(new FlowLayout(FlowLayout.LEFT));
        upperPanelRight.setPreferredSize(new Dimension(200,30));

        JTextField textField = new JTextField();
        textField.setName("SearchField");
        textField.setPreferredSize(new Dimension(300,30));


        JButton loadButton = new JButton(new ImageIcon("folder-blue-open-icon.png"));
        loadButton.setName("OpenButton");
        loadButton.setPreferredSize(new Dimension(24,24));

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        add(fileChooser);
        loadButton.addActionListener(actionListener -> {
            try {
                fileChooser.showOpenDialog(null);
//                textArea.setText(new String(Files.readAllBytes(Paths.get(textField.getText()))));
                textArea.setText(new String(Files.readAllBytes(fileChooser.getSelectedFile().toPath())));
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText("");
            }
        });

        JButton saveButton = new JButton(new ImageIcon("Actions-save-all-icon.png"));
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(new Dimension(24,24));
        saveButton.addActionListener(actionListener -> {
            try {
                fileChooser.showOpenDialog(null);
                Files.write(fileChooser.getSelectedFile().toPath(), textArea.getText().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        JButton startSearch = new JButton(new ImageIcon("search-icon.png"));
        startSearch.setName("StartSearchButton");
        startSearch.setPreferredSize(new Dimension(24,24));

        Stack<Integer> foundIndexes = new Stack<Integer>();
        startSearch.addActionListener(actionListener -> {

            System.out.println(foundIndexes.size());
            foundIndexes.clear();
            index = 0;
            if(!textField.getText().isEmpty()){
                pattern = Pattern.compile(textField.getText());
                matcherNext = pattern.matcher(textArea.getText());
                if(matcherNext.find()){
                    searchLength = matcherNext.group().length();

                    textArea.setCaretPosition(matcherNext.end());
                    textArea.select(matcherNext.start(), matcherNext.end());
                    textArea.grabFocus();
                    //populate the stack with all the starting indexes of all matched searches
                    do{
                        foundIndexes.add(matcherNext.start());
                    }while(matcherNext.find());
                }
            }
        });

        JButton previousMatch = new JButton(new ImageIcon("back-icon.png"));
        previousMatch.setName("PreviousMatchButton");
        previousMatch.setPreferredSize(new Dimension(24,24));
        previousMatch.addActionListener(actionEvent -> {
            if(!foundIndexes.isEmpty()){
                if(index == 0){
                    index = foundIndexes.size()-1;
                    indexStart = foundIndexes.peek();
                }else {
                    index--;
                    indexStart = foundIndexes.get(index);
                }

                textArea.setCaretPosition(indexStart + searchLength);
                textArea.select(indexStart,indexStart + searchLength);
                textArea.grabFocus();
            }
        });

        JButton nextMatchButton = new JButton(new ImageIcon("forward-icon.png"));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.setPreferredSize(new Dimension(24,24));

        nextMatchButton.addActionListener(actionEvent -> {
            if(!foundIndexes.isEmpty()){
                index = index == foundIndexes.size()-1 ? 0 : index + 1;

                indexStart = foundIndexes.get(index);
                textArea.setCaretPosition(indexStart + searchLength);
                textArea.select(indexStart,indexStart + searchLength);
                textArea.grabFocus();
            }

        });

        JCheckBox regexCheckBox = new JCheckBox("Use regex");
        regexCheckBox.setName("UseRegExCheckbox");

        addMenu(loadButton,saveButton,startSearch,nextMatchButton,previousMatch,regexCheckBox);

        upperPanelRight.add(startSearch);

        upperPanelRight.add(previousMatch);
        upperPanelRight.add(nextMatchButton);
        upperPanelRight.add(regexCheckBox);

        upperPanelLeft.add(loadButton);
        upperPanelLeft.add(saveButton);
        upperPanelLeft.add(textField);

        upperPanel.add(upperPanelLeft);
        upperPanel.add(upperPanelRight);
        add(upperPanel, BorderLayout.NORTH);

        add(new JPanel(), BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.EAST);
        add(scrollableTextArea, BorderLayout.CENTER);

        textArea.setMaximumSize(new Dimension(300,100));
        setVisible(true);
    }
    //method for setting up all menu items according specification
    void addMenu(JButton loadButton, JButton saveButton, JButton startSearch,
                 JButton nextSearch, JButton previousSearch, JCheckBox regexCheckBox){

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menu.setName("MenuFile");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        JMenuItem load = new JMenuItem("Open");
        load.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        load.setName("MenuOpen");
        load.addActionListener(al -> loadButton.doClick());
        menu.add(load);

        JMenuItem save = new JMenuItem("Save");
        save.setName("MenuSave");
        save.addActionListener(al-> saveButton.doClick());
        save.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menu.add(save);

        JMenuItem exit = new JMenuItem("Exit");
        exit.setName("MenuExit");
        exit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        exit.addActionListener(al -> dispose());
        menu.add(exit);

        JMenu menuSearch = new JMenu("Search");
        menuSearch.setName("MenuSearch");

        JMenuItem searchItem = new JMenuItem("Start Search");
        searchItem.setName("MenuStartSearch");
        searchItem.addActionListener(al-> startSearch.doClick());
        searchItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.ALT_MASK));

        JMenuItem searchNextItem = new JMenuItem("Next match");
        searchNextItem.setName("MenuNextMatch");
        searchNextItem.addActionListener(al-> nextSearch.doClick());
        searchNextItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));

        JMenuItem searchPreviousItem = new JMenuItem("Previous match");
        searchPreviousItem.setName("MenuPreviousMatch");
        searchPreviousItem.addActionListener(al-> previousSearch.doClick());
        searchPreviousItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));

        JMenuItem regexCheckboxItem = new JMenuItem("Use regular expressions");
        regexCheckboxItem.setName("MenuUseRegExp");
        regexCheckboxItem.addActionListener(al-> regexCheckBox.doClick());
        regexCheckboxItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.ALT_MASK));

        menuSearch.add(searchItem);
        menuSearch.add(searchPreviousItem);
        menuSearch.add(searchNextItem);

        menuSearch.add(regexCheckboxItem);
        menuBar.add(menuSearch);

    }
}



