package Exp1;

import Exp1.Load;

import java.util.List;

public class PhysicalGraph {
    /**
     * 节点数量
     * */
    public int Node;
    /**
     * 物理链路数量
     */
    public int Edge;
    /**
     * 节点的容量，cpu和mem两个部分
     */
    public Load NodeCapacity[];
    /**
     * 链路的容量，定义为信道容量
     */
    public double EdgeCapacity[][];
    /**
     * 节点的载荷
     */
    public  Load nodeLoad[];
    /**
     * 物理网络中颜色图
     */
    public String Color[][];

    public TempMapping temperature[];

    /**
     * 每个物理机上都有哪些虚拟机
     */
    public List<VNode> VMInPM[];

    boolean [][] path;

    public PhysicalGraph() {

    }

    /**
     * 根据虚拟网络中虚拟机资源变化更新物理机资源变化
     * @param virtualGraphs
     * @param 
     */
    public void updatePhysicalGraph(VirtualGraph[] virtualGraphs){
        for (int i = 0; i <this.Node ; i++) {
            double tempCpu = 0;
            double tempMem = 0;
            for (int j = 0; j <this.VMInPM[i].size() ; j++) {
                int VGNum = this.VMInPM[i].get(j).VGnum;
                int id = this.VMInPM[i].get(j).id;
                tempCpu += virtualGraphs[VGNum].NodeCapacity[id].cpu;
                tempMem += virtualGraphs[VGNum].NodeCapacity[id].mem;
            }
            this.nodeLoad[i].cpu = tempCpu;
            this.nodeLoad[i].mem = tempMem;
        }
    }

}
