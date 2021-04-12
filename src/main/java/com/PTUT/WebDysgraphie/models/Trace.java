package com.PTUT.WebDysgraphie.models;


import java.util.ArrayList;

public class Trace {

    private ArrayList<Point> myPoints;

    public Trace(ArrayList<Point> mP) {
        this.myPoints = mP;
    }

    /**
     *
     * @return points contenus dans le tracé
     */
    public ArrayList<Point> getPoint() {
        return myPoints;
    }

    /**
     * Calcule l'acceleration en tout point du trace, sauf le premier et le
     * dernier. Se sert de la moyenne des intervalles du trace au lieu de celle
     * réelle pour éviter les faux pics d'acceleration.
     *
     * @return liste des valeurs d'acceleration du trace
     */
    public ArrayList<Double> getAcceleration() {
        ArrayList<Double> res = new ArrayList();
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
     * Fait la moyenne de toutes les valeurs remises en positif d'une liste
     *
     * @param l
     * @return
     */
    public double moyennePositive(ArrayList<Double> l) {
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
        ArrayList<Double> accel = getAcceleration();
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

}
