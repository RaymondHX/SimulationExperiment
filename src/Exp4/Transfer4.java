package Exp4;

import Exp1.PhysicalGraph;
import Exp1.VirtualGraph;

public class Transfer4 {
    PhysicalGraph physicalGraph;
    VirtualGraph[] virtualGraphs;
    Transfer4(PhysicalGraph physicalGraph, VirtualGraph[] virtualGraphs){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
    }

    /**
     * 在当前时刻，选出满足迁移条件的节点并找到它要迁移到的节点完成迁移
     */
    public void nodeChoose(){
        Predict predict = new Predict();
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(predict.overUtilizedHostDetection(physicalGraph,i)){
                double max = Double.MIN_VALUE;
                int vmSize = physicalGraph.VMInPM[i].size();
                for (int j = 0; j <vmSize ; j++) {
                    // todo
                }
            }
        }
    }
}
