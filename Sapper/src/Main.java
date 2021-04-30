import Sapper.FieldSize;
import Sapper.SapperModel;

public class Main {

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++) {
            SapperModel sapper = new SapperModel(FieldSize.SMALL);
            sapper.firstStep(1, 1);
            System.out.println(sapper.toString());
        }
    }
}
