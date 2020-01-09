package Exp2;

public class GA_Algorithm {
    protected PhysicalGraph physicalGraph;
    protected VirtualGraph virtualGraphs[];
    //总的虚拟机数量
    protected int M = 0;
    //虚拟机在bucket code 里的编号和虚拟网络中编号的映射
    protected VNode[] VM_index;

    GA_Algorithm(PhysicalGraph physicalGraph,VirtualGraph[] virtualGraphs){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
    }

    //完成VM_index中的映射
    protected void FillVM_Index(){
        for (int i = 0; i <virtualGraphs.length ; i++) {
            M+= virtualGraphs[i].Node;
        }
        VM_index = new VNode[M];
        int k = 0;
        for (int i = 0; i <virtualGraphs.length ; i++) {
            for (int j = 0; j <virtualGraphs[i].Node ; j++) {
                VM_index[k] = new VNode(virtualGraphs[i].id,virtualGraphs[i].NodeCapacity[j],i);
            }
        }
    }

    
}
