package Exp2;

public class VirtualGraph {

    //当前虚拟网络编号
    protected int id;
    //虚拟网络中节点数
    protected int Node = 0;
    //虚拟网络中边数
    protected int Edge;
    //虚拟机到物理机的映射
    protected int VN2PN[];
    //虚拟网络中节点的负载
    protected Load NodeCapacity[];
    //虚拟网络中链路带宽
    protected int EdgeCapacity[][];
}
