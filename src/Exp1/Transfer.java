package Exp1;

import Exp1.PhysicalGraph;
import Exp1.VNode;
import Exp1.VirtualGraph;

import java.util.Arrays;
import java.util.Queue;

public class Transfer {
    public PhysicalGraph physicalGraph;
    public VirtualGraph[] virtualGraphs;
    Util util = new Util();

    public  Transfer(PhysicalGraph physicalGraph, VirtualGraph virtualGraphs[]){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
    }

    //把一个虚拟机迁移到另一台物理机上
    public void TransferVirtualNode(int VGnum,int virtualNode,int newPhysicalNode){
        System.out.println("迁移第"+VGnum+"个虚拟网络上"+"第"+virtualNode+"个"+"虚拟机"+"到物理机"+newPhysicalNode);
        int oldPhysicalNode = virtualGraphs[VGnum].VN2PN[virtualNode];
        VNode vNode = null;
        //从原物理机上删除这个虚拟机
        for (int i = 0; i <physicalGraph.VMInPM[oldPhysicalNode].size() ; i++) {
            if(physicalGraph.VMInPM[oldPhysicalNode].get(i).id == virtualNode){
                vNode = physicalGraph.VMInPM[oldPhysicalNode].get(i);
                physicalGraph.VMInPM[oldPhysicalNode].remove(i);
                physicalGraph.NodeCapacity[oldPhysicalNode].cpu -= virtualGraphs[VGnum].NodeCapacity[virtualNode].cpu;
                physicalGraph.NodeCapacity[oldPhysicalNode].mem -= virtualGraphs[VGnum].NodeCapacity[virtualNode].mem;
                break;
            }
        }
        //在新物理机上添加这个虚拟机
        physicalGraph.VMInPM[newPhysicalNode].add(vNode);
        physicalGraph.NodeCapacity[newPhysicalNode].cpu += virtualGraphs[VGnum].NodeCapacity[virtualNode].cpu;
        physicalGraph.NodeCapacity[newPhysicalNode].mem += virtualGraphs[VGnum].NodeCapacity[virtualNode].mem;

        //修改虚拟网络到物理网络的映射
        virtualGraphs[VGnum].VN2PN[virtualNode] = newPhysicalNode;
        //更新颜色图
        util.updatePhysicalCompleteGraph(physicalGraph,oldPhysicalNode,newPhysicalNode);

        //        for (int i = 0; i <virtualGraphs[PGnum].Node ; i++) {
//            virtualGraphs[PGnum].VE2PE[virtualNode][i].clear();
//            virtualGraphs[PGnum].VE2PE[i][virtualNode].clear();
//        }
//        Util util = new Util();
//        for (int i = 0; i <virtualGraphs[PGnum].Node ; i++) {
//            if(virtualGraphs[PGnum].EdgeCapacity[virtualNode][i]!=0&&i!=virtualNode){
//             int to = virtualGraphs[PGnum].VN2PN[i];
////             util.findPath(physicalGraph,newPhysicalNode,to,virtualGraphs[PGnum].VE2PE[virtualNode][i]);
////             util.findPath(physicalGraph,to,newPhysicalNode,virtualGraphs[PGnum].VE2PE[i][virtualNode]);
//
//            }
//        }
    }


    //选出需要迁移的节点和迁移到的节点
    public void Migration(PhysicalGraph physicalGraph){
        //按降序对temprature排序
        Arrays.sort(physicalGraph.temperature);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.temperature[i].temperature!=0.0){
                System.out.println("第"+physicalGraph.temperature[i].PM+"物理机发生过载");
                Queue<VNode> queue = util.VMChoose(physicalGraph, physicalGraph.temperature[i].PM);
                while(!queue.isEmpty()){
                    VNode vNode = queue.poll();
                    //找到这个虚拟机所处的物理机
                    int PMid = virtualGraphs[vNode.VGnum].VN2PN[vNode.id];
                    System.out.println("迁移物理机"+PMid+"上的虚拟机");
                    int newPM = 0;
                    double min_dis = Double.MAX_VALUE;
                    boolean migrated = false;
                    double dis[] = util.FindMinPath(physicalGraph,PMid);
//                    for (int j = 0; j <dis.length ; j++) {
//                        System.out.println(dis[j]);
//                    }
                    //首先找绿色的边迁移，有多条时选择最短的
                    for (int j = 0; j <physicalGraph.Node ; j++) {
                        //System.out.println(dis[j]);
                        if(physicalGraph.Color[PMid][j].equals("green")){
                            if(dis[j]<min_dis){
                                min_dis = dis[j];
                                newPM = j;
                                migrated = true;
                            }
                        }
                    }
                    if(migrated)
                        TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                    else {
                        //没有绿边时选择黄边
                        for (int j = 0; j <physicalGraph.Node ; j++) {
                            if(physicalGraph.Color[PMid][j].equals("yellow")){
                                if(dis[j]<min_dis){
                                    min_dis = dis[j];
                                    newPM = j;
                                    migrated = true;
                                }
                            }
                        }
                        if (migrated)
                            TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                        else {
                            //没有黄边时选择蓝边
                            for (int j = 0; j <physicalGraph.Node ; j++) {
                                if(physicalGraph.Color[PMid][j].equals("blue")){
                                    if(dis[j]<min_dis){
                                        min_dis = dis[j];
                                        newPM = j;
                                        migrated = true;
                                    }
                                }
                                if(migrated)
                                    TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                            }
                        }
                    }

                }
            }
        }
    }


}
