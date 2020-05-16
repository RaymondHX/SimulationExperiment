package Exp1;

import Exp1.Load;

import java.util.List;

public class VirtualGraph {

    public int id;
    public int Node = 0;
    public int Edge;
    public int VN2PN[];
   // public List VE2PE[];

    public List VE2PE[][];
    public int VEindex;
    public double VEBandwidth[];

    public Load NodeCapacity[];
    public int EdgeCapacity[][];

    //用来定义虚拟机随着时间资源的变化
    public Load nodeChange[][];

    public VirtualGraph(int id){
        this.id = id;
        nodeChange = new Load[2][288];
        for (int i = 0; i <288 ; i++) {
            nodeChange[0][i] = new Load();
            nodeChange[1][i] = new Load();
        }
        initialResourceChange();
    }

    /**
     * 初始化随时间变化的资源
     */
    public void initialResourceChange(){
        //0-8点处于一个低峰
        for (int i = 0; i <96 ; i++) {
            nodeChange[0][i].cpu = NormalDistribution(6,2);
            nodeChange[0][i].mem = nodeChange[0][i].cpu;
            nodeChange[1][i].cpu = NormalDistribution(6,2);
            nodeChange[1][i].mem = nodeChange[1][i].cpu;
        }
        //8-24为一个上升趋势
        float increase = 0;
        for (int i = 96; i <288 ; i++) {
            nodeChange[0][i].cpu = NormalDistribution((float) (6+increase*0.3),2);
            nodeChange[0][i].mem = nodeChange[0][i].cpu;
            nodeChange[1][i].cpu = NormalDistribution((float) (6+increase*0.3),2);
            nodeChange[1][i].mem = nodeChange[1][i].cpu;
            if(i%12==0){
                increase++;
            }
        }
    }

    /**
     * 正态分布
     * @param u  均值
     * @param v  方差
     * @return
     */
    public static double NormalDistribution(float u,float v) {
        java.util.Random random = new java.util.Random();
        return Math.sqrt(v) * random.nextGaussian() + u;
    }


    /**
     * 更新虚拟网络资源
     * @param t t时刻 以五分钟为单位，t每加1代表五分钟
     */
    public void updateVirtualGraph(int t){
        NodeCapacity[0].cpu = nodeChange[0][t].cpu;
        NodeCapacity[0].mem = nodeChange[0][t].mem;
        NodeCapacity[1].cpu = nodeChange[1][t].cpu;
        NodeCapacity[1].mem = nodeChange[1][t].mem;
    }

}
