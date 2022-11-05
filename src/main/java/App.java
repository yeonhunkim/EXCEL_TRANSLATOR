
public class App 
{
    public static void main( String[] args )
    {
        System.setProperty("GOOGLE_API_KEY", "AIzaSyCBVdYYCDaAzNWhteALLJTqYFGctat3jmk");
        //String test = Translator.sendStringToPapago("MASTER EMO가 눌렸을 때 발생\n");
        //System.out.println(test);
        ExcelController controller = new ExcelController();
        //controller.convertExcelToCsv("C:\\Users\\E211102\\Downloads\\SKBA2_모듈동_에러코드_221007.xlsx");
        //controller.translateCsvToExcel("test.csv", "SKBA2_module_errorcode_221007.xlsx");
        controller.translateExcel("C:\\Users\\E211102\\Downloads\\SKBA2_모듈동_에러코드_221007.xlsx", "SKBA2_module_errorcode_221007.xlsx");
    }
}
