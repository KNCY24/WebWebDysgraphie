package com.PTUT.WebDysgraphie.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

public class Lecteur {

    private FileInputStream file; // Read XSL file

    public Lecteur() throws IOException {
    }

    /**
     * Lit le fichier CSV et extrait les informations nécéssaires à la création
     * d'un objet Trace
     *
     * @return les valeurs normes d'éciture pour un sexe et un niveau
     */
    public ArrayList<Object> lire(String name,String sexe, String niveau) throws FileNotFoundException, IOException {
        ArrayList listModele = new ArrayList();
        file = new FileInputStream(new File(name));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        System.out.println(sexe);
        System.out.println(niveau);
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() > 0) {
                HSSFCell cellSexe = sheet.getRow(row.getRowNum()).getCell(0);
                HSSFCell cellNiveau = sheet.getRow(row.getRowNum()).getCell(1);
                HSSFCell cellTemps = sheet.getRow(row.getRowNum()).getCell(2);
                HSSFCell cellPmoy = sheet.getRow(row.getRowNum()).getCell(3);
                HSSFCell cellPet = sheet.getRow(row.getRowNum()).getCell(4);
                HSSFCell cellDistance = sheet.getRow(row.getRowNum()).getCell(5);
                HSSFCell cellnbAcc = sheet.getRow(row.getRowNum()).getCell(6);
                HSSFCell cellnbDec = sheet.getRow(row.getRowNum()).getCell(7);

                if(cellSexe!=null && cellNiveau!=null) {
                    if (cellSexe.getStringCellValue().equals(sexe) && cellNiveau.getStringCellValue().equals(niveau)) {
                        listModele.add(cellTemps.getNumericCellValue());
                        listModele.add(cellPmoy.getNumericCellValue());
                        listModele.add(cellPet.getNumericCellValue());
                        listModele.add(cellDistance.getNumericCellValue());
                        listModele.add(cellnbAcc.getNumericCellValue());
                        listModele.add(cellnbDec.getNumericCellValue());
                    }
                }
            }
        }
        return listModele;
    }

    public String analyzePressure(ArrayList<Object> listValeur, ArrayList<Double> listPression){
        double pmoy = 0;
        double pet=0;
        //calcul de la pression
        for( double p : listPression){
            pmoy= pmoy + p;
        }
        pmoy = pmoy/listPression.size();
        //calcul de l'écart-type
        //|xi - moy| au carré
        for(double p: listPression){
            pet = pet + Math.pow(Math.abs(p-pmoy),2);
        }
        pet = pet/4;
        pet = Math.sqrt(pet);


        String result="";
        if(listValeur.size()>0) {
            //comparer moyenne aux normes
            if ((double) listValeur.get(1) - pmoy > 0.1) {
                result = result + "Tu appliques une pression sur le stylo plus forte que la moyenne.";
            } else if ((double) listValeur.get(1) - pmoy < 0.1){
                result = result + "Tu appliques une pression sur le stylo moins forte que la moyenne.";
            } else {
                result = result + "Tu appliques une pression sur le stylo équivalente à la moyenne.";
            }

            //comparer ecart-type aux normes
            if ((double) listValeur.get(2) - pet > 0.1) {
                result = result + "La pression appliquée sur le stylo varie plus que la moyenne.";
            } else if((double) listValeur.get(2) - pet < 0.1){
                result = result + " La pression appliquée sur le stylo varie moins que la moyenne.";
            }
            return result;
        }

        return "Analyse de la pression impossible.";
    }

    public String analyzeAcceleration(ArrayList<Object> listValeur, ArrayList<Point> listPoint){
        for(int i= 2; i< listPoint.size() ;i++) {
            double acc = (listPoint.get(i - 2).vitesseEntre(listPoint.get(i - 1)) - listPoint.get(i - 1).vitesseEntre(listPoint.get(i))) * 1000 / listPoint.get(i - 2).IntervalleEntre(listPoint.get(i));
            double nbPA = 0;
            double nbPD = 0;
            // Pics
            if (acc > 15.0) {
                nbPA = nbPA + 1;
            } else {
                if (acc < -15.0) {
                    nbPD = nbPD + 1;
                }
            }
            String result ="";
            if(listValeur.size()>0) {
                if (nbPA - (double) listValeur.get(4) > 1) {
                    result = result + "Tu produis plus d'accélérations que la moyenne.";
                } else if (nbPA - (double) listValeur.get(4) < 1) {
                    result = result + "Tu produis moins d'accélérations que la moyenne.";
                }

                if (nbPD - (double) listValeur.get(5) > 1) {
                    result = result + "Tu produis plus de décélérations que la moyenne.";
                } else if (nbPD - (double) listValeur.get(5) < 1){
                    result = result + "Tu produis moins de décélérations que la moyenne.";
                }

                if(result == ""){
                    result = "Il n'y a rien dire sur les accélérations/décélérations de tes lettres.";
                }
                return result;
            }
        }
        return "Analyse des accélérations/décélérations impossible.";
    }

    public String analyzeTaTf(ArrayList<Object> listValeur, long tempsE, long tempsT){
        double tatf = (tempsT - tempsE)/tempsE;
        if(listValeur.size()>0) {
            if ((double) listValeur.get(0) - tatf > 0) {
                return "Tu prends plus de temps pour écrire que la moyenne.";
            } else {
                return "Il n'y a rien à dire sur tes levers de crayon.";
            }
        }
        return "Analyse des levers de crayon impossible.";
    }

    public String analyzeDistance(ArrayList<Object> listValeur, ArrayList<Point> listPoint){
        double dist=0;
        for(int i =1 ; i< listPoint.size() ; i++) {
            dist = dist + listPoint.get(i).distanceAvec(listPoint.get(i - 1));
        }
        if(listValeur.size()>0) {
            if (dist - (double) listValeur.get(3) > 200) {
                return "Ton écriture est plus étroite que la moyenne.";
            } else if (dist - (double) listValeur.get(3) < -200) {
                return "Ton écriture est plus espacée que la moyenne.";
            } else {
                return "Il n'y a rien dire sur l'espacement de tes lettres.";
            }
        }
        return "Analyse de la distance parcourue impossible.";
    }

}
