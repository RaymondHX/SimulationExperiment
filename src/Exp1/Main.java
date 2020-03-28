package Exp1;


import Exp2.Bucket;
import Exp2.GA_Algorithm;
import Exp3.NewTransfer;

public class Main {
    public static PhysicalGraph physicalGraph1 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs1[] = new VirtualGraph[50];
    public static PhysicalGraph physicalGraph2 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs2[] = new VirtualGraph[50];
    public static PhysicalGraph physicalGraph3 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs3[] = new VirtualGraph[50];
    public static Util util = new Util();
    public static int VGnum = 50;
    public static double totalCostBefore = 0;
    public static double totlalCostAfter = 0;
    public static void main(String[] args) {
        for (int i = 0; i < VGnum; i++) {
            virtualGraphs1[i] = new VirtualGraph(i);
        }
        for (int i = 0; i < VGnum; i++) {
            virtualGraphs2[i] = new VirtualGraph(i);
        }
        for (int i = 0; i < VGnum; i++) {
            virtualGraphs3[i] = new VirtualGraph(i);
        }
        //构建物理网络拓扑
        util.ConstructPhysicalGraph(physicalGraph1);
        util.ConstructPhysicalGraph(physicalGraph2);
        util.ConstructPhysicalGraph(physicalGraph3);
        util.updatePhysicalGraphNode(physicalGraph1,physicalGraph2,physicalGraph3);
        //构建虚拟网络拓扑
        util.ConstructVirtualGraph(virtualGraphs1);
        util.ConstructVirtualGraph(virtualGraphs2);
        util.ConstructVirtualGraph(virtualGraphs3);
        util.updateVirtualNode(virtualGraphs1,virtualGraphs2,virtualGraphs3);
        //构建虚拟网络到物理网络的映射
        for (int i = 0; i < VGnum; i++) {
            util.Mapping(physicalGraph1, virtualGraphs1[i],physicalGraph2,virtualGraphs2[i],physicalGraph3,virtualGraphs3[i]);
        }
        //生成物理网络拓扑中颜色完全图
        util.ConstructPhysicalCompleteGraph(physicalGraph1);
        //计算此时发生过载的节点
        util.CalTemperature(physicalGraph1);

//        System.out.println("原能耗开销："+util.calEnergyConsumption(physicalGraph1));
        System.out.println("通信开销"+util.calCommunCost(physicalGraph1));


        Transfer transfer = new Transfer(physicalGraph1, virtualGraphs1);
        while (!weatherStable(physicalGraph1)){
            transfer.Migration(physicalGraph1);
            util.CalTemperature(physicalGraph1);
        }
        transfer.migrateColdSpot(physicalGraph1);
        System.out.println("能耗开销："+util.calEnergyConsumption(physicalGraph1));
        System.out.println("迁移开销："+transfer.getMigrationCost());
        System.out.println("迁移次数："+transfer.migrationTime+"\n");


        System.out.println("通信开销："+util.calCommunCost(physicalGraph3));
        NewTransfer newTransfer = new NewTransfer(physicalGraph3,virtualGraphs3);
        util.CalTemperature(physicalGraph3);
        while(!weatherStable(physicalGraph3)){
            newTransfer.Migration(physicalGraph3);
            util.CalTemperature(physicalGraph3);
        }

        System.out.println("能耗开销："+util.calEnergyConsumption(physicalGraph3));
        System.out.println("迁移开销："+newTransfer.migrationCost);
        System.out.println("迁移次数："+newTransfer.migrationTime+"\n");

        GA_Algorithm ga_algorithm = new GA_Algorithm(physicalGraph2,virtualGraphs2,VGnum*2,20);
        ga_algorithm.FillVM_Index();
        Bucket bucket = ga_algorithm.FindBestCodeWithGA();
    }

    public static boolean weatherStable(PhysicalGraph physicalGraph){
        boolean result = true;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.temperature[i].temperature!=0){
                result = false;
                break;
            }
        }
        return result;
    }
}
