package Exp3;

import Exp1.PhysicalGraph;
import Exp1.Util;
import Exp1.VNode;
import Exp1.VirtualGraph;

import java.util.List;


public class NewTransfer {
    public PhysicalGraph physicalGraph;
    public VirtualGraph[] virtualGraphs;
    public double migrationCost = 0;
    public int migrationTime = 0;


    public  NewTransfer(PhysicalGraph physicalGraph, VirtualGraph virtualGraphs[]){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
    }
    public void TransferVirtualNode(int VGnum,int virtualNode,int newPhysicalNode){
        migrationTime++;
        int oldPhysicalNode = virtualGraphs[VGnum].VN2PN[virtualNode];
        VNode vNode = null;
        //从原物理机上删除这个虚拟机
        for (int i = 0; i <physicalGraph.VMInPM[oldPhysicalNode].size() ; i++) {
            if(physicalGraph.VMInPM[oldPhysicalNode].get(i).id == virtualNode){
                vNode = physicalGraph.VMInPM[oldPhysicalNode].get(i);
                physicalGraph.VMInPM[oldPhysicalNode].remove(i);
                physicalGraph.nodeLoad[oldPhysicalNode].cpu -= vNode.load.cpu;
                physicalGraph.nodeLoad[oldPhysicalNode].mem -= vNode.load.mem;
                //计算一次迁移开销
                migrationCost  += vNode.load.mem;
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


    //选出需要迁移的节点和迁移到的节点
    public void Migration(PhysicalGraph physicalGraph){
            for (int i = 0; i <physicalGraph.Node ; i++) {
                if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu>0.8){
                    VNode vnode = findMaxCpuVirtualMachine(physicalGraph.VMInPM[i]);
                    //找出图中哪些节点满足迁移条件
                    for (int j = 0; j <physicalGraph.Node ; j++) {
                        if((physicalGraph.nodeLoad[j].cpu+vnode.load.cpu)/physicalGraph.NodeCapacity[j].cpu<0.8&&i!=j){
                            TransferVirtualNode(vnode.VGnum,vnode.id,j);
                            break;
                        }
                    }
                }
            }
    }


    public VNode findMaxCpuVirtualMachine(List<VNode> nodes){
        double cpu = 0;
        VNode result = null;
        for (VNode node:nodes) {
            if(node.load.cpu>cpu){
                cpu = node.load.cpu;
                result = node;
            }
        }
        return result;
    }
}
