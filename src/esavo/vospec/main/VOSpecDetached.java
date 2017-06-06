/* 
 * Copyright (C) 2017 ESDC/ESA 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package esavo.vospec.main;

//import esavo.voespace.service.Client;
import esac.archive.absi.modules.cl.samp.Interop;
import esac.archive.absi.modules.cl.samp.Interop.GuiServiceMode;
import esac.archive.absi.modules.cl.samp.InteropMenuBuilder;
import esac.archive.absi.modules.cl.samp.InteropStatusMenu;
import esac.archive.absi.modules.cl.samp.MonitorFactory;
import esac.archive.absi.modules.cl.samp.RegistrationMenu;
import esavo.skycoords.EquatorialCoordinates;
import esavo.utils.units.parser.*;
import esavo.vospec.dataingestion.*;
import esavo.vospec.math.*;
import esavo.vospec.plastic.*;
import esavo.vospec.plot.ExtendedPlot;
import esavo.vospec.plot.PlotSettings;
import esavo.vospec.resourcepanel.Node;
import esavo.vospec.resourcepanel.ResourcesPanelManager;
import esavo.vospec.samp.AllSpectrumSenderListener;
import esavo.vospec.samp.AllTableSenderListener;
import esavo.vospec.samp.MessageSenderMenu;
import esavo.vospec.samp.VOSpecMessageReceiver;
import esavo.vospec.slap.SlapUtils;
import esavo.vospec.spectrum.*;
import esavo.vospec.usage.UsageLogging;
import esavo.vospec.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import uk.ac.starlink.plastic.*;

/**
 *
 * @author  ESAVO team
 */
public class VOSpecDetached extends JFrame {

    /** Creates new form AioSpecToolDetached */
    public VOSpecDetached() {

        System.setProperty("units.filepath", "/esavo/utils/units/parser/conf/vospecUnitList.xml");

        setResizable(true);
        setSize(new Dimension(780, 740));
        SplashWindow.setProgressValue(2);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        initComponents();

        //This is needed when the application is launched as webstart and javaws.exe process need to be removed
        // Dispose method doesn't work with standalone and webstart
        if (!Utils.getExitMan()) {
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        } else {
            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        }

        helpToolBar.setVisible(false);
        helpVisible = false;

        resetResourcesPanel();

        TableJToolBar.setUI(new javax.swing.plaf.basic.BasicToolBarUI() {

            //These methods are overridden to avoid the resizing of the JToolBar
            //when something is modified inside (changing tab)
            @Override
            protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
                JFrame dialog = (JFrame) this.createFloatingFrame(toolbar);
                return dialog;

            }

            @Override
            protected JFrame createFloatingFrame(JToolBar toolbar) {
                Window window = SwingUtilities.getWindowAncestor(toolbar);
                JFrame frame = new JFrame(toolbar.getName(),
                        (window != null) ? window.getGraphicsConfiguration() : null) {
                };
                frame.getRootPane().setName("ToolBar.FloatingFrame");
                frame.setResizable(false);
                WindowListener wl = createFrameListener();
                frame.addWindowListener(wl);

                frame.setPreferredSize(new Dimension(600, 400));
                frame.setResizable(true);

                return frame;
            }

            @Override
            public boolean canDock(Component c,
                    Point p) {
                return false;
            }
        });

        jSplitPane1.setBottomComponent(barPanel);
        SplashWindow.setProgressValue(2);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        getContentPane().setBackground(new Color(223, 222, 222));

        addListenerFromKey();

        //Setting Cache
        Cache.putCacheInMemory();

        infoWindow = new Vector();
        rowsClicked = new Vector();

        utils = new Utils();
        utils.setAioSpecToolDetached(this);

        SlapUtils.setAioSpecToolDetached(this);
        SlapUtils.setSLAPViewer(null);

        plot = new ExtendedPlot(coordJLabel, this);
        //plot.setSize(570,370);

        showMathBar();

        sv = new SpectraViewer(plot, displayPanel);

        sv.setWaveToVelSelected(waveToVelocityCheckBox.isSelected());

        logVector = new Vector();
        logChanger = new LogChanger(this);

        successVector = new Vector();

        titlePanel.add(jMenuBar, java.awt.BorderLayout.CENTER);

        try {
            new Thread(logChanger).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set key shortcuts
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK);
        NormalizeItem.setAccelerator(ctrlN);

