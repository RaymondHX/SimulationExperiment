package Exp1;


import Exp4.LeastSquareMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Transfer {
    public PhysicalGraph physicalGraph;
    public VirtualGraph[] virtualGraphs;
    public double migrationCost = 0;
    public double communcationCost = 0;
    public int migrationTime = 0;
    List<LeastSquareMethod> leastSquareMethods;
    double dis[];
    Util util = new Util();

    public  Transfer(PhysicalGraph physicalGraph, VirtualGraph virtualGraphs[],List<LeastSquareMethod> leastSquareMethods){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
        this.leastSquareMethods = leastSquareMethods;
    }

    //把一个虚拟机迁移到另一台物理机上
    public void TransferVirtualNode(int VGnum,int virtualNode,int newPhysicalNode){
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
        //更新颜色图
        util.updatePhysicalCompleteGraph(physicalGraph,oldPhysicalNode,newPhysicalNode);

    }


    //选出需要迁移的节点和迁移到的节点
    public void migration(PhysicalGraph physicalGraph){
        Predict1 predict1 = new Predict1(leastSquareMethods);
        //按降序对temprature排序
        Arrays.sort(physicalGraph.temperature);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.temperature[i].temperature!=0.0&&predict1.overUtilizedHostDetection(physicalGraph,physicalGraph.temperature[i].PM)){
                communcationCost+=util.calCommunCost(physicalGraph);
                Queue<VNode> queue = util.VMChoose(physicalGraph, physicalGraph.temperature[i].PM);
                if(queue.size()==0){
                    System.out.println("未选出虚拟机");
                    System.exit(-1);
                }
  //              System.out.println(queue.size());
                while(!queue.isEmpty()){
                    VNode vNode = queue.poll();
                    //找到这个虚拟机所处的物理机
                    int PMid = virtualGraphs[vNode.VGnum].VN2PN[vNode.id];
                    //System.out.println("迁移物理机"+PMid+"上的虚拟机");
                    int newPM = 0;
                    double min_dis = Double.MAX_VALUE;
                    boolean migrated = false;
                    dis= util.FindMinPath(physicalGraph,PMid);
                    //首先找绿色的边迁移，有多条时选择最短的
                    for (int j = 0; j <physicalGraph.Node ; j++) {
                        //System.out.println(dis[j]);
                        if(physicalGraph.Color[PMid][j].equals("green")
                                &&(physicalGraph.nodeLoad[j].cpu+vNode.load.cpu)/physicalGraph.NodeCapacity[j].cpu<0.8){
                            if(dis[j]<min_dis){
                                min_dis = dis[j];
                                newPM = j;
                                migrated = true;
                            }
                        }
                    }
                    if(migrated){
                        TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                    }

                    else {
                        //没有绿边时选择黄边
                        for (int j = 0; j <physicalGraph.Node ; j++) {
                            if(physicalGraph.Color[PMid][j].equals("yellow")
                                    &&(physicalGraph.nodeLoad[j].cpu+vNode.load.cpu)/physicalGraph.NodeCapacity[j].cpu<0.8){
                                if(dis[j]<min_dis){
                                    min_dis = dis[j];
                                    newPM = j;
                                    migrated = true;
                                }
                            }
                        }
                        if (migrated){
                            TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                        }
                        else {
                            //没有黄边时选择蓝边
                            for (int j = 0; j <physicalGraph.Node ; j++) {
                                if(physicalGraph.Color[PMid][j].equals("blue")
                                        &&(physicalGraph.nodeLoad[j].cpu+vNode.load.cpu)/physicalGraph.NodeCapacity[j].cpu<0.8){
                                    if(dis[j]<min_dis){
                                        min_dis = dis[j];
                                        newPM = j;
                                        migrated = true;
                                    }
                                }
                            }
                            if(migrated){
                                TransferVirtualNode(vNode.VGnum,vNode.id,newPM);
                            }

                        }
                    }

                }
            }
        }
    }

    /**
     * 迁移冷节点
     * @param physicalGraph
     */
    public void migrateColdSpot(PhysicalGraph physicalGraph){
        for (int i = 0; i <physicalGraph.Node ; i++) {
            //这是一个冷节点
            if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu<0.2&&i%10!=0){
                dis= util.FindMinPath(physicalGraph,i);
                for (int j = 0; j <physicalGraph.Node ; j++) {
                    if(physicalGraph.Color[i][j].equals("yellow")&&
                            (physicalGraph.nodeLoad[j].cpu+physicalGraph.nodeLoad[i].cpu)/physicalGraph.NodeCapacity[j].cpu<0.8){
                        List<VNode> VMlist = physicalGraph.VMInPM[i];
                        List<VNode> temp = new ArrayList<>();
                        for (int k = 0; k <VMlist.size() ; k++) {
                            temp.add(new VNode(VMlist.get(k).id,VMlist.get(k).load,VMlist.get(k).VGnum));
                        }
                        for (int k = 0; k <temp.size() ; k++) {
                            TransferVirtualNode(temp.get(k).VGnum,temp.get(k).id,j);
                        }
                    }
                }
            }
        }
    }


    public double getMigrationCost(){
        return migrationCost;
    }

}
