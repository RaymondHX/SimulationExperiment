package Exp4;
import Exp1.PhysicalGraph;
import Jama.Matrix;
public class MUP {
    static int n = 20;
    public static double UP(PhysicalGraph physicalGraph,int m,int t,int node){
        double x[][] = new double[n-m][m+1];
        double y[][] = new double[n-m][1];
        double beta[] = new double[m+1];
        for (int i = 0; i <n-m ; i++) {
            x[i][0] = 1;
            for (int j = t-n+i; j <t-n+i+m ; j++) {
                if(physicalGraph.loadHistory[node][j].cpu==0){
                    return physicalGraph.NodeCapacity[node].cpu*0.5;
                }
                x[i][j-t+n-i+1] = physicalGraph.loadHistory[node][j].cpu;
            }
            y[i][0] = physicalGraph.loadHistory[node][i+t-n+m].cpu;
        }
        Matrix XM = new Matrix(x);
        Matrix YM = new Matrix(y);
        try {
            Matrix temp = (XM.transpose().times(XM)).inverse();
        }catch (Exception e){
            Matrix matrix = XM.transpose().times(XM);
            System.out.println(matrix.det());
        }

       // double[][] doubles = temp.getArray();
        Matrix BetaM = (((XM.transpose().times(XM)).inverse()) .times(XM.transpose())).times(YM);
        double UDP[][] = new double[m+1][1];
        UDP[0][0] = 1;
        for (int i = 1; i <m+1 ; i++) {
            UDP[i][0] = physicalGraph.loadHistory[node][t+i-m].cpu;
        }
        Matrix UDPM = new Matrix(UDP);
        double next = BetaM.transpose().times(UDPM).get(0,0);
        physicalGraph.loadHistory[node][t].cpu = next;
        return next;
    }
}
