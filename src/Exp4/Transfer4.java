package Exp4;

import Exp1.PhysicalGraph;
import Exp1.Util;
import Exp1.VNode;
import Exp1.VirtualGraph;

import java.util.List;

public class Transfer4 {
    PhysicalGraph physicalGraph;
    VirtualGraph[] virtualGraphs;
    List<LeastSquareMethod> leastSquareMethods;
    Util util = new Util();
    Transfer4(PhysicalGraph physicalGraph, VirtualGraph[] virtualGraphs, List<LeastSquareMethod> leastSquareMethods){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
        this.leastSquareMethods = leastSquareMethods;
    }

    //把一个虚拟机迁移到另一台物理机上
    public void TransferVirtualNode(int VGnum,int virtualNode,int newPhysicalNode){
       // migrationTime++;
        int oldPhysicalNode = virtualGraphs[VGnum].VN2PN[virtualNode];
        VNode vNode = null;
        //从原物理机上删除这个虚拟机
        for (int i = 0; i <physicalGraph.VMInPM[oldPhysicalNode].size() ; i++) {
            if(physicalGraph.VMInPM[oldPhysicalNode].get(i).id == virtualNode&&physicalGraph.VMInPM[oldPhysicalNode].get(i).VGnum==VGnum){
                vNode = physicalGraph.VMInPM[oldPhysicalNode].get(i);
                physicalGraph.VMInPM[oldPhysicalNode].remove(i);
                physicalGraph.nodeLoad[oldPhysicalNode].cpu -= vNode.load.cpu;
                physicalGraph.nodeLoad[oldPhysicalNode].mem -= vNode.load.mem;
                //计算一次迁移开销
               // migrationCost  += vNode.load.mem*dis[newPhysicalNode]*10;
                break;
            }
        }
        //在新物理机上添加这个虚拟机
        physicalGraph.VMInPM[newPhysicalNode].add(vNode);
        physicalGraph.nodeLoad[newPhysicalNode].cpu += vNode.load.cpu;
        physicalGraph.nodeLoad[newPhysicalNode].mem += vNode.load.mem;

        //修改虚拟网络到物理网络的映射
        virtualGraphs[VGnum].VN2PN[virtualNode] = newPhysicalNode;
        //更新颜色图
        util.updatePhysicalCompleteGraph(physicalGraph,oldPhysicalNode,newPhysicalNode);


    }

    /**
     * 在当前时刻，选出满足迁移条件的节点并找到它要迁移到的节点完成迁移
     */
    public void nodeChoose(){
        Predict predict = new Predict(leastSquareMethods);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(predict.overUtilizedHostDetection(physicalGraph,i)){
                double max = Double.MIN_VALUE;
                double transferVN = 0;
                int vmSize = physicalGraph.VMInPM[i].size();
                for (int j = 0; j <vmSize ; j++) {
                    VNode vNode = physicalGraph.VMInPM[i].get(j);
                    double temp = RT(i,vNode)*vNode.load.mem;
                    if(temp>max){
                        transferVN = j;
                        max = temp;
                    }
                }

            }
        }
    }

    public double RT(int node, VNode vNode){
        double u = physicalGraph.nodeLoad[node].cpu-vNode.load.cpu;
        double h = physicalGraph.NodeCapacity[node].cpu;
        return Math.pow(u-h,2);
    }
}
