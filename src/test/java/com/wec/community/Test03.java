package com.wec.community;

public class Test03 {
    public static void main(String[] args) {
        double c[] = {30,
                32,
                34,
                36,
                38,
                40,
                42,
                44,
                46,
                48,
                50,
                52,
                54,
                56,
                58,
                60,
                62,
                64,
                66,
                68,
                70
        };
        for (int i = 0; i < c.length; i++) {
            double f = c[i] + 273.15;
            System.out.println(f);
        }


//        double a = 1.602/((30.0+273.15)*38.386);
//        double b = (a-1.381)/1.381;
//        System.out.println(a);
//        System.out.println(b);

//        double a = 1.602/((27.0+273.15)*38.373);
//        double b = (a-1.381)/1.381;
//        System.out.println(a);
//        System.out.println(b);

    }
}
