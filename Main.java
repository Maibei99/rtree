import test.Results;
import test.TestMaker;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        int[] nValues = {(int) Math.pow(2, 10), (int) Math.pow(2, 12), (int) Math.pow(2, 13),
                (int) Math.pow(2, 14), (int) Math.pow(2, 15), (int) Math.pow(2, 16),
                (int) Math.pow(2, 17), (int) Math.pow(2, 18), (int) Math.pow(2, 19),
                (int) Math.pow(2, 20), (int) Math.pow(2, 21), (int) Math.pow(2, 22), (int) Math.pow(2, 23),
                (int) Math.pow(2, 24), (int) Math.pow(2, 25)};
        String[] names = {"NXTree", "HilbertTree", "STRTree"};

        for (int value : nValues) {
            List<Results> results= new TestMaker(value, 124).test();

            int i = 0;
            System.out.println("Los resultados para n = " + value + " son:");
            while (i < 3) {
                System.out.println("Para " + names[i] + ":");
                System.out.println(results.get(i).toString());
                i++;
            }

        }
    }

}
