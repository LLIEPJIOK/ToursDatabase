import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;

// class for main window
public class MainFrame extends JFrame implements ActionListener {
    // staff for showing table
    enum ShowMod {
        UNSORTED, SORTED, REVERSE_SORTED, FIND
    }

    enum ShowKey {
        TOUR_NAME, CLIENT_NAME, DAYS
    }

    enum FindMod {
        EQUAL, LESS, GREATER
    }

    private static ShowMod showMod = ShowMod.UNSORTED;
    private static ShowKey showKey = ShowKey.TOUR_NAME;
    private static FindMod findMod = FindMod.EQUAL;
    private static String findingKey;

    // file staff
    private static String FILE_NAME = "Tours.dat";
    private static String FILE_NAME_BACK = "Tours.~dat";
    private static String IDX_NAME = "Tours.idx";
    private static String IDX_NAME_BACK = "Tours.~idx";
    static final String IDX_EXT = "idx";

    // Customize staff
    private static Font headerFont = new Font("Clarendon", Font.BOLD | Font.ITALIC, 15);
    private static Font menuFont = new Font("Clarendon", Font.BOLD, 13);
    private static Font menuFontItalic = new Font("Clarendon", Font.BOLD | Font.ITALIC, 12);
    private static Font menuObjectFont = new Font("Clarendon", Font.BOLD | Font.ITALIC, 11);

    // some menuBar staff
    private static JMenuBar menuBar;

    private static JMenu fileMenu;
    private static JMenuItem openItem;
    private static JMenuItem exitItem;

    private static JMenu commandMenu;
    private static JMenuItem addItem;
    private static JMenuItem removeItem;
    private static JMenuItem clearDataItem;
    private static JMenuItem showItem;
    private static JMenuItem showSortedItem;
    private static JMenuItem findItem;

    private static JMenu helpMenu;
    private static JMenuItem aboutItem;

    // status bar
    private static JLabel statusLabel;

    // header staff
    private static JLabel headerLabel;
    private static final String[] headersNames = { "All books unsorted:", "All books sorted by ",
            "All books reverse sorted by ", "All books with " };

    // staff for table
    private static JTable table;
    private static JScrollPane scrollPane;
    private static final String[] COLUMNS_NAME = { "Tour name", "Client name", "Price per day", "Days", "Fare",
            "Cost of travel", "Zipped" };
    private static final int[] COLUMNS_WIDTH = { 100, 140, 110, 60, 80, 110, 60 };

    // staff for dialog that adds tours
    private static JDialog addingDialog;
    private static final String[] ADDING_LABELS = { "Tour name:", "Client name:", "Price per day:", "Days:", "Fare:",
            "Cost of travel:" };
    private static JTextField[] addingTextFields = new JTextField[6];
    private static JCheckBox addingCheckBox;

    // staff for dialog that removes tours
    private static JDialog removingDialog;
    private static JTextField removingTextField;
    private static JComboBox<String> removingComboBox;

    // staff for dialog that sorts tours
    private static JDialog sortingDialog;
    private static JCheckBox sotringCheckBox;
    private static JComboBox<String> soringComboBox;

    // staff for dialog that finds tours
    private static JDialog findingDialog;
    private static JComboBox<String> findingKeyTypeComboBox;
    private static JComboBox<String> findingComparisonTypeComboBox;
    private static JTextField findingKeyValueTextField;

    // staff for about dialog
    private static JDialog aboutDialog;

    // index for tours
    private static Index idx;

