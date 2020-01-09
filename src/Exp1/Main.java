package Exp1;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static PhysicalGraph physicalGraph = new PhysicalGraph();
    public static VirtualGraph virtualGraphs[] = new VirtualGraph[50];
    public static Util util = new Util();
    public static int VGnum = 40;

    public static void main(String[] args) {
        for (int i = 0; i <VGnum ; i++) {
            virtualGraphs[i] = new VirtualGraph(i);
        }
        //构建物理网络拓扑
        util.ConstructPhysicalGraph(physicalGraph);
        //构建虚拟网络拓扑
        util.ConstructVirtualGraph(virtualGraphs);
        //构建虚拟网络到物理网络的映射
        for (int i = 0; i <VGnum ; i++) {
            util.Mapping(physicalGraph,virtualGraphs[i]);
        }
        //生成物理网络拓扑中颜色完全图
        util.ConstructPhysicalCompleteGraph(physicalGraph);
        //计算此时发生过载的节点
        util.CalTemperature(physicalGraph);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.temperature[i].temperature!=0.0)
                System.out.println("物理机"+physicalGraph.temperature[i].PM+"过载");
        }
        Transfer transfer = new Transfer(physicalGraph,virtualGraphs);
        transfer.Migration(physicalGraph);
        //一直运行这个系统，不断更新某些虚拟机的参数，每个一段时间检查物理机过载情况，并迁移
        int i = 0;
        Random random = new Random();
        while (true){
            try {
                Thread.sleep(200);
                i++;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int VG_num = random.nextInt(VGnum);
            int id = random.nextInt(4);
            double cpu = Math.random()*(80-50)+50;
            double mem = Math.random()*(12-8)+8;
            util.refreshVNode(id,cpu,mem,physicalGraph,virtualGraphs[VG_num]);
            if(i%10==0){
                util.CalTemperature(physicalGraph);
                for (int j = 0; j <physicalGraph.Node ; j++) {
                    if(physicalGraph.temperature[j].temperature!=0.0)
                        System.out.println("物理机"+physicalGraph.temperature[j].PM+"过载");
                }
                transfer.Migration(physicalGraph);
            }
        }
//        System.out.println("physicalgraph edge"+physicalGraph.Edge);
//        System.out.println("physicalgraph node"+physicalGraph.Node);

//        for (int i = 0; i <physicalGraph.Node ; i++) {
//            for (int j = 0; j <physicalGraph.Node ; j++) {
//                System.out.println("物理边"+i+"-"+j+"权重:"+physicalGraph.EdgeCapacity[i][j]);
//            }
//        }
//        for (int i = 0; i <physicalGraph.Node ; i++) {
//            System.out.println("物理点"+i+":"+physicalGraph.NodeCapacity[i].cpu);
//        }

//        for (int i = 0; i <VGnum ; i++) {
//            System.out.println("第"+i+"个虚拟网络");
//            System.out.println("虚拟节点数："+virtualGraphs[i].Node);
//            System.out.println("虚拟边数："+virtualGraphs[i].Edge);
//            for (int j = 0; j <virtualGraphs[i].Node ; j++) {
//                for (int k = 0; k <virtualGraphs[i].Node ; k++) {
//                    System.out.println("边"+j+"-"+k+"权重:"+virtualGraphs[i].EdgeCapacity[j][k]);
//                }
//            }
//            for (int j = 0; j <virtualGraphs[i].Node ; j++) {
//                System.out.println("点"+j+"权重"+virtualGraphs[i].NodeCapacity[j].cpu);
//            }
//            for (int j = 0; j <virtualGraphs[i].Node ; j++) {
//                System.out.println("虚拟节点"+j+"映射到物理节点"+virtualGraphs[i].VN2PN[j]);
//            }
//            for (int j = 0; j <virtualGraphs[i].Node; j++) {
//                for (int k = 0; k <virtualGraphs[i].Node ; k++) {
//                    if(virtualGraphs[i].EdgeCapacity[j][k]!=-1){
//                        System.out.println("虚拟边"+j+"-"+k+"映射到物理边");
//                        System.out.println("起始物理点："+virtualGraphs[i].VN2PN[j]);
//                        for(Object o:virtualGraphs[i].VE2PE[j][k]){
//                            System.out.printf(o+" ");
//                        }
//                        System.out.println("终止物理点："+virtualGraphs[i].VN2PN[k]);
//                    }
//
//                }
//            }
        }
//        while(true){
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("虚拟网络i迁移节点j到物理机k");
//            int i = scanner.nextInt();
//            int j = scanner.nextInt();
//            int k = scanner.nextInt();
//            Transfer transfer = new Transfer(physicalGraph,virtualGraphs[i]);
//            transfer.TransferVirtualNode(j,k);
//            System.out.println("虚拟节点数："+virtualGraphs[i].Node);
//            System.out.println("虚拟边数："+virtualGraphs[i].Edge);
//            for ( j = 0; j <virtualGraphs[i].Node ; j++) {
//                for ( k = 0; k <virtualGraphs[i].Node ; k++) {
//                    System.out.println("边"+j+"-"+k+"权重:"+virtualGraphs[i].EdgeCapacity[j][k]);
//                }
//            }
//            for (j = 0; j <virtualGraphs[i].Node ; j++) {
//                System.out.println("点"+j+"权重"+virtualGraphs[i].NodeCapacity[j].cpu);
//            }
//            for ( j = 0; j <virtualGraphs[i].Node ; j++) {
//                System.out.println("虚拟节点"+j+"映射到物理节点"+virtualGraphs[i].VN2PN[j]);
//            }
//            for (j = 0; j <virtualGraphs[i].Node; j++) {
//                for ( k = 0; k <virtualGraphs[i].Node ; k++) {
//                    if(virtualGraphs[i].EdgeCapacity[j][k]!=-1){
//                        System.out.println("虚拟边"+j+"-"+k+"映射到物理边");
//                        System.out.println("起始物理点："+virtualGraphs[i].VN2PN[j]);
//                        for(Object o:virtualGraphs[i].VE2PE[j][k]){
//                            System.out.printf(o+" ");
//                        }
//                        System.out.println("终止物理点："+virtualGraphs[i].VN2PN[k]);
//                    }
//
//                }
//            }
//
//
//        }
    //}
}
