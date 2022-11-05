
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("안녕하세요?");
        Translator translator = new Translator();
        String test = translator.putTextToPapago("MASTER EMO가 눌렸을 때 발생");
        System.out.println(test);
        String test2 = translator.putTextToPapago("안녕하십니까");
        System.out.println(test2);
        //ExcelController controller = new ExcelController();
        //controller.write(test);
    }
}