        KeyStroke ctrlo = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK);
        openItem.setAccelerator(ctrlo);

        KeyStroke ctrls = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
        saveItem.setAccelerator(ctrls);

        KeyStroke ctrlq = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK);
        exitItem.setAccelerator(ctrlq);

        KeyStroke ctrlh = KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK);
        javaHelpItem.setAccelerator(ctrlh);

        KeyStroke ctrla = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK);
        arithmeticItem2.setAccelerator(ctrla);

        KeyStroke ctrlf = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK);
        fittingItem.setAccelerator(ctrlf);

        KeyStroke ctrlm = KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK);
        mirroringItem.setAccelerator(ctrlm);

        KeyStroke ctrlb = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK);
        bisectorItem.setAccelerator(ctrlb);

        KeyStroke ctrle = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK);
        EquivalentItem.setAccelerator(ctrle);

        KeyStroke ctrli = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK);
        integratedItem.setAccelerator(ctrli);

        KeyStroke ctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK);
        waveletButtonItem.setAccelerator(ctrlw);

        KeyStroke ctrlt = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK);
        tuningItem.setAccelerator(ctrlt);

        KeyStroke ctrlu = KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK);
        unzoomItem.setAccelerator(ctrlu);

        KeyStroke ctrlc = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK);
        clearCacheItem.setAccelerator(ctrlc);

        KeyStroke ctrld = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK);
        filteringItem.setAccelerator(ctrld);

        KeyStroke ctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK);
        registerItem.setAccelerator(ctrlp);

        final String ctrlPressed = "control pressed CONTROL";
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(ctrlPressed), ctrlPressed);
        getRootPane().getActionMap().put(ctrlPressed, new AbstractAction() {

            public void actionPerformed(ActionEvent ignored) {
                setCrossHairCursor();
                plot.setCrossActive();

            }
        });

        final String ctrlReleased = "released CONTROL";
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(ctrlReleased), ctrlReleased);
        getRootPane().getActionMap().put(ctrlReleased, new AbstractAction() {

            public void actionPerformed(ActionEvent ignored) {
                setDefaultCursor();
                plot.setCrossNotActive();
            }
        });

        targetField.requestFocus();

        SplashWindow.setProgressValue(5);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        //Connect to PLASTIC at startup

        try {
            File plasticFile = null;
            if (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1) {
                plasticFile = new File(System.getProperty("user.home") + "\\.plastic");
            } else {
                plasticFile = new File(System.getProperty("user.home") + "/.plastic");
            }
            if (plasticFile.exists()) {
                System.out.println("plasticFile exists " + plasticFile);

                putil.registerToPlastic();
                putil.checkApplicationRegistered();
            } else {
                System.out.println("Plastic file doesn't exists");
                startHub();
                putil.registerToPlastic();
                putil.checkApplicationRegistered();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SplashWindow.setProgressValue(6);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        SplashWindow.setProgressValue(7);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        interop = new Interop("VOSpec", "ESA-VO Spectral Analysis Tool", GuiServiceMode.AUTOMATIC, "http://esavo.esac.esa.int/VOSpecManual/Images/index2_25.gif", "http://www.sciops.esa.int/index.php?project=ESAVO&page=vospec");

        try {
            //Initialize SAMP menu
            (new Thread() {

                public void run() {
                    initializeSAMP();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resourcespanelmanager = new ResourcesPanelManager(this.TableJToolBar, this);

        SplashWindow.setProgressValue(8);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        sedSpectrums = new Hashtable();
        localSpectrums = new Hashtable();

        spectrumSet = new SpectrumSet();

        SplashWindow.setProgressValue(9);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() / 2, (screenSize.height / 2) - this.getHeight() / 2);

        SplashWindow.setProgressValue(10);
        SplashWindow.setProgressMessage("Loading VOSpec " + EnvironmentDefs.getVERSION());

        mathematicMethodExecution = 0;


        spectrumConverters.start();

        this.jProgressBar1.setVisible(false);
        this.jProgressBar2.setVisible(false);

        this.toFront();

    }

    public VOSpecDetached(String sTitle, Properties props) throws Exception {
        this();
        setSetting(props);
        launchServersQuery();
    }

    public VOSpecDetached(String sTitle, AppletContext parentAppletContext, Properties props) throws Exception {
        this();
        this.parentAppletContext = parentAppletContext;
        setSetting(props);

        launchServersQuery();
    }

    private void launchServersQuery() {
        advancedSelector = new VOSpecAdvancedSelector(this);
        advancedSelector.setVisible(false);
        advancedSelector.setTrees();
    }

    public void setSetting(Properties props) throws Exception {

        this.setSize(new Dimension(770, 700));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() / 2, (screenSize.height / 2) - this.getHeight() / 2);

        try {

            // JFrame1 Create and show the JFrame1 with a title
            System.out.println("Calling VOSpec ");
            System.out.println("props.getProperty SERVERHOST " + props.getProperty("SERVERHOST"));
            System.out.println("props.getProperty SERVERPORT  " + props.getProperty("SERVERPORT"));
            ParseEnvironment environment = new ParseEnvironment("http://" + props.getProperty("SERVERHOST") + ":" + props.getProperty("SERVERPORT") + "/vospec/conf/aioSpecEnvironmentDefs.xml");
            props = environment.getProperties();
            Utils.propertiesDefinition(props);

        } catch (Exception e3) {
            System.out.println("Problems when setting Properties");
            e3.printStackTrace();
            return;
        }

    }
    protected RegistrationMenu registrationMenu;
    protected MessageSenderMenu allSpectrumSenderMenu;
    protected MessageSenderMenu voTableSenderMenu;
    protected AllSpectrumSenderListener allSpectrumSenderListener;
    protected AllTableSenderListener voTableSenderListener;
    protected InteropStatusMenu statusMenu;
    public Interop interop;
    SpectrumConvertersRunner spectrumConverters = new SpectrumConvertersRunner();

    private void initializeSAMP() {


        //Build JMenu
        java.util.List menuBlocks = new LinkedList();
        registrationMenu = new RegistrationMenu(interop);

        allSpectrumSenderMenu =
                new MessageSenderMenu(interop, "Send as Spectrum to", "All", "spectrum.load.ssa-generic");

        voTableSenderMenu = new MessageSenderMenu(interop, "Send as Table to", "All", "table.load.votable");

        // SENDING listener
        allSpectrumSenderListener = new AllSpectrumSenderListener("spectrum.load.ssa-generic", interop, this);
        allSpectrumSenderMenu.addActionListener(allSpectrumSenderListener);

        voTableSenderListener = new AllTableSenderListener("table.load.votable", interop, this);
        voTableSenderMenu.addActionListener(voTableSenderListener);

        // RECEIVING listener
        interop.addMessageHandler(new VOSpecMessageReceiver("spectrum.load.ssa-generic", this));
        interop.addMessageHandler(new VOSpecMessageReceiver("table.load.votable", this));
        interop.addMessageHandler(new VOSpecMessageReceiver("table.load.fits", this));
        interop.addMessageHandler(new VOSpecMessageReceiver("table.highlight.row", this));
        interop.addMessageHandler(new VOSpecMessageReceiver("table.select.rowlist", this));

        //Add JMenu to the JMenuBar

        statusMenu = new InteropStatusMenu(interop, MonitorFactory.JSAMP_MONITOR);

        menuBlocks.add(registrationMenu);
        menuBlocks.add(allSpectrumSenderMenu);
        menuBlocks.add(voTableSenderMenu);
        menuBlocks.add(statusMenu);

        InteropMenuBuilder interopMenu = new InteropMenuBuilder("SAMP", interop, menuBlocks);

        jMenuBar.add(interopMenu, 5);

        jMenuBar.validate();
        jMenuBar.repaint();
        this.validate();
        this.repaint();


        if (!interop.isConnected()) {

            interop.startHub();
        }



    }

    public void addListenerFromKey() {

        targetField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                returnPressed(evt);
            }
        });
        raField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                returnPressed(evt);
            }
        });
        decField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                returnPressed(evt);
            }
        });
        sizeField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                returnPressed(evt);
            }
        });
    }

    public void resetResourcesPanel() {

        if (resourcespanelmanager != null) {
            resourcespanelmanager.resetPanel();
        }

    }

    public void returnPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            setWaitCursor();
            callVOSpecAdvancedSelector();
            setDefaultCursor();
        }
    }

    /**
     * Search for SSA/TSA Servers and open Server Selector
     *  
     */
    public void callVOSpecAdvancedSelector() {

        try {
            searchSsaServer();

            if (advancedSelector != null) {
                advancedSelector.dispose();
            }
            advancedSelector = new VOSpecAdvancedSelector(this);
            advancedSelector.setBasicParamsToBeSelected(ssaRequest);
            advancedSelector.setTrees();
            advancedSelector.setVisible(true);

        } catch (java.lang.Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening Server Selector. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            System.out.println("Problems opening Server Selector");
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        openVOEspace = new javax.swing.JMenuItem();
        saveVOEspace = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        saveItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editDataItem = new javax.swing.JMenuItem();
        setItem = new javax.swing.JMenuItem();
        clearCacheItem = new javax.swing.JMenuItem();
        changeView = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        plotSizeItem = new javax.swing.JMenuItem();
        unzoomItem = new javax.swing.JMenuItem();
        arithmeticBarItem = new javax.swing.JMenuItem();
        helpBarItem = new javax.swing.JMenuItem();
        opMenu = new javax.swing.JMenu();
        getPointItem = new javax.swing.JMenuItem();
        arithmeticItem2 = new javax.swing.JMenuItem();
        fittingItem = new javax.swing.JMenuItem();
        NormalizeItem = new javax.swing.JMenuItem();
        slapItem = new javax.swing.JMenuItem();
        luminosityItem = new javax.swing.JMenuItem();
        mirroringItem = new javax.swing.JMenuItem();
        bisectorItem = new javax.swing.JMenuItem();
        EquivalentItem = new javax.swing.JMenuItem();
        integratedItem = new javax.swing.JMenuItem();
        waveletButtonItem = new javax.swing.JMenuItem();
        filteringItem = new javax.swing.JMenuItem();
        tuningItem = new javax.swing.JMenuItem();
        statisticsItem = new javax.swing.JMenuItem();
        syntheticPhotometry = new javax.swing.JMenuItem();
        interOpMenu = new javax.swing.JMenu();
        registerItem = new javax.swing.JMenuItem();
        unregisterItem = new javax.swing.JMenuItem();
        internalHubItem = new javax.swing.JMenuItem();
        externalHubItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        sendSpectrum = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        howToItem = new javax.swing.JMenuItem();
        aboutItem = new javax.swing.JMenuItem();
        contactItem = new javax.swing.JMenuItem();
        javaHelpItem = new javax.swing.JMenuItem();
        titlePanel = new javax.swing.JPanel();
        titleImagePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        bannerLabel = new javax.swing.JLabel();
        iconMenuPanel = new javax.swing.JPanel();
        generalToolBar = new javax.swing.JToolBar();
        openFileButton = new javax.swing.JButton();
        localDataButton = new javax.swing.JButton();
        saveImageButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        slapButton = new javax.swing.JButton();
        normButton1 = new javax.swing.JButton();
        scissorsButton = new javax.swing.JButton();
        fullSizeButton = new javax.swing.JButton();
        fixButton = new javax.swing.JButton();
        clearCacheButton = new javax.swing.JButton();
        plasticButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        helpToolBar = new javax.swing.JToolBar();
        mailButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        optionPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        waveLabelPanel = new javax.swing.JPanel();
        waveLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        umPanel = new javax.swing.JPanel();
        waveChoice = new javax.swing.JComboBox();
        logWave = new javax.swing.JCheckBox();
        fluxLabelPanel = new javax.swing.JPanel();
        fluxLabel = new javax.swing.JLabel();
        fluxPanel = new javax.swing.JPanel();
        fluxChoice = new javax.swing.JComboBox();
        logFlux = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        redShiftPanel = new javax.swing.JPanel();
        redShiftLabel = new javax.swing.JLabel();
        redShiftValue = new javax.swing.JTextField();
        redShiftCheck = new javax.swing.JCheckBox();
        deredPanel = new javax.swing.JPanel();
        deRedLabel = new javax.swing.JLabel();
        deRedCheck = new javax.swing.JCheckBox();
        waveVelocityPanel = new javax.swing.JPanel();
        waveToVelocityLabel = new javax.swing.JLabel();
        waveToVelocityValue = new javax.swing.JTextField();
        waveToVelocityCheckBox = new javax.swing.JCheckBox();
        yErrorLabel = new javax.swing.JLabel();
        xErrorLabel = new javax.swing.JLabel();
        yErrorCheck = new javax.swing.JCheckBox();
        xErrorCheck = new javax.swing.JCheckBox();
        displayColorPanel = new javax.swing.JPanel();
        displaySpectraLabel = new javax.swing.JLabel();
        displayColorScrollPanel = new javax.swing.JScrollPane();
        displayColorContainerPanel = new javax.swing.JPanel();
        viewAllPanel = new javax.swing.JPanel();
        viewAllButton = new javax.swing.JButton();
        viewerPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        target = new javax.swing.JLabel();
        targetField = new javax.swing.JTextField();
        ra = new javax.swing.JLabel();
        raField = new javax.swing.JTextField();
        dec = new javax.swing.JLabel();
        decField = new javax.swing.JTextField();
        sizeLabel = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        containerPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        svPanel = new javax.swing.JPanel();
        displayPanel = new javax.swing.JPanel();
        mathBar = new javax.swing.JToolBar();
        arithmeticButton2 = new javax.swing.JButton();
        polynomialButton = new javax.swing.JButton();
        bisectorButton = new javax.swing.JButton();
        mirroringButton = new javax.swing.JButton();
        waveletButton = new javax.swing.JButton();
        equivalentButton = new javax.swing.JButton();
        integratedButton = new javax.swing.JButton();
        filteringButton = new javax.swing.JButton();
        tuningButton = new javax.swing.JButton();
        statisticButton = new javax.swing.JButton();
        syntheticPhotometryButton = new javax.swing.JButton();
        barPanel = new javax.swing.JPanel();
        TableJToolBar = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        optPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        coordJLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        displayButton = new javax.swing.JButton();
        resetButton3 = new javax.swing.JButton();
        resetButton2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        logLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        cancelButton.setVisible(false);
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        openItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/open.gif"))); // NOI18N
        openItem.setText("Open Spectrum");
        openItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        fileMenu.add(openItem);
        fileMenu.add(jSeparator2);

        openVOEspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/openVOEspace.gif"))); // NOI18N
        openVOEspace.setText("Load from VOEspace");
        openVOEspace.setEnabled(false);
        openVOEspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVOEspaceActionPerformed(evt);
            }
        });
        fileMenu.add(openVOEspace);

        saveVOEspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/save.gif"))); // NOI18N
        saveVOEspace.setText("Save in VOEspace");
        saveVOEspace.setEnabled(false);
        saveVOEspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveVOEspaceActionPerformed(evt);
            }
        });
        fileMenu.add(saveVOEspace);
        fileMenu.add(jSeparator7);

        saveItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/save.gif"))); // NOI18N
        saveItem.setText("Save as");
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveItem);

        exitItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/exitIconLittle.png"))); // NOI18N
        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);

        jMenuBar.add(fileMenu);

        editMenu.setText("Edit");

        editDataItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/wrapper.gif"))); // NOI18N
        editDataItem.setText("Local Data Editor (SSAP Wrapper)");
        editDataItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDataItemActionPerformed(evt);
            }
        });
        editMenu.add(editDataItem);

        setItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/settings.gif"))); // NOI18N
        setItem.setText("Plot Settings");
        setItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setItemActionPerformed(evt);
            }
        });
        editMenu.add(setItem);

        clearCacheItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/clarcache.gif"))); // NOI18N
        clearCacheItem.setText("Clear Cache");
        clearCacheItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCacheItemActionPerformed(evt);
            }
        });
        editMenu.add(clearCacheItem);

        changeView.setText("Change Tree to Table and viceversa");
        changeView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeViewActionPerformed(evt);
            }
        });
        editMenu.add(changeView);

        jMenuBar.add(editMenu);

        viewMenu.setText("View");

        plotSizeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/fullSize.gif"))); // NOI18N
        plotSizeItem.setText("Plot Full Size");
        plotSizeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotSizeItemActionPerformed(evt);
            }
        });
        viewMenu.add(plotSizeItem);

        unzoomItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/unzoom.gif"))); // NOI18N
        unzoomItem.setText("Unzoom");
        unzoomItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unzoomItemActionPerformed(evt);
            }
        });
        viewMenu.add(unzoomItem);

        arithmeticBarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/integratedFluxIconLittle.png"))); // NOI18N
        arithmeticBarItem.setText("Analysis ToolBar");
        arithmeticBarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arithmeticBarItemActionPerformed(evt);
            }
        });
        viewMenu.add(arithmeticBarItem);

        helpBarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/about.png"))); // NOI18N
        helpBarItem.setText("Help ToolBar");
        helpBarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpBarItemActionPerformed(evt);
            }
        });
        viewMenu.add(helpBarItem);

        jMenuBar.add(viewMenu);

        opMenu.setText("Operations");

        getPointItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/transparent.gif"))); // NOI18N
        getPointItem.setText("Get Point   (CTRL-click)");
        getPointItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getPointItemActionPerformed(evt);
            }
        });
        opMenu.add(getPointItem);

        arithmeticItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/arithmeticBetweenspectraIconLittle.png"))); // NOI18N
        arithmeticItem2.setText("Arithmetic Operations ");
        arithmeticItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arithmeticItem2ActionPerformed(evt);
            }
        });
        opMenu.add(arithmeticItem2);

        fittingItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/fit2.gif"))); // NOI18N
        fittingItem.setText("Fitting Utilities");
        fittingItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fittingItemActionPerformed(evt);
            }
        });
        opMenu.add(fittingItem);

        NormalizeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/norm.gif"))); // NOI18N
        NormalizeItem.setText("Normalization Tool");
        NormalizeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NormalizeItemActionPerformed(evt);
            }
        });
        opMenu.add(NormalizeItem);

        slapItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/slap.gif"))); // NOI18N
        slapItem.setText("SLAP");
        slapItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slapItemActionPerformed(evt);
            }
        });
        opMenu.add(slapItem);

        luminosityItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/luminosityDifference.gif"))); // NOI18N
        luminosityItem.setText("Luminosity Difference");
        luminosityItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                luminosityItemActionPerformed(evt);
            }
        });
        opMenu.add(luminosityItem);

        mirroringItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/mirrorIconLittle.png"))); // NOI18N
        mirroringItem.setText("Mirroring");
        mirroringItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mirroringItemActionPerformed(evt);
            }
        });
        opMenu.add(mirroringItem);

        bisectorItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/bisectorIconLittle.png"))); // NOI18N
        bisectorItem.setText("Bisector");
        bisectorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bisectorItemActionPerformed(evt);
            }
        });
        opMenu.add(bisectorItem);

        EquivalentItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/equivalentWidthIconLittle.png"))); // NOI18N
        EquivalentItem.setText("Equivalent Width");
        EquivalentItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EquivalentItemActionPerformed(evt);
            }
        });
        opMenu.add(EquivalentItem);

        integratedItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/integratedFluxIconLittle.png"))); // NOI18N
        integratedItem.setText("Integrated Flux");
        integratedItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                integratedItemActionPerformed(evt);
            }
        });
        opMenu.add(integratedItem);

        waveletButtonItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/waveletIconLittle.png"))); // NOI18N
        waveletButtonItem.setText("Wavelet");
        waveletButtonItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveletButtonItemActionPerformed(evt);
            }
        });
        opMenu.add(waveletButtonItem);

        filteringItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/noiseIconLittle.png"))); // NOI18N
        filteringItem.setText("De-Noising");
        filteringItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filteringItemActionPerformed(evt);
            }
        });
        opMenu.add(filteringItem);

        tuningItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/toolKeyLittle.png"))); // NOI18N
        tuningItem.setText("Tuning");
        tuningItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tuningItemActionPerformed(evt);
            }
        });
        opMenu.add(tuningItem);

        statisticsItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/statisticsIconLittle.png"))); // NOI18N
        statisticsItem.setText("Statistics");
        statisticsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statisticsItemActionPerformed(evt);
            }
        });
        opMenu.add(statisticsItem);

        syntheticPhotometry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/photometryLittle.png"))); // NOI18N
        syntheticPhotometry.setText("Synthetic photometry");
        syntheticPhotometry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syntheticPhotometryActionPerformed(evt);
            }
        });
        opMenu.add(syntheticPhotometry);

        jMenuBar.add(opMenu);

        interOpMenu.setLabel("Plastic");

        registerItem.setText("Register with Plastic");
        registerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerItemActionPerformed(evt);
            }
        });
        interOpMenu.add(registerItem);

        unregisterItem.setText("Unregister with Plastic");
        unregisterItem.setEnabled(false);
        unregisterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unregisterItemActionPerformed(evt);
            }
        });
        interOpMenu.add(unregisterItem);

        internalHubItem.setText("Start Internal Plastic HUB");
        internalHubItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internalHubItemActionPerformed(evt);
            }
        });
        interOpMenu.add(internalHubItem);

        externalHubItem.setText("Start External Plastic HUB");
        externalHubItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalHubItemActionPerformed(evt);
            }
        });
        interOpMenu.add(externalHubItem);

        jSeparator3.setEnabled(false);
        interOpMenu.add(jSeparator3);

        sendSpectrum.setText("Send Spectrum to...");
        sendSpectrum.setEnabled(false);
        interOpMenu.add(sendSpectrum);

        jMenuBar.add(interOpMenu);

        helpMenu.setText("Help");

        howToItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/HowTo.gif"))); // NOI18N
        howToItem.setText("How To");
        howToItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                howToItemActionPerformed(evt);
            }
        });
        helpMenu.add(howToItem);

        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/about.png"))); // NOI18N
        aboutItem.setText("About VOSpec");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutItem);

        contactItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/mail4.gif"))); // NOI18N
        contactItem.setText("Contact ESAVO Team");
        contactItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactItemActionPerformed(evt);
            }
        });
        helpMenu.add(contactItem);

        javaHelpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/integratedFluxIconLittle.png"))); // NOI18N
        javaHelpItem.setText("Analysis Tools Help");
        javaHelpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                javaHelpItemActionPerformed(evt);
            }
        });
        helpMenu.add(javaHelpItem);

        jMenuBar.add(helpMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("VOSpec");
        setBackground(new java.awt.Color(223, 222, 222));
        setName("VOSpec"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        titlePanel.setBackground(new java.awt.Color(0, 0, 0));
        titlePanel.setAlignmentX(0.0F);
        titlePanel.setAlignmentY(0.0F);
        titlePanel.setPreferredSize(new java.awt.Dimension(760, 140));
        titlePanel.setLayout(new java.awt.BorderLayout());

        titleImagePanel.setBackground(new java.awt.Color(65, 64, 78));
        titleImagePanel.setPreferredSize(new java.awt.Dimension(760, 70));
        titleImagePanel.setLayout(new java.awt.BorderLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/VOSpecHeader2.png"))); // NOI18N
        titleImagePanel.add(jLabel3, java.awt.BorderLayout.EAST);

        bannerLabel.setBackground(new java.awt.Color(255, 255, 255));
        bannerLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        bannerLabel.setForeground(new java.awt.Color(255, 255, 255));
        bannerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/VOSpecHeader1.png"))); // NOI18N
        bannerLabel.setAlignmentX(0.5F);
        bannerLabel.setAlignmentY(0.0F);
        bannerLabel.setIconTextGap(0);
        bannerLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        titleImagePanel.add(bannerLabel, java.awt.BorderLayout.CENTER);

        titlePanel.add(titleImagePanel, java.awt.BorderLayout.NORTH);

        iconMenuPanel.setBackground(new java.awt.Color(235, 235, 235));
        iconMenuPanel.setMaximumSize(new java.awt.Dimension(2000, 24));
        iconMenuPanel.setPreferredSize(new java.awt.Dimension(680, 40));
        iconMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        generalToolBar.setBackground(new java.awt.Color(235, 235, 235));
        generalToolBar.setMaximumSize(new java.awt.Dimension(658, 440));
        generalToolBar.setPreferredSize(new java.awt.Dimension(500, 42));
        generalToolBar.setRequestFocusEnabled(false);

        openFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/open.gif"))); // NOI18N
        openFileButton.setToolTipText("Open Spectrum");
        openFileButton.setMaximumSize(new java.awt.Dimension(40, 40));
        openFileButton.setMinimumSize(new java.awt.Dimension(40, 40));
        openFileButton.setPreferredSize(new java.awt.Dimension(40, 40));
        openFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(openFileButton);

        localDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/editLocalFile.gif"))); // NOI18N
        localDataButton.setToolTipText("Local Spectra Editor (SSAP Wrapper)");
        localDataButton.setMaximumSize(new java.awt.Dimension(40, 40));
        localDataButton.setMinimumSize(new java.awt.Dimension(40, 40));
        localDataButton.setPreferredSize(new java.awt.Dimension(40, 40));
        localDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localDataButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(localDataButton);

        saveImageButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/save.gif"))); // NOI18N
        saveImageButton2.setToolTipText("Save As..");
        saveImageButton2.setMaximumSize(new java.awt.Dimension(40, 40));
        saveImageButton2.setMinimumSize(new java.awt.Dimension(40, 40));
        saveImageButton2.setPreferredSize(new java.awt.Dimension(40, 40));
        saveImageButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageButton2ActionPerformed(evt);
            }
        });
        generalToolBar.add(saveImageButton2);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/settings.gif"))); // NOI18N
        jButton1.setToolTipText("Plot Settings");
        jButton1.setMaximumSize(new java.awt.Dimension(40, 40));
        jButton1.setMinimumSize(new java.awt.Dimension(40, 40));
        jButton1.setPreferredSize(new java.awt.Dimension(40, 40));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        generalToolBar.add(jButton1);

        slapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/slap.gif"))); // NOI18N
        slapButton.setToolTipText("Simple Line Access");
        slapButton.setMaximumSize(new java.awt.Dimension(40, 40));
        slapButton.setMinimumSize(new java.awt.Dimension(40, 40));
        slapButton.setPreferredSize(new java.awt.Dimension(40, 40));
        slapButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slapButtonMouseClicked(evt);
            }
        });
        generalToolBar.add(slapButton);

        normButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/norm.gif"))); // NOI18N
        normButton1.setToolTipText("Normalization Tool");
        normButton1.setMaximumSize(new java.awt.Dimension(40, 40));
        normButton1.setMinimumSize(new java.awt.Dimension(40, 40));
        normButton1.setPreferredSize(new java.awt.Dimension(40, 40));
        normButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normButton1ActionPerformed(evt);
            }
        });
        generalToolBar.add(normButton1);

        scissorsButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        scissorsButton.setForeground(new java.awt.Color(61, 60, 60));
        scissorsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/scissors.png"))); // NOI18N
        scissorsButton.setToolTipText("Cut Spectrum");
        scissorsButton.setMaximumSize(new java.awt.Dimension(40, 40));
        scissorsButton.setMinimumSize(new java.awt.Dimension(40, 40));
        scissorsButton.setPreferredSize(new java.awt.Dimension(40, 40));
        scissorsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scissorsButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(scissorsButton);

        fullSizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/fullSize.gif"))); // NOI18N
        fullSizeButton.setToolTipText("Plot Full Size");
        fullSizeButton.setMaximumSize(new java.awt.Dimension(40, 40));
        fullSizeButton.setMinimumSize(new java.awt.Dimension(40, 40));
        fullSizeButton.setPreferredSize(new java.awt.Dimension(40, 40));
        fullSizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fullSizeButtonMouseClicked(evt);
            }
        });
        generalToolBar.add(fullSizeButton);

        fixButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        fixButton.setForeground(new java.awt.Color(61, 60, 60));
        fixButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/unzoom.gif"))); // NOI18N
        fixButton.setToolTipText("Unzoom");
        fixButton.setMaximumSize(new java.awt.Dimension(40, 40));
        fixButton.setMinimumSize(new java.awt.Dimension(40, 40));
        fixButton.setPreferredSize(new java.awt.Dimension(40, 40));
        fixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(fixButton);

        clearCacheButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        clearCacheButton.setForeground(new java.awt.Color(61, 60, 60));
        clearCacheButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/clarcache.gif"))); // NOI18N
        clearCacheButton.setToolTipText("Clear Cache");
        clearCacheButton.setMaximumSize(new java.awt.Dimension(40, 40));
        clearCacheButton.setMinimumSize(new java.awt.Dimension(40, 40));
        clearCacheButton.setPreferredSize(new java.awt.Dimension(40, 40));
        clearCacheButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCacheButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(clearCacheButton);

        plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hubBroken.gif"))); // NOI18N
        plasticButton.setToolTipText("Register to the Plastic HUB");
        plasticButton.setMaximumSize(new java.awt.Dimension(40, 40));
        plasticButton.setMinimumSize(new java.awt.Dimension(40, 40));
        plasticButton.setPreferredSize(new java.awt.Dimension(40, 40));
        plasticButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plasticButtonActionPerformed(evt);
            }
        });
        generalToolBar.add(plasticButton);

        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/table.gif"))); // NOI18N
        viewButton.setToolTipText("Tree/Table view");
        viewButton.setMaximumSize(new java.awt.Dimension(40, 40));
        viewButton.setMinimumSize(new java.awt.Dimension(40, 40));
        viewButton.setPreferredSize(new java.awt.Dimension(40, 40));
        viewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewButtonMouseClicked(evt);
            }
        });
        generalToolBar.add(viewButton);

        iconMenuPanel.add(generalToolBar);

        helpToolBar.setBackground(new java.awt.Color(235, 235, 235));
        helpToolBar.setPreferredSize(new java.awt.Dimension(138, 42));

        mailButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/mail4.gif"))); // NOI18N
        mailButton.setToolTipText("Contact ESAVO Team");
        mailButton.setMaximumSize(new java.awt.Dimension(40, 40));
        mailButton.setPreferredSize(new java.awt.Dimension(30, 30));
        mailButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mailButtonMouseClicked(evt);
            }
        });
        helpToolBar.add(mailButton);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/HowTo.gif"))); // NOI18N
        jButton4.setToolTipText("HowTo Document");
        jButton4.setMaximumSize(new java.awt.Dimension(40, 40));
        jButton4.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        helpToolBar.add(jButton4);

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/about.png"))); // NOI18N
        aboutButton.setToolTipText("About VOSpec");
        aboutButton.setMaximumSize(new java.awt.Dimension(40, 40));
        aboutButton.setPreferredSize(new java.awt.Dimension(30, 30));
        aboutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                aboutButtonMouseClicked(evt);
            }
        });
        helpToolBar.add(aboutButton);

        iconMenuPanel.add(helpToolBar);

        titlePanel.add(iconMenuPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(titlePanel, java.awt.BorderLayout.NORTH);

        optionPanel.setBackground(new java.awt.Color(223, 222, 222));
        optionPanel.setPreferredSize(new java.awt.Dimension(150, 550));
        optionPanel.setLayout(new java.awt.BorderLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(150, 250));
        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(249, 244, 244));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setNextFocusableComponent(redShiftCheck);
        jPanel1.setPreferredSize(new java.awt.Dimension(150, 250));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        waveLabelPanel.setBackground(new java.awt.Color(249, 244, 244));
        waveLabelPanel.setMaximumSize(new java.awt.Dimension(130, 20));
        waveLabelPanel.setMinimumSize(new java.awt.Dimension(130, 20));
        waveLabelPanel.setPreferredSize(new java.awt.Dimension(120, 20));
        waveLabelPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        waveLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waveLabel.setForeground(new java.awt.Color(61, 60, 60));
        waveLabel.setText("Wave Unit");
        waveLabel.setAlignmentX(0.5F);
        waveLabelPanel.add(waveLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 20));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(61, 60, 60));
        jLabel2.setText("Log");
        jLabel2.setMaximumSize(new java.awt.Dimension(48, 20));
        jLabel2.setMinimumSize(new java.awt.Dimension(48, 20));
        jLabel2.setPreferredSize(new java.awt.Dimension(48, 20));
        waveLabelPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, 20, 20));

        jPanel1.add(waveLabelPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 3, 140, -1));

        umPanel.setBackground(new java.awt.Color(249, 244, 244));
        umPanel.setMinimumSize(new java.awt.Dimension(53, 0));
        umPanel.setPreferredSize(new java.awt.Dimension(120, 25));
        umPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        waveChoice.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        waveChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "micron", "m", "Angstrom", "Hz", "Kev", "1/cm" }));
        waveChoice.setMaximumSize(new java.awt.Dimension(81, 22));
        waveChoice.setMinimumSize(new java.awt.Dimension(81, 22));
        waveChoice.setPreferredSize(new java.awt.Dimension(81, 22));
        waveChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveChoiceActionPerformed(evt);
            }
        });
        umPanel.add(waveChoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 0, -1, -1));

        logWave.setBackground(new java.awt.Color(249, 244, 244));
        logWave.setSelected(true);
        logWave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logWaveActionPerformed(evt);
            }
        });
        umPanel.add(logWave, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 0, -1, -1));

        jPanel1.add(umPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 26, 140, -1));

        fluxLabelPanel.setBackground(new java.awt.Color(249, 244, 244));
        fluxLabelPanel.setMaximumSize(new java.awt.Dimension(112, 20));
        fluxLabelPanel.setMinimumSize(new java.awt.Dimension(112, 20));
        fluxLabelPanel.setPreferredSize(new java.awt.Dimension(112, 20));
        fluxLabelPanel.setLayout(new java.awt.BorderLayout());

        fluxLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        fluxLabel.setForeground(new java.awt.Color(61, 60, 60));
        fluxLabel.setText("Flux Unit");
        fluxLabel.setAlignmentX(0.5F);
        fluxLabel.setMaximumSize(new java.awt.Dimension(47, 12));
        fluxLabel.setMinimumSize(new java.awt.Dimension(47, 12));
        fluxLabel.setPreferredSize(new java.awt.Dimension(47, 12));
        fluxLabelPanel.add(fluxLabel, java.awt.BorderLayout.CENTER);

        jPanel1.add(fluxLabelPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 50, 140, -1));

        fluxPanel.setBackground(new java.awt.Color(249, 244, 244));
        fluxPanel.setMaximumSize(new java.awt.Dimension(112, 31));
        fluxPanel.setMinimumSize(new java.awt.Dimension(112, 31));
        fluxPanel.setPreferredSize(new java.awt.Dimension(112, 31));
        fluxPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fluxChoice.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        fluxChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jy", "W/cm2/um", "erg/cm2/s/A", "Counts", "W/m2", "erg/cm2/s" }));
        fluxChoice.setMaximumSize(new java.awt.Dimension(81, 22));
        fluxChoice.setMinimumSize(new java.awt.Dimension(81, 22));
        fluxChoice.setPreferredSize(new java.awt.Dimension(81, 22));
        fluxChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fluxChoiceActionPerformed(evt);
            }
        });
        fluxPanel.add(fluxChoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 0, -1, -1));

        logFlux.setBackground(new java.awt.Color(249, 244, 244));
        logFlux.setSelected(true);
        logFlux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logFluxActionPerformed(evt);
            }
        });
        fluxPanel.add(logFlux, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 0, -1, -1));

        jPanel1.add(fluxPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 70, 140, -1));

        jSeparator1.setPreferredSize(new java.awt.Dimension(110, 2));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 103, -1, -1));

        redShiftPanel.setBackground(new java.awt.Color(249, 244, 244));
        redShiftPanel.setMaximumSize(new java.awt.Dimension(112, 31));
        redShiftPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        redShiftLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        redShiftLabel.setForeground(new java.awt.Color(61, 60, 60));
        redShiftLabel.setText("RedShift");
        redShiftLabel.setAlignmentX(0.5F);
        redShiftPanel.add(redShiftLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 9, -1, -1));

        redShiftValue.setColumns(3);
        redShiftValue.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        redShiftValue.setText("0.00");
        redShiftValue.setMaximumSize(new java.awt.Dimension(27, 17));
        redShiftValue.setMinimumSize(new java.awt.Dimension(27, 17));
        redShiftValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redShiftValueActionPerformed(evt);
            }
        });
        redShiftPanel.add(redShiftValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 7, -1, -1));

        redShiftCheck.setBackground(new java.awt.Color(249, 244, 244));
        redShiftCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redShiftCheckActionPerformed(evt);
            }
        });
        redShiftPanel.add(redShiftCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 5, -1, -1));

        jPanel1.add(redShiftPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 108, 140, -1));

        deredPanel.setBackground(new java.awt.Color(249, 244, 244));
        deredPanel.setMaximumSize(new java.awt.Dimension(112, 31));
        deredPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        deRedLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        deRedLabel.setForeground(new java.awt.Color(61, 60, 60));
        deRedLabel.setText("De-reddening");
        deRedLabel.setAlignmentX(0.5F);
        deredPanel.add(deRedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 9, -1, -1));

        deRedCheck.setBackground(new java.awt.Color(249, 244, 244));
        deRedCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deRedCheckMouseClicked(evt);
            }
        });
        deRedCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deRedCheckActionPerformed(evt);
            }
        });
        deredPanel.add(deRedCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 5, -1, -1));

        jPanel1.add(deredPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 135, 140, -1));

        waveVelocityPanel.setBackground(new java.awt.Color(249, 244, 244));
        waveVelocityPanel.setMaximumSize(new java.awt.Dimension(112, 31));
        waveVelocityPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        waveToVelocityLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waveToVelocityLabel.setForeground(new java.awt.Color(61, 60, 60));
        waveToVelocityLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/wavelengthVelocityLittle.png"))); // NOI18N
        waveToVelocityLabel.setToolTipText("CTRL-Click to get points");
        waveToVelocityLabel.setAlignmentX(0.5F);
        waveVelocityPanel.add(waveToVelocityLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 30, 30));

        waveToVelocityValue.setColumns(3);
        waveToVelocityValue.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        waveToVelocityValue.setText("0.00");
        waveToVelocityValue.setToolTipText("CTRL-Click to get points");
        waveToVelocityValue.setMaximumSize(new java.awt.Dimension(27, 17));
        waveToVelocityValue.setMinimumSize(new java.awt.Dimension(27, 17));
        waveToVelocityValue.setPreferredSize(new java.awt.Dimension(33, 17));
        waveToVelocityValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveToVelocityValueActionPerformed(evt);
            }
        });
        waveVelocityPanel.add(waveToVelocityValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 10, 50, -1));

        waveToVelocityCheckBox.setBackground(new java.awt.Color(249, 244, 244));
        waveToVelocityCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                waveToVelocityCheckBoxMouseClicked(evt);
            }
        });
        waveToVelocityCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveToVelocityCheckBoxActionPerformed(evt);
            }
        });
        waveVelocityPanel.add(waveToVelocityCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 7, -1, -1));

        jPanel1.add(waveVelocityPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 160, 140, -1));

        yErrorLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        yErrorLabel.setForeground(new java.awt.Color(61, 60, 60));
        yErrorLabel.setText("Y-axis error");
        yErrorLabel.setAlignmentX(0.5F);
        jPanel1.add(yErrorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 80, 20));

        xErrorLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        xErrorLabel.setForeground(new java.awt.Color(61, 60, 60));
        xErrorLabel.setText("X-axis error");
        xErrorLabel.setAlignmentX(0.5F);
        jPanel1.add(xErrorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, 20));

        yErrorCheck.setBackground(new java.awt.Color(249, 244, 244));
        yErrorCheck.setSelected(true);
        yErrorCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yErrorCheckActionPerformed(evt);
            }
        });
        jPanel1.add(yErrorCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 200, -1, -1));

        xErrorCheck.setBackground(new java.awt.Color(249, 244, 244));
        xErrorCheck.setSelected(true);
        xErrorCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xErrorCheckActionPerformed(evt);
            }
        });
        jPanel1.add(xErrorCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, -1, -1));

        jPanel9.add(jPanel1, java.awt.BorderLayout.NORTH);

        optionPanel.add(jPanel9, java.awt.BorderLayout.NORTH);

        displayColorPanel.setBackground(new java.awt.Color(231, 230, 230));
        displayColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        displayColorPanel.setAutoscrolls(true);
        displayColorPanel.setMaximumSize(new java.awt.Dimension(140, 32767));
        displayColorPanel.setMinimumSize(new java.awt.Dimension(140, 36));
        displayColorPanel.setPreferredSize(new java.awt.Dimension(139, 600));
        displayColorPanel.setLayout(new java.awt.BorderLayout());

        displaySpectraLabel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        displaySpectraLabel.setForeground(new java.awt.Color(61, 60, 60));
        displaySpectraLabel.setText("Graphic Mode");
        displayColorPanel.add(displaySpectraLabel, java.awt.BorderLayout.NORTH);

        displayColorScrollPanel.setBackground(new java.awt.Color(245, 245, 245));
        displayColorScrollPanel.setBorder(null);
        displayColorScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        displayColorContainerPanel.setBackground(new java.awt.Color(249, 244, 244));
        displayColorContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        displayColorContainerPanel.setAutoscrolls(true);
        displayColorContainerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        displayColorScrollPanel.setViewportView(displayColorContainerPanel);

        displayColorPanel.add(displayColorScrollPanel, java.awt.BorderLayout.CENTER);

        viewAllPanel.setBackground(new java.awt.Color(231, 230, 230));
        viewAllPanel.setPreferredSize(new java.awt.Dimension(120, 25));
        viewAllPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        viewAllButton.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        viewAllButton.setForeground(new java.awt.Color(61, 60, 60));
        viewAllButton.setText("View");
        viewAllButton.setMaximumSize(new java.awt.Dimension(76, 24));
        viewAllButton.setMinimumSize(new java.awt.Dimension(76, 24));
        viewAllButton.setPreferredSize(new java.awt.Dimension(76, 24));
        viewAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAllButtonActionPerformed(evt);
            }
        });
        viewAllPanel.add(viewAllButton);

        displayColorPanel.add(viewAllPanel, java.awt.BorderLayout.SOUTH);

        optionPanel.add(displayColorPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(optionPanel, java.awt.BorderLayout.WEST);

        viewerPanel.setBackground(new java.awt.Color(223, 222, 222));
        viewerPanel.setPreferredSize(new java.awt.Dimension(610, 650));
        viewerPanel.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(223, 222, 222));
        jPanel3.setPreferredSize(new java.awt.Dimension(580, 30));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 5));

        target.setBackground(new java.awt.Color(223, 222, 222));
        target.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        target.setForeground(new java.awt.Color(61, 60, 60));
        target.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        target.setText("Target");
        target.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        target.setMaximumSize(new java.awt.Dimension(55, 15));
        target.setMinimumSize(new java.awt.Dimension(55, 15));
        target.setPreferredSize(new java.awt.Dimension(50, 15));
        jPanel3.add(target);

        targetField.setColumns(6);
        targetField.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        targetField.setMaximumSize(new java.awt.Dimension(70, 17));
        targetField.setMinimumSize(new java.awt.Dimension(70, 17));
        targetField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFieldActionPerformed(evt);
            }
        });
        jPanel3.add(targetField);

        ra.setBackground(new java.awt.Color(223, 222, 222));
        ra.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        ra.setForeground(new java.awt.Color(61, 60, 60));
        ra.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ra.setText("Ra");
        ra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ra.setMaximumSize(new java.awt.Dimension(55, 15));
        ra.setMinimumSize(new java.awt.Dimension(55, 15));
        ra.setPreferredSize(new java.awt.Dimension(30, 15));
        jPanel3.add(ra);

        raField.setColumns(7);
        raField.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        raField.setToolTipText("Right Ascension in degress");
        raField.setMaximumSize(new java.awt.Dimension(70, 17));
        raField.setMinimumSize(new java.awt.Dimension(70, 17));
        jPanel3.add(raField);

        dec.setBackground(new java.awt.Color(223, 222, 222));
        dec.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        dec.setForeground(new java.awt.Color(61, 60, 60));
        dec.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dec.setText("Dec");
        dec.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dec.setMaximumSize(new java.awt.Dimension(55, 15));
        dec.setMinimumSize(new java.awt.Dimension(35, 15));
        dec.setPreferredSize(new java.awt.Dimension(35, 15));
        jPanel3.add(dec);

        decField.setColumns(7);
        decField.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        decField.setToolTipText("Declination in degress");
        decField.setMaximumSize(new java.awt.Dimension(70, 17));
        decField.setMinimumSize(new java.awt.Dimension(70, 17));
        jPanel3.add(decField);

        sizeLabel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        sizeLabel.setForeground(new java.awt.Color(61, 60, 60));
        sizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sizeLabel.setText("Size");
        sizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sizeLabel.setMaximumSize(new java.awt.Dimension(55, 15));
        sizeLabel.setMinimumSize(new java.awt.Dimension(55, 15));
        sizeLabel.setPreferredSize(new java.awt.Dimension(35, 15));
        jPanel3.add(sizeLabel);

        sizeField.setColumns(3);
        sizeField.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        sizeField.setText("1");
        sizeField.setToolTipText("Size in degrees");
        sizeField.setMaximumSize(new java.awt.Dimension(44, 17));
        sizeField.setMinimumSize(new java.awt.Dimension(44, 17));
        jPanel3.add(sizeField);

        jLabel4.setBackground(new java.awt.Color(223, 222, 222));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/transparent.gif"))); // NOI18N
        jPanel3.add(jLabel4);

        jSeparator6.setBackground(new java.awt.Color(223, 222, 222));
        jSeparator6.setForeground(new java.awt.Color(223, 222, 222));
        jSeparator6.setPreferredSize(new java.awt.Dimension(10, 10));
        jPanel3.add(jSeparator6);

        jButton2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton2.setForeground(new java.awt.Color(102, 102, 102));
        jButton2.setText("Query");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        viewerPanel.add(jPanel3, java.awt.BorderLayout.NORTH);

        containerPanel.setBackground(new java.awt.Color(235, 235, 235));
        containerPanel.setMinimumSize(new java.awt.Dimension(580, 620));
        containerPanel.setPreferredSize(new java.awt.Dimension(580, 620));
        containerPanel.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBackground(new java.awt.Color(235, 235, 235));
        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(580, 620));
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(580, 620));

        svPanel.setBackground(new java.awt.Color(235, 235, 235));
        svPanel.setLayout(new java.awt.BorderLayout());

        displayPanel.setBackground(new java.awt.Color(235, 235, 235));
        svPanel.add(displayPanel, java.awt.BorderLayout.CENTER);

        mathBar.setBackground(new java.awt.Color(235, 235, 235));
        mathBar.setMaximumSize(new java.awt.Dimension(598, 64));
        mathBar.setPreferredSize(new java.awt.Dimension(650, 50));

        arithmeticButton2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        arithmeticButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/arithmeticBetweenspectraIcon.png"))); // NOI18N
        arithmeticButton2.setToolTipText("Arithmetic Operations between Spectra");
        arithmeticButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        arithmeticButton2.setMaximumSize(new java.awt.Dimension(60, 45));
        arithmeticButton2.setMinimumSize(new java.awt.Dimension(30, 30));
        arithmeticButton2.setPreferredSize(new java.awt.Dimension(40, 40));
        arithmeticButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                arithmeticButton2MouseClicked(evt);
            }
        });
        arithmeticButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arithmeticButton2ActionPerformed(evt);
            }
        });
        mathBar.add(arithmeticButton2);

        polynomialButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/fit2.gif"))); // NOI18N
        polynomialButton.setToolTipText("Fitting Utilities");
        polynomialButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        polynomialButton.setMaximumSize(new java.awt.Dimension(60, 45));
        polynomialButton.setMinimumSize(new java.awt.Dimension(30, 30));
        polynomialButton.setPreferredSize(new java.awt.Dimension(40, 40));
        polynomialButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                polynomialButtonMouseClicked(evt);
            }
        });
        mathBar.add(polynomialButton);

        bisectorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/bisectorIcon.png"))); // NOI18N
        bisectorButton.setToolTipText("Bisector Method");
        bisectorButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        bisectorButton.setMaximumSize(new java.awt.Dimension(60, 45));
        bisectorButton.setMinimumSize(new java.awt.Dimension(30, 30));
        bisectorButton.setPreferredSize(new java.awt.Dimension(40, 40));
        bisectorButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bisectorButtonMouseClicked(evt);
            }
        });
        mathBar.add(bisectorButton);

        mirroringButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/mirrorIcon3.png"))); // NOI18N
        mirroringButton.setToolTipText("Spectrum Mirroring");
        mirroringButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        mirroringButton.setMaximumSize(new java.awt.Dimension(60, 45));
        mirroringButton.setMinimumSize(new java.awt.Dimension(30, 30));
        mirroringButton.setPreferredSize(new java.awt.Dimension(40, 40));
        mirroringButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mirroringButtonMouseClicked(evt);
            }
        });
        mathBar.add(mirroringButton);

        waveletButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/waveletIcon.png"))); // NOI18N
        waveletButton.setToolTipText("Wavlet");
        waveletButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        waveletButton.setMaximumSize(new java.awt.Dimension(60, 45));
        waveletButton.setMinimumSize(new java.awt.Dimension(30, 30));
        waveletButton.setPreferredSize(new java.awt.Dimension(40, 40));
        waveletButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                waveletButtonMouseClicked(evt);
            }
        });
        mathBar.add(waveletButton);

        equivalentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/equivalentWidthIcon.png"))); // NOI18N
        equivalentButton.setToolTipText("Equivalent Width");
        equivalentButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        equivalentButton.setMaximumSize(new java.awt.Dimension(60, 45));
        equivalentButton.setMinimumSize(new java.awt.Dimension(30, 30));
        equivalentButton.setPreferredSize(new java.awt.Dimension(40, 40));
        equivalentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                equivalentButtonMouseClicked(evt);
            }
        });
        mathBar.add(equivalentButton);

        integratedButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/integratedFluxIcon.png"))); // NOI18N
        integratedButton.setToolTipText("Integrated Flux");
        integratedButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        integratedButton.setMaximumSize(new java.awt.Dimension(60, 45));
        integratedButton.setMinimumSize(new java.awt.Dimension(30, 30));
        integratedButton.setPreferredSize(new java.awt.Dimension(40, 40));
        integratedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                integratedButtonMouseClicked(evt);
            }
        });
        mathBar.add(integratedButton);

        filteringButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/noiseIcon.png"))); // NOI18N
        filteringButton.setToolTipText("Filtering");
        filteringButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        filteringButton.setMaximumSize(new java.awt.Dimension(60, 45));
        filteringButton.setMinimumSize(new java.awt.Dimension(30, 30));
        filteringButton.setPreferredSize(new java.awt.Dimension(40, 40));
        filteringButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filteringButtonMouseClicked(evt);
            }
        });
        mathBar.add(filteringButton);

        tuningButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/toolKey.png"))); // NOI18N
        tuningButton.setToolTipText("Tuning");
        tuningButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        tuningButton.setMaximumSize(new java.awt.Dimension(60, 45));
        tuningButton.setMinimumSize(new java.awt.Dimension(30, 30));
        tuningButton.setPreferredSize(new java.awt.Dimension(40, 40));
        tuningButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tuningButtonMouseClicked(evt);
            }
        });
        mathBar.add(tuningButton);

        statisticButton.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        statisticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/statisticsIcon.png"))); // NOI18N
        statisticButton.setToolTipText("Statistics");
        statisticButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        statisticButton.setMaximumSize(new java.awt.Dimension(60, 45));
        statisticButton.setMinimumSize(new java.awt.Dimension(30, 30));
        statisticButton.setPreferredSize(new java.awt.Dimension(40, 40));
        statisticButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statisticButtonMouseClicked(evt);
            }
        });
        mathBar.add(statisticButton);

        syntheticPhotometryButton.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        syntheticPhotometryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/photometry.png"))); // NOI18N
        syntheticPhotometryButton.setToolTipText("Synthetic photometry");
        syntheticPhotometryButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));
        syntheticPhotometryButton.setFocusable(false);
        syntheticPhotometryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        syntheticPhotometryButton.setMaximumSize(new java.awt.Dimension(60, 45));
        syntheticPhotometryButton.setMinimumSize(new java.awt.Dimension(30, 30));
        syntheticPhotometryButton.setPreferredSize(new java.awt.Dimension(40, 40));
        syntheticPhotometryButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        syntheticPhotometryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syntheticPhotometryButtonActionPerformed(evt);
            }
        });
        mathBar.add(syntheticPhotometryButton);

        svPanel.add(mathBar, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setTopComponent(svPanel);

        barPanel.setLayout(new java.awt.BorderLayout());

        TableJToolBar.setBackground(new java.awt.Color(235, 235, 235));
        TableJToolBar.setRollover(false
        );
        TableJToolBar.setMaximumSize(null);
        TableJToolBar.setMinimumSize(null);
        TableJToolBar.setPreferredSize(null);
        barPanel.add(TableJToolBar, java.awt.BorderLayout.CENTER);

        jSplitPane1.setBottomComponent(barPanel);

        containerPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        viewerPanel.add(containerPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(viewerPanel, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(223, 222, 222));
        jPanel2.setLayout(new java.awt.BorderLayout());

        optPanel.setBackground(new java.awt.Color(223, 222, 222));
        optPanel.setMinimumSize(new java.awt.Dimension(816, 31));
        optPanel.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(223, 222, 222));
        jPanel8.setPreferredSize(new java.awt.Dimension(150, 35));

        coordJLabel.setBackground(new java.awt.Color(153, 153, 255));
        coordJLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        coordJLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        coordJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        coordJLabel.setMaximumSize(new java.awt.Dimension(150, 25));
        coordJLabel.setMinimumSize(new java.awt.Dimension(150, 25));
        coordJLabel.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel8.add(coordJLabel);

        optPanel.add(jPanel8, java.awt.BorderLayout.WEST);

        jPanel4.setBackground(new java.awt.Color(223, 222, 222));
        jPanel4.setMaximumSize(new java.awt.Dimension(250, 25));
        jPanel4.setMinimumSize(new java.awt.Dimension(250, 25));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        displayButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        displayButton.setForeground(new java.awt.Color(102, 51, 0));
        displayButton.setText("RETRIEVE");
        displayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayButtonActionPerformed(evt);
            }
        });
        jPanel4.add(displayButton);

        resetButton3.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        resetButton3.setForeground(new java.awt.Color(61, 60, 60));
        resetButton3.setText("Unmark All");
        resetButton3.setActionCommand("showResult()");
        resetButton3.setMaximumSize(new java.awt.Dimension(100, 24));
        resetButton3.setMinimumSize(new java.awt.Dimension(100, 24));
        resetButton3.setPreferredSize(new java.awt.Dimension(120, 24));
        resetButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(resetButton3);

        resetButton2.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        resetButton2.setForeground(new java.awt.Color(61, 60, 60));
        resetButton2.setText("Reset");
        resetButton2.setActionCommand("showResult()");
        resetButton2.setMaximumSize(new java.awt.Dimension(76, 24));
        resetButton2.setMinimumSize(new java.awt.Dimension(76, 24));
        resetButton2.setPreferredSize(new java.awt.Dimension(76, 24));
        resetButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(resetButton2);

        optPanel.add(jPanel4, java.awt.BorderLayout.EAST);

        jPanel6.setBackground(new java.awt.Color(223, 222, 222));
        jPanel6.setMaximumSize(new java.awt.Dimension(10, 5));
        jPanel6.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel6.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jProgressBar1.setPreferredSize(new java.awt.Dimension(140, 18));
        jProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar1MouseClicked(evt);
            }
        });
        jPanel6.add(jProgressBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 20));

        logLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        logLabel.setForeground(new java.awt.Color(255, 0, 0));
        jPanel6.add(logLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, -1, -1));

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/close_cross.gif"))); // NOI18N
        cancelButton.setToolTipText("STOP operation");
        cancelButton.setMaximumSize(null);
        cancelButton.setMinimumSize(null);
        cancelButton.setPreferredSize(new java.awt.Dimension(22, 22));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel6.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 20, 20));
        cancelButton.getAccessibleContext().setAccessibleName("STOP operation");

        jProgressBar2.setPreferredSize(new java.awt.Dimension(140, 18));
        jProgressBar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar2MouseClicked(evt);
            }
        });
        jPanel6.add(jProgressBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, -1, 20));

        optPanel.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel2.add(optPanel, java.awt.BorderLayout.NORTH);

        jPanel7.setBackground(new java.awt.Color(223, 222, 222));
        jPanel7.setMinimumSize(new java.awt.Dimension(815, 10));
        jPanel7.setPreferredSize(new java.awt.Dimension(815, 20));

        jPanel5.setBackground(new java.awt.Color(223, 222, 222));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jLabel1.setBackground(new java.awt.Color(129, 126, 126));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(162, 159, 159));
        jLabel1.setText("Copyright ESAC - Villanueva de la Cañada - Madrid, Spain");
        jPanel5.add(jLabel1);

        jPanel7.add(jPanel5);

        jPanel2.add(jPanel7, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Returns a new, randomly seeded, Random object.
     *
     * @return  random
     */
    static Random createRandom() {
        byte[] seedBytes = new SecureRandom().generateSeed(8);
        long seed = 0L;
        for (int i = 0; i < 8; i++) {
            seed = (seed << 8) | (seedBytes[i] & 0xff);
        }
        return new Random(seed);
    }

    private void polynomialButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_polynomialButtonMouseClicked
        fittingUtilities();
    }//GEN-LAST:event_polynomialButtonMouseClicked

    private void arithmeticButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_arithmeticButton2MouseClicked
        VOSpeculator vs = new VOSpeculator();
        vs.setAioSpecToolDetached(this);
        vs.setVisible(true);
    }//GEN-LAST:event_arithmeticButton2MouseClicked

    private void bisectorButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bisectorButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(6));
    }//GEN-LAST:event_bisectorButtonMouseClicked

    private void mirroringButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mirroringButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(4));
    }//GEN-LAST:event_mirroringButtonMouseClicked

    private void waveletButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_waveletButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(2));
    }//GEN-LAST:event_waveletButtonMouseClicked

    private void equivalentButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_equivalentButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(0));
    }//GEN-LAST:event_equivalentButtonMouseClicked

    private void integratedButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_integratedButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(1));
    }//GEN-LAST:event_integratedButtonMouseClicked

    private void filteringButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filteringButtonMouseClicked
        if (fw != null) {
            fw.setVisible(true);
        } else {
            fw = new FilteringWindow(plot, spectrumSet/*,model*/);
            fw.setAioSpecToolDetached(this);

            fw.setVisible(true);
        }
    }//GEN-LAST:event_filteringButtonMouseClicked

    private void tuningButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tuningButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(5));
    }//GEN-LAST:event_tuningButtonMouseClicked

    private void statisticButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statisticButtonMouseClicked
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(3));
    }//GEN-LAST:event_statisticButtonMouseClicked

    private void saveVOEspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveVOEspaceActionPerformed
        // VOEspace save (to decomment)
        //try{
        //    setWaitCursor();
        //    System.out.println("serviceEndPoint "+serviceEndPoint);
        //    voespaceClient = new Client(serviceEndPoint);
        //    List fileList = new ArrayList();
        //    //fileList = (List) voespaceClient.browseVOEspace("http-get");
        //    fileList = (List) voespaceClient.browseVOEspace("gridftp");
        //    //fileList = (List) voespaceClient.browseVOEspace();
        //    setDefaultCursor();
        //    VOEspaceWinSave vw = new VOEspaceWinSave(fileList);
        //    vw.setAioSpecToolDetached(this);
        //    vw.setSize(470,350);
        //    vw.setVisible(true);
        // }catch(Exception e){
        //    e.printStackTrace();
        // }
    }//GEN-LAST:event_saveVOEspaceActionPerformed

    public void saveFileInVOEspace() {
        // VOEspace save (to decomment)
        //try{
        //    //Saves a file in VOEspace repository
        //    String wave = (String)waveChoice.getSelectedItem();
        //    String flux = (String)fluxChoice.getSelectedItem();
        //    Vector data = plot.getPointsLinear();
        //    if(data==null){
        //        JOptionPane.showMessageDialog(this, "Please open file first");
        //    }
        //    File temp = new File(getVOEspaceFileName());
        //    PrintWriter out =  new PrintWriter(new BufferedWriter(new FileWriter(temp, false)));
        //    Utils.createHeader(temp,wave,flux);
        //    Utils.fillData(temp,data);
        //    Utils.closeFile(temp);
        //    out.flush();
        //    out.close();
        //    JOptionPane.showMessageDialog(this, "File saved and available for run here: " + voespaceClient.saveFileInVOEspace(temp,"http-put"));
        //    //JOptionPane.showMessageDialog(this, "File saved and available for run here: " + voespaceClient.saveFileInVOEspace(temp));
        // }catch(Exception e){
        //     e.printStackTrace();
        //}
    }

    private void openVOEspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVOEspaceActionPerformed
        // VOEspace open file (to decomment)
        //try {
        //    setWaitCursor();
        //    //VOSpec retrieves a file from VOEspace repository
        //    System.out.println("serviceEndPoint "+serviceEndPoint);
        //    voespaceClient = new Client(serviceEndPoint);
        //    List fileList = new ArrayList();
        //    //fileList = (List) voespaceClient.browseVOEspace("http-get");
        //    fileList = (List) voespaceClient.browseVOEspace("gridftp");
        //    //fileList = (List) voespaceClient.browseVOEspace();
        //    VOEspaceWin vw = new VOEspaceWin(fileList);
        //    vw.setAioSpecToolDetached(this);
        //    vw.setSize(470,350);
        //    vw.setVisible(true);
        //    setDefaultCursor();
        //} catch(Exception e){
        //    System.out.println("\n\n\n#### ERROR in VOEspaceServiceTest class!!!"
        //           + e.getMessage());
        //    e.printStackTrace();
        //}
    }//GEN-LAST:event_openVOEspaceActionPerformed

    public void openVOEspaceFiles() {
        // VOEspace get File (to decomment)
        // setWaitCursor();
        // voespaceClient = new Client(serviceEndPoint);
        // Vector selectedFiles = new Vector();
        // selectedFiles = (Vector)getVOEspaceList();
        // for(int i=0;i<selectedFiles.size();i++){
        //     //File retrievedFile = voespaceClient.retrieveFileFromVOEspace((String)selectedFiles.elementAt(i),"http-get");
        //     //File retrievedFile = voespaceClient.retrieveFileFromVOEspace((String)selectedFiles.elementAt(i),"gridftp");
        //     //File retrievedFile = voespaceClient.retrieveFileFromVOEspace((String)selectedFiles.elementAt(i));
        //     //System.out.println("retrievedFile "+retrievedFile.toString());
        //     //String urlString = voespaceClient.retrieveURLFromVOEspace(selectedFiles.elementAt(i).toString(),"http-get");
        //     String urlString = voespaceClient.retrieveURLFromVOEspace(selectedFiles.elementAt(i).toString(),"gridftp");
        //     //String urlString = voespaceClient.retrieveURLFromVOEspace(selectedFiles.elementAt(i).toString());
        //     localDataDialog.addNewDirectly(urlString,urlString);
        // }
        // setDefaultCursor();
    }

    public void openVOEspaceURI(esavo.vospec.spectrum.Spectrum spectrum) {
        // VOEspace  (to decomment)
        //setWaitCursor();
        // voespaceClient = new Client(serviceEndPoint);
        // System.out.println("Retrieve from voespace "+spectrum.getUrl());
        // String urlString = voespaceClient.retrieveURLFromVOEspace(spectrum.getUrl(),"gridftp");
        // spectrum.setUrl(urlString);
        // if(urlString != null){
        //     System.out.println("Setting "+urlString);
        //     System.out.println("Spectrum URL after calling voespace "+spectrum.getUrl());
        //     FitsSpectrum fitsSpectrum = new FitsSpectrum(spectrum);
        //     fitsSpectrum.setSelected(true);
        //     fitsSpectrum.setToWait(true);
        //     System.out.println("FitsSpectrum setted");
        //     localDataDialog.addSpectrum(fitsSpectrum);
        //     localDataDialog.addLocalData();
        //     System.out.println("FitsSpectrum displayed");
        // }else{
        //     System.out.println("Problems retrieving url from VOEspace");
        // }
        // setDefaultCursor();
    }

    /////////////// VOEspace Task to be refactored ////////////////////
    public void setVOEspaceList(Vector voespaceList) {
        this.voespaceList = voespaceList;
    }

    public Vector getVOEspaceList() {
        return voespaceList;
    }

    public void setVOEspaceFileName(String voespaceFileName) {
        this.voespaceFileName = voespaceFileName;
    }

    public String getVOEspaceFileName() {
        return voespaceFileName;
    }

    ///////////////////////////////////////////////////////////////////
    private void getPointItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPointItemActionPerformed
        System.out.println("CTRL is pressed");
        setCrossHairCursor();
        plot.setCrossActive();
    }//GEN-LAST:event_getPointItemActionPerformed

    private void waveToVelocityValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveToVelocityValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waveToVelocityValueActionPerformed

    private void waveToVelocityCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_waveToVelocityCheckBoxMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_waveToVelocityCheckBoxMouseClicked

    private void waveletButtonItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveletButtonItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(2));
    }//GEN-LAST:event_waveletButtonItemActionPerformed

    private void normButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normButton1ActionPerformed
        setHandCursor();
        plot.setNormActive();
    }//GEN-LAST:event_normButton1ActionPerformed

    private void javaHelpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_javaHelpItemActionPerformed

        try {
            URL url = new URL("http://" + EnvironmentDefs.getSERVERHOST() + ":" + EnvironmentDefs.getSERVERPORT() + "/vospec/javaHelp/helpfile.hs");
            HelpSet hs = new HelpSet(null, url);
            HelpBroker hb = hs.createHelpBroker();
            hb.setDisplayed(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening JavaHelp. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            e.printStackTrace();
        }

    }//GEN-LAST:event_javaHelpItemActionPerformed

    private void statisticsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statisticsItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(3));
    }//GEN-LAST:event_statisticsItemActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setWaitCursor();
        callVOSpecAdvancedSelector();
        setDefaultCursor();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tuningItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tuningItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(5));
    }//GEN-LAST:event_tuningItemActionPerformed

    private void scissorsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scissorsButtonActionPerformed
        Spectrum spectrumModified = new Spectrum();

        spectrumModified = getXDataYData();
        spectrumModified.setTitle("Spectra section");
        spectrumModified.setUrl("Spectra section" + this.mathematicMethodExecution);
        this.mathematicMethodExecution++;
        spectrumModified.setFormat("application/fits");
        spectrumModified.setUnits(new Unit(waveChoice.getSelectedItem().toString(), fluxChoice.getSelectedItem().toString()));

        SpectrumUtils.checkVelocity(this, spectrumModified);

        SpectrumSet spectrumSetTmp = new SpectrumSet();
        spectrumSetTmp.addSpectrum(0, spectrumModified);

        //add to the previous spectrumSet a new Spectrum trasformed
        spectrumSet.addSpectrumSet(spectrumSetTmp);
        spectrumModified.setRow(spectrumSet.getSpectrumSet().size() - 1);
        JOptionPane.showMessageDialog(this, " Spectra section saved and reachable at the end of your Spectra List ");

        addSpectrum("Spectra Sections", spectrumModified, (javax.swing.JTextArea) null/*,nameNode*/);

    }//GEN-LAST:event_scissorsButtonActionPerformed

    // Needed by scissors Method
    public Spectrum getXDataYData() {

        double xMinOriginal = 0;
        double xMaxOriginal = 0;

        Vector dataToFit = plot.getPoints();
        int numberOfPoints = dataToFit.size();

        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];

        boolean logX = plot.getXLog();
        boolean logY = plot.getYLog();

        for (int i = 0; i < numberOfPoints; i++) {

            double[] element = (double[]) dataToFit.elementAt(i);

            xData[i] = element[0];
            yData[i] = element[1];

            if (logX) {
                xData[i] = Math.pow(10., xData[i]);
            }
            if (logY) {
                yData[i] = Math.pow(10., yData[i]);
            }

            if (i == 0) {
                xMinOriginal = xData[i];
                xMaxOriginal = xData[i];
            }
            if (xData[i] < xMinOriginal) {
                xMinOriginal = xData[i];
            }
            if (xData[i] > xMaxOriginal) {
                xMaxOriginal = xData[i];
            }

        }

        Spectrum spectrum = new Spectrum();
        spectrum.setWaveValues(xData);
        spectrum.setFluxValues(yData);

        return spectrum;

    }

    public Spectrum getLinearXDataYData() {

        double xMinOriginal = 0;
        double xMaxOriginal = 0;

        Vector dataToFit = plot.getPoints();
        int numberOfPoints = dataToFit.size();
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];

        for (int i = 0; i < numberOfPoints; i++) {

            double[] element = (double[]) dataToFit.elementAt(i);

            xData[i] = element[0];
            yData[i] = element[1];

            if (i == 0) {
                xMinOriginal = xData[i];
                xMaxOriginal = xData[i];
            }
            if (xData[i] < xMinOriginal) {
                xMinOriginal = xData[i];
            }
            if (xData[i] > xMaxOriginal) {
                xMaxOriginal = xData[i];
            }

        }

        Spectrum spectrum = new Spectrum();
        spectrum.setWaveValues(xData);
        spectrum.setFluxValues(yData);

        return spectrum;

    }

    private void helpBarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpBarItemActionPerformed
        if (helpVisible) {
            helpToolBar.setVisible(false);
            helpVisible = false;
        } else {
            helpToolBar.setVisible(true);
            helpVisible = true;
        }
    }//GEN-LAST:event_helpBarItemActionPerformed

    private void filteringItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filteringItemActionPerformed
        if (fw != null) {
            fw.setVisible(true);
        } else {
            fw = new FilteringWindow(plot, spectrumSet/*,model*/);
            fw.setAioSpecToolDetached(this);
            fw.setVisible(true);
        }
    }//GEN-LAST:event_filteringItemActionPerformed

    private void integratedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_integratedItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(1));
    }//GEN-LAST:event_integratedItemActionPerformed

    private void EquivalentItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EquivalentItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(0));
    }//GEN-LAST:event_EquivalentItemActionPerformed

    private void bisectorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisectorItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(6));
    }//GEN-LAST:event_bisectorItemActionPerformed

    private void mirroringItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mirroringItemActionPerformed
        callAnalysisWindow();
        analysisUtilities((Component) analysisWindow.jTabbedPane1.getComponent(4));
    }//GEN-LAST:event_mirroringItemActionPerformed

    private void arithmeticBarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arithmeticBarItemActionPerformed

        if (mathVisible) {
            hideMathBar();
        } else {
            showMathBar();
        }

    }//GEN-LAST:event_arithmeticBarItemActionPerformed

    public void showMathBar() {

        svPanel.add(mathBar, java.awt.BorderLayout.SOUTH);
        svPanel.validate();
        svPanel.repaint();
        mathVisible = true;

    }

    public void hideMathBar() {

        svPanel.remove(mathBar);
        svPanel.validate();
        svPanel.repaint();
        mathVisible = false;

    }

    public void callAnalysisWindow() {

        if (analysisWindow != null) {
            analysisWindow.setVisible(true);
        } else {
            analysisWindow = new AnalysisWindow(plot, spectrumSet/*,model*/);
        }

    }

    public void analysisUtilities(Component selectedComponent) {

        analysisWindow.jTabbedPane1.setSelectedComponent(selectedComponent);
        analysisWindow.setAioSpecToolDetached(this);
        analysisWindow.setVisible(true);
        //refreshJTree();
        setDefaultCursor();

    }

    private void arithmeticItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arithmeticItem2ActionPerformed
        VOSpeculator vs = new VOSpeculator();
        vs.setAioSpecToolDetached(this);
        vs.setVisible(true);
    }//GEN-LAST:event_arithmeticItem2ActionPerformed

    public void arithmeticBetweenSpectra() {
        arithmeticWindowMultipleSpectra = new ArithmeticWindowMultipleSpectra();
        arithmeticWindowMultipleSpectra.setAioSpecToolDetached(this);

        Vector spectraVector = new Vector();
        if (spectrumSet != null) {
            for (int s = 0; s < spectrumSet.getSpectrumSet().size(); s++) {
                Spectrum tmpSpectrum = spectrumSet.getSpectrum(s);
                if (tmpSpectrum.getSelected()) {
                    spectraVector.add(tmpSpectrum);
                }
            }
            arithmeticWindowMultipleSpectra.setSpectraVector(spectraVector);
            arithmeticWindowMultipleSpectra.drawSpectraSelected();
        } else {
            JOptionPane.showMessageDialog(this, "No spectra selected");
            return;
        }

        arithmeticWindowMultipleSpectra.setVisible(true);
    }

    public void arithmeticSpectraConstant() {
        arithmeticWindow = new ArithmeticWindow();
        arithmeticWindow.setAioSpecToolDetached(this);

        Vector spectraVector = new Vector();
        if (spectrumSet != null) {
            for (int s = 0; s < spectrumSet.getSpectrumSet().size(); s++) {
                Spectrum tmpSpectrum = spectrumSet.getSpectrum(s);
                if (tmpSpectrum.getSelected()) {
                    spectraVector.add(tmpSpectrum);

                }
            }
            arithmeticWindow.setSpectraVector(spectraVector);
            arithmeticWindow.drawSpectraSelected();
        } else {
            JOptionPane.showMessageDialog(this, "No spectra selected");
            return;
        }

        arithmeticWindow.setVisible(true);
    }

    private void internalHubItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internalHubItemActionPerformed
        startHub();
    }//GEN-LAST:event_internalHubItemActionPerformed

    public void startHub() {
        try {
            ServerSet ss = new ServerSet(new File(".plastic"));
            hub = new PlasticHub(ss);
            System.out.println("Hub running..");
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(this, " Problems starting Hub. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            System.out.println("Hub Error " + e);
        }
    }

    private void externalHubItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalHubItemActionPerformed
        try {
           uk.ac.starlink.plastic.PlasticUtils.startExternalHub(true);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(this, " Unable to launch External Hub. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            System.out.println("Unable to launch External Hub" + e);
        }
    }//GEN-LAST:event_externalHubItemActionPerformed

    public void unregisterInterop() {
        interop.unregisterFromHub();
        interop.stopHub();
        putil.plastic.unregister();
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        try {
            putil.plastic.unregister();
            interop.unregisterFromHub();
            localDataDialog.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_formWindowClosed

    private void unregisterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unregisterItemActionPerformed
        putil.registerToPlastic();
    }//GEN-LAST:event_unregisterItemActionPerformed

    private void registerItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerItemActionPerformed
        putil.registerToPlastic();
        putil.checkApplicationRegistered();
    }//GEN-LAST:event_registerItemActionPerformed

    private void openItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openItemActionPerformed
        localDataDialog.addNewDirectly();
    }//GEN-LAST:event_openItemActionPerformed

    private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemActionPerformed
        ChooserSave chooserSave = new ChooserSave();
        chooserSave.setAioSpecToolDetached(this);
        chooserSave.setVisible(true);
    }//GEN-LAST:event_saveItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed

        putil.plastic.unregister();
        interop.unregisterFromHub();
        localDataDialog.dispose();
        if (Utils.getExitMan()) {
            System.exit(0);
        } else {
            dispose();
        }
    }//GEN-LAST:event_exitItemActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
    }//GEN-LAST:event_fileMenuActionPerformed

    private void editDataItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDataItemActionPerformed
        localDataDialog.show();
    }//GEN-LAST:event_editDataItemActionPerformed

    private void setItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setItemActionPerformed
        PlotSettings plotSettings = new PlotSettings(plot);
        plotSettings.setAioSpecToolDetached(this);
        plotSettings.setVisible(true);
    }//GEN-LAST:event_setItemActionPerformed

    private void clearCacheItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCacheItemActionPerformed
        clearCache();
    }//GEN-LAST:event_clearCacheItemActionPerformed

    private void plotSizeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotSizeItemActionPerformed
        fullSizePlot();
    }//GEN-LAST:event_plotSizeItemActionPerformed

    private void unzoomItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unzoomItemActionPerformed
        plot.fillPlot();
    }//GEN-LAST:event_unzoomItemActionPerformed

    private void fittingItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fittingItemActionPerformed
        fittingUtilities();
    }//GEN-LAST:event_fittingItemActionPerformed

    private void NormalizeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NormalizeItemActionPerformed
        setHandCursor();
        plot.setNormActive();
    }//GEN-LAST:event_NormalizeItemActionPerformed

    private void slapItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_slapItemActionPerformed
        slap();
    }//GEN-LAST:event_slapItemActionPerformed

    private void luminosityItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_luminosityItemActionPerformed
