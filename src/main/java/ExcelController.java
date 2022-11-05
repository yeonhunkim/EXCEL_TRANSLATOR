import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ExcelController {


    public static String getSheetName(String filePath, int sheetNo) {
        XSSFSheet sheet = null;
        try {
            FileInputStream file = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(sheetNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheet.getSheetName();
    }
    public void convertExcelToCsv(String filePath) {
        try {
            FileInputStream file = new FileInputStream(filePath);
            StringBuilder csvString = new StringBuilder();
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) {
                        case BOOLEAN:
                            csvString.append(cell.getBooleanCellValue());
                        case NUMERIC:
                            csvString.append(cell.getNumericCellValue());
                            break;
                        case STRING:
                            String stringCellValue = cell.getStringCellValue();
                            stringCellValue.replaceAll("\"", "\"\"");
                            csvString.append("\"").append(stringCellValue).append("\"");
                            break;
                    }
                    if (cell.getColumnIndex() != row.getLastCellNum()-1) {
                        csvString.append(",");
                    }
                }
                csvString.append("\n");
            }
            Files.write(Paths.get("test.csv"), csvString.toString().getBytes("UTF-8"));
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void translateCsvToExcel(String csvFilePath, String excelFilePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sample");
        String csvString = "";
        try {
            csvString = Translator.readStringFromCsv(csvFilePath);
            String[] csvLine = csvString.split("\n");
            for (int i = 0; i < csvLine.length; i++) {
                Row row = sheet.createRow(i);
                System.out.println(csvLine[i]);
                csvLine[i] = csvLine[i].replaceAll("\"", "");

                String translateResult = Translator.sendStringToPapago(csvLine[i]);
                System.out.println(translateResult);
                String[] csvCells = translateResult.split(",");
                for (int j = 0; j < csvCells.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(csvCells[j]);
                }
            }
            FileOutputStream out = new FileOutputStream(excelFilePath);
            workbook.write(out);
            out.close();
            System.out.println(excelFilePath + " written successfully on disk.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyExcel(String sourceFilePath, String targetFilePath) {
        FileSystem system = FileSystems.getDefault();
        Path original = system.getPath(sourceFilePath);
        Path target = system.getPath(targetFilePath);
        try {
            // Throws an exception if the original file is not found.
            Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println("ERROR");
        }
    }

    public void translateExcel(String sourceFilePath, String targetFilePath) {
        try {
            copyExcel(sourceFilePath, targetFilePath);
            FileInputStream sourceFile = new FileInputStream(sourceFilePath);
            FileInputStream targetFile = new FileInputStream(targetFilePath);
            XSSFWorkbook sourceWorkbook = new XSSFWorkbook(sourceFile);
            XSSFWorkbook targetWorkbook = new XSSFWorkbook(targetFile);
            XSSFSheet sourceSheet = sourceWorkbook.getSheetAt(0);
            XSSFSheet targetSheet = targetWorkbook.getSheetAt(0);
            //Iterate through each rows one by one
            Iterator<Row> sourceRowIterator = sourceSheet.iterator();
            Iterator<Row> targetRowIterator = targetSheet.iterator();
            while (sourceRowIterator.hasNext()) {
                Row sourceRow = sourceRowIterator.next();
                Row targetRow = targetRowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> sourceCellIterator = sourceRow.cellIterator();
                Iterator<Cell> targetCellIterator = targetRow.cellIterator();
                while (sourceCellIterator.hasNext()) {
                    String cellString = "";
                    Cell sourceCell = sourceCellIterator.next();
                    Cell targetCell = targetCellIterator.next();
                    //Check the cell type and format accordingly
                    switch (sourceCell.getCellType()) {
                        case BOOLEAN:
                            cellString = Boolean.toString(sourceCell.getBooleanCellValue());
                            break;
                        case NUMERIC:
                            cellString = Double.toString(sourceCell.getNumericCellValue());
                            break;
                        case STRING:
                            cellString = sourceCell.getStringCellValue();
                            break;
                    }
                    String resultString = Translator.sendStringToPapago(cellString);
                    System.out.println(resultString);
                    targetCell.setCellValue(resultString);
                }
                sourceFile.close();
                FileOutputStream out = new FileOutputStream(targetFilePath);
                targetWorkbook.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}