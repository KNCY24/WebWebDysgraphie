package projetdysgraphie;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tableau {

    private String version = "v.1.14";

    private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    public Tableau(String fileName, String sheetName, ArrayList<Point> listPoint) {

        FileOutputStream outFile = null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(sheetName);
            int rownum = 0;
            Cell cell;
            Row row;
            //
            HSSFCellStyle style = createStyleForTitle(workbook);
            row = sheet.createRow(rownum);
            // Seg
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue("Seg");
            cell.setCellStyle(style);
            // Num
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue("Num");
            cell.setCellStyle(style);
            // X/100mm
            cell = row.createCell(2, CellType.STRING);
            //cell.setCellValue("X/100mm");
            cell.setCellValue("X");
            cell.setCellStyle(style);
            // Y/100mm
            cell = row.createCell(3, CellType.STRING);
            //cell.setCellValue("Y/100mm");
            cell.setCellValue("Y");
            cell.setCellStyle(style);
            // Interv ms
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue("Interv ms");
            cell.setCellStyle(style);
            // Time ms
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue("Time ms");
            cell.setCellStyle(style);
            // Tip
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue("Tip");
            cell.setCellStyle(style);
            // P
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue("P");
            cell.setCellStyle(style);
            // Az
            cell = row.createCell(8, CellType.STRING);
            //cell.setCellValue("Az");
            cell.setCellValue("Distance");
            cell.setCellStyle(style);
            // Al
            cell = row.createCell(9, CellType.STRING);
            //cell.setCellValue("Al");
            cell.setCellValue("Vitesse (10-3)");
            cell.setCellStyle(style);
            cell = row.createCell(10, CellType.STRING);
            cell.setCellValue("Accélération (10-3)");
            cell.setCellStyle(style);
            cell = row.createCell(11, CellType.STRING);
            //cell.setCellValue("Al");
            cell.setCellValue("Pics d'accélération");
            cell.setCellStyle(style);
            // Data
            for (int i = 0; i < listPoint.size(); i++) {
                rownum++;
                row = sheet.createRow(rownum);

                // Seg (A)
                cell = row.createCell(0, CellType.NUMERIC);
                cell.setCellValue(0);
                // Num (B)
                cell = row.createCell(1, CellType.NUMERIC);
                cell.setCellValue(listPoint.get(i).getNum());
                // X (C)
                cell = row.createCell(2, CellType.NUMERIC);
                cell.setCellValue(listPoint.get(i).getX());
                // Y (D)
                cell = row.createCell(3, CellType.NUMERIC);
                cell.setCellValue(listPoint.get(i).getY());
                // Interv ms (E)
                cell = row.createCell(4, CellType.NUMERIC);
                cell.setCellValue(listPoint.get(i).getInterval());
                // Time ms (F)
                cell = row.createCell(5, CellType.NUMERIC);
                cell.setCellValue(listPoint.get(i).getTime());
                // P (H)
                cell = row.createCell(7, CellType.NUMERIC);
                //METTRE LA VALEUR DE LA PRESSION
                cell.setCellValue(2);
                // Tip (G)
                int num;
                if (cell.getNumericCellValue() > 0) {
                    num = 1;
                } else {
                    num = 0;
                }
                cell = row.createCell(6, CellType.NUMERIC);
                cell.setCellValue(num);
                // Distance (I)
                if (rownum > 1) {
                    double dist = listPoint.get(i).distanceAvec(listPoint.get(i - 1));
                    cell = row.createCell(8, CellType.NUMERIC);
                    cell.setCellValue(dist);
                }
                // Vitesse (J)
                if (rownum > 1) {
                    double vit = 1000 * listPoint.get(i).distanceAvec(listPoint.get(i - 1)) / (listPoint.get(i).getInterval());
                    cell = row.createCell(9, CellType.NUMERIC);
                    cell.setCellValue(vit);
                }
                // Accélération (K)
                if (rownum > 2) {
                    double acc = (listPoint.get(i - 2).vitesseEntre(listPoint.get(i - 1)) - listPoint.get(i - 1).vitesseEntre(listPoint.get(i))) * 1000 / listPoint.get(i - 2).IntervalleEntre(listPoint.get(i));
                    int nbP = 0;
                    cell = row.createCell(10, CellType.NUMERIC);
                    cell.setCellValue(acc);
                    // Pics
                    if (acc > 15.0) {
                        nbP = 1;
                    } else {
                        if (acc < -15.0) {
                            nbP = -1;
                        }
                    }
                    cell = row.createCell(11, CellType.NUMERIC);
                    cell.setCellValue(nbP);
                }
            }
            File file = new File("C:/ProjetDysgraphie-master-" + version + "/Dataset/" + fileName);
            file.getParentFile().mkdirs();
            outFile = new FileOutputStream(file);
            workbook.write(outFile);
            System.out.println("Created file: " + file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tableau.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tableau.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outFile.close();
            } catch (IOException ex) {
                Logger.getLogger(Tableau.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
