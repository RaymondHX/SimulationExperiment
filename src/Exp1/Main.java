package Exp1;

import Exp2.Bucket;
import Exp2.GA_Algorithm;
import Exp3.NewTransfer;
import Exp4.LeastSquareMethod;
import Exp4.Transfer4;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int VGnum = 500;
    public static PhysicalGraph physicalGraph1 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs1[] = new VirtualGraph[VGnum];
    public static PhysicalGraph physicalGraph2 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs2[] = new VirtualGraph[VGnum];
    public static PhysicalGraph physicalGraph3 = new PhysicalGraph();
    public static VirtualGraph virtualGraphs3[] = new VirtualGraph[VGnum];
    public static List<LeastSquareMethod> leastSquareMethods;
    public static Util util = new Util();
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
        leastSquareMethods = getSquareMethod();
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

        Transfer transfer = new Transfer(physicalGraph1,virtualGraphs1,leastSquareMethods);
        for (int t = 0; t <30 ; t++) {
            for (int j = 0; j <VGnum ; j++) {
                virtualGraphs1[j].updateVirtualGraph(t);
            }
            physicalGraph1.updatePhysicalGraph(virtualGraphs1);
        }
        util.ConstructPhysicalCompleteGraph(physicalGraph1);
        for (int t = 30; t <278 ; t++) {
            for (int j = 0; j <VGnum ; j++) {
                virtualGraphs1[j].updateVirtualGraph(t);
            }
            physicalGraph1.updatePhysicalGraph(virtualGraphs1);
            util.ConstructPhysicalCompleteGraph(physicalGraph1);
            util.calTemperature(physicalGraph1);
            transfer.migration(physicalGraph1);
        }
        System.out.println("通信开销"+transfer.communcationCost);
        System.out.println("迁移开销："+transfer.migrationCost);
        System.out.println("迁移次数："+transfer.migrationTime+"\n");

        Transfer4 transfer4 = new Transfer4(physicalGraph2,virtualGraphs2);
        for (int t = 0; t <30 ; t++) {
            for (int j = 0; j <VGnum ; j++) {
                virtualGraphs2[j].updateVirtualGraph(t);
            }
            physicalGraph2.updatePhysicalGraph(virtualGraphs2);
        }
        for (int t = 30; t <278 ; t++) {
            for (int j = 0; j <VGnum ; j++) {
                virtualGraphs2[j].updateVirtualGraph(t);
            }
            physicalGraph2.updatePhysicalGraph(virtualGraphs2);

            transfer4.nodeChoose();
        }
        System.out.println("通信开销"+transfer4.communcationCost);
        System.out.println("迁移开销："+transfer4.migrationCost);
        System.out.println("迁移次数："+transfer4.migrationTime+"\n");
//        //计算此时发生过载的节点
//        util.CalTemperature(physicalGraph1);
//
//        Transfer transfer = new Transfer(physicalGraph1, virtualGraphs1);
//        while (!weatherStable(physicalGraph1)){
//            transfer.Migration(physicalGraph1);
//            util.CalTemperature(physicalGraph1);
//        }
//        transfer.migrateColdSpot(physicalGraph1);
//        System.out.println("能耗开销："+util.calEnergyConsumption(physicalGraph1));
//        System.out.println("通信开销"+transfer.communcationCost);
//        System.out.println("迁移开销："+transfer.getMigrationCost());
//        System.out.println("迁移次数："+transfer.migrationTime+"\n");
//
//
//
//        NewTransfer newTransfer = new NewTransfer(physicalGraph3,virtualGraphs3);
//        util.CalTemperature(physicalGraph3);
//        while(!weatherStable(physicalGraph3)){
//            newTransfer.Migration(physicalGraph3);
//            util.CalTemperature(physicalGraph3);
//        }
//
//        System.out.println("能耗开销："+util.calEnergyConsumption(physicalGraph3));
//        System.out.println("通信开销："+newTransfer.communicationCost);
//        System.out.println("迁移开销："+newTransfer.migrationCost);
//        System.out.println("迁移次数："+newTransfer.migrationTime+"\n");

//        GA_Algorithm ga_algorithm = new GA_Algorithm(physicalGraph2,virtualGraphs2,VGnum*2,500);
//        ga_algorithm.FillVM_Index();
//        Bucket bucket = ga_algorithm.FindBestCodeWithGA();
    }

    public static boolean weatherStable(PhysicalGraph physicalGraph){
        boolean result = true;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.nodeLoad[i].cpu>physicalGraph.NodeCapacity[i].cpu*0.8){
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 计算每个物理节点的预测函数
     * @return
     */
    public static List<LeastSquareMethod> getSquareMethod(){
        Util util = new Util();
        PhysicalGraph physicalGraph = new PhysicalGraph();
        VirtualGraph[] virtualGraphs = new VirtualGraph[VGnum];
        util.ConstructPhysicalGraph(physicalGraph);
        for (int i = 0; i <VGnum ; i++) {
            virtualGraphs[i] = new VirtualGraph(i);
        }
        util.ConstructVirtualGraph(virtualGraphs);
        for (int i = 0; i <VGnum ; i++) {
            util.Mapping(physicalGraph,virtualGraphs[i]);
        }
        double[] x = new double[288];
        for (int i = 0; i <288 ; i++) {
            x[i] = i;
        }
        List<LeastSquareMethod> leastSquareMethods = new ArrayList<>();
        for (int i = 0; i <physicalGraph.Node ; i++) {
            double[] data = new double[288];
            for (int t = 0; t <288 ; t++) {
                for (int j = 0; j <VGnum ; j++) {
                    virtualGraphs[j].updateVirtualGraph(t);
                }
                physicalGraph.updatePhysicalGraph(virtualGraphs);
                data[t] = physicalGraph.nodeLoad[i].cpu;
            }
            LeastSquareMethod leastSquareMethod = new LeastSquareMethod(x,data,5);
            leastSquareMethods.add(leastSquareMethod);
        }
        System.out.println(leastSquareMethods.get(3).fit(280));
        return leastSquareMethods;
    }
}