    // constructor
    MainFrame() {
        // configuring window
        this.setTitle("Tours database");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(200, 100);
        this.setSize(692, 405);
        this.setResizable(false);
        Image icon = new ImageIcon("Icons\\Main_Window_icon.png").getImage();
        this.setIconImage(icon);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveIndex();
                System.exit(0);
            }
        });

        // creating staff
        createHeader();
        createTable();
        createStatusBar();
        createMenus();
        createAddingDialog();
        createRemovingDialog();
        createSortingDialog();
        createFindingWindow();
        createAboutDialogWindow();
        createIndex();

        // making window visible
        this.setVisible(true);
    }

    // creating header
    private void createHeader() {
        headerLabel = new JLabel("", SwingConstants.CENTER);
        headerLabel.setFont(headerFont);
        headerLabel.setBounds(0, 0, 692, 20);
        headerLabel.setBackground(new Color(224, 255, 255));
        headerLabel.setOpaque(true);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        this.add(headerLabel, BorderLayout.NORTH);
    }

    // creating table
    private void createTable() {
        table = new JTable();
        table.getTableHeader().setFont(menuFont);
        table.getTableHeader().setBackground(new Color(173, 216, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setEnabled(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(menuFont);

        // creating ScrollPane
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(new Color(224, 255, 255));
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(new Color(224, 255, 255));

        // updating table
        updateTable();

        // adding scrollPane
        this.add(scrollPane);
    }

    // creating status bar
    private void createStatusBar() {
        statusLabel = new JLabel("Press Alt+x to exit");
        statusLabel.setFont(menuFontItalic);
        statusLabel.setBackground(new Color(224, 255, 255));
        statusLabel.setOpaque(true);
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        this.add(statusLabel, BorderLayout.SOUTH);
    }

    // creating menu
    private void createMenus() {
        // creating menuBar
        menuBar = new JMenuBar();

        // creating fileMenu
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        exitItem = new JMenuItem("Exit");

        // adding action listeners
        openItem.addActionListener(this);
        exitItem.addActionListener(this);

        // adding mouse listener
        openItem.addMouseListener(new MyMouseAdapter("Choose a file to work with", statusLabel));
        exitItem.addMouseListener(new MyMouseAdapter("Exit from application", statusLabel));

        // customize fileMenu
        fileMenu.setFont(menuFont);
        openItem.setFont(menuObjectFont);
        exitItem.setFont(menuObjectFont);

        // configuring hotkeys
        fileMenu.setMnemonic('F');
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));
        openItem.setMnemonic('O');
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
        exitItem.setMnemonic('x');

        // adding items to the fileMenu
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // creating commandMenu
        commandMenu = new JMenu("Command");
        addItem = new JMenuItem("Add item");
        removeItem = new JMenuItem("Remove item");
        clearDataItem = new JMenuItem("Clear data");
        showItem = new JMenuItem("Show");
        showSortedItem = new JMenuItem("Show sorted");
        findItem = new JMenuItem("Find");

        // adding adciton listeners
        addItem.addActionListener(this);
        removeItem.addActionListener(this);
        clearDataItem.addActionListener(this);
        showItem.addActionListener(this);
        showSortedItem.addActionListener(this);
        findItem.addActionListener(this);

        // adding mouse listener
        addItem.addMouseListener(new MyMouseAdapter("Add a new book", statusLabel));
        removeItem.addMouseListener(new MyMouseAdapter("Remove an existing book by key", statusLabel));
        clearDataItem.addMouseListener(new MyMouseAdapter("Clear all tours data", statusLabel));
        showItem.addMouseListener(new MyMouseAdapter("Show all books unsorted", statusLabel));
        showSortedItem.addMouseListener(new MyMouseAdapter("Show all books sorted by key", statusLabel));
        findItem.addMouseListener(new MyMouseAdapter("Find and show books by key", statusLabel));

        // customize commandMenu
        commandMenu.setFont(menuFont);
        addItem.setFont(menuObjectFont);
        removeItem.setFont(menuObjectFont);
        clearDataItem.setFont(menuObjectFont);
        showItem.setFont(menuObjectFont);
        showSortedItem.setFont(menuObjectFont);
        findItem.setFont(menuObjectFont);

        // configuring hotkeys
        commandMenu.setMnemonic('C');
        addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
        addItem.setMnemonic('A');
        removeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
        removeItem.setMnemonic('R');
        clearDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));
        clearDataItem.setMnemonic('l');
        showItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)); // *HOTKEYS*
        showItem.setMnemonic('S');
        showSortedItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK));
        showSortedItem.setMnemonic('h');
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_DOWN_MASK));
        findItem.setMnemonic('F');

        // adding items to the commandMenu
        commandMenu.add(addItem);
        commandMenu.addSeparator();
        commandMenu.add(removeItem);
        commandMenu.add(clearDataItem);
        commandMenu.addSeparator();
        commandMenu.add(showItem);
        commandMenu.add(showSortedItem);
        commandMenu.addSeparator();
        commandMenu.add(findItem);

        // creating helpMenu
        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About");

        // adding items to the commandMenu
        aboutItem.addActionListener(this);

        // adding mouse listener
        aboutItem.addMouseListener(new MyMouseAdapter("Show the information about the application", statusLabel));

        // customize helpMenu
        helpMenu.setFont(menuFont);
        aboutItem.setFont(menuObjectFont);

        // configuring hotkeys
        helpMenu.setMnemonic('H');
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK));
        aboutItem.setMnemonic('b');

        // adding items to the helpMenu
        helpMenu.add(aboutItem);

        // adding menus to the menuBar
        menuBar.add(fileMenu);
        menuBar.add(commandMenu);
        menuBar.add(helpMenu);

        // setting menuBar
        this.setJMenuBar(menuBar);
    }

    // creating dialog for adding tours
    private void createAddingDialog() {
        // creating window
        addingDialog = new JDialog(this, "Adding", true);
        Image icon = new ImageIcon("Icons\\Add_Window_icon.png").getImage();
        addingDialog.setIconImage(icon);
        // creating number formating for text fields
        NumberFormat intFormat = NumberFormat.getInstance();
        NumberFormatter intFormatter = new NumberFormatter(intFormat);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(0);
        intFormatter.setMaximum(1000000000);
        intFormatter.setAllowsInvalid(false);

        NumberFormat decFormat = DecimalFormat.getInstance();
        decFormat.setMinimumFractionDigits(2);
        decFormat.setMaximumFractionDigits(2);
        NumberFormatter doubleFormatter = new NumberFormatter(decFormat);
        doubleFormatter.setValueClass(Double.class);
        doubleFormatter.setMinimum(0.0);
        doubleFormatter.setMaximum(1000000000.0);
        doubleFormatter.setAllowsInvalid(false);

        // creating and locate elements
        for (int i = 0; i < 6; ++i) {
            JLabel l = new JLabel(ADDING_LABELS[i]);
            l.setBounds(10, 10 + i * 25, 100, 20);
            l.setFont(menuFont);

            if (i < 2) {
                addingTextFields[i] = new JTextField();
            } else if (i == 3) {
                addingTextFields[i] = new JTextField();
                addingTextFields[i].addKeyListener(new IntKeyListener());
            } else {
                addingTextFields[i] = new JTextField();
                addingTextFields[i].addKeyListener(new DoubleKeyListener());
            }

            addingTextFields[i].setFont(menuFontItalic);
            addingTextFields[i].setBounds(130, 10 + i * 25, 140, 20);

            addingDialog.add(l);
            addingDialog.add(addingTextFields[i]);
        }

        addingCheckBox = new JCheckBox("Is zipped");
        addingCheckBox.setBounds(10, 160, 300, 30);
        addingCheckBox.setFont(menuFont);

        JButton confirmButton = new JButton("OK");
        confirmButton.setPreferredSize(new Dimension(100, 40));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setOpaque(true);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        confirmButton.setFocusPainted(false);
        confirmButton.setContentAreaFilled(true);
        confirmButton.setBounds(75, 190, 150, 26);
        confirmButton.setFont(menuFont);
        confirmButton.addActionListener(event -> addTour(event));

        // cofigurating dialog
        addingDialog.add(addingCheckBox);
        addingDialog.add(confirmButton);
        addingDialog.add(new Panel());
        addingDialog.setLocation(new Point(this.getX() + 120, this.getY() + 120));
        addingDialog.setSize(300, 260);
        addingDialog.setResizable(false);
    }

    // Creating removing dialog
    private void createRemovingDialog() {
        removingDialog = new JDialog(this, "Removing", true);
        Image icon = new ImageIcon("Icons\\Remove_Window_icon.png").getImage();
        removingDialog.setIconImage(icon);
        removingDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1));

        String[] items = { "Tour name", "Client name", "Days" };
        removingComboBox = new JComboBox<>(items);
        removingComboBox.setFont(menuFont);

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(removingComboBox);

        removingTextField = new JTextField(10);
        removingTextField.setFont(menuFontItalic);
        row.add(removingTextField);
        panel.add(row);

        JButton confirmButton = new JButton("OK");
        confirmButton.setPreferredSize(new Dimension(100, 40));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setOpaque(true);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        confirmButton.setFocusPainted(false);
        confirmButton.setContentAreaFilled(true);
        confirmButton.addActionListener(event -> removeTour(event));

        removingDialog.add(panel, BorderLayout.CENTER);
        removingDialog.add(confirmButton, BorderLayout.SOUTH);
        removingDialog.setLocation(new Point(this.getX() + 120, this.getY() + 120));
        removingDialog.setSize(270, 120);
        removingDialog.setResizable(false);
        removingDialog.setVisible(false);
    }

    // Creating Sort dialog window
    private void createSortingDialog() {
        sortingDialog = new JDialog(this, "Sorting", true);
        Image icon = new ImageIcon("Icons\\Sort_Window_icon.png").getImage();
        sortingDialog.setIconImage(icon);
        sortingDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2));

        String[] items = { "Tour name", "Client name", "Days" };
        soringComboBox = new JComboBox<>(items);
        soringComboBox.setFont(menuFont);
        sotringCheckBox = new JCheckBox("Reversed");
        panel.add(soringComboBox);
        panel.add(sotringCheckBox);

        JButton confirmButton = new JButton("OK");
        confirmButton.setPreferredSize(new Dimension(100, 40));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setOpaque(true);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        confirmButton.setFocusPainted(false);
        confirmButton.setContentAreaFilled(true);
        confirmButton.addActionListener(event -> setSorting(event));

        sortingDialog.add(panel, BorderLayout.CENTER);
        sortingDialog.add(confirmButton, BorderLayout.SOUTH);
        sortingDialog.setLocation(new Point(this.getX() + 120, this.getY() + 120));
        sortingDialog.setSize(270, 100);
        sortingDialog.setResizable(false);
        sortingDialog.setVisible(false);
    }

    // Creating finding dialog window
    private void createFindingWindow() {
        findingDialog = new JDialog(this, "Finding", true);
        Image icon = new ImageIcon("Icons\\Find_Window_icon.png").getImage();
        findingDialog.setIconImage(icon);
        findingDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel chooseLabel = new JLabel("Choose a key:", SwingConstants.CENTER);
        chooseLabel.setFont(menuFont);
        findingKeyValueTextField = new JTextField(10);

        findingKeyTypeComboBox = new JComboBox<>(new String[] { "Tour name",
                "Client name", "Days" });
        findingKeyTypeComboBox.addActionListener(event -> {
            if ((String) findingKeyTypeComboBox.getSelectedItem() == "Days") {
                findingKeyValueTextField.addKeyListener(new IntKeyListener());
            } else {
                findingKeyValueTextField.addKeyListener(null);
            }
            findingKeyValueTextField.setText("");
        });

        findingComparisonTypeComboBox = new JComboBox<>(new String[] {
                "<", "==", ">" });

        panel.add(chooseLabel);
        panel.add(new JPanel());
        panel.add(new JLabel("Key type:"));
        panel.add(findingKeyTypeComboBox);
        panel.add(new JLabel("Key value:"));
        panel.add(findingKeyValueTextField);
        panel.add(new JLabel("Comparison type:"));
        panel.add(findingComparisonTypeComboBox);

        JButton findButton = new JButton("Find");
        findButton.setPreferredSize(new Dimension(100, 40));
        findButton.setBackground(Color.WHITE);
        findButton.setForeground(Color.BLACK);
        findButton.setOpaque(true);
        findButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        findButton.setFocusPainted(false);
        findButton.setContentAreaFilled(true);
        findButton.addActionListener(event -> setFinding(event));

        findingDialog.add(panel, BorderLayout.CENTER);
        findingDialog.add(findButton, BorderLayout.SOUTH);
        findingDialog.setSize(300, 180);
        findingDialog.setLocation(new Point(this.getX() + 120, this.getY() + 120));
        findingDialog.setResizable(false);
        findingDialog.setVisible(false);
    }

    // creating an About dialog window
    private void createAboutDialogWindow() {
        aboutDialog = new JDialog(this, "About", true);
        Image icon = new ImageIcon("Icons\\About_Window_icon.png").getImage();
        aboutDialog.setIconImage(icon);
        aboutDialog.setTitle("Dialog");
        aboutDialog.setSize(455, 150);
        aboutDialog.setResizable(false);
        aboutDialog.setLocationRelativeTo(null);

        JLabel label1 = new JLabel("It's a simple tour database. You can add, sort, find and remove tours.");
        label1.setFont(menuFontItalic);
        label1.setBounds(25, 7, 500, 30);

        JLabel label2 = new JLabel("Prepared by Denis Lebedev and Matthew Kvetko");
        label2.setFont(menuFontItalic);
        label2.setBounds(145, 30, 500, 30);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setOpaque(true);
        okButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        okButton.setFocusPainted(false);
        okButton.setContentAreaFilled(true);
        okButton.addActionListener(e -> aboutDialog.dispose());
        okButton.setBounds(150, 66, 150, 36);

        aboutDialog.add(label1);
        aboutDialog.add(label2);
        aboutDialog.add(okButton);
        aboutDialog.add(new JPanel());
        aboutDialog.setVisible(false);
    }

    // creating index
    private void createIndex() {
        try {
            idx = Index.load(IDX_NAME);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Something went wrong",
                    "Error", JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }
    }

    // saving index
    private void saveIndex() {
        try {
            idx.save(IDX_NAME);
        } catch (Exception e) {
            // some exception
            JOptionPane.showMessageDialog(this,
                    "Something went wrong",
                    "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // adding tour
    private void addTour(ActionEvent event) {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw")) {
            // checking empty fields
            for (int i = 0; i < 6; ++i) {
                if (addingTextFields[i].getText().trim().length() == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Empty fields",
                            "Information", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
            }

            // creating tour
            String tourName = addingTextFields[0].getText().trim();
            String clientName = addingTextFields[1].getText().trim();
            double pricePerDay = Double.parseDouble(addingTextFields[2].getText());
            int days = Integer.parseInt(addingTextFields[3].getText());
            double fare = Double.parseDouble(addingTextFields[4].getText());
            double costOfTravel = Double.parseDouble(addingTextFields[5].getText());
            boolean isZipped = addingCheckBox.isSelected();
            Tour tour = new Tour(tourName, clientName, pricePerDay, days, fare, costOfTravel);

            // adding tour
            idx.test(tour);
            long pos = Buffer.writeObject(raf, tour, isZipped);
            idx.put(tour, pos);

            // clear after using
            addingDialog.setVisible(false);
            for (int i = 0; i < 6; ++i) {
                addingTextFields[i].setText("");
            }
            addingCheckBox.setSelected(false);
            updateTable();
            raf.close();
        } catch (KeyNotUniqueException e) {
            // this name of tour exists
            JOptionPane.showMessageDialog(this,
                    "Tour with the same name already exist",
                    "Information", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            // some exception
            JOptionPane.showMessageDialog(this,
                    "Something went wrong",
                    "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // removing tour
    private void removeTour(ActionEvent event) {
        // checking empty field
        if (removingTextField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Empty fields",
                    "Information", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        // getting positions
        Long[] poss = null;
        try {
            IndexBase pidx = null;
            switch ((String) removingComboBox.getSelectedItem()) {
                case "Tour name": {
                    pidx = idx.names;
                    break;
                }
                case "Client name": {
                    pidx = idx.fullNames;
                    break;
                }
                case "Days": {
                    pidx = idx.days;
                    break;
                }
            }
            poss = pidx.get(removingTextField.getText().trim());

            // recreating files
            backup();
            idx = Index.load(IDX_NAME);
            Arrays.sort(poss);
            RandomAccessFile fileBak = new RandomAccessFile(FILE_NAME_BACK, "rw");
            RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw");
            boolean[] wasZipped = new boolean[] { false };

            // rewriting files without "deleting tours"
            long pos;
            while ((pos = fileBak.getFilePointer()) < fileBak.length()) {
                Tour tour = (Tour) Buffer.readObject(fileBak, pos, wasZipped);
                if (Arrays.binarySearch(poss, pos) < 0) {
                    long ptr = Buffer.writeObject(file, tour, wasZipped[0]);
                    idx.put(tour, ptr);
                }
            }

            // update
            updateTable();

            // clear after using
            fileBak.close();
            file.close();
            removingTextField.setText("");
            removingDialog.setVisible(false);
        } catch (Exception e) {
            // some exception
            JOptionPane.showMessageDialog(this,
                    "Something went wrong",
                    "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // setting sorting
    private void setSorting(ActionEvent event) {
        switch ((String) soringComboBox.getSelectedItem()) {
            case "Tour name": {
                showKey = ShowKey.TOUR_NAME;
                break;
            }
            case "Days": {
                showKey = ShowKey.DAYS;
                break;
            }
            case "Client name": {
                showKey = ShowKey.CLIENT_NAME;
                break;
            }
        }
        if (sotringCheckBox.isSelected()) {
            showMod = ShowMod.REVERSE_SORTED;
        } else {
            showMod = ShowMod.SORTED;
        }

        // update
        updateTable();

        // clear after using
        sortingDialog.setVisible(false);
    }

    // setting finding
    private void setFinding(ActionEvent event) {
        // checking empty field
        if (findingKeyValueTextField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Empty fields",
                    "Information", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        switch ((String) findingKeyTypeComboBox.getSelectedItem()) {
            case "Tour name": {
                showKey = ShowKey.TOUR_NAME;
                break;
            }
            case "Days": {
                showKey = ShowKey.DAYS;
                break;
            }
            case "Client name": {
                showKey = ShowKey.CLIENT_NAME;
                break;
            }
        }
        switch ((String) findingComparisonTypeComboBox.getSelectedItem()) {
            case "<": {
                findMod = FindMod.LESS;
                break;
            }
            case "==": {
                findMod = FindMod.EQUAL;
                break;
            }
            case ">": {
                findMod = FindMod.GREATER;
                break;
            }
        }

        showMod = ShowMod.FIND;
        findingKey = findingKeyValueTextField.getText();

        // clear after using
        findingDialog.setVisible(false);

        // update
        updateTable();

    }

    // updating header
    private void updateHeader() {
        // key for showing if sorting
        String key = "";
        switch (showKey) {
            case TOUR_NAME:
                key = "tour name";
                break;
            case CLIENT_NAME:
                key = "client name";
                break;
            case DAYS:
                key = "days";
                break;
        }

        // comparasiong mod for showing if finding
        String comp = "";
        switch (findMod) {
            case LESS:
                comp = " less than ";
                break;
            case EQUAL:
                comp = " equal to ";
                break;
            case GREATER:
                comp = " greater than ";
                break;
        }

        // setting text
        switch (showMod) {
            case UNSORTED:
                headerLabel.setText(headersNames[0]);
                break;
            case SORTED:
                headerLabel.setText(headersNames[1] + key + ":");
                break;
            case REVERSE_SORTED:
                headerLabel.setText(headersNames[2] + key + ":");
                break;
            case FIND:
                headerLabel.setText(headersNames[3] + key + comp + findingKey);
                break;
            default:
                break;
        }
    }

    // updating table
    private void updateTable() {
        try {
            // creating table model
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 0; i < 7; ++i) {
                model.addColumn(COLUMNS_NAME[i]);
            }
            RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw");
            if (showMod == ShowMod.UNSORTED) {
                // unsorted
                // adding to table
                long pos;
                while ((pos = raf.getFilePointer()) < raf.length()) {
                    Tour tour = getTour(raf, pos);
                    model.addRow(new Object[] { tour.tourName, tour.clientName, tour.pricePerDay, tour.days, tour.fare,
                            tour.costOfTravel, tour.isZipped });
                }
            } else if (showMod == ShowMod.FIND) {
                // finding
                IndexBase pidx = getIndexBase();
                // equal
                if (findMod == FindMod.EQUAL) {
                    // no such key
                    if (!pidx.contains(findingKey)) {
                        JOptionPane.showMessageDialog(this,
                                "Key not found: " + findingKey,
                                "Information", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        // adding to table
                        Long[] poss = pidx.get(findingKey);
                        for (long pos : poss) {
                            Tour tour = getTour(raf, pos);
                            model.addRow(
                                    new Object[] { tour.tourName, tour.clientName, tour.pricePerDay, tour.days,
                                            tour.fare,
                                            tour.costOfTravel, tour.isZipped });
                        }
                    }
                } else {
                    // less or greater
                    Comparator<String> comp = null;
                    if (showKey == ShowKey.DAYS) {
                        comp = findMod == FindMod.GREATER ? new KeyIntCompReverse() : new KeyIntComp();
                    } else {
                        comp = findMod == FindMod.GREATER ? new KeyStringCompReverse() : new KeyStringComp();
                    }
                    String[] keys = pidx.getKeys(comp);
                    for (int i = 0; i < keys.length; i++) {
                        if (comp.compare(findingKey, keys[i]) <= 0) {
                            if (i == 0) {
                                // no tours greater/less
                                JOptionPane.showMessageDialog(this,
                                        "There are no such tours",
                                        "Information", JOptionPane.PLAIN_MESSAGE);
                            }
                            break;
                        }
                        // adding to table
                        Long[] poss = pidx.get(keys[i]);
                        for (long pos : poss) {
                            Tour tour = getTour(raf, pos);
                            model.addRow(
                                    new Object[] { tour.tourName, tour.clientName, tour.pricePerDay, tour.days,
                                            tour.fare,
                                            tour.costOfTravel, tour.isZipped });
                        }
                    }
                }
            } else {
                // sorted for tour name and full name
                Comparator<String> comp = null;
                if (showKey == ShowKey.DAYS) {
                    comp = showMod == ShowMod.REVERSE_SORTED ? new KeyIntCompReverse() : new KeyIntComp();
                } else {
                    comp = showMod == ShowMod.REVERSE_SORTED ? new KeyStringCompReverse() : new KeyStringComp();
                }
                IndexBase pidx = getIndexBase();
                String[] keys = (String[]) pidx.getKeys(comp);
                // adding to table
                for (String key : keys) {
                    Long[] poss = pidx.get(key);
                    for (long pos : poss) {
                        Tour tour = getTour(raf, pos);
                        model.addRow(
                                new Object[] { tour.tourName, tour.clientName, tour.pricePerDay, tour.days, tour.fare,
                                        tour.costOfTravel, tour.isZipped });
                    }
                }
            }

            // setting table
            table.setModel(model);
            for (int i = 0; i < COLUMNS_WIDTH.length; ++i) {
                table.getColumnModel().getColumn(i).setPreferredWidth(COLUMNS_WIDTH[i]);
            }

            // updating header
            updateHeader();

            // clear after using
            raf.close();
        } catch (Exception e) {
            // some exception
            JOptionPane.showMessageDialog(this,
                    "Something went wrong",
                    "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // getting indexBase using showKey
    private IndexBase getIndexBase() {
        IndexBase pidx = null;
        if (showKey == ShowKey.TOUR_NAME) {
            pidx = idx.names;
        } else if (showKey == ShowKey.CLIENT_NAME) {
            pidx = idx.fullNames;
        } else
            pidx = idx.days;
        return pidx;
    }

    // getting tour using position
    private static Tour getTour(RandomAccessFile raf, long pos) throws ClassNotFoundException, IOException {
        boolean[] wasZipped = new boolean[] { false };
        Tour tour = (Tour) Buffer.readObject(raf, pos, wasZipped);
        tour.setIsZipped(wasZipped[0]);
        return tour;
    }

    // renaming files
    private static void renameFiles(File file) {
        FILE_NAME = file.getName();

        String path = file.getPath();
        path = path.substring(0, path.indexOf(FILE_NAME));

        String[] str = FILE_NAME.split("\\.");
        String fileExt = str[1];
        IDX_NAME = str[0] + "." + IDX_EXT;
        FILE_NAME_BACK = str[0] + ".~" + fileExt;
        IDX_NAME_BACK = str[0] + ".~" + IDX_EXT;

        FILE_NAME = path + FILE_NAME;
        IDX_NAME = path + IDX_NAME;
        FILE_NAME_BACK = path + FILE_NAME_BACK;
        IDX_NAME_BACK = path + IDX_NAME_BACK;
    }

    // deleting backups
    private static void deleteBackup() {
        new File(FILE_NAME_BACK).delete();
        new File(IDX_NAME_BACK).delete();
    }

    // deleting all files
    static void deleteFile() {
        deleteBackup();
        new File(FILE_NAME).delete();
        new File(IDX_NAME).delete();
    }

    // making backup
    private static void backup() {
        deleteBackup();
        new File(FILE_NAME).renameTo(new File(FILE_NAME_BACK));
        new File(IDX_NAME).renameTo(new File(IDX_NAME_BACK));
    }

    // opening fileDialog
    private void openFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DAT files", "dat");
        fileChooser.setFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showDialog(null, "Choose directory");
        // renaming files
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            saveIndex();
            renameFiles(fileChooser.getSelectedFile());
            createIndex();
        }
    }

    // action handler
    @Override
    public void actionPerformed(ActionEvent event) {
        // hanle open file
        if (event.getSource() == openItem) {
            openFileDialog();

            // updating
            updateTable();
        }

        // hanle exit
        if (event.getSource() == exitItem) {
            saveIndex();
            System.exit(0);
        }

        // hanle add
        if (event.getSource() == addItem) {
            addingDialog.setVisible(true);
        }

        // hanle remove
        if (event.getSource() == removeItem) {
            // creating RemoveItem dialog window
            removingDialog.setVisible(true);
        }

        // hanle clear data
        if (event.getSource() == clearDataItem) {
            deleteFile();
            createIndex();
            updateTable();
        }

        // handle show tours
        if (event.getSource() == showItem) {
            showMod = ShowMod.UNSORTED;
            updateTable();
        }

        // handle show sorted tours
        if (event.getSource() == showSortedItem) {
            sortingDialog.setVisible(true);
        }

        // handle find tour
        if (event.getSource() == findItem) {
            findingDialog.setVisible(true);
        }

        // handle about
        if (event.getSource() == aboutItem) {
            aboutDialog.setVisible(true);
        }
    }
}
