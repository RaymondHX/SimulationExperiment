package Exp2;



import java.util.List;

public class PhysicalGraph {
    //节点数量
    protected int Node;
    //物理链路数量
    protected int Edge;
    //节点的负载，cpu和mem两个部分
    protected Load NodeCapacity[];
    //链路的容量，定义为信道容量
    protected  double EdgeCapacity[][];

    //每个物理机上都有哪些虚拟机
    protected List<VNode> VMInPM[];
}
