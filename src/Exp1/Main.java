package Exp1;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static PhysicalGraph physicalGraph = new PhysicalGraph();
    public static VirtualGraph virtualGraphs[] = new VirtualGraph[50];
    public static Util util = new Util();
    public static int VGnum = 35;
    public static double totalCostBefore = 0;
    public static double totlalCostAfter = 0;
    public static void main(String[] args) {
        for (int i = 0; i < VGnum; i++) {
            virtualGraphs[i] = new VirtualGraph(i);
        }
        //构建物理网络拓扑
        util.ConstructPhysicalGraph(physicalGraph);
        //构建虚拟网络拓扑
        util.ConstructVirtualGraph(virtualGraphs);
        //构建虚拟网络到物理网络的映射
        for (int i = 0; i < VGnum; i++) {
            util.Mapping(physicalGraph, virtualGraphs[i]);
        }
        //生成物理网络拓扑中颜色完全图
        util.ConstructPhysicalCompleteGraph(physicalGraph);
        //计算此时发生过载的节点
        util.CalTemperature(physicalGraph);
        for (int i = 0; i < physicalGraph.Node; i++) {
            if (physicalGraph.temperature[i].temperature != 0.0) {
                System.out.println("物理机" + physicalGraph.temperature[i].PM + "过载");
            }

        }
        System.out.println("原能耗开销："+util.calEnergyConsumption(physicalGraph));
        System.out.println("原SLAV:"+util.calCommunCost(physicalGraph));


        Transfer transfer = new Transfer(physicalGraph, virtualGraphs);
        while (!weatherStable(physicalGraph)){
            transfer.Migration(physicalGraph);
            util.CalTemperature(physicalGraph);
        }
        System.out.println("迁移开销："+transfer.getMigrationCost());

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
