package projetdysgraphie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Trace {

    private List<Point> myPoints;

    /**
     * créé un objet tracé correspondant au CSV source
     *
     * @param source
     * @throws IOException
     */
//    public Trace(File source) throws IOException{
//        Scanner sc = new Scanner(source);
//        Lecteur l;
//        if(!sc.next().equals("Identification")){
//            l = new Lecteur(source);
//        }
//        else{
//           sc.nextLine();
//           sc.nextLine();
//           sc.nextLine();
//           sc.nextLine();
//           String input ="";
//           while(sc.hasNextLine()){
//               input+="\n"+sc.nextLine();
//           }
//           StringReader sourceStr = new StringReader(input);
//           l = new Lecteur(sourceStr);
//        }
//        myPoints = l.lire();
//        Double averageInterval = getAverageInter();
//        for(Point p:myPoints){
//            p.setInter((int) Math.round(averageInterval));
//        }
//    }
    public Trace(String name) throws IOException {
        Lecteur l = new Lecteur(name);
        myPoints = l.lire(name);
        Double averageInterval = getAverageInter();
        for (Point p : myPoints) {
            p.setInter((int) Math.round(averageInterval));
        }
    }

    public Trace(List<Point> mP) {
        myPoints = mP;
    }

    /**
     *
     * @return points contenus dans le tracé
     */
    public List<Point> getPoint() {
        return myPoints;
    }

    /**
     * Calcule l'acceleration en tout point du trace, sauf le premier et le
     * dernier. Se sert de la moyenne des intervalles du trace au lieu de celle
     * réelle pour éviter les faux pics d'acceleration.
     *
     * @return liste des valeurs d'acceleration du trace
     */
    public List<Double> getAcceleration() {
        List<Double> res = new ArrayList();
        Double averageInterval = getAverageInter();
        for (int i = 1; i < myPoints.size() - 1; i++) {
            Point pAv = myPoints.get(i - 1);
            Point pMi = myPoints.get(i);
            Point pAp = myPoints.get(i + 1);
            Double acc = (pAv.vitesseEntre(pMi) - pMi.vitesseEntre(pAp)) / ((double) 2 * averageInterval);
            res.add(acc);
        }
        return res;
    }

    /**
     *
     * @return la moyenne d'intervalles de temps entre les points
     */
    public double getAverageInter() {
        double res = 0;
        for (Point p : myPoints) {
            res += p.getInterval();
        }
        res = res / myPoints.size();
        return res;
    }

    /**
     *
     * @return une liste de points affichages de valeur x=num et y=acceleration
     */
    public List<PointAffichage> getPointsAcceleration() {
        ArrayList<PointAffichage> res = new ArrayList();
        List<Double> accel = getAcceleration();
        Double min = accel.get(0);
        for (int i = 0; i < accel.size(); i++) {
            double y = accel.get(i);
            PointAffichage p = new PointAffichage(i, y);
            res.add(p);
            if (y < min) {
                min = y;
            }
        }
        if (min < 0) {
            for (PointAffichage p : res) {
                p.setY(p.getY() - min);
            }
        }
        return res;
    }

    /**
     * Fait la moyenne de toutes les valeurs remises en positif d'une liste
     *
     * @param l
     * @return
     */
    public double moyennePositive(List<Double> l) {
        double res = 0;
        for (double d : l) {
            res += Math.abs(d);
        }
        res = res / l.size();
        return res;
    }

    /**
     *
     * @return le nombre de pic d'acceleration supérieurs à 1/2*moyenne
     * d'acceleration positive du tracé
     */
    public int getNbPic() {
        int res = 0;
        int signe = 0;
        List<Double> accel = getAcceleration();
        double average = moyennePositive(accel);
        if (accel.get(0) - accel.get(1) < 0) {
            signe = 1;
        } else {
            signe = -1;
        }
        for (int i = 0; i < accel.size() - 1; i++) {
            double signeAct = accel.get(i) - accel.get(i + 1);
            if (((signe < 0 && signeAct > 0)
                    || (signe > 0 && signeAct < 0))
                    && Math.abs(signeAct) > average * 0.5) {

                signe = -signe;
                res++;
            }
        }
        return res;
    }


    /**
     * Verifie l'essai est semblable au modèle avec une marge d'erreur de 20%
     * (compare le nombre de pics)
     *
     * @param essai tracé à verifier
     * @return
     */
    public boolean estSimilaireAuModele(Trace essai, double margeErreur) {
        int nbPicModele = getNbPic();
        int nbPicEssai = essai.getNbPic();
//        double margeErreur = 0.20;
        boolean res = Math.abs(nbPicModele - nbPicEssai) <= nbPicModele * margeErreur;
        return res;
    }
}
