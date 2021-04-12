package projetdysgraphie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagePremiereLettre extends JFrame {

    private String version = "v.1.14"; // Version du projet.
    private final int nbFichiers; // Nombre de fichiers contenus dans le dossier Dataset.

    private Trace tModele; // Enregistrement de la Trace de la lettre modèle.
    private ArrayList<Point> listPoint = new ArrayList<Point>(); // Liste des points à tracer.
    private String nomFichier; // Nom du fichier enregistré dans le dossier Dataset.

    // Define constants for the various dimensions
//   public static final int CANVAS_WIDTH = 500;
//   public static final int CANVAS_HEIGHT = 300;
    public static Color LINE_COLOR = Color.BLUE; // Couleur d'écriture (bleu par défaut).

    // Lines drawn, consists of a List of PolyLine instances
    private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine; // the current line (for capturing)

    Tableau fichier; // Création du fichier Excel xls.

    private long tempsDebut; // Temps de debut.

    /**
     * Permet de compter le nombre de fichiers enregistrés dans le dossier
     * Dataset.
     *
     * @param parent est le chemin du dossier dans lequel compter les fichiers.
     * @return nombre e fichiers (int).
     */
    static final int countFiles(String parent) throws Exception {
        File file = new File(parent);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file.list().length;
    }

    /**
     * Permet de donner le nom du fichier à enregistrer.
     *
     * @param nb est le nombre de fichiers dans le dossier Dataset.
     * @return le nom du fichier à retourner..
     */
    static final String nomFichier(int nb) {
        return ("fichier" + nb + "-Modele.xls");
    }

    /**
     * Creates new form NewJFrame
     *
     * @param t
     */
    public PagePremiereLettre(Trace t) throws Exception {
        this.nbFichiers = countFiles("C:/ProjetDysgraphie-master-" + version + "/Dataset");
        tModele = t;
        initComponents();
        afficherGraphs();
        tempsDebut = System.currentTimeMillis();
        Paint();
        jLabel2.setVisible(false);
        jSeparator3.setVisible(false);
        jButtonOui.setVisible(false);
        jButtonNon.setVisible(false);
        chargerImage("Pingouin", jPanel3);
        chargerImage("Chien", jPanel5);
    }

    public PagePremiereLettre() throws Exception {
        System.out.println("fgv");
        this.nbFichiers = countFiles("C:/ProjetDysgraphie-master-" + version + "/Dataset");
        initComponents();
        tempsDebut = System.currentTimeMillis();
        Paint();
        jLabel2.setVisible(false);
        jSeparator3.setVisible(false);
        jButtonOui.setVisible(false);
        jButtonNon.setVisible(false);
        chargerImage("Pingouin", jPanel3);
        chargerImage("Chien", jPanel5);
    }

    /**
     * Permet de créer une zone de dessin dans laquelle un tracé peut être fait.
     */
    public void Paint() {
        JPanel ct = new DrawCanvas();
        courbeTrace.removeAll();
        ct.setSize(courbeTrace.getSize());
        ct.setBackground(Color.WHITE);
        ct.addMouseListener(new MouseAdapter() {
            @Override
            /**
             * Permet de tracer au clic de souris / stylo sur tablette.
             *
             * @param evt est le clic.
             */
            public void mousePressed(MouseEvent evt) {
                // Begin a new line
                currentLine = new PolyLine();
                lines.add(currentLine);
                currentLine.addPoint(evt.getX(), evt.getY());

            }
        });
        ct.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            /**
             * Permet tracer quand la souris est enfoncée / le stylo appuie sur
             * la tablette.
             *
             * @param evt est le clic.
             */
            public void mouseDragged(MouseEvent evt) {
                currentLine.addPoint(evt.getX(), evt.getY());
                repaint();  // invoke paintComponent()
//                System.out.println("X =" + evt.getX() + " Y =" + evt.getY() + " id =" + listPoint.size() ) ;
//                System.out.println(System.currentTimeMillis()-tempsDebut);
                long time = System.currentTimeMillis() - tempsDebut;
                listPoint.add(new Point(evt.getX(), evt.getY(), listPoint.size(), 0, (int) time));
                if (listPoint.size() > 1) {
                    listPoint.get(listPoint.size() - 1).setInter(listPoint.get(listPoint.size() - 1).IntervalleEntre(listPoint.get(listPoint.size() - 2)));
                } else {
                    listPoint.get(listPoint.size() - 1).setInter(listPoint.get(listPoint.size() - 1).getTime());
                }
            }
        });
        courbeTrace.add(ct);
    }

    // Define inner class DrawCanvas, which is a JPanel used for custom drawing
    private class DrawCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) { // called back via repaint()
            super.paintComponent(g);
            g.setColor(LINE_COLOR);
            for (PolyLine line : lines) {
                line.draw(g);
            }
        }
    }

    public void chargerImage(String animal, JPanel panel) {
        String image;
        image = "/static/medias/" + animal + ".png";
        ImageIcon img = new ImageIcon(getClass().getResource(image));
        JLabel label = new JLabel();
        label.setIcon(img);
        label.setSize(panel.getSize());
        panel.removeAll();
        panel.add(label);
        label.setVisible(true);
        panel.setVisible(true);
        this.pack();
        this.revalidate();
        this.repaint();
    }

    /**
     * Permet d'afficher les graphiques calulés à partir du tracé.
     */
    public void afficherGraphs() {
        JPanel ca = new Courbe(tModele.getPointsAcceleration());
        courbeAccel.removeAll();
        ca.setSize(courbeAccel.getSize());
        courbeAccel.add(ca);

//        JPanel c = new Courbe(tModele);
//        courbeTrace.removeAll();
//        c.setSize(courbeTrace.getSize());
//        courbeTrace.add(c);
        courbeAccel.setVisible(true);
        courbeTrace.setVisible(true);
        this.pack();
        revalidate();
        repaint();
    }

    /**
     * Permet d'enregistrer un fichier.
     */
    public void saveFile() throws Exception {
        try {
//            System.out.println("nb fichiers = " + nbFichiers);
            this.fichier = new Tableau(nomFichier(nbFichiers), "sheet1", listPoint);
        } catch (Exception ex) {
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
        nomFichier = ("C:/ProjetDysgraphie-master-" + version + "/Dataset/" + nomFichier(nbFichiers));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonNon = new javax.swing.JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        courbeAccel = new JPanel();
        courbeTrace = new JPanel();
        jButtonOui = new javax.swing.JButton();
        jButtonCouleur = new javax.swing.JButton();
        jButtonVoir = new javax.swing.JButton();
        jPanel3 = new JPanel();
        jPanel5 = new JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFichier = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(51, 255, 204));
        setForeground(Color.pink);

        jButtonNon.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jButtonNon.setForeground(new Color(0, 153, 153));
        jButtonNon.setText("Non");
        jButtonNon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNonActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new Color(51, 255, 204));
        jLabel1.setFont(new java.awt.Font("French Script MT", 0, 36)); // NOI18N
        jLabel1.setText("Ecris ta lettre modèle");

        jLabel2.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jLabel2.setText("Es-tu satisfait de ta lettre ?");

        jLabel3.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jLabel3.setText("L'accélération de ton mouvement");

        javax.swing.GroupLayout courbeAccelLayout = new javax.swing.GroupLayout(courbeAccel);
        courbeAccel.setLayout(courbeAccelLayout);
        courbeAccelLayout.setHorizontalGroup(
            courbeAccelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 391, Short.MAX_VALUE)
        );
        courbeAccelLayout.setVerticalGroup(
            courbeAccelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 137, Short.MAX_VALUE)
        );

        courbeTrace.setBackground(new Color(255, 255, 255));
        courbeTrace.setBorder(new javax.swing.border.LineBorder(new Color(0, 0, 0), 5, true));
        courbeTrace.setMinimumSize(new java.awt.Dimension(20, 300));
        courbeTrace.setPreferredSize(new java.awt.Dimension(20, 300));

        javax.swing.GroupLayout courbeTraceLayout = new javax.swing.GroupLayout(courbeTrace);
        courbeTrace.setLayout(courbeTraceLayout);
        courbeTraceLayout.setHorizontalGroup(
            courbeTraceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1193, Short.MAX_VALUE)
        );
        courbeTraceLayout.setVerticalGroup(
            courbeTraceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );

        jButtonOui.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jButtonOui.setForeground(new Color(0, 153, 153));
        jButtonOui.setText("Oui");
        jButtonOui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOuiActionPerformed(evt);
            }
        });

        jButtonCouleur.setFont(new java.awt.Font("French Script MT", 0, 11)); // NOI18N
        jButtonCouleur.setForeground(new Color(0, 153, 153));
        jButtonCouleur.setText("Couleur");
        jButtonCouleur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCouleurActionPerformed(evt);
            }
        });

        jButtonVoir.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jButtonVoir.setForeground(new Color(0, 153, 153));
        jButtonVoir.setText("Voir");
        jButtonVoir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVoirActionPerformed(evt);
            }
        });

        jPanel3.setPreferredSize(new java.awt.Dimension(70, 90));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );

        jPanel5.setPreferredSize(new java.awt.Dimension(170, 170));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );

        jMenuBar1.setBackground(new Color(255, 255, 255));

        menuFichier.setText("File");
        menuFichier.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                menuFichierMouseClicked(evt);
            }
        });
        jMenuBar1.add(menuFichier);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(courbeTrace, javax.swing.GroupLayout.PREFERRED_SIZE, 1203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCouleur)
                .addGap(74, 74, 74))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(courbeAccel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(jButtonVoir, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(179, 179, 179)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(98, 98, 98))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jButtonOui, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNon)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonCouleur))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(courbeTrace, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButtonVoir, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButtonNon, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButtonOui, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(courbeAccel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Lance la PagePremiereLettre lettre lors du clic sur le jButtonNon.
     * @param evt
     */
    private void jButtonNonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNonActionPerformed
        // TODO add your handling code here:
        PagePremiereLettre p = null;
        try {
            p = new PagePremiereLettre();
        } catch (Exception ex) {
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
        p.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonNonActionPerformed

    /**
     * Ouvre l'explorateur de fichier et permet de selectionner un fichier CSV à
     * Lire lors du clic sur le menu fichier.
     * @param evt
     */
    private void menuFichierMouseClicked(MouseEvent evt) {//GEN-FIRST:event_menuFichierMouseClicked
        JFileChooser f = new JFileChooser();
        int result = f.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File fichier = f.getSelectedFile();
//            try {
//                tModele = new Trace(fichier);
//                afficherGraphs();
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this, "fichier : " + fichier.getAbsolutePath() + " introuvable");
//            }
//        }
    }//GEN-LAST:event_menuFichierMouseClicked

    /**
     * Au clic sur "Oui" enregistre un fichier et passe au tracé des essais (PageDeuxiemeLettre).
     * @param evt
     */
    private void jButtonOuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOuiActionPerformed
        // TODO add your handling code here:
        PageDeuxiemeLettre p = null;
        try {
            p = new PageDeuxiemeLettre(tModele, 0);
        } catch (Exception ex) {
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
        p.setVisible(true);
        try {
            saveFile();
        } catch (Exception ex) {
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_jButtonOuiActionPerformed

    /**
     * Permet de choisir la couleur du tracé.
     *
     * @param evt
     */
    private void jButtonCouleurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCouleurActionPerformed
        Color c = JColorChooser.showDialog(null,
                "Choix de la couleur d'écriture", null);
        if (c != null) {
            jButtonCouleur.setBackground(c);
        }
        LINE_COLOR = c;
    }//GEN-LAST:event_jButtonCouleurActionPerformed

    /**
     * Permet d'afficher le graphique de l'accélération.
     *
     * @param evt
     */
    private void jButtonVoirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVoirActionPerformed
        jButtonVoir.setVisible(false);
        jLabel2.setVisible(true);
        jSeparator3.setVisible(true);
        jButtonOui.setVisible(true);
        jButtonNon.setVisible(true);
        try {
            saveFile();
            tModele = new Trace(nomFichier);
            afficherGraphs();
        } catch (Exception ex) {
            System.out.println("Problème Page 1");
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonVoirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel courbeAccel;
    private JPanel courbeTrace;
    private javax.swing.JButton jButtonCouleur;
    private javax.swing.JButton jButtonNon;
    private javax.swing.JButton jButtonOui;
    private javax.swing.JButton jButtonVoir;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private JPanel jPanel3;
    private JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenu menuFichier;
    // End of variables declaration//GEN-END:variables
}
