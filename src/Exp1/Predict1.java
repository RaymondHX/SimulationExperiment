package Exp1;

import Exp4.LeastSquareMethod;

import java.util.List;

public class Predict1 {
    List<LeastSquareMethod> leastSquareMethods;
    private double hotThreshold = 0.8;
    private double coldThreshold = 0.2;
    Predict1(List<LeastSquareMethod> leastSquareMethods){
        this.leastSquareMethods = leastSquareMethods;
    }
    /**
     * 获取当前时间
     * @param physicalGraph
     * @return
     */
    public int getTime(PhysicalGraph physicalGraph){
        return physicalGraph.t;
    }

    /**
     * 预测当前物理网络的某个节点是否符合迁移条件
     * @param physicalGraph
     * @return
     */
    public boolean overUtilizedHostDetection(PhysicalGraph physicalGraph,int node){
        int t = this.getTime(physicalGraph);
        if(physicalGraph.nodeLoad[node].cpu<physicalGraph.NodeCapacity[node].cpu*hotThreshold){
            return false;
        }
        for (int k = 0; k <6 ; k++) {
            if(getPredictCpuResourse(physicalGraph,node,t+k)<physicalGraph.NodeCapacity[node].cpu*hotThreshold){
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
            if(getPredictCpuResourse(physicalGraph,node,t+k)>physicalGraph.NodeCapacity[node].cpu*coldThreshold){
                return false;
            }
        }
        return true;
    }


    /**
     * 得到某一时刻某台物理机的预测CPU资源值
     * @param physicalGraph
     * @param node
     * @param t
     * @return
     */
    public double getPredictCpuResourse(PhysicalGraph physicalGraph,int node ,int t){
            return leastSquareMethods.get(node).fit(t);
    }
}