//        splineButtonAction();
    }//GEN-LAST:event_luminosityItemActionPerformed

    private void howToItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_howToItemActionPerformed

        try {
            showInBrowser("http://esavo.esa.int/VOSpecManual", this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening VOSpecManual. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");

        }
    }//GEN-LAST:event_howToItemActionPerformed

    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed

        try {
            if (about == null) {
                about = new About(this);
            }
            about.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening VOSpec About Window. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");

        }

    }//GEN-LAST:event_aboutItemActionPerformed

    private void contactItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactItemActionPerformed

        try {
            showInBrowser("http://esavo.esa.int/support", this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening ESA VO HelpDesk. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");

        }

    }//GEN-LAST:event_contactItemActionPerformed

    private void deRedCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deRedCheckMouseClicked
        if (deRedCheck.isSelected()) {
            if (deReddeningWindow == null) {
                deReddeningWindow = new DeReddeningWindow(this, false);
            }
            deReddeningWindow.show();
        } else {
            deReddeningWindow.setVisible(false);
        }
    }//GEN-LAST:event_deRedCheckMouseClicked

    public String getVOTable() {

        String votable = "";
        try {
            // Create temp file.
            File temp = File.createTempFile("tmpVOTable", "");
            VOTableUtils.saveSpectrumSetInVOTable(this.spectrumSet, temp);
            votable = VOTableUtils.getStringFromFile(temp);
            // Delete temp file when program exits.
            temp.deleteOnExit();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please open spectra first ");
            System.out.println("Error creating file " + e);
        }

        return votable;
    }

    public String[] getDimensionalEquation(String unitString) {

        String[] result = null;
        UnitEquation equation;

        UnitEquationFactory factory = new UnitEquationFactory();

        equation = (UnitEquation) factory.resolveEquation(unitString);
        result = new String[2];
        result[0] = equation.getDimeEq();
        result[1] = equation.getScaleEq();

        return result;
    }

    public void errorPlastic() {
        JOptionPane.showMessageDialog(this, " VOSpec plot doesn't contain spectra ");
    }

    public java.util.List getRegisteredApplications(Plastic plastic) {
        java.util.List applications = plastic.getRegisteredNames();
        return applications;
    }

    private void goUnitsConversionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goUnitsConversionButtonActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_goUnitsConversionButtonActionPerformed

    private void openFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileButtonActionPerformed
        localDataDialog.addNewDirectly();
    }//GEN-LAST:event_openFileButtonActionPerformed

    private void fullSizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullSizeButtonMouseClicked
        fullSizePlot();
    }//GEN-LAST:event_fullSizeButtonMouseClicked

    public void fullSizePlot() {

        VOSpecFullSize frame = new VOSpecFullSize();
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        displayPanel.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        sv = new SpectraViewer(plot, displayPanel);
        sv.setWaveToVelSelected(waveToVelocityCheckBox.isSelected());

        fullSizeTitlePanel = new JPanel();
        fullSizeTitlePanel.setPreferredSize(new java.awt.Dimension(760, 70));
        fullSizeTitlePanel.setLayout(new java.awt.BorderLayout());
        fullSizeTitlePanel.setBackground(new java.awt.Color(65, 64, 78));
        fullSizeTitlePanel.add(titleImagePanel, java.awt.BorderLayout.NORTH);
        frame.displayPane.add(fullSizeTitlePanel, java.awt.BorderLayout.NORTH);
        frame.displayPane.add(displayPanel, java.awt.BorderLayout.CENTER);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                titlePanel.add(titleImagePanel, java.awt.BorderLayout.NORTH);
                titlePanel.add(jMenuBar, java.awt.BorderLayout.CENTER);
                titlePanel.add(iconMenuPanel, java.awt.BorderLayout.SOUTH);

                jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
                svPanel.add(displayPanel, java.awt.BorderLayout.CENTER);

                jSplitPane1.setTopComponent(svPanel);

                jSplitPane1.setBottomComponent(barPanel);
                containerPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);
                viewerPanel.add(containerPanel, java.awt.BorderLayout.CENTER);
                getContentPane().add(viewerPanel, java.awt.BorderLayout.CENTER);
            }
        });
    }

    public SpectraViewer getSV() {
        return this.sv;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        PlotSettings plotSettings = new PlotSettings(plot);
        plotSettings.setAioSpecToolDetached(this);
        plotSettings.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void saveImageButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageButton2ActionPerformed
        ChooserSave chooserSave = new ChooserSave();
        chooserSave.setAioSpecToolDetached(this);
        chooserSave.setVisible(true);
    }//GEN-LAST:event_saveImageButton2ActionPerformed

    private void localDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localDataButtonActionPerformed
        localDataDialog.show();
    }//GEN-LAST:event_localDataButtonActionPerformed

    public boolean showInBrowser(String url, Frame frame) {
        //minimizes the app
        //if (frame != null)
        //frame.setExtendedState(JFrame.ICONIFIED);

        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.indexOf("win") >= 0) {
                String[] cmd = new String[4];
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = "start";
                cmd[3] = url;
                rt.exec(cmd);
            } else if (os.indexOf("mac") >= 0) {
                rt.exec("open " + url);
            } else {
                //prioritized 'guess' of users' preference
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx"};

                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++) {
                    cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
                }

                rt.exec(new String[]{"sh", "-c", cmd.toString()});
                //rt.exec("firefox http://www.google.com");
                //System.out.println(cmd.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "\n\n The system failed to invoke your default web browser while attempting to access: \n\n " + url + "\n\n",
                    "Browser Error",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }

    private void slapButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slapButtonMouseClicked
        slap();
    }//GEN-LAST:event_slapButtonMouseClicked

    public void slap() {

        if (!plot.isSlap) {
            slapButton.setForeground(java.awt.Color.red);
            slapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/slapInv.gif")));

            setHandCursor();
            plot.setSlapActive();
        } else {
            slapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/slap.gif")));

            setDefaultCursor();
            plot.setSlapNotActive();
        }

    }

    public void fittingUtilities() {

        FittingWindow fittingWindow = new FittingWindow(plot, spectrumSet, this/*,model*/);
        fittingWindow.setVisible(true);
        //refreshJTree();

    }

    private void redShiftValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redShiftValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_redShiftValueActionPerformed

    private void targetFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_targetFieldActionPerformed

    private void clearCacheButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCacheButtonActionPerformed
        clearCache();
    }//GEN-LAST:event_clearCacheButtonActionPerformed

    public void clearCache() {
        setWaitCursor();
        File dir = new File(Cache.DIRECTORY);

        if (dir.isDirectory()) {
            //Cache.deleteDir(dir);
            Cache.deleteCacheInMemory();
            JOptionPane.showMessageDialog(this, Cache.DIRECTORY + " is empty");
        }
        setDefaultCursor();
    }

    public void initializeHashTable() {

        //initialize Hashtable
        hashSel = new Hashtable();
        for (int k = 0; k < spectrumSet.getSpectrumSet().size(); k++) {
            hashSel.put(k + "", "false");
        }
    }

    public void savingAsImage() {

        try {

            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(VOSpecDetached.this);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File localFile = fileChooser.getSelectedFile();
            BufferedImage bufferedImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
            setWaitCursor();
            bufferedImage = plot.exportImage();
            ImageIO.write(bufferedImage, "png", localFile);
            setDefaultCursor();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems saving Spectra as Image. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            e.printStackTrace();
        }

    }

    public void savingAsVOTable() {

        try {

            Vector dataValues = plot.getPointsLinear();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Saving all spectra");
            int returnVal = fileChooser.showSaveDialog(VOSpecDetached.this);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String file = fileChooser.getSelectedFile().getPath();
            String wave = (String) waveChoice.getSelectedItem();
            String flux = (String) fluxChoice.getSelectedItem();
            setWaitCursor();

            Spectrum spectrum = new Spectrum();
            spectrum.setUnitsW(wave);
            spectrum.setUnitsF(flux);

            double[] waveValues = new double[dataValues.size()];
            double[] fluxValues = new double[dataValues.size()];

            for (int i = 0; i < dataValues.size(); i++) {
                waveValues[i] = ((double[]) dataValues.get(i))[0];
            }
            for (int i = 0; i < dataValues.size(); i++) {
                fluxValues[i] = ((double[]) dataValues.get(i))[1];
            }

            spectrum.setWaveValues(waveValues);
            spectrum.setFluxValues(fluxValues);

            SedSpectrum sedSpectrum = new SedSpectrum(spectrum);
            sedSpectrum.serializeToVOTable(file);
            setDefaultCursor();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems saving spectra as VOTable. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");

        }
    }

    private void viewAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAllButtonActionPerformed
        setWaitCursor();
        this.launchThreads.stop = false;
        displaySelectedSpectra();
        setDefaultCursor();
    }//GEN-LAST:event_viewAllButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
    }//GEN-LAST:event_jButton7ActionPerformed

    private void fixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixButtonActionPerformed
        plot.fillPlot();
    }//GEN-LAST:event_fixButtonActionPerformed

    private void jenskyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jenskyActionPerformed
    }//GEN-LAST:event_jenskyActionPerformed

    private void fixAxesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixAxesButtonActionPerformed
        plot.fillPlot();
    }//GEN-LAST:event_fixAxesButtonActionPerformed

    private void resetButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButton2ActionPerformed

        try {
            setWaitCursor();
            resetButton2ActionPerformed();
            setDefaultCursor();

            queryServers = 0;
            queryServersReady = 0;
            finishedQuery = true;
            jProgressBar1.setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Reset Problems. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
        }

    }//GEN-LAST:event_resetButton2ActionPerformed

    public void resetButton2ActionPerformed() {


            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this,
                    "This will reset the tool to start-up status. Proceed?",
                    "Warning",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 1) {
                return;
            }

        ssaServerList = new SsaServerList();
        showLocalData = false;
        localDataDialog.setSelectedForAll(false);
        spectrumSet = new SpectrumSet();
        localSpectrums = new Hashtable();
        sedSpectrums = new Hashtable();

        this.localDataDialog.deleteAll();
        showResults();
        displayButtonActionPerformed();
        resetResourcesPanel();

        //displayPanel.remove(sv);
        validate();
        repaint();
        show();
    }

    private void displayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayButtonActionPerformed


        if (downloadedSpectra < totalSpectra) {
            JOptionPane.showMessageDialog(this, " Please cancel the retrieval before starting a new one");
        } else {
            displayButtonActionPerformed();
        }

    }//GEN-LAST:event_displayButtonActionPerformed
    private boolean isApplet = false;

    public void setIsApplet(boolean isApplet) {
        this.isApplet = isApplet;
    }
    int downloadedSpectra = 0;
    int totalSpectra = 0;

    public void displayButtonActionPerformed() {



        int spectraCount = 0;

        // count number of spectra to be downloaded
        for (int ct = 0; ct < spectrumSet.getSpectrumSet().size(); ct++) {

            Spectrum spectrum = spectrumSet.getSpectrum(ct);

            boolean isSelected = spectrum.getNode().getIsSelected();
            if (isSelected) {
                spectraCount++;
            }
        }


        final int downloads = spectraCount;
         (new Thread() {
            @Override
            public void run() {
                System.out.println("Inserting retrievals "+downloads);
                UsageLogging.insertDataRetrievalLog(downloads);
            }
        }).start();



        if (spectraCount > 30 && isApplet) {
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this,
                    "About to retrieve " + spectraCount + " spectra. Usage of " +
                    "WebStart or standalone versions is encouraged for bigger " +
                    "memory requirements. Continue?",
                    "Warning",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 1) {
                return;
            }
        }


        //utils.removeAllRunningSpectra();

        plot.clearPoints();
        setWaitCursor();

        downloadedSpectra = 0;

        totalSpectra = spectraCount;

        if (totalSpectra > 0) {

            jProgressBar2.setVisible(true);
            jProgressBar2.setStringPainted(true);
            jProgressBar2.setValue(0);
            jProgressBar2.setString("Starting retrieval...");
            cancelButton.setVisible(true);

        }


        (new Thread() {

            @Override
            public void run() {

                try {
                    launchThreads();
                } catch (Exception e) {
                    System.out.println("Problems in displayButtonActionPerformed");
                    e.printStackTrace();
                }
                removeComponentFromColorContainer();
/*
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displaySpectraColorList();
                    }
                });
  */

                setDefaultCursor();

                //refreshJTree();

            }
        }).start();

    }

    private void repaintVospec() {
        this.validate();
        this.repaint();
    }

    synchronized public void spectrumDownloaded() {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                downloadedSpectra++;

                jProgressBar2.setString(downloadedSpectra + " of " + totalSpectra);

                int progress = (int) Math.floor(new Float(downloadedSpectra) / new Float(totalSpectra) * 100);
                jProgressBar2.setValue(progress);

                if (downloadedSpectra >= totalSpectra) {
                    jProgressBar2.setValue(100);
                    jProgressBar2.setString(totalSpectra + " retrieved");
                    //UsageLogging.insertDataRetrievalLog(totalSpectra);
                    //jProgressBar2.setVisible(false);
                    cancelButton.setVisible(false);
                    setDefaultCursor();
                    repaintVospec();
                }
            }
        });

    }

    public String getWaveChoice() {
        String wave = (String) waveChoice.getSelectedItem();
        return wave;
    }

    //To be used in Analysis & Filtering windows for output messages
    public String getWaveChoiceToPrint() {
        String wave = "";

        if (this.isInVelocitySpace()) {
            wave = "Km/s";
        } else {
            wave = (String) waveChoice.getSelectedItem();
        }

        return wave;
    }

    public String getFluxChoice() {
        String flux = (String) fluxChoice.getSelectedItem();
        return flux;
    }

    public void searchSsaServer() {

        targetValue = targetField.getText();
        sizeValue = sizeField.getText();

        if (!targetValue.equals("")) {
            try {
                String as[] = Sesame.getNameResolved(targetValue);
                if (as != null) {

                    raField.setText(as[0]);
                    decField.setText(as[1]);

                    raValue = as[0];
                    decValue = as[1];
                } else {
                    JOptionPane.showMessageDialog(this, "No match");
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No connection to Name Resolver. Please enter coordinates manually");
                e.printStackTrace();
            }
        }

        // Try to convert to format to be sure the inserted parameters are correct
        try {

            if (!raField.getText().equals("") || !decField.getText().equals("")) {
                raValue = "" + utils.getCoordinateValue(raField.getText());
                decValue = "" + utils.getCoordinateValue(decField.getText());

                if (!decValue.equals("")) {
                    Float decFloat = new Float(decValue);
                }
                if (!raValue.equals("")) {
                    Float raFloat = new Float(raValue);
                }
                if (!sizeValue.equals("")) {
                    Float sizeFloat = new Float(sizeValue);
                }
            } else {
                raValue = "";
                decValue = "";

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input parameters are not valid");
            return;
        }

        ssaRequest = new SsaRequest();
        ssaRequest.setDec(decValue);
        ssaRequest.setRa(raValue);
        if (!decValue.equals("") && !raValue.equals("")) {
            ssaRequest.setPos(raValue + "," + decValue);
        } else {
            ssaRequest.setPos("");
        }
        ssaRequest.setTarget(targetValue);
        ssaRequest.setSize(sizeValue);

    }

    private void arithmeticButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arithmeticButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_arithmeticButton2ActionPerformed

    private void aboutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutButtonMouseClicked
        if (about == null) {
            about = new About(this);
        }
        about.setVisible(true);
}//GEN-LAST:event_aboutButtonMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showInBrowser("http://esavo.esa.int/VOSpecManual", this);
}//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        try {
            showURL("howTo", "http://esavo.esa.int/VOSpecManual");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening VOSpecManual. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");

        }
}//GEN-LAST:event_jButton4MouseClicked

    private void mailButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mailButtonMouseClicked

        try {
            showInBrowser("http://esavo.esa.int/support", this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening ESA VO HelpDesk. Please contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
        }

    }//GEN-LAST:event_mailButtonMouseClicked

    private void viewButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewButtonMouseClicked
        view();
}//GEN-LAST:event_viewButtonMouseClicked

    private void waveChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveChoiceActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_waveChoiceActionPerformed

    private void logWaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logWaveActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_logWaveActionPerformed

    private void fluxChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fluxChoiceActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_fluxChoiceActionPerformed

    private void logFluxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logFluxActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_logFluxActionPerformed

    private void redShiftCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redShiftCheckActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_redShiftCheckActionPerformed

    private void deRedCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deRedCheckActionPerformed
        //repaintSpectra();
    }//GEN-LAST:event_deRedCheckActionPerformed

    private void waveToVelocityCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveToVelocityCheckBoxActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_waveToVelocityCheckBoxActionPerformed

    private void resetButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButton3ActionPerformed
        resourcespanelmanager.uncheckAll();
    }//GEN-LAST:event_resetButton3ActionPerformed

    private void jProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar1MouseClicked

        QueryInfo queryInfo = new QueryInfo(successVector, logVector);
        queryInfo.setVisible(true);
    }//GEN-LAST:event_jProgressBar1MouseClicked

    private void plasticButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plasticButtonActionPerformed

        putil.registerToPlastic();

    }//GEN-LAST:event_plasticButtonActionPerformed

    private void yErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yErrorCheckActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_yErrorCheckActionPerformed

    private void xErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xErrorCheckActionPerformed
        repaintSpectra();
    }//GEN-LAST:event_xErrorCheckActionPerformed

    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm

        try {
            unregisterInterop();
            localDataDialog.dispose();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_exitForm
    SyntheticPhotoDialog pfsParser = null;

    private void syntheticPhotometryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syntheticPhotometryButtonActionPerformed

        performSyntheticPhotometry();

    }//GEN-LAST:event_syntheticPhotometryButtonActionPerformed

    private void performSyntheticPhotometry() {

        Vector dataValues = plot.getPointsLinear();

        String wave = (String) waveChoice.getSelectedItem();
        String flux = (String) fluxChoice.getSelectedItem();


        Spectrum spectrum = new Spectrum();
        spectrum.setUnitsW(wave);
        spectrum.setUnitsF(flux);
        String[] unitsW = Utils.getDimensionalEquation(wave);
        String[] unitsF = Utils.getDimensionalEquation(flux);
        spectrum.setUnits(new Unit(unitsW[1], unitsW[0], unitsF[1], unitsF[0]));

        double[] waveValues = new double[dataValues.size()];
        double[] fluxValues = new double[dataValues.size()];

        double[] waveUpperError = new double[dataValues.size()];
        double[] waveLowerError = new double[dataValues.size()];
        double[] fluxUpperError = new double[dataValues.size()];
        double[] fluxLowerError = new double[dataValues.size()];

        for (int i = 0; i < dataValues.size(); i++) {
            waveValues[i] = ((double[]) dataValues.get(i))[0];
            fluxValues[i] = ((double[]) dataValues.get(i))[1];
            //System.out.println("flux "+fluxValues[i]);
            waveUpperError[i] = ((double[]) dataValues.get(i))[2];
            waveLowerError[i] = ((double[]) dataValues.get(i))[3];
            fluxUpperError[i] = ((double[]) dataValues.get(i))[4];
            //System.out.println("fluxUpper "+fluxUpperError[i]);
            fluxLowerError[i] = ((double[]) dataValues.get(i))[5];
            //System.out.println("fluxLower "+fluxLowerError[i]);
        }

        spectrum.setWaveValues(waveValues);
        spectrum.setFluxValues(fluxValues);
        spectrum.setWaveErrorUpper(waveUpperError);
        spectrum.setWaveErrorLower(waveLowerError);
        spectrum.setFluxErrorUpper(fluxUpperError);
        spectrum.setFluxErrorLower(fluxLowerError);

        try {
            pfsParser = new SyntheticPhotoDialog(false, spectrum, this);
            pfsParser.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void syntheticPhotometryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syntheticPhotometryActionPerformed
        this.performSyntheticPhotometry();
    }//GEN-LAST:event_syntheticPhotometryActionPerformed

    private void changeViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeViewActionPerformed
        view();
}//GEN-LAST:event_changeViewActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        launchThreads.finish();
        jProgressBar2.setIndeterminate(false);
        jProgressBar2.setVisible(false);
        cancelButton.setVisible(false);
        this.downloadedSpectra = 0;
        this.totalSpectra = 0;
}//GEN-LAST:event_cancelButtonActionPerformed

    private void jProgressBar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jProgressBar2MouseClicked

    public void repaintSpectra() {
        setWaitCursor();
        displaySelectedSpectra();
        setDefaultCursor();
    }

    //Switch between JTable and JTree
    public void view() {

        if (table) {

            viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/table.gif")));
            resourcespanelmanager.viewTree();
            table = false;

        } else {
            viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/tree.gif")));
            resourcespanelmanager.viewTable();
            table = true;
        }

    }

    public void viewTable() {
        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/tree.gif")));
        resourcespanelmanager.viewTable();
        table = true;
        this.repaint();
    }

    public static void main(String args[]) {

        VOSpecDetached tool = new VOSpecDetached();
        tool.setVisible(true);
        tool.launchServersQuery();

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem EquivalentItem;
    private javax.swing.JMenuItem NormalizeItem;
    private javax.swing.JToolBar TableJToolBar;
    private javax.swing.JButton aboutButton;
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenuItem arithmeticBarItem;
    private javax.swing.JButton arithmeticButton2;
    private javax.swing.JMenuItem arithmeticItem2;
    private javax.swing.JLabel bannerLabel;
    private javax.swing.JPanel barPanel;
    private javax.swing.JButton bisectorButton;
    private javax.swing.JMenuItem bisectorItem;
    private javax.swing.JButton cancelButton;
    private javax.swing.JMenuItem changeView;
    private javax.swing.JButton clearCacheButton;
    private javax.swing.JMenuItem clearCacheItem;
    private javax.swing.JMenuItem contactItem;
    public javax.swing.JPanel containerPanel;
    private javax.swing.JLabel coordJLabel;
    private javax.swing.JCheckBox deRedCheck;
    private javax.swing.JLabel deRedLabel;
    private javax.swing.JLabel dec;
    public javax.swing.JTextField decField;
    private javax.swing.JPanel deredPanel;
    private javax.swing.JButton displayButton;
    private javax.swing.JPanel displayColorContainerPanel;
    private javax.swing.JPanel displayColorPanel;
    private javax.swing.JScrollPane displayColorScrollPanel;
    public javax.swing.JPanel displayPanel;
    private javax.swing.JLabel displaySpectraLabel;
    private javax.swing.JMenuItem editDataItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JButton equivalentButton;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenuItem externalHubItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton filteringButton;
    private javax.swing.JMenuItem filteringItem;
    private javax.swing.JMenuItem fittingItem;
    private javax.swing.JButton fixButton;
    public javax.swing.JComboBox fluxChoice;
    private javax.swing.JLabel fluxLabel;
    private javax.swing.JPanel fluxLabelPanel;
    private javax.swing.JPanel fluxPanel;
    private javax.swing.JButton fullSizeButton;
    private javax.swing.JToolBar generalToolBar;
    private javax.swing.JMenuItem getPointItem;
    private javax.swing.JMenuItem helpBarItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToolBar helpToolBar;
    private javax.swing.JMenuItem howToItem;
    private javax.swing.JPanel iconMenuPanel;
    private javax.swing.JButton integratedButton;
    private javax.swing.JMenuItem integratedItem;
    public javax.swing.JMenu interOpMenu;
    private javax.swing.JMenuItem internalHubItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    public javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    public javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem javaHelpItem;
    private javax.swing.JButton localDataButton;
    private javax.swing.JCheckBox logFlux;
    private javax.swing.JLabel logLabel;
    private javax.swing.JCheckBox logWave;
    private javax.swing.JMenuItem luminosityItem;
    private javax.swing.JButton mailButton;
    public javax.swing.JToolBar mathBar;
    private javax.swing.JButton mirroringButton;
    private javax.swing.JMenuItem mirroringItem;
    private javax.swing.JButton normButton1;
    private javax.swing.JMenu opMenu;
    private javax.swing.JButton openFileButton;
    private javax.swing.JMenuItem openItem;
    private javax.swing.JMenuItem openVOEspace;
    private javax.swing.JPanel optPanel;
    private javax.swing.JPanel optionPanel;
    public javax.swing.JButton plasticButton;
    private javax.swing.JMenuItem plotSizeItem;
    private javax.swing.JButton polynomialButton;
    private javax.swing.JLabel ra;
    public javax.swing.JTextField raField;
    private javax.swing.JCheckBox redShiftCheck;
    private javax.swing.JLabel redShiftLabel;
    private javax.swing.JPanel redShiftPanel;
    private javax.swing.JTextField redShiftValue;
    public javax.swing.JMenuItem registerItem;
    private javax.swing.JButton resetButton2;
    private javax.swing.JButton resetButton3;
    private javax.swing.JButton saveImageButton2;
    private javax.swing.JMenuItem saveItem;
    private javax.swing.JMenuItem saveVOEspace;
    private javax.swing.JButton scissorsButton;
    public javax.swing.JMenu sendSpectrum;
    private javax.swing.JMenuItem setItem;
    public javax.swing.JTextField sizeField;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JButton slapButton;
    private javax.swing.JMenuItem slapItem;
    private javax.swing.JButton statisticButton;
    private javax.swing.JMenuItem statisticsItem;
    public javax.swing.JPanel svPanel;
    private javax.swing.JMenuItem syntheticPhotometry;
    private javax.swing.JButton syntheticPhotometryButton;
    private javax.swing.JLabel target;
    public javax.swing.JTextField targetField;
    private javax.swing.JPanel titleImagePanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JButton tuningButton;
    private javax.swing.JMenuItem tuningItem;
    private javax.swing.JPanel umPanel;
    public javax.swing.JMenuItem unregisterItem;
    private javax.swing.JMenuItem unzoomItem;
    private javax.swing.JButton viewAllButton;
    private javax.swing.JPanel viewAllPanel;
    private javax.swing.JButton viewButton;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPanel viewerPanel;
    public javax.swing.JComboBox waveChoice;
    private javax.swing.JLabel waveLabel;
    private javax.swing.JPanel waveLabelPanel;
    public javax.swing.JCheckBox waveToVelocityCheckBox;
    private javax.swing.JLabel waveToVelocityLabel;
    public javax.swing.JTextField waveToVelocityValue;
    private javax.swing.JPanel waveVelocityPanel;
    private javax.swing.JButton waveletButton;
    private javax.swing.JMenuItem waveletButtonItem;
    private javax.swing.JCheckBox xErrorCheck;
    private javax.swing.JLabel xErrorLabel;
    private javax.swing.JCheckBox yErrorCheck;
    private javax.swing.JLabel yErrorLabel;
    // End of variables declaration//GEN-END:variables
    // Strore the URL here for the run method
    public javax.swing.JPanel fullSizeTitlePanel;
    public JLabel spectrumNameLabel;
    public SsaServerList ssaServerList;
    public SpectrumSet spectrumSet;
    //public SpectrumSet  remoteSpectrumSet;
    // Variables used by AioSpecSelector
    public VOSpecAdvancedSelector advancedSelector = null;
    public Hashtable localSpectrums;
    public Hashtable sedSpectrums;
    public AppletContext parentAppletContext;
    public VOSpecInfo specInfo;
    public String targetValue;
    public String raValue;
    public String decValue;
    public String sizeValue;
    public ExtendedTableModel tableModel;
    public String serverTotalName;
    public DefaultTableCellRenderer cell;
    //public ExtendedJTable jTable;
    public ExtendedPlot plot;
    public SpectraViewer spectraViewer;
    public String completeUrl;
    //  public String marks;
    public boolean connected;
    public int columnStatus;
    public int selectColumn;
    public File cache;
    public Properties props;
    public SpectraViewer sv;
    public ExtendedJTextField colorButton;
    public int cl;
    public JComboBox graphButton;
    public JCheckBox colorBox;
    public Graphic graphic;
    public GraphicSet graphicSet;
    public String m;
    public TableColumn column;
    public Vector infoWindow;
    public Vector rowsClicked;
    public About about;
    public String waveDrawn;
    public String fluxDrawn;
    public boolean logw;
    public boolean logf;
    public boolean isNorm;
    public Hashtable hashSel;
    public Vector logVector = new Vector();
    public Vector successVector = new Vector();
    public DefaultTreeModel model;
    public SsaRequest ssaRequest = null;
    public ResourcesPanelManager resourcespanelmanager;
    public LocalDataDialog localDataDialog = new LocalDataDialog(this, true);
    public DeReddeningWindow deReddeningWindow;
    public boolean showLocalData = false;
    public ArithmeticWindow arithmeticWindow;
    public ArithmeticWindowMultipleSpectra arithmeticWindowMultipleSpectra;
    public AnalysisWindow analysisWindow = null;
    boolean helpVisible;
    boolean mathVisible;
    public FilteringWindow fw = null;
    public HashSet serverMap = null;
    public int lastServerInServerListTree = 0;
    public java.util.List runningThreads = null;
    public esavo.vospec.plastic.PlasticUtils putil = new esavo.vospec.plastic.PlasticUtils(this);
    public PlasticHub hub;
    public boolean table = false;
    //public String serviceEndPoint = "http://esavo02.esac.esa.int:8080/voespace-1.0/services/VOSpaceService";
    //public String serviceEndPoint = "http://satss01.esac.esa.int:"+EnvironmentDefs.SERVERPORT+"/voespace-1.0/services/VOSpaceService";
    //public Client voespaceClient = null;
    public Vector voespaceList = null;
    public String voespaceFileName = null;
    //public ClientProfile profile = null;
    //public HubConnector conn = null;
    //public Map.Entry pairs;
    public int mathematicMethodExecution = 0;
    public int tsapFittingExecution = 0;
    public SsaServerList serverList = null;
    LogChanger logChanger;
    public Utils utils;
    //----------------------------------------------------------
    public Color[] colors = {
        new Color(0xff0000), // red
        new Color(0x0000ff), // blue
        new Color(0x00aaaa), // cyan-ish
        new Color(0x000000), // black
        new Color(0xffa500), // orange
        new Color(0x53868b), // cadetblue4
        new Color(0xff7f50), // coral
        new Color(0x45ab1f), // dark green-ish
        new Color(0x90422d), // sienna-ish
        new Color(0xa0a0a0), // grey-ish
        new Color(0x14ff14), // green-ish
    };

    //  displayConvertedSpectra(int i) is associated with the launchThread method
    public void displayConvertedSpectra(final Spectrum spectrum) {



        (new Thread() {

            @Override
            public void run() {

                try {
                    String marks = "Points";
                    if (!spectrum.getRealData()) {
                        marks = "Lines";
                    } else {
                        if ((spectrum.getFormat()).equals("spectrum/photometry")) {
                            marks = "Dots";
                        }
                    }

                    drawSpectrum(marks, spectrum);
                    putil.checkApplicationRegistered();

                } catch (Exception e) {
                    spectrum.getNode().setFailed(true);
                    e.printStackTrace();
                }

                if (spectrum.getUrl().startsWith("file")) {
                    repaintVospec();
                }


            }
        }).start();

        

    }

    /**
     * Called Spectrum itself
     * @param marks
     * @param spectrum
     * @throws Exception
     */
    public void drawSpectrum(String marks, Spectrum spectrum) throws Exception {

        try {
            String s = (String) waveChoice.getSelectedItem();
            String s1 = (String) fluxChoice.getSelectedItem();

            boolean realData = spectrum.getRealData();
            boolean isTsap = spectrum.getToBeNormalized();

            int i = 0;
            while (spectrumSet.getSpectrum(i) != null) {
                if (spectrumSet.getSpectrum(i).equals(spectrum)) {
                    break;
                }
                i++;
            }
            sv.setMarksStyle(marks, realData, i, isTsap);
            SpectrumConverter spectrumConverter;
            spectrumConverter = new SpectrumConverter(s, s1, sv);
            spectrumConverter.setAioSpecToolDetached(this);
            spectrumConverter.setRedShift(getRedShift());
            spectrumConverter.setRunProperties(spectrum, i, false);

            if (waveToVelocityCheckBox.isSelected()) {
                spectrumConverter.setWaveToVel(getRefWaveValue());
            }
            //SwingUtilities.invokeLater(spectrumConverter);
            spectrumConverters.operation(spectrumConverter, SpectrumConvertersRunner.ADD);
            //spectrumConverter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNewSpectraViewer() {
        logw = logWave.isSelected();
        logf = logFlux.isSelected();

        sv = new SpectraViewer(plot, logw, logf, displayPanel);
        sv.setWaveToVelSelected(waveToVelocityCheckBox.isSelected());
        
        displayPanel.removeAll();
        displayPanel.add(sv, java.awt.BorderLayout.CENTER);
    }
    //this method create a Hashtable with the spectra selected in ColorPanel
    ActionListener listenerForCheckBox = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            int i = (new Integer(evt.getActionCommand())).intValue();

            if (hashSel.get(i + "") == "true") {
                hashSel.put(i + "", "false");
            } else {
                hashSel.put(i + "", "true");
            }
        }
    };
    ActionListener listener = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            int i = (new Integer(evt.getActionCommand())).intValue();
            JComboBox cb = (JComboBox) evt.getSource();
            Object newItem = cb.getSelectedItem();
            String label = newItem.toString();
            graphic = graphicSet.getGraphic(i);
            graphic.setComboBoxLabel(label);
            displayOneAction(spectrumSet.getSpectrum(i));
        }
    };
    ActionListener l = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            int i = (new Integer(evt.getActionCommand())).intValue();
            displayOneAction(spectrumSet.getSpectrum(i));
        }
    };

    public void displayOneAction(Spectrum spectrum) {
        //utils.removeAllRunningSpectra();
        showHighLighted(spectrum);
        //refreshJTree();
        displayOneSpectrum(spectrum);
    }

    public void showHighLighted(Spectrum spectrum) {
        resourcespanelmanager.higlightNode(spectrum.getNode());
    }

    public void highlightColor(Spectrum spectrum) {

        if (spectrum.getNode().isDownloaded) {
            unHighlightColors();
            if (spectrum.getColorNode() != null) {
                spectrum.getColorNode().setSize(new java.awt.Dimension(18, 18));
            }
        } else {
            unHighlightColors();
        }
    }

    public void unHighlightColors() {
        for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {
            boolean sel = spectrumSet.getSpectrum(i).getSelected();
            if (sel) {
                spectrumSet.getSpectrum(i).getColorNode().setSize(new java.awt.Dimension(14, 14));
            }
        }
    }

    public int getColorSpectrum(int i) {
        return i;
    }

    /*
     *
     * Method called from VOSpecAdvancedSelector when an SSA query is performed
     *
     * */
    public void showResults() {

        SpectrumSet sedSpectrumSet = new SpectrumSet();
        Vector sedspec = new Vector(sedSpectrums.values());
        for (int i = 0; i < sedspec.size(); i++) {
            sedSpectrumSet.addSpectrum(sedSpectrumSet.spectrumSet.size(), (Spectrum) spectrumSet.getSpectrum((Integer) sedspec.get(i)));
        }
        sedSpectrums.clear();
        localSpectrums.clear();

        spectrumSet = new SpectrumSet();

        for (int i = 0; i < sedSpectrumSet.spectrumSet.size(); i++) {
            sedSpectrums.put(spectrumSet.spectrumSet.size(), spectrumSet.spectrumSet.size());
            spectrumSet.addSpectrum(spectrumSet.spectrumSet.size(), sedSpectrumSet.getSpectrum(i));
        }

        resourcespanelmanager.deleteSSATables();
        //setWaitCursor();

        if (showLocalData) {
            addLocalData();
        }
        startSsaThreads();

    }
    int queryServers = 0;
    int queryServersReady = 0;
    public boolean finishedQuery = true;

    public void startSsaThreads() {
        try {

            finishedQuery = false;
            logVector = new Vector();
            logChanger.restart();
            successVector = new Vector();
            jProgressBar1.setVisible(true);
            queryServersReady = 0;
            jProgressBar1.setValue(0);
            jProgressBar1.setString("Starting query...");
            jProgressBar1.setStringPainted(true);
            //jProgressBar1.validate();
            //jProgressBar1.repaint();
            queryServers = ssaServerList.getSsaServerList().size();
            runningThreads = new ArrayList();
            for (int i = 0; i < ssaServerList.getSsaServerList().size(); i++) {
                SsaServer ssaServer = ssaServerList.getSsaServer(i);
                SSAThread thread = new SSAThread(this, ssaServer);
                runningThreads.add(thread);
                thread.start();
            }

        } catch (Exception e) {
            System.out.println("Problems in startThreads");
            e.printStackTrace();
        }

    }

    public void ssaThreadReady(Thread thread) {

        queryServersReady++;
        int progress = (int) Math.floor(new Float(queryServersReady) / new Float(queryServers) * 100);
        jProgressBar1.setValue(progress);
        jProgressBar1.setString(queryServersReady + " of " + queryServers);
        //jProgressBar1.validate();
        //jProgressBar1.repaint();

        if (queryServersReady >= queryServers) {
            finishedQuery = true;
            jProgressBar1.setString(queryServers + " queried");
            UsageLogging.insertQueryLog(queryServers);
        }

        Iterator iter = runningThreads.iterator();
        boolean done = false;
        while (!done && iter.hasNext()) {
            Thread runningThread = (Thread) iter.next();
            if (runningThread == thread) {
                iter.remove();
                done = true;
            }
        }

        if (runningThreads.size() == 0) {
            System.out.println("0 threads running");
            //setDefaultCursor();
        }

    }

    public synchronized void errorsFromSSASearch(String errorMessage) {
        errorsFromSSASearch_NS(errorMessage);
    }

    public void errorsFromSSASearch_NS(String errorMessage) {
        logVector.add(errorMessage);
    }

    public synchronized void successFromSSASearch(String successMessage) {
        successVector.add(successMessage);
    }

    public int containsSpectrum(SpectrumSet sp, Spectrum s) {

        for (int j = 0; j < sp.getSpectrumSet().size(); j++) {
            Spectrum spectrum = sp.getSpectrum(j);
            if (spectrum.getUrl().equals(s.getUrl())) {
                return j;
            }
        }
        return -1;
    }

