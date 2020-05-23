package Exp4;

import Exp1.PhysicalGraph;
import Exp1.VirtualGraph;

import java.util.List;

public class Predict4 {
    private double hotThreshold = 0.8;
    private double coldThreshold = 0.2;
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
//        for (int k = 0; k <6 ; k++) {
//            double temp = MUP.UP(physicalGraph,k+1,t+k+1,node);
//            if(temp<physicalGraph.NodeCapacity[node].cpu*hotThreshold){
//                return false;
//            }
//        }
        else {
            return true;
        }


    }

    /**
     * 预测当前物理网络的某个节点是否满足coldspot
     * @param physicalGraph
     * @return
     */
    public boolean underUtilizedHostDetection(PhysicalGraph physicalGraph,int node){
        int t = this.getTime(physicalGraph);
        if(physicalGraph.nodeLoad[node].cpu>physicalGraph.NodeCapacity[node].cpu*coldThreshold){
            return false;
        }
        else {
            return true;
        }
//        for (int k = 0; k <6 ; k++) {
//            double temp = MUP.UP(physicalGraph,k+1,t,node);
//            if(temp>physicalGraph.NodeCapacity[node].cpu*coldThreshold){
//                return false;
//            }
//        }
    }


}
