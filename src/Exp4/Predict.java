package Exp4;

import Exp1.PhysicalGraph;
import Exp1.VirtualGraph;

public class Predict {
    double[] beta = new double[9];
    /**
     * 获取当前时间
     * @param physicalGraph
     * @return
     */
    public int getTime(PhysicalGraph physicalGraph){
        return 0;
    }

    /**
     * 预测当前物理网络的某个节点是否符合迁移条件
     * @param physicalGraph
     * @return
     */
    public boolean overUtilizedHostDetection(PhysicalGraph physicalGraph,int node){
        int t = this.getTime(physicalGraph);
        for (int k = 0; k <6 ; k++) {
            if(getPredictCpuResourse(physicalGraph,node,t+k*5)<physicalGraph.nodeLoad[node].cpu*0.8){
                return false;
            }
        }
        return true;
    }

    /**
     * 预测当前物理网络的某个节点是否满足coldspot
     * @param physicalGraph
     * @return
     */
    public boolean underUtilizedHostDetection(PhysicalGraph physicalGraph,int node){
        int t = this.getTime(physicalGraph);
        for (int k = 0; k <6 ; k++) {
            if(getPredictCpuResourse(physicalGraph,node,t+k*5)>physicalGraph.nodeLoad[node].cpu*0.2){
                return false;
            }
        }
        return true;
    }


    public double getPredictCpuResourse(PhysicalGraph physicalGraph,int node ,int t){
        if(t>288){
            t = t%288;
        }
        //todo 利用最小二乘法计算预测
        return 0;
    }
}
