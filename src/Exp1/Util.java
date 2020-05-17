package Exp1;

import Exp1.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Util {

    public String physicalGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\PNet\\1000.brite";
    public String virtualGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\VNet\\test.brite";
    public int VGnum = 3500;
    public double cpu_mean_center = 1500;
    public double cpu_square_center = 300;
    public double cpu_mean_side = 100;
    public double cpu_square_side = 30;
    public double cpu_mean_virtual = 10;
    public double cpu_square_virtual = 3;
    public double mem_mean_center = 1000;
    public double mem_square_cenetr = 100;
    public double mem_mean_side = 150;
    public double mem_square_side = 40;
    public double mem_mean_virtual = 10;
    public double mem_square_virtual = 1;
    public double edge_mean = 10;
    public double edge_square = 1;
    //CPU资源过载的阈值
    public double hot_threshold = 0.8;
    //coldspot的阈值
    public double cold_threshold = 0.2;

    /**
     *     读入物理网络的相关信息
     */
    public void ConstructPhysicalGraph(PhysicalGraph physicalGraph){
        BufferedReader reader = null;
        Random random = new Random();
        try {
            reader = new BufferedReader(new FileReader(physicalGraphPath));
            String temp = reader.readLine();
            while(temp.contains("Nodes:") == false) {
                temp = reader.readLine();
            }
            String Nodestr[] = temp.split(" ");
            physicalGraph.Node = Integer.parseInt(Nodestr[2]);
            physicalGraph.NodeCapacity = new Load[physicalGraph.Node];
            physicalGraph.EdgeCapacity = new double[physicalGraph.Node][physicalGraph.Node];
            physicalGraph.VMInPM = new List[physicalGraph.Node];
            physicalGraph.Color = new String[physicalGraph.Node][physicalGraph.Node];
            physicalGraph.temperature = new TempMapping[physicalGraph.Node];
            physicalGraph.nodeLoad = new Load[physicalGraph.Node];
            physicalGraph.nodePower = new double[physicalGraph.Node];
            physicalGraph.loadHistory = new Load[physicalGraph.Node][288];
            for (int i = 0; i <physicalGraph.Node ; i++) {
                physicalGraph.NodeCapacity[i] = new Load();
                physicalGraph.nodeLoad[i] = new Load();
                for (int j = 0; j <288 ; j++) {
                    physicalGraph.loadHistory[i][j] = new Load();
                }
                Arrays.fill(physicalGraph.EdgeCapacity[i],-1);
                physicalGraph.VMInPM[i] = new ArrayList<>();
                physicalGraph.nodePower[i] = Math.sqrt(0.5)+random.nextGaussian()+1;
            }

            for (int i = 0; i < physicalGraph.Node; i ++){
                temp = reader.readLine();
                //这些节点为中心节点
                if(i==10){
                    physicalGraph.NodeCapacity[i].cpu = Math.sqrt(cpu_square_center)*random.nextGaussian()+cpu_mean_center;
                    physicalGraph.NodeCapacity[i].mem = Math.sqrt(mem_square_cenetr)*random.nextGaussian()+mem_mean_center;
                }
                //这些节点为边缘节点
                else {
                    physicalGraph.NodeCapacity[i].cpu = Math.sqrt(cpu_square_side)*random.nextGaussian()+cpu_mean_side;
                    physicalGraph.NodeCapacity[i].mem = physicalGraph.NodeCapacity[i].cpu;
                }

            }
            while(temp.contains("Edges:") == false){
                temp = reader.readLine();
            }
            String Edgestr[] = temp.split(" ");
            physicalGraph.Edge = Integer.parseInt(Edgestr[2]);
            physicalGraph.EdgeCapacity = new double[physicalGraph.Node][physicalGraph.Node];
            for(int i = 0; i < physicalGraph.Node; i ++) {
                Arrays.fill(physicalGraph.EdgeCapacity[i], -1);
            }
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
            System.err.println("read from brite error!");
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


    public void updatePhysicalGraphNode(PhysicalGraph physicalGraph1,PhysicalGraph physicalGraph2,PhysicalGraph physicalGraph3){
        Random random = new Random();
        for (int i = 0; i < physicalGraph1.Node; i ++){
            //这些节点为中心节点
            if(i==10){
                double temp1 = Math.sqrt(cpu_square_center)*random.nextGaussian()+cpu_mean_center;
                double temp2 = Math.sqrt(mem_square_cenetr)*random.nextGaussian()+mem_mean_center;
                physicalGraph1.NodeCapacity[i].cpu = temp1;
                physicalGraph1.NodeCapacity[i].mem = temp2;
                physicalGraph2.NodeCapacity[i].cpu = temp1;
                physicalGraph2.NodeCapacity[i].mem = temp2;
                physicalGraph3.NodeCapacity[i].cpu = temp1;
                physicalGraph3.NodeCapacity[i].mem = temp2;
            }
            //这些节点为边缘节点
            else {
                double temp1 = Math.sqrt(cpu_square_side)*random.nextGaussian()+cpu_mean_side;
                double temp2 = Math.sqrt(mem_square_side)*random.nextGaussian()+mem_mean_side;
                physicalGraph1.NodeCapacity[i].cpu = temp1;
                physicalGraph1.NodeCapacity[i].mem = temp2;
                physicalGraph2.NodeCapacity[i].cpu = temp1;
                physicalGraph2.NodeCapacity[i].mem = temp2;
                physicalGraph3.NodeCapacity[i].cpu = temp1;
                physicalGraph3.NodeCapacity[i].mem = temp2;
            }

        }
    }

    /**
     * 构建物理网络的完全图
     * @param physicalGraph
     */
    public void ConstructPhysicalCompleteGraph(PhysicalGraph physicalGraph){
        physicalGraph.path = wetherHavePath(physicalGraph);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            for (int j = 0; j <physicalGraph.Node ; j++) {
                //如果物理网络中不相连，为红色
                if(!physicalGraph.path[i][j]){
                    physicalGraph.Color[i][j] = "red";
                }
                else {
                    //节点i过载
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        //节点j也过载
                        if((physicalGraph.nodeLoad[j].cpu/physicalGraph.NodeCapacity[j].cpu)>hot_threshold){
                            physicalGraph.Color[i][j] = "red";
                        }
                        //节点j比coldspot小
                        else if((physicalGraph.nodeLoad[j].cpu/physicalGraph.NodeCapacity[j].cpu)<cold_threshold){
                            physicalGraph.Color[i][j] = "blue";
                        }
                        else{
                            physicalGraph.Color[i][j] = "green";
                        }
                    }
                    //节点i为coldspot
                    else if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)<cold_threshold){
                        if((physicalGraph.nodeLoad[j].cpu/physicalGraph.NodeCapacity[j].cpu)>hot_threshold){
                            physicalGraph.Color[i][j] = "blue";
                        }
                        else{
                            physicalGraph.Color[i][j] = "yellow";
                        }
                    }
                    //节点i负载正常
                    else {
                        if((physicalGraph.nodeLoad[j].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                            physicalGraph.Color[i][j] = "green";
                        }
                        else{
                            physicalGraph.Color[i][j] = "yellow";
                        }
                    }
                }

               // System.out.println(physicalGraph.Color[i][j]);
            }
        }

    }

    /**
     *  完成一次迁移后，更新两个点的所有颜色边
     * @param physicalGraph
     * @param from
     * @param to
     */
    public void updatePhysicalCompleteGraph(PhysicalGraph physicalGraph, int from, int to){
        for (int i = 0; i <physicalGraph.Node ; i++) {
            //如果物理网络中不相连，为红色
            if(!physicalGraph.path[from][i]){
                physicalGraph.Color[from][i] = "red";
                physicalGraph.Color[i][from] = "red";
            }
            else {
                //节点from过载
                if((physicalGraph.nodeLoad[from].cpu/physicalGraph.NodeCapacity[from].cpu)>hot_threshold){
                    //节点i也过载
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[from][i] = "red";
                        physicalGraph.Color[i][from] = "red";
                    }
                    //节点i比coldspot小
                    else if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)<cold_threshold){
                        physicalGraph.Color[from][i] = "blue";
                        physicalGraph.Color[i][from] = "blue";
                    }
                    else{
                        physicalGraph.Color[from][i] = "green";
                        physicalGraph.Color[i][from] = "green";
                    }
                }
                //节点from为coldspot
                else if((physicalGraph.nodeLoad[from].cpu/physicalGraph.NodeCapacity[from].cpu)<cold_threshold){
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[from][i] = "blue";
                        physicalGraph.Color[i][from] = "blue";
                    }
                    else{
                        physicalGraph.Color[from][i] = "yellow";
                        physicalGraph.Color[i][from] = "yellow";
                    }

                }
                //节点from负载正常
                else {
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[from][i] = "green";
                        physicalGraph.Color[i][from] = "green";
                    }
                    else{
                        physicalGraph.Color[from][i] = "yellow";
                        physicalGraph.Color[i][from] = "yellow";
                    }

                }
            }
        }
        for (int i = 0; i <physicalGraph.Node ; i++) {
            //如果物理网络中不相连，为红色
            if(!physicalGraph.path[to][i]){
                physicalGraph.Color[to][i] = "red";
                physicalGraph.Color[i][to] = "red";
            }
            else {
                //节点to过载
                if((physicalGraph.nodeLoad[to].cpu/physicalGraph.NodeCapacity[to].cpu)>hot_threshold){
                    //节点i也过载
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[to][i] = "red";
                        physicalGraph.Color[i][to] = "red";
                    }
                    //节点i比coldspot小
                    else if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)<cold_threshold){
                        physicalGraph.Color[to][i] = "blue";
                        physicalGraph.Color[i][to] = "blue";
                    }
                    else{
                        physicalGraph.Color[to][i] = "green";
                        physicalGraph.Color[i][to] = "green";
                    }
                }
                //节点to为coldspot
                else if((physicalGraph.nodeLoad[to].cpu/physicalGraph.NodeCapacity[to].cpu)<cold_threshold){
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[to][i] = "blue";
                        physicalGraph.Color[i][to] = "blue";
                    }
                    else{
                        physicalGraph.Color[to][i] = "yellow";
                        physicalGraph.Color[i][to] = "yellow";
                    }

                }
                //节点to负载正常
                else {
                    if((physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu)>hot_threshold){
                        physicalGraph.Color[to][i] = "green";
                        physicalGraph.Color[i][to] = "green";
                    }
                    else{
                        physicalGraph.Color[to][i] = "yellow";
                        physicalGraph.Color[i][to] = "yellow";
                    }

                }
            }
        }

    }

    /**
     * 构建虚拟网络图
     * @param virtualGraph
     */
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
                    //暂定图中capacity是我结构中的cpu
                    virtualGraph[v].NodeCapacity[i].cpu = Math.sqrt(cpu_square_virtual)*random.nextGaussian()+cpu_mean_virtual;
                    virtualGraph[v].NodeCapacity[i].mem = virtualGraph[v].NodeCapacity[i].cpu;
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

    public void updateVirtualNode(VirtualGraph[] virtualGraph1,VirtualGraph[] virtualGraph2,VirtualGraph[] virtualGraph3){
        Random random = new Random();
        for (int v = 0; v <VGnum ; v++) {
            for (int i = 0; i < virtualGraph1[v].Node; i ++) {
                double temp1 = Math.sqrt(cpu_square_virtual) * random.nextGaussian() + cpu_mean_virtual;
                double temp2 = Math.sqrt(mem_square_virtual) * random.nextGaussian() + mem_mean_virtual;
                virtualGraph1[v].NodeCapacity[i].cpu = temp1;
                virtualGraph1[v].NodeCapacity[i].mem = temp2;
                virtualGraph2[v].NodeCapacity[i].cpu = temp1;
                virtualGraph2[v].NodeCapacity[i].mem = temp2;
                virtualGraph3[v].NodeCapacity[i].cpu = temp1;
                virtualGraph3[v].NodeCapacity[i].mem = temp2;
            }
        }

    }

    /**
     * 构建物理网络与虚拟网络的映射
     * @param physicalGraph1
     * @param virtualGraph1
     */
    public void Mapping(PhysicalGraph physicalGraph1,VirtualGraph virtualGraph1,
                        PhysicalGraph physicalGraph2,VirtualGraph virtualGraph2,
                        PhysicalGraph physicalGraph3,VirtualGraph virtualGraph3){
        virtualGraph1.VN2PN = new int[virtualGraph1.Node];
        virtualGraph2.VN2PN = new int[virtualGraph2.Node];
        virtualGraph3.VN2PN = new int[virtualGraph3.Node];
        Arrays.fill(virtualGraph1.VN2PN,-1);
        Arrays.fill(virtualGraph2.VN2PN,-1);
        Arrays.fill(virtualGraph3.VN2PN,-1);
        virtualGraph1.VE2PE = new List[virtualGraph1.Node][virtualGraph1.Node];
        virtualGraph2.VE2PE = new List[virtualGraph2.Node][virtualGraph2.Node];
        virtualGraph3.VE2PE = new List[virtualGraph3.Node][virtualGraph3.Node];
        for (int i = 0; i <virtualGraph1.Node ; i++) {
            for (int j = 0; j <virtualGraph1.Node ; j++) {
                virtualGraph1.VE2PE[i][j] = new ArrayList();
                virtualGraph2.VE2PE[i][j] = new ArrayList();
                virtualGraph3.VE2PE[i][j] = new ArrayList();
            }
        }
        //这里选择把一个虚拟机随机映射到某一个物理机上
        for (int i = 0; i <virtualGraph1.Node ; i++) {
            Random rd = new Random();
            int random = rd.nextInt(physicalGraph1.Node);
            if(random%9==0&&physicalGraph1.VMInPM[random].size()==0){
                virtualGraph1.VN2PN[i] = random;
                physicalGraph1.VMInPM[random].add(new VNode(i,virtualGraph1.NodeCapacity[i],virtualGraph1.id));
                physicalGraph1.nodeLoad[random].cpu += virtualGraph1.NodeCapacity[i].cpu;
                physicalGraph1.nodeLoad[random].mem += virtualGraph1.NodeCapacity[i].mem;
                virtualGraph2.VN2PN[i] = random;
                physicalGraph2.VMInPM[random].add(new VNode(i,virtualGraph2.NodeCapacity[i],virtualGraph2.id));
                physicalGraph2.nodeLoad[random].cpu += virtualGraph2.NodeCapacity[i].cpu;
                physicalGraph2.nodeLoad[random].mem += virtualGraph2.NodeCapacity[i].mem;
                virtualGraph3.VN2PN[i] = random;
                physicalGraph3.VMInPM[random].add(new VNode(i,virtualGraph3.NodeCapacity[i],virtualGraph3.id));
                physicalGraph3.nodeLoad[random].cpu += virtualGraph3.NodeCapacity[i].cpu;
                physicalGraph3.nodeLoad[random].mem += virtualGraph3.NodeCapacity[i].mem;
            }
            //判断加入这台虚拟机后物理机资源利用率是否大于1
            else if(physicalGraph1.nodeLoad[random].cpu+virtualGraph1.NodeCapacity[i].cpu<physicalGraph1.NodeCapacity[random].cpu
            &&physicalGraph1.nodeLoad[random].mem+virtualGraph1.NodeCapacity[i].mem<physicalGraph1.NodeCapacity[random].mem&&random%9!=0){
                virtualGraph1.VN2PN[i] = random;
                physicalGraph1.VMInPM[random].add(new VNode(i,virtualGraph1.NodeCapacity[i],virtualGraph1.id));
                physicalGraph1.nodeLoad[random].cpu += virtualGraph1.NodeCapacity[i].cpu;
                physicalGraph1.nodeLoad[random].mem += virtualGraph1.NodeCapacity[i].mem;
                virtualGraph2.VN2PN[i] = random;
                physicalGraph2.VMInPM[random].add(new VNode(i,virtualGraph2.NodeCapacity[i],virtualGraph2.id));
                physicalGraph2.nodeLoad[random].cpu += virtualGraph2.NodeCapacity[i].cpu;
                physicalGraph2.nodeLoad[random].mem += virtualGraph2.NodeCapacity[i].mem;
                virtualGraph3.VN2PN[i] = random;
                physicalGraph3.VMInPM[random].add(new VNode(i,virtualGraph3.NodeCapacity[i],virtualGraph3.id));
                physicalGraph3.nodeLoad[random].cpu += virtualGraph3.NodeCapacity[i].cpu;
                physicalGraph3.nodeLoad[random].mem += virtualGraph3.NodeCapacity[i].mem;
            }
            else {
                i--;
            }
        }
    }


    public void Mapping(PhysicalGraph physicalGraph,VirtualGraph virtualGraph) {
        virtualGraph.VN2PN = new int[virtualGraph.Node];

        Arrays.fill(virtualGraph.VN2PN,-1);

        virtualGraph.VE2PE = new List[virtualGraph.Node][virtualGraph.Node];

        for (int i = 0; i <virtualGraph.Node ; i++) {
            for (int j = 0; j <virtualGraph.Node ; j++) {
                virtualGraph.VE2PE[i][j] = new ArrayList();

            }
        }
        //这里选择把一个虚拟机随机映射到某一个物理机上
        for (int i = 0; i <virtualGraph.Node ; i++) {
            Random rd = new Random();
            int random = rd.nextInt(physicalGraph.Node);
            if(random%9==0&&physicalGraph.VMInPM[random].size()==0){
                virtualGraph.VN2PN[i] = random;
                physicalGraph.VMInPM[random].add(new VNode(i,virtualGraph.NodeCapacity[i],virtualGraph.id));
                physicalGraph.nodeLoad[random].cpu += virtualGraph.NodeCapacity[i].cpu;
                physicalGraph.nodeLoad[random].mem += virtualGraph.NodeCapacity[i].mem;
            }
            //判断加入这台虚拟机后物理机资源利用率是否大于1
            else if(physicalGraph.nodeLoad[random].cpu+virtualGraph.NodeCapacity[i].cpu<physicalGraph.NodeCapacity[random].cpu
                    &&physicalGraph.nodeLoad[random].mem+virtualGraph.NodeCapacity[i].mem<physicalGraph.NodeCapacity[random].mem&&random%9!=0){
                virtualGraph.VN2PN[i] = random;
                physicalGraph.VMInPM[random].add(new VNode(i,virtualGraph.NodeCapacity[i],virtualGraph.id));
                physicalGraph.nodeLoad[random].cpu += virtualGraph.NodeCapacity[i].cpu;
                physicalGraph.nodeLoad[random].mem += virtualGraph.NodeCapacity[i].mem;
            }
            else {
                i--;
            }
        }
    }

    /**
     * 根据颜色图，找到迁移的路径
     * @param physicalGraph
     * @param from
     * @param to
     * @param VE2PE
     */
    public void findPath(PhysicalGraph physicalGraph, int from, int to, List VE2PE){
        //System.out.println("想要找"+from+"-"+to+"的路径");
        boolean visited[] = new boolean[physicalGraph.Node];
        visited[from] = true;
            if(physicalGraph.EdgeCapacity[from][to]!=-1){
                return;
            }

        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.EdgeCapacity[from][i]>0&&i!=from){
                VE2PE.add(i);
                boolean result = DFS(physicalGraph,i,to,visited,VE2PE);
                if(result){
                    return;
                }

                else {
                    VE2PE.remove(i);
                }
            }
        }
    }

    public boolean DFS(PhysicalGraph physicalGraph, int from,int to,boolean visited[], List VE2PE){
        //System.out.println("from"+from);
        //System.out.println("to"+to);
        visited[from] = true;
            if(physicalGraph.EdgeCapacity[from][to]>0)
                return true;

        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.EdgeCapacity[from][i]>0&&i!=from&&!visited[i]){
                VE2PE.add(i);
                boolean result = DFS(physicalGraph,i,to,visited,VE2PE);
                if(result)
                    return true;
                else {
                    VE2PE.remove(i);
                }
            }

        }
        return false;
    }

    /**
     * 利用广度优先搜索，判断任意两个节点之间是否有路径
     * @param physicalGraph
     * @return
     */
    public boolean[][] wetherHavePath(PhysicalGraph physicalGraph){
        boolean [][]path = new boolean[physicalGraph.Node][physicalGraph.Node];
        boolean [][]visted = new boolean[physicalGraph.Node][physicalGraph.Node];
        for (int i = 0; i <physicalGraph.Node ; i++) {
            Queue<Integer> queue = new LinkedList<>();
            path[i][i] = false;
            visted[i][i] =true;
            queue.add(i);
            while(!queue.isEmpty()){
                int node = queue.poll();
                for (int j = 0; j <physicalGraph.Node ; j++) {
                    if(physicalGraph.EdgeCapacity[node][j]!=-1&&!visted[i][j]){
                        path[node][j] = true;
                        path[i][j] = true;
                        visted[i][j] = true;
                        queue.add(j);
                    }
                }
            }
        }
        return path;
    }


    //计算过载的目标函数
    public void calTemperature(PhysicalGraph physicalGraph){
        double α = 10,β = 10;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu>hot_threshold){
                physicalGraph.temperature[i] =new TempMapping(i, α*(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu-hot_threshold)+β*physicalGraph.nodeLoad[i].mem/100);
            }
            else{
                physicalGraph.temperature[i] = new TempMapping(i,0.0);
            }
        }
    }

    //使用优先队列实现dijkstra算法找到两点间的最短路径
    public  double[] FindMinPath(PhysicalGraph physicalGraph, int from){
        Queue<Node> queue = new PriorityQueue<>();
        double dis[] = new double[physicalGraph.Node];
        boolean visited[] = new boolean[physicalGraph.Node];
        for (int i = 0; i <physicalGraph.Node ; i++) {
            dis[i] = Double.MAX_VALUE;
            visited[i] = false;
        }
        dis[from] = 0;
        queue.add(new Node(from,0));
        while (!queue.isEmpty()){
            Node now = queue.poll();
            if(visited[now.id]){
                continue;
            }
            visited[now.id] = true;
            for (int i = 0; i <physicalGraph.Node ; i++) {
                double egde_size = 1/physicalGraph.EdgeCapacity[now.id][i];
                if(egde_size!=-1){
                    if(egde_size+dis[now.id]<dis[i]){
                        dis[i] = egde_size+dis[now.id];
                        queue.add(new Node(i,dis[i]));
                    }
                }

            }
        }
        return dis;
    }

    class Node implements Comparable<Node>{
        int id;
        double dis;
        Node(int id, double dis){
            this.dis = dis;
            this.id = id;
        }

        @Override
        public int compareTo(Node node) {
            return (int)(dis-node.dis);
        }
    }


    //选出需要迁移的虚拟机，使迁出虚拟机的cpu负载大于过载量，并所选的虚拟机的Mem和最小，使用01背包计算
    public Queue<VNode> VMChoose(PhysicalGraph physicalGraph, int PM){
        int number = physicalGraph.VMInPM[PM].size(); // 物品的数量

        // 注意：我们声明数组的长度为"n+1",并另score[0]和time[0]等于0。
        // 从而使得 数组的下标，对应于题目的序号。即score[1]对应于第一题的分数,time[1]对应于第一题的时间
        int[] weight = new int[number + 1]; // {0,2,3,4,5} 每个物品对应的重量
        int[] value = new int[number + 1]; // {0,3,4,5,6} 每个物品对应的价值

        weight[0] = 0;
        int capacity =(int)(physicalGraph.NodeCapacity[PM].cpu*hot_threshold); // 背包容量
        for (int i = 1; i < number + 1; i++) {
            weight[i] = (int)physicalGraph.VMInPM[PM].get(i-1).load.cpu+1;
        }
        int total = 0;
        for (int i = 1; i < number + 1; i++) {
            total+=weight[i];
        }
//        System.out.println("total:"+total);
//        System.out.println("load:"+physicalGraph.nodeLoad[PM].cpu);
//        System.out.println("capacity:"+capacity);
//        if(total<physicalGraph.nodeLoad[PM].cpu){
//            System.out.println();
//        }
        value[0] = 0;
        for (int i = 1; i < number + 1; i++) {
            value[i] = (int)physicalGraph.VMInPM[PM].get(i-1).load.mem+1;
        }



        /* 2.求解01背包问题 */

        int[][] v = new int[number + 1][capacity + 1];// 声明动态规划表.其中v[i][j]对应于：当前有i个物品可选，并且当前背包的容量为j时，我们能得到的最大价值

        // 填动态规划表。当前有i个物品可选，并且当前背包的容量为j。
        for (int i = 0; i < number + 1; i++) {
            for (int j = 0; j < capacity + 1; j++) {
                if (i == 0) {
                    v[i][j] = 0; // 边界情况：若只有0道题目可以选做，那只能得到0分。所以令V(0,j)=0
                } else if (j == 0) {
                    v[i][j] = 0; // 边界情况：若只有0分钟的考试时间，那也只能得0分。所以令V(i,0)=0
                } else {
                    if (j < weight[i]) {
                        v[i][j] = v[i - 1][j];// 包的容量比当前该物品体积小，装不下，此时的价值与前i-1个的价值是一样的，即V(i,j)=V(i-1,j)；
                    } else {
                        v[i][j] = Math.max(v[i - 1][j], v[i - 1][j - weight[i]] + value[i]);// 还有足够的容量可以装当前该物品，但装了当前物品也不一定达到当前最优价值，所以在装与不装之间选择最优的一个，即V(i,j)=max｛V(i-1,j)，V(i-1,j-w(i))+v(i)｝。
                    }
                }
            }
        }

        /* 3.价值最大时，包内装入了哪些物品？ */

        int[] item = new int[number + 1];// 下标i对应的物品若被选中，设置值为1
        Arrays.fill(item, 0);// 将数组item的所有元素初始化为0
        Queue<VNode> queue = new LinkedList<>();
        // 从最优解，倒推回去找
        int j = capacity;
        for (int i = number; i > 0; i--) {
            if (v[i][j] > v[i - 1][j]) {// 在最优解中，v[i][j]>v[i-1][j]说明选择了第i个商品
                item[i] = 1;
                j = j - weight[i];
            }
        }

//        System.out.print("包内物品的编号为：");
        for (int i = 0; i < number + 1; i++) {
            if (item[i] == 0&&i!=0) {
                queue.add(physicalGraph.VMInPM[PM].get(i-1));
            }
        }
        return queue;
    }


    /**
     * 计算能耗
     * @param physicalGraph
     * @return
     */
    public double calEnergyConsumption(PhysicalGraph physicalGraph){
        double cost = 0;
        for (int i = 0; i <physicalGraph.Node ; i++) {
                //如果某台机器上有虚拟机，代表已经打开
                if(physicalGraph.nodeLoad[i].cpu>0){
                    cost++;
                }
        }
        return cost;
    }

    /**
     * 计算通信开销
     * @param physicalGraph
     * @return
     */
    public double calCommunCost(PhysicalGraph physicalGraph){
        double result = 0;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            //如果某个节点过载，则计算一个SLAV
            if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu>hot_threshold){
                double average = (physicalGraph.NodeCapacity[i].cpu*hot_threshold)/physicalGraph.VMInPM[i].size();
                for (int j = 0; j <physicalGraph.VMInPM[i].size() ; j++) {
                    //这台虚拟机的需求大于物理机给他的分配
                    if(physicalGraph.VMInPM[i].get(j).load.cpu>average){
                        result += (physicalGraph.VMInPM[i].get(j).load.cpu-average)/physicalGraph.VMInPM[i].get(j).load.cpu;
                    }
                }
            }
        }
        return result;
    }



}
