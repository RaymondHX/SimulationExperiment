package Exp2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Util {
    public String physicalGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\PNet\\20.brite";
    public String virtualGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\VNet\\test.brite";
    public int VGnum = 40;
    public double cpu_mean = 100;
    public double cpu_square = 10;
    public double mem_mean = 10;
    public double mem_square = 1;
    public double edge_mean = 1;
    public double edge_square = 0.1;

    //物理节点的最大负载
    public double cpu_max = 300;
    //CPU资源过载的阈值
    public double hot_threshold = 0.8;


    //读入物理网络的相关信息
    public void ConstructPhysicalGraph(PhysicalGraph physicalGraph){
        BufferedReader reader = null;
        Random random = new Random();
        try {
            reader = new BufferedReader(new FileReader(physicalGraphPath));
            String temp = reader.readLine();
            while(temp.contains("Nodes:") == false)
                temp = reader.readLine();
            String Nodestr[] = temp.split(" ");
            physicalGraph.Node = Integer.parseInt(Nodestr[2]);
            physicalGraph.NodeCapacity = new Load[physicalGraph.Node];
            physicalGraph.EdgeCapacity = new double[physicalGraph.Node][physicalGraph.Node];
            physicalGraph.VMInPM = new List[physicalGraph.Node];

            for (int i = 0; i <physicalGraph.Node ; i++) {
                physicalGraph.NodeCapacity[i] = new Load();
                Arrays.fill(physicalGraph.EdgeCapacity[i],-1);
                physicalGraph.VMInPM[i] = new ArrayList<>();
            }
//
//            for (int i = 0; i < physicalGraph.Node; i ++){
//                temp = reader.readLine();
//
//                physicalGraph.NodeCapacity[i].cpu = Math.sqrt(cpu_square)*random.nextGaussian()+cpu_mean;
//                physicalGraph.NodeCapacity[i].mem = Math.sqrt(mem_square)*random.nextGaussian()+mem_mean;
//            }
            while(temp.contains("Edges:") == false)
                temp = reader.readLine();
            String Edgestr[] = temp.split(" ");
            physicalGraph.Edge = Integer.parseInt(Edgestr[2]);
            physicalGraph.EdgeCapacity = new double[physicalGraph.Node][physicalGraph.Node];
            for(int i = 0; i < physicalGraph.Node; i ++)
                Arrays.fill(physicalGraph.EdgeCapacity[i],-1);
            for(int i = 0; i < physicalGraph.Edge; i ++){
                temp = reader.readLine();
                String line[] = temp.split("\\t");
                int from = Integer.parseInt(line[1]);
                int to = Integer.parseInt(line[2]);
                double capcity = Math.sqrt(edge_square)*random.nextGaussian()+edge_mean;
                physicalGraph.EdgeCapacity[from][to] = capcity;
                physicalGraph.EdgeCapacity[to][from] = capcity;
            }
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("read from brite error!");
        }
        finally {
            if(reader != null) {
                try{
                    reader.close();
                }catch (IOException e1){

                }
            }
        }
    }


    //读入虚拟网络的相关信息
    public void ConstructVirtualGraph(VirtualGraph virtualGraph[]){
        for (int v = 0; v < VGnum; v ++) {
            BufferedReader reader = null;
            try {
                int ct = 0;
                reader = new BufferedReader(new FileReader(virtualGraphPath));
                String temp = reader.readLine();
                String Nodestr[] = temp.split(" ");
                virtualGraph[v].id = v;
                virtualGraph[v].Node = Integer.parseInt(Nodestr[0]);
                virtualGraph[v].NodeCapacity = new Load[virtualGraph[v].Node];
                virtualGraph[v].EdgeCapacity = new int[virtualGraph[v].Node][virtualGraph[v].Node];
                for(int i = 0; i < virtualGraph[v].Node; i ++){
                    Arrays.fill(virtualGraph[v].EdgeCapacity[i],-1);
                    virtualGraph[v].NodeCapacity[i] = new Load();
                }
                Random random = new Random();
                for (int i = 0; i < virtualGraph[v].Node; i ++){
                    temp = reader.readLine();
                    String line[] = temp.split(" ");
                    virtualGraph[v].NodeCapacity[i].cpu = Math.sqrt(cpu_square)*random.nextGaussian()+cpu_mean-80;
                    virtualGraph[v].NodeCapacity[i].mem = Math.sqrt(mem_square)*random.nextGaussian()+mem_mean-8;
                    //  virtualGraph[v].NodeCapacity[i] = virtualGraph[v].NodeCapacity[i];
                    for (int k=1; k < line.length; k=k+2)
                    {
                        virtualGraph[v].EdgeCapacity[Integer.parseInt(line[k])-1][i]=Integer.parseInt(line[k+1]);
                        virtualGraph[v].EdgeCapacity[i][Integer.parseInt(line[k])-1]=Integer.parseInt(line[k+1]);
                        virtualGraph[v].EdgeCapacity[Integer.parseInt(line[k])-1][i]=virtualGraph[v].EdgeCapacity[Integer.parseInt(line[k])-1][i];
                        virtualGraph[v].EdgeCapacity[i][Integer.parseInt(line[k])-1]=virtualGraph[v].EdgeCapacity[i][Integer.parseInt(line[k])-1];
                        ct=ct+1;
                    }
                }
                virtualGraph[v].Edge = ct/2;
                reader.close();
            }
            catch (IOException e){
                e.printStackTrace();
                System.out.println("read from brite error!");
            }
            finally {
                if(reader != null) {
                    try{
                        reader.close();
                    }catch (IOException e1){

                    }
                }
            }
        }
    }


    //首先构造初始情况，随机把所有虚拟节点映射到物理节点
    public void Mapping(PhysicalGraph physicalGraph,VirtualGraph virtualGraph){
        virtualGraph.VN2PN = new int[virtualGraph.Node];
        Arrays.fill(virtualGraph.VN2PN,-1);
        //这里选择把一个虚拟机随机映射到某一个物理机上
        for (int i = 0; i <virtualGraph.Node ; i++) {
            Random rd = new Random();
            int random = rd.nextInt(physicalGraph.Node);
            virtualGraph.VN2PN[i] = random;
            physicalGraph.VMInPM[random].add(new VNode(i,virtualGraph.NodeCapacity[i],virtualGraph.id));
            physicalGraph.NodeCapacity[random].cpu += virtualGraph.NodeCapacity[i].cpu;
            physicalGraph.NodeCapacity[random].mem += virtualGraph.NodeCapacity[i].mem;
        }

    }

}
