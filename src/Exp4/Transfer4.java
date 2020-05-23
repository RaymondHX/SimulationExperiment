package Exp4;

import Exp1.*;

import java.util.ArrayList;
import java.util.List;

public class Transfer4 {
    PhysicalGraph physicalGraph;
    VirtualGraph[] virtualGraphs;
    Util util = new Util();
    public double migrationCost = 0;
    public int migrationTime = 0;
    public double communicationCost = 0;
    double dis[];
    public Transfer4(PhysicalGraph physicalGraph,VirtualGraph[] virtualGraphs){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
    }

    //把一个虚拟机迁移到另一台物理机上
    public void transferVirtualNode(int VGnum,int virtualNode,int newPhysicalNode){
        migrationTime++;
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
                dis = util.FindMinPath(physicalGraph,oldPhysicalNode);
                migrationCost  += vNode.load.mem*dis[newPhysicalNode]*10;
                break;
            }
        }
        //在新物理机上添加这个虚拟机
        physicalGraph.VMInPM[newPhysicalNode].add(vNode);
        physicalGraph.nodeLoad[newPhysicalNode].cpu += vNode.load.cpu;
        physicalGraph.nodeLoad[newPhysicalNode].mem += vNode.load.mem;

        //修改虚拟网络到物理网络的映射
        virtualGraphs[VGnum].VN2PN[virtualNode] = newPhysicalNode;


    }

    /**
     * 在当前时刻，选出满足迁移条件的节点并找到它要迁移到的节点完成迁移
     */
    public void nodeChoose(){
        Predict4 predict4 = new Predict4();
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(predict4.overUtilizedHostDetection(physicalGraph,i)){
                communicationCost += util.calCommunCost(physicalGraph);
                while (predict4.overUtilizedHostDetection(physicalGraph,i)){
                    double max = Double.MIN_VALUE;
                    int transferVN = 0;
                    int vmSize = physicalGraph.VMInPM[i].size();
                    //选出需要迁移的虚拟机
                    for (int j = 0; j <vmSize ; j++) {
                        VNode vNode = physicalGraph.VMInPM[i].get(j);
                        double temp = RT(i,vNode)*vNode.load.mem;
                        if(temp>max){
                            transferVN = j;
                            max = temp;
                        }
                    }
                    //选出要迁移到的节点
                    double power = Double.MAX_VALUE;
                    int targetNode = 0;
                    for (int j = 0; j <physicalGraph.Node ; j++) {
                        //加入后仍然满足
                        if(physicalGraph.nodeLoad[j].cpu+physicalGraph.VMInPM[i].get(transferVN).load.cpu<physicalGraph.NodeCapacity[j].cpu*0.8){
                            if(physicalGraph.nodePower[j]<power){
                                power = physicalGraph.nodePower[j];
                                targetNode = j;
                            }
                        }
                    }
                    transferVirtualNode(physicalGraph.VMInPM[i].get(transferVN).VGnum,physicalGraph.VMInPM[i].get(transferVN).id,targetNode);
                }
            }
        }
    }

    public double RT(int node, VNode vNode){
        double u = physicalGraph.nodeLoad[node].cpu-vNode.load.cpu;
        double h = physicalGraph.NodeCapacity[node].cpu;
        return Math.pow(u-h,2);
    }

    /**
     * 迁移冷节点
     * @param physicalGraph
     */
    public void migrateColdSpot(PhysicalGraph physicalGraph){
        Predict4 predict4 = new Predict4();
        for (int i = 0; i <physicalGraph.Node ; i++) {
            //这是一个冷节点
            if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu<0.2&&i%10!=0&&physicalGraph.nodeLoad[i].cpu!=0
                    &&predict4.underUtilizedHostDetection(physicalGraph,i)){
                dis= util.FindMinPath(physicalGraph,i);
                for (int j = 0; j <physicalGraph.Node ; j++) {
                    if((physicalGraph.nodeLoad[j].cpu+physicalGraph.nodeLoad[i].cpu)/physicalGraph.NodeCapacity[j].cpu<0.8){
                        List<VNode> VMlist = physicalGraph.VMInPM[i];
                        List<VNode> temp = new ArrayList<>();
                        for (int k = 0; k <VMlist.size() ; k++) {
                            temp.add(new VNode(VMlist.get(k).id,VMlist.get(k).load,VMlist.get(k).VGnum));
                        }
                        for (int k = 0; k <temp.size() ; k++) {
                            transferVirtualNode(temp.get(k).VGnum,temp.get(k).id,j);
                        }
                    }
                }
            }
        }
    }
}
