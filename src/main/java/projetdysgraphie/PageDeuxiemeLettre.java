package projetdysgraphie;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageDeuxiemeLettre extends javax.swing.JFrame {

    private String version = "v.1.14"; // Version du projet.
    private final int nbFichiers; // Nombre de fichiers contenus dans le dossier Dataset.

    private Trace modele; // Enregistrement de la Trace de la lettre modèle.
    private Trace essai; // Enregistrement de la Trace de l'essai à comparer.
    private ArrayList<Point> listPoint = new ArrayList<Point>(); // Liste des points à tracer.
    private String nomFichier; // Nom du fichier enregistré dans le dossier Dataset.

    // Define constants for the various dimensions
//   public static final int CANVAS_WIDTH = 500;
//   public static final int CANVAS_HEIGHT = 300;
    public static final Color LINE_COLOR = Color.BLUE;

    // Lines drawn, consists of a List of PolyLine instances
    private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)

    Tableau fichier; // Création du fichier Excel xls.

    private long tempsDebut; // Temps de debut.
    private int nbEssai;

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
        return ("fichier" + nb + ".xls");
    }

    /**
     * Creates new form NewJFrame
     *
     * @param tModele
     */
    public PageDeuxiemeLettre(Trace tModele, int nbE) throws Exception {
        this.nbFichiers = countFiles("C:/ProjetDysgraphie-master-" + version + "/Dataset");
        modele = tModele;
        initComponents();
        tempsDebut = System.currentTimeMillis();
        Paint();
        nbEssai = nbE;
        jLabel2.setVisible(false);
        jSeparator3.setVisible(false);
        jButtonOui.setVisible(false);
        jButtonNon.setVisible(false);
        chargerImage("Nounours", jPanel1);
        chargerImage("Cochon", jPanel2);
    }

    public PageDeuxiemeLettre() throws Exception {
        this.nbFichiers = countFiles("C:/ProjetDysgraphie-master-" + version + "/Dataset");
        initComponents();
        tempsDebut = System.currentTimeMillis();
        Paint();
        jLabel2.setVisible(false);
        jSeparator3.setVisible(false);
        jButtonOui.setVisible(false);
        jButtonNon.setVisible(false);
        chargerImage("Nounours", jPanel1);
        chargerImage("Cochon", jPanel2);
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
                long time = System.currentTimeMillis() - tempsDebut;
                listPoint.add(new Point(evt.getX(), evt.getY(), evt.getID(), listPoint.size(), (int) time));
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

    /**
     * Permet d'afficher les graphiques calulés à partir du tracé.
     */
    public void afficherGraphs() {
        JPanel ca = new Courbe(essai.getPointsAcceleration());
        courbeAccel.removeAll();
        ca.setSize(courbeAccel.getSize());
        courbeAccel.add(ca);

//        JPanel c = new Courbe(essai);
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

//    /**
//     * Charge le Trace "essai"
//     */
//    public void chargerFichier() {
//        JFileChooser f = new JFileChooser();
//        int result = f.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File fichier = f.getSelectedFile();
//            try {
//                essai = new Trace(fichier);
//                afficherGraphs();
//                chargerImage(modele.estSimilaireAuModele(essai));
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this, "fichier : " + fichier.getAbsolutePath() + " introuvable");
//            }
//        }
//    }
    /**
     * Charge et affiche l'image selon l'exactitude de l'écriture
     *
     * @param bon bon=true si elles sont assez similaires, false sinon
     */
    public void chargerImage(boolean bon) {
        String image;
        if (bon) {
            if (nbEssai < 4) {
                image = "/static/medias/Oui.png";
            } else {
                image = "/static/medias/Feu.png";
            }
        } else {
            image = "/static/medias/Non.png";
        }
        ImageIcon img = new ImageIcon(getClass().getResource(image));
        JLabel label = new JLabel();
        label.setIcon(img);
        label.setSize(panelImage.getSize());
        panelImage.removeAll();
        panelImage.add(label);
        label.setVisible(true);
        panelImage.setVisible(true);
        this.pack();
        this.revalidate();
        this.repaint();
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

    private void playSound(String expression) {
        {
            String son;
            son = "/static/medias/" + expression + ".wav";
            try {
                InputStream audioSrc = getClass().getResourceAsStream(son);
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (Exception ex) {
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOui = new javax.swing.JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        courbeTrace = new JPanel();
        courbeAccel = new JPanel();
        panelImage = new JPanel();
        jButtonNon = new javax.swing.JButton();
        jButtonVoir = new javax.swing.JButton();
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFichier = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1304, 781));

        jButtonOui.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jButtonOui.setForeground(new Color(0, 153, 153));
        jButtonOui.setText("Oui");
        jButtonOui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOuiActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("French Script MT", 0, 36)); // NOI18N
        jLabel1.setText("Entraîne-toi !");

        jLabel2.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jLabel2.setText("Es-tu satisfait de ta lettre ?");

        jLabel3.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jLabel3.setText("L'accélération de ton mouvement");

        courbeTrace.setBackground(new Color(255, 255, 255));
        courbeTrace.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0), 5));

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

        javax.swing.GroupLayout panelImageLayout = new javax.swing.GroupLayout(panelImage);
        panelImage.setLayout(panelImageLayout);
        panelImageLayout.setHorizontalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
        panelImageLayout.setVerticalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );

        jButtonNon.setFont(new java.awt.Font("French Script MT", 0, 28)); // NOI18N
        jButtonNon.setForeground(new Color(0, 153, 153));
        jButtonNon.setText("Non");
        jButtonNon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNonActionPerformed(evt);
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

        jPanel1.setPreferredSize(new java.awt.Dimension(208, 182));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 198, Short.MAX_VALUE)
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(61, 64));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 69, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

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
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(courbeAccel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonVoir, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(87, 87, 87))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonOui, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNon)
                        .addGap(137, 137, 137))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 10, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(courbeTrace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(courbeTrace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jButtonVoir, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(panelImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 4, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(courbeAccel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButtonOui, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButtonNon, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(76, 76, 76))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(60, 60, 60))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Enregistre le fichier dans le dossier Dataset.
     *
     * @param evt
     */
    private void jButtonOuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOuiActionPerformed
        //chargerFichier();
        PageDeuxiemeLettre p = null;
        nbEssai++;
        System.out.println("nb E = " + nbEssai);
        try {
            System.out.println("nb fichiers = " + nbFichiers);
            this.fichier = new Tableau(nomFichier(nbFichiers), "sheet1", listPoint);
            if (nbEssai < 5) {
                p = new PageDeuxiemeLettre(modele, nbEssai);
                p.setVisible(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.dispose();
    }//GEN-LAST:event_jButtonOuiActionPerformed

    /**
     * Permet de charger un nouvel essai lors du clic sur le menu "fichier".
     *
     * @param evt
     */
    private void menuFichierMouseClicked(MouseEvent evt) {//GEN-FIRST:event_menuFichierMouseClicked
//        chargerFichier();
    }//GEN-LAST:event_menuFichierMouseClicked

    /**
     * Lance la PageDeuxiemeLettre encore.
     *
     * @param evt
     */
    private void jButtonNonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNonActionPerformed
        PageDeuxiemeLettre p = null;
        try {
            p = new PageDeuxiemeLettre(modele, nbEssai);
        } catch (Exception ex) {
            Logger.getLogger(PageDeuxiemeLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
        p.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonNonActionPerformed

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
        double margeErreur = 0.25;
        try {
            saveFile();
            essai = new Trace(nomFichier);
            afficherGraphs();
            switch (nbEssai) {
                case 0:
                    margeErreur = 0.25;
                    break;
                case 1:
                    margeErreur = 0.20;
                    break;
                case 2:
                    margeErreur = 0.15;
                    break;
                case 3:
                    margeErreur = 0.10;
                    break;
                case 4:
                    margeErreur = 0.10;
                    break;
                default:
                    margeErreur = 0.25;
                    break;
            }
            System.out.println("nbEssai = " + nbEssai + " et marge Erreur = " + margeErreur);
            chargerImage(modele.estSimilaireAuModele(essai, margeErreur));
            if (modele.estSimilaireAuModele(essai, margeErreur)) {
                if (nbEssai == 4) {
                    playSound("applaudissements");
                } else {
                    playSound("joie");
                }
            } else {
                playSound("degout");
            }
        } catch (Exception ex) {
            System.out.println("Problème Page 2");
            Logger.getLogger(PagePremiereLettre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonVoirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel courbeAccel;
    private JPanel courbeTrace;
    private javax.swing.JButton jButtonNon;
    private javax.swing.JButton jButtonOui;
    private javax.swing.JButton jButtonVoir;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenu menuFichier;
    private JPanel panelImage;
    // End of variables declaration//GEN-END:variables
}
