package com.wec.community;

public class Test2 {
    public static void main(String[] args) {
        System.out.println("aaaa");
       /*double d[] = {46.889,46.622,46.262,44.414,45.399,43.357};
       double s = 0;
        for (int i = 0; i < d.length; i++) {
            s += d[i];
        }
        double p = s/6.0;
        System.out.println("平均值:"+p);
        double R = (p/(4.0*20.0*589.3))*1000000;
        System.out.println("R:"+R);

        double sx = 0;
        for (int i = 0; i < d.length; i++) {
            sx += (d[i]-p)*(d[i]-p);
        }
        sx = Math.sqrt(sx/30.0);
        System.out.println("UA:"+sx);
        double Ub = 0.00231;
        double U = Math.sqrt(Ub*Ub + sx*sx);
        System.out.println("U:"+U);
        double Er = Math.sqrt((U/p)*(U/p) + 0.0001+ (0.4/589.3)*(0.4/589.3));
        System.out.println("Er:"+Er);
        double ur = R*Er;
        System.out.println(ur);*/
        double s = (1.0/600.0)*(Math.sin(20.467));
        s = Math.sin(30);
        System.out.println(s);


    }
}
