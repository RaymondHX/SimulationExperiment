package Exp4;

import Exp1.PhysicalGraph;
import Exp1.VirtualGraph;

import java.util.List;

public class Predict {
    List<LeastSquareMethod> leastSquareMethods;
    private double hotThreshold = 0.8;
    private double coldThreshold = 0.2;
    Predict(List<LeastSquareMethod> leastSquareMethods){
        this.leastSquareMethods = leastSquareMethods;
    }
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
            if(getPredictCpuResourse(physicalGraph,node,t+k)<physicalGraph.nodeLoad[node].cpu*0.8){
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
            if(getPredictCpuResourse(physicalGraph,node,t+k)>physicalGraph.nodeLoad[node].cpu*0.2){
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
        if(t>288){
            return leastSquareMethods.get(node).fit(t-288);
        }
        else {
            return leastSquareMethods.get(node).fit(t);
        }
    }
}