// JTree access methods. All calls should be done through a SwingUtilities invokeLater to prevent problems
// with the main swing thread
// This thread will call indirectly the "method"_NT that do the work and this _NT method should be synchronized
// to prevent multiples instances
    public void addLocalData() {

        mathVisible = true;
        Runnable swingThreadSafe = new SwingThreadSafe("addLocalData_NT", this);
        SwingUtilities.invokeLater(swingThreadSafe);
    }

    public synchronized void addLocalData_NT() {

        boolean newSpectrumDisplayed = false;
        HashSet toBeDownloaded = new HashSet();

        if (spectrumSet == null) {
            spectrumSet = new SpectrumSet();
        }

        showLocalData = true;
        setWaitCursor();

        //send a spectrumset with the new local data to the tablespane,
        //substituting with the old ones the repeated ones to mantain the
        //checked status
        SpectrumSet newlocalspectrums = localDataDialog.getSpectrumSet();

        if (spectrumSet != null) {
            for (int i = 0; i < newlocalspectrums.spectrumSet.size(); i++) {
                //System.out.println("adding local data " + spectrumSet.getSpectrumSet().size());
                //boolean already_exists=false;
                for (int j = 0; j < spectrumSet.getSpectrumSet().size(); j++) {
                    if (newlocalspectrums.getSpectrum(i).equals(spectrumSet.getSpectrum(j))) {
                        //substitute with the old one
                        newlocalspectrums.addSpectrum(i, spectrumSet.getSpectrum(j));
                    }
                }
            }
        }
        SpectrumSet filteredLocalSpectrums = resourcespanelmanager.addLocal(newlocalspectrums);
        //remove the local spectrums that are not in the new local spectrum list
        if (localSpectrums == null) {
            localSpectrums = new Hashtable();
        }
        SpectrumSet cleanedSpectrumSet = new SpectrumSet();
        int index = 0;

        for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {
            //check if this spectrum is in the new local spectrums
            //Vector localvector=new Vector(filteredLocalSpectrums);
            boolean isInLocal = false;
            for (int j = 0; j < filteredLocalSpectrums.getSpectrumSet().size(); j++) {
                if (filteredLocalSpectrums.getSpectrum(j).getTitle().equals(spectrumSet.getSpectrum(i).getTitle())) {
                    isInLocal = true;
                }
            }
            //if this spectrum is local and is not in the new local spectrums
            //remove it
            if (localSpectrums.get(i) != null) {
                if (!isInLocal) {
                    localSpectrums.remove(i);
                } else {
                    cleanedSpectrumSet.addSpectrum(index, spectrumSet.getSpectrum(i));
                    localSpectrums.remove(i);
                    localSpectrums.put(index, index);
                    index++;
                }

            } else {
                cleanedSpectrumSet.addSpectrum(index, spectrumSet.getSpectrum(i));
                index++;
            }
        }
        spectrumSet = cleanedSpectrumSet;

        //add or replace the new local spectrums in the global spectrum set
        for (int i = 0; i < filteredLocalSpectrums.getSpectrumSet().size(); i++) {

            Spectrum newspectrum = filteredLocalSpectrums.getSpectrum(i);
            int spectrumIndex = -1;
            if (spectrumSet != null) {
                spectrumIndex = containsSpectrum(spectrumSet, newspectrum);
            }

            if (spectrumIndex == -1) {
                if (newspectrum.getSelected() && newspectrum.getToWait()) {
                    newSpectrumDisplayed = true;
                    toBeDownloaded.add(newspectrum);
                    newspectrum.getNode().setWaiting(true);
                }
                localSpectrums.put(spectrumSet.getSpectrumSet().size(), spectrumSet.getSpectrumSet().size());
                spectrumSet.addSpectrum(spectrumSet.getSpectrumSet().size(), newspectrum);
            }

            if (spectrumIndex != -1) {
                if (newspectrum.getSelected() && newspectrum.getToWait()) {
                    newSpectrumDisplayed = true;
                    toBeDownloaded.add(newspectrum);
                    newspectrum.getNode().setWaiting(true);
                }
                localSpectrums.put(spectrumIndex, spectrumIndex);
                spectrumSet.addSpectrum(spectrumIndex, newspectrum);
            }
        }
        createNewSpectraViewer();
        //utils.removeAllRunningSpectra();
        plot.clearPoints();
        setWaitCursor();
        launchThreads();


        if (newSpectrumDisplayed) {
            
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    removeComponentFromColorContainer();
                    displaySpectraColorList();
                }
            });
        }

        /*
        if (toBeDownloaded.size() > 0) {

            launchThreads = new LaunchThreads(this, toBeDownloaded);
            try {
                launchThreads.start();
                System.out.println("Launching 2");
            } catch (Exception e) {
                System.out.println("Error Launching Threads");
                e.printStackTrace();
            }
        }*/

        this.validate();
        this.repaint();
        //refreshJTree();
        setDefaultCursor();

    }

    public void removeComponentFromColorContainer() {
        displayColorContainerPanel.removeAll();
        displayColorContainerPanel.validate();
        displayColorContainerPanel.repaint();
    }

    public void displaySpectraColorList() {
        int ct = 0;
        graphicSet = new GraphicSet();

        //initialize hashtable with all the values false for the checkBox in ColorPanel
        initializeHashTable();

        for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {
            boolean sel = spectrumSet.getSpectrum(i).getSelected();

            if (this.launchThreads.finishing()) {
                return;
            }

            if (sel) {
                boolean realData = spectrumSet.getSpectrum(i).getRealData();
                boolean isTsap = spectrumSet.getSpectrum(i).getToBeNormalized();

                graphic = new Graphic();
                cl = getColorSpectrum(i);
                graphic.setColorButton(cl);
                graphic.setRow(i);
                String name = spectrumSet.getSpectrum(i).getTitle();
                colorButton = new ExtendedJTextField();
                colorButton.setBackground(plot.getDataSetColor(i, realData, isTsap));
                colorButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                colorButton.setPreferredSize(new java.awt.Dimension(14, 14));
                colorButton.setText("" + i);
                colorButton.setSpectrum(spectrumSet.getSpectrum(i));
                spectrumSet.getSpectrum(i).setColorNode(colorButton);
                colorButton.setForeground(plot.getDataSetColor(i, realData, isTsap));
                colorButton.setEditable(false);
                colorButton.addMouseListener(new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        displayOneAction(((ExtendedJTextField) evt.getComponent()).getSpectrum());
                    }
                });

                colorButton.setToolTipText(name);
                displayColorContainerPanel.add(colorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5 + 5 * 5 * ct, -1, -1));
                displayColorScrollPanel.validate();
                colorBox = new JCheckBox();
                colorBox.setBackground(new Color(249, 244, 244));
                colorBox.setPreferredSize(new java.awt.Dimension(22, 15));
                colorBox.setActionCommand("" + i);
                colorBox.addActionListener(listenerForCheckBox);
                colorBox.setToolTipText(name);
                colorBox.setSelected(true);
                hashSel.put(i + "", "true");
                //displayColorContainerPanel has a private access
                displayColorContainerPanel.add(colorBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 5 + 5 * 5 * ct, -1, -1));
                displayColorScrollPanel.validate();

                graphButton = new JComboBox();
                graphButton.setFont(new java.awt.Font("Dialog", 1, 10));


                if (!realData) {
                    graphButton.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Lines", "Points", "Dots", "Big Dots", "Various", "Pixels"}));
                } else if ((spectrumSet.getSpectrum(i).getFormat()).equals("spectrum/photometry")) {
                    graphButton.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Dots", "Big Dots", "Points", "Lines", "Various", "Pixels"}));
                } else {
                    graphButton.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Points", "Lines", "Dots", "Big Dots", "Various", "Pixels"}));
                }

                graphButton.setPreferredSize(new java.awt.Dimension(70, 23));
                graphButton.setActionCommand(i + "");
                graphButton.addActionListener(listener);
                String label = (String) graphButton.getItemAt(0);
                graphic.setComboBoxLabel(label);
                displayColorContainerPanel.add(graphButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, (5 + 5 * 5 * ct) - 3, -1, -1));
                displayColorScrollPanel.validate();

                graphicSet.addGraphic(i, graphic);
                ct++;
            }
        }
    }

    /*
     * Called by SSAThread. Adds gathered spectra and returns (if createNodes)
     * the list of associated new nodes
     *
     */
    public synchronized Vector addSpectraToNodes(String tableName, SpectrumSet sv, boolean isTSA, boolean createNodes) {

        Vector nodesVector = new Vector();
        Spectrum thisSpectrum;

        for (int i = 0; i < sv.getSpectrumSet().size(); i++) {

            thisSpectrum = sv.getSpectrum(i);

            //Add the DISTANCE METADATA FIELD
            if (thisSpectrum.getRa() != null && thisSpectrum.getDec() != null) {

                try {
                    EquatorialCoordinates coord = new EquatorialCoordinates(Double.valueOf(thisSpectrum.getRa()), Double.valueOf(thisSpectrum.getDec()));
                    thisSpectrum.getMetaData().put("Distance (degrees)", (0.000277777778) * coord.distance(new EquatorialCoordinates(Double.valueOf(this.raValue), Double.valueOf(this.decValue))));
                    thisSpectrum.getMetadata_identifiers().add(0, "Distance (degrees)");
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    //no distance displayed
                }
            }

            if (isTSA) {
                thisSpectrum.setToBeNormalized(true);
                sv.addSpectrum(i, thisSpectrum);
            }

            if (createNodes) {
                Node ssaSpectrumNode = new Node(thisSpectrum.getTitle(), thisSpectrum.getMetaData(), thisSpectrum.getMetadata_identifiers());
                thisSpectrum.setNode(ssaSpectrumNode);
                ssaSpectrumNode.setRelatedObject(thisSpectrum);
                nodesVector.addElement(ssaSpectrumNode);
            }
        }
        spectrumSet.addSpectrumSet(sv);
        return nodesVector;
    }

    public synchronized void addNodesToTable(final String tableName, final String tableId, final Vector nodesVector, final String url) {

        Runnable doWorkRunnable = new Runnable() {

            VOSpecDetached aios;

            public void run() {
                resourcespanelmanager.addSSATable(tableName, tableId, nodesVector, url);
            }
        };
        SwingUtilities.invokeLater(doWorkRunnable);
    }
    /**
     * NAME: launchThreads()
     *
     * PURPOSE:
     *
     * Method which starts new Threads for each Spectrum selected.
     *
     * INPUT PARAMETERS: None.
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE: None
     */
    public LaunchThreads launchThreads = new LaunchThreads(this);

    public void launchThreads() {

        setSpectrumSelection();
        createNewSpectraViewer();

        launchThreads = new LaunchThreads(this);
        try {
            //System.out.println("Launching thread");
            new Thread(launchThreads).start();
        } catch (Exception e) {
            System.out.println("Error Launching Threads");
            e.printStackTrace();
        }
    }

    public void setSpectrumSelection() {

        Vector<Node> nonselected_tablenodes = resourcespanelmanager.getNonSelectedNodes();
        for (int i = 0; i < nonselected_tablenodes.size(); i++) {
            Node node = nonselected_tablenodes.get(i);
            node.setReady(true);
            ((Spectrum) node.getRelatedObject()).setSelected(false);
        }

        Vector<Node> selected_tablenodes = resourcespanelmanager.getSelectedNodes();
        for (int i = 0; i < selected_tablenodes.size(); i++) {
            Node node = selected_tablenodes.get(i);
            node.setWaiting(true);
            ((Spectrum) node.getRelatedObject()).setSelected(true);
        }
    }

    public void setPlotForVisualization() {

        waveDrawn = (String) waveChoice.getSelectedItem();
        fluxDrawn = (String) fluxChoice.getSelectedItem();
        logw = logWave.isSelected();
        logf = logFlux.isSelected();
 
        //plot.clear(true);
        plot.validate();
        plot.repaint();
        
        plot.clearPoints();

    }

    public void displayAllSpectra() {

        setPlotForVisualization();



        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {

                    Spectrum spectrum = spectrumSet.getSpectrum(i);
                    boolean isSelected = spectrum.getSelected();

                    if (isSelected) {
                        displaySpectrum(i, waveDrawn, fluxDrawn, logw, logf);
                    }
                }

            }
        });







        validate();
        repaint();
        show();
    }

    //this method checks which spectrum is selected in the ColorPanel and has to be visualized
    //if no spectra are selected there are shown all spectra
    public void displaySelectedSpectra() {

        //setPlotForVisualization();


        waveDrawn = (String) waveChoice.getSelectedItem();
        fluxDrawn = (String) fluxChoice.getSelectedItem();
        logw = logWave.isSelected();
        logf = logFlux.isSelected();

        plot.clear(true);

        plot.validate();
        plot.repaint();


        boolean nothingSelected = true;
        //utils.removeAllRunningSpectra();

        for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {

            if (hashSel.get(i + "") == "true") {
                nothingSelected = false;
                Spectrum spectrum = spectrumSet.getSpectrum(i);
                displaySpectrum(i, waveDrawn, fluxDrawn, logw, logf);
            }
        }

        if (nothingSelected) {
            displayAllSpectra();
        }
        validate();
        repaint();
        show();
    }

    public void displayOneSpectrum(Spectrum spectrum) {

        //setPlotForVisualization();

        waveDrawn = (String) waveChoice.getSelectedItem();
        fluxDrawn = (String) fluxChoice.getSelectedItem();
        logw = logWave.isSelected();
        logf = logFlux.isSelected();

        plot.clear(true);

        plot.validate();
        plot.repaint();





        int index = 0;
        for (int i = 0; i < spectrumSet.getSpectrumSet().size(); i++) {
            if (spectrumSet.getSpectrum(i).equals(spectrum)) {
                index = i;
                break;
            }
        }

        System.out.println("INDEX "+index);

        displaySpectrum(index, waveDrawn, fluxDrawn, logw, logf);
        validate();
        repaint();
        plot.validate();
        plot.repaint();
        show();
    }

    public void displaySpectrum(int i, String wave, String flux, boolean logw, boolean logf) {

        String marks = graphicSet.getGraphic(i).getComboBoxLabel();
        Spectrum spectrum = spectrumSet.getSpectrum(i);
        boolean realData = spectrum.getRealData();
        boolean isTsap = spectrum.getToBeNormalized();

        //sv = new SpectraViewer(plot, logw, logf, displayPanel);
        sv.setWaveToVelSelected(waveToVelocityCheckBox.isSelected());
        sv.setPlot(false, logw, logf);
        sv.setErrors(xErrorCheck.isSelected(), yErrorCheck.isSelected());
        sv.setMarksStyle(marks, realData, i, isTsap);

        //displayPanel.add(sv, java.awt.BorderLayout.CENTER);

        SpectrumConverter spectrumConverter;
        try {
            spectrumConverter = new SpectrumConverter(wave, flux, sv);
            spectrumConverter.setAioSpecToolDetached(this);
            spectrumConverter.setRedShift(getRedShift());

            if (waveToVelocityCheckBox.isSelected()) {
                spectrumConverter.setWaveToVel(getRefWaveValue());
            }

            spectrumConverter.setRunProperties(spectrum, i, false);
            new Thread(spectrumConverter).start();
            //SwingUtilities.invokeLater(spectrumConverter);

        } catch (Exception e) {
            if (spectrum != null) {
                if (spectrum.getNode() != null) {
                    spectrum.getNode().setFailed(true);
                }
            }

            System.out.println("Error converting spectrum ");
            e.printStackTrace();
        }
    }

    public void addSpectrum(String nodeTitle, Spectrum spectrum, javax.swing.JTextArea jTextArea/*,CheckNode fittingNode*/) {

        spectrum.setAioSpecToolDetached(this);

        resourcespanelmanager.addSpectrumToTable(nodeTitle, spectrum);

        try {
            //if (utils.addRunningSpectrum(spectrum)) {
            new Thread(spectrum).start();
            //}
            while (spectrum.getToWait()) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Error doing new Thread (spectrum) in AioSpecToolDetached");
            e.printStackTrace();
            return;
        }

        String metadata = spectrum.getString();
        if (jTextArea != null) {
            jTextArea.setText(metadata);
        }

        

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                removeComponentFromColorContainer();
                displaySpectraColorList();
            }
        });
        //refreshJTree();
        show();
        setDefaultCursor();


    }

    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
    }

    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
    }

    public void setHandCursor() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        setCursor(handCursor);
    }

    public void setMoveCursor() {
        Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
        setCursor(moveCursor);
    }

    public void setCrossHairCursor() {
        Cursor moveCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        setCursor(moveCursor);
    }

    public void setRa(String raTmp) {
        raValue = raTmp;
        raField.setText(raTmp);
    }

    public void setDec(String decTmp) {
        decValue = decTmp;
        decField.setText(decTmp);
    }

    public void setSize(String sizeTmp) {
        sizeValue = sizeTmp;
        sizeField.setText(sizeTmp);
    }

    public double getRedShift() {
        if (!redShiftCheck.isSelected()) {
            return 0.0;
        }
        try {
            return (new Double(redShiftValue.getText())).doubleValue();
        } catch (Exception e) {
            return 0.;
        }
    }

    public class ColoredTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
            setEnabled(table == null || table.isEnabled());
            if ((row == spectrumSet.getSpectrum(row).getRow()) && (spectrumSet.getSpectrum(row).getSelected())) {
                setBackground(plot.getDataSetColor(row, spectrumSet.getSpectrum(row).getRealData(), spectrumSet.getSpectrum(row).getToBeNormalized()));
            } else {
                setBackground(null);
            }
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            return this;
        }
    }
    /*
    public class ComponentResize implements ComponentListener {

    public ComponentResize() {
    super();
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    plot.setDisplayPanel(displayPanel);
    pack();
    validate();
    repaint();
    }

    public void componentShown(ComponentEvent e) {
    }
    }
     */

    /**
     * NAME: showURL
     *
     * PURPOSE:
     *
     * Method which actually displays an URL on a browser window, if
     * the applet is not running under Netscape or Explorer it
     * tries to reuse a running netscape and if it fails it starts
     * one.
     *
     * INPUT PARAMETERS:
     *
     * @param url the URL to show
     * @param browserID the ID of the browser window to use for the display
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE: None.
     *
     */
    public void showURL(String browserID, String urlString) throws Exception {
        try {
            URL url = new URL(urlString);
            parentAppletContext.showDocument(url, browserID);

        } catch (Exception e) {
            VOSpecHowTo howTo = new VOSpecHowTo(this);
            howTo.setVisible(true);
        }
    }

    public void normalizeSpectra(double x, double y) {

        String wave = (String) waveChoice.getSelectedItem();
        String flux = (String) fluxChoice.getSelectedItem();

        for (int ct = 0; ct < spectrumSet.getSpectrumSet().size(); ct++) {
            Spectrum returnSpectrum = new Spectrum();
            Spectrum spectrum = spectrumSet.getSpectrum(ct);

            if (spectrum.getToBeNormalized() && spectrum.getSelected()) {
                returnSpectrum = SpectrumNormalizer.calculateNorm(spectrum, wave, flux, x, y);
                spectrumSet.addSpectrum(ct, returnSpectrum);
            }
        }
        setWaitCursor();
        displaySelectedSpectra();
        setDefaultCursor();
    }

    public boolean deRedSelected() {
        return deRedCheck.isSelected();
    }

    public void setDeRedSelected(boolean selected) {
        deRedCheck.setSelected(selected);
    }

    public DeReddeningWindow getDeReddeningWindow() {
        return deReddeningWindow;
    }

    public synchronized void setLogText(String logMessage) {
        logLabel.setText(logMessage);
    }

    public boolean isInVelocitySpace() {
        return waveToVelocityCheckBox.isSelected();

    }

    public double getRefWaveValueAioSpecUnits() {
        double refWaveInAioSpecUnits = 0.;
        try {
            refWaveInAioSpecUnits = (new Double(waveToVelocityValue.getText())).doubleValue();
            return refWaveInAioSpecUnits;
        } catch (Exception e) {
            return refWaveInAioSpecUnits;
        }
    }

    public double getRefWaveValue() {
        double refWaveInMeters = 0.;
        try {
            double refWaveInAioSpecUnits = (new Double(waveToVelocityValue.getText())).doubleValue();
            Unit aioSpecUnits = new Unit(this.getWaveChoice(), this.getFluxChoice());
            Unit mksUnits = new Unit("L", "1.", "ML-1T-3", "1.");
            SpectrumConverter sc = new SpectrumConverter();
            sc.setAioSpecToolDetached(this);
            double[] point = sc.convertPoint(refWaveInAioSpecUnits, 1.0, aioSpecUnits, mksUnits);
            refWaveInMeters = point[0];
            return refWaveInMeters;
        } catch (Exception e) {
            return refWaveInMeters;
        }
    }
}
