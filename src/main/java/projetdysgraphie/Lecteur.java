package projetdysgraphie;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lecteur {
//    private CSVParser parser;

    private FileInputStream file; // Read XSL file
    private HSSFWorkbook workbook;// Get the workbook instance for XLS file
    private HSSFSheet sheet; // Get first sheet from the workbook

//    public Lecteur(File source) throws IOException{
    public Lecteur(String name) throws IOException {
//        parser = CSVParser.parse(source, Charset.forName("ISO-8859-1"), CSVFormat.TDF.withHeader());
        file = new FileInputStream(new File(name));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
    }

//    public Lecteur(Reader source) throws IOException{
//        parser = new CSVParser(source, CSVFormat.TDF.withHeader());
//    }
    /**
     * Lit le fichier CSV et extrait les informations nécéssaires à la création
     * d'un objet Trace
     *
     * @return une liste de points
     */
    public List<Point> lire(String name) throws FileNotFoundException, IOException {
        ArrayList lesPoints = new ArrayList();

        file = new FileInputStream(new File(name));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
//        int i=0;
//        for(CSVRecord c:parser){
//            if(c.get("Tip").equals("1")){
//                lesPoints.add(new Point(Integer.parseInt(c.get("X")),Integer.parseInt(c.get("Y")), i, Integer.parseInt(c.get("Interv ms")), Integer.parseInt((c.get("Time ms")))));
//                i++;
//            }
//        }
//        return lesPoints;

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
//            System.out.println("row = " + row.getRowNum());

            if (row.getRowNum() > 0) {
                HSSFCell cellX = sheet.getRow(row.getRowNum()).getCell(2);
                HSSFCell cellY = sheet.getRow(row.getRowNum()).getCell(3);
                HSSFCell cellNum = sheet.getRow(row.getRowNum()).getCell(1);
                HSSFCell cellInter = sheet.getRow(row.getRowNum()).getCell(4);
                HSSFCell cellTime = sheet.getRow(row.getRowNum()).getCell(5);

                lesPoints.add(new Point((int) cellX.getNumericCellValue(),
                        (int) cellY.getNumericCellValue(),
                        (int) cellNum.getNumericCellValue(),
                        (int) cellInter.getNumericCellValue(),
                        (int) cellTime.getNumericCellValue()));
            }
        }
        return lesPoints;
    }

}
