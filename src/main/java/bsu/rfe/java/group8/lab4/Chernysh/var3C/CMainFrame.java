package bsu.rfe.java.group8.lab4.Chernysh.var3C;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class CMainFrame extends JFrame {
    private static final int iFWidth = 1080;
    private static final int iFHeight = 720;
    private JFileChooser fileChooser = null;
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem rotatesMenuItem;
    private JMenuItem secondGraph;
    private CGraphicsDisplay display = new CGraphicsDisplay();
    private boolean fileLoaded = false;

    private CMainFrame() {
        super("Plotting graphs");
        setSize(iFWidth, iFHeight);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - iFWidth) / 2, (kit.getScreenSize().height - iFHeight) / 2);
        setExtendedState(MAXIMIZED_BOTH);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        Action openGraphicsAction = new AbstractAction("Open file with graph") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(CMainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile());
                }
            }
         };
        fileMenu.add(openGraphicsAction);

        JMenu graphicsMenu = new JMenu("Graph");
        menuBar.add(graphicsMenu);
         Action showAxisAction = new AbstractAction("Show axes") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);

        Action showMarkersAction = new AbstractAction("Show points") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);

        Action rotatesShapeClockAction = new AbstractAction("Rotate") {
            public void actionPerformed(ActionEvent event) {
                display.setClockRotate(rotatesMenuItem.isSelected());
            }
        };
        rotatesMenuItem = new JCheckBoxMenuItem(rotatesShapeClockAction);
        graphicsMenu.add(rotatesMenuItem);
        rotatesMenuItem.setSelected(false);
        
        Action addSecondGraph = new AbstractAction("Second graph") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(CMainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile());
                }
            }
        };
        secondGraph = new JMenuItem(addSecondGraph);
        graphicsMenu.add(secondGraph);
        showMarkersMenuItem.setEnabled(false);
        
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        getContentPane().add(display, BorderLayout.CENTER);
        }

    private void openGraphics(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            Double[][] graphicsData = new Double[in.available() / (Double.SIZE / 8) / 2][];
            int i = 0;
            while (in.available() > 0) {
                double x = in.readDouble();
                double y = in.readDouble();
                graphicsData[i++] = new Double[] {x, y};
            }
            if (graphicsData != null && graphicsData.length > 0) {
                fileLoaded = true;
                display.showGraphics(graphicsData);
            }
            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(CMainFrame.this, "File don't exist", "Loading error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(CMainFrame.this, "Reading points error", "Loading error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        CMainFrame frame = new CMainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class GraphicsMenuListener implements MenuListener {
        public void menuSelected(MenuEvent e) {
            secondGraph.setEnabled(fileLoaded);
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
            rotatesMenuItem.setEnabled(fileLoaded);
        }
        public void menuDeselected(MenuEvent e) {
        }
        public void menuCanceled(MenuEvent e) {
        }
    }
}
