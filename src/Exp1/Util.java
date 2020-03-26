package Exp1;

import Exp1.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Util {

    public String physicalGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\PNet\\20.brite";
    public String virtualGraphPath = "D:\\Sophomore2\\network topology\\VNM10_1\\BRITE\\VNet\\test.brite";
    public int VGnum = 35;
    public double cpu_mean_center = 100;
    public double cpu_square_center = 10;
    public double cpu_mean_side = 10;
    public double cpu_square_side = 2;
    public double cpu_mean_virtual = 1;
    public double cpu_square_virtual = 0.1;
    public double mem_mean_center = 100;
    public double mem_square_cenetr = 10;
    public double mem_mean_side = 10;
    public double mem_square_side = 2;
    public double mem_mean_virtual = 1;
    public double mem_square_virtual = 0.1;
    public double edge_mean = 1;
    public double edge_square = 0.1;
    //CPU资源过载的阈值
    public double hot_threshold = 0.8;
    //coldspot的阈值
    public double cold_threshold = 0.1;

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
            for (int i = 0; i <physicalGraph.Node ; i++) {
                physicalGraph.NodeCapacity[i] = new Load();
                physicalGraph.nodeLoad[i] = new Load();
                Arrays.fill(physicalGraph.EdgeCapacity[i],-1);
                physicalGraph.VMInPM[i] = new ArrayList<>();
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
                    physicalGraph.NodeCapacity[i].mem = Math.sqrt(mem_square_side)*random.nextGaussian()+mem_mean_side;
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

    /**
     * 构建物理网络的完全图
     * @param physicalGraph
     */
    public void ConstructPhysicalCompleteGraph(PhysicalGraph physicalGraph){
        boolean [][] path = wetherHavePath(physicalGraph);
        for (int i = 0; i <physicalGraph.Node ; i++) {
            for (int j = 0; j <physicalGraph.Node ; j++) {
//                System.out.println("节点"+i+" cpu "+physicalGraph.NodeCapacity[i].cpu/physicalGraph.NodeCapacity[i].cpu);
//                System.out.println("节点"+j+" cpu "+physicalGraph.NodeCapacity[j].cpu/physicalGraph.NodeCapacity[i].cpu);
 //               System.out.println(path[i][j]);
                //如果物理网络中不相连，为红色
                if(!path[i][j]){
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

                System.out.println(physicalGraph.Color[i][j]);
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
            if(physicalGraph.EdgeCapacity[from][i]==-1){
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
            if(physicalGraph.EdgeCapacity[to][i]==-1){
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
                    virtualGraph[v].NodeCapacity[i].mem = Math.sqrt(mem_square_virtual)*random.nextGaussian()+mem_mean_virtual;
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

    /**
     * 构建物理网络与虚拟网络的映射
     * @param physicalGraph1
     * @param virtualGraph1
     */
    public void Mapping(PhysicalGraph physicalGraph1,VirtualGraph virtualGraph1,PhysicalGraph physicalGraph2,VirtualGraph virtualGraph2){
        virtualGraph1.VN2PN = new int[virtualGraph1.Node];
        virtualGraph2.VN2PN = new int[virtualGraph2.Node];
        Arrays.fill(virtualGraph1.VN2PN,-1);
        Arrays.fill(virtualGraph2.VN2PN,-1);
        virtualGraph1.VE2PE = new List[virtualGraph1.Node][virtualGraph1.Node];
        virtualGraph2.VE2PE = new List[virtualGraph2.Node][virtualGraph2.Node];
        for (int i = 0; i <virtualGraph1.Node ; i++) {
            for (int j = 0; j <virtualGraph1.Node ; j++) {
                virtualGraph1.VE2PE[i][j] = new ArrayList();
                virtualGraph2.VE2PE[i][j] = new ArrayList();
            }
        }
        //这里选择把一个虚拟机随机映射到某一个物理机上
        for (int i = 0; i <virtualGraph1.Node ; i++) {
            Random rd = new Random();
            int random = rd.nextInt(physicalGraph1.Node);
            //判断加入这台虚拟机后物理机资源利用率是否大于1
            if(physicalGraph1.nodeLoad[random].cpu+virtualGraph1.NodeCapacity[i].cpu<physicalGraph1.NodeCapacity[random].cpu
            &&physicalGraph1.nodeLoad[random].mem+virtualGraph1.NodeCapacity[i].mem<physicalGraph1.NodeCapacity[random].mem){
                virtualGraph1.VN2PN[i] = random;
                physicalGraph1.VMInPM[random].add(new VNode(i,virtualGraph1.NodeCapacity[i],virtualGraph1.id));
                physicalGraph1.nodeLoad[random].cpu += virtualGraph1.NodeCapacity[i].cpu;
                physicalGraph1.nodeLoad[random].mem += virtualGraph1.NodeCapacity[i].mem;
                virtualGraph2.VN2PN[i] = random;
                physicalGraph2.VMInPM[random].add(new VNode(i,virtualGraph2.NodeCapacity[i],virtualGraph2.id));
                physicalGraph2.nodeLoad[random].cpu += virtualGraph2.NodeCapacity[i].cpu;
                physicalGraph2.nodeLoad[random].mem += virtualGraph2.NodeCapacity[i].mem;
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
        System.out.println("想要找"+from+"-"+to+"的路径");
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
        System.out.println("from"+from);
        System.out.println("to"+to);
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
    public void CalTemperature(PhysicalGraph physicalGraph){
        double α = 1,β = 1;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.nodeLoad[i].cpu/physicalGraph.NodeCapacity[i].cpu>hot_threshold){
                physicalGraph.temperature[i] =new TempMapping(i, α*(physicalGraph.NodeCapacity[i].cpu/physicalGraph.NodeCapacity[i].cpu-hot_threshold)+β*physicalGraph.NodeCapacity[i].mem);
            }
            else
                physicalGraph.temperature[i] = new TempMapping(i,0.0);
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
        Queue<VNode> queue = new LinkedList<>();
        int a = (int)physicalGraph.NodeCapacity[PM].cpu;
        int VM[][] = new int[physicalGraph.VMInPM[PM].size()+1][a+1];
        for (int i = 0; i <=physicalGraph.VMInPM[PM].size(); i++) {
            for (int j = 0; j <=a; j++) {
                VM[i][j] = 0;
            }
        }
        for (int i = 1; i <=physicalGraph.VMInPM[PM].size() ; i++) {
            for (int j = 1; j <=a ; j++) {
                VM[i][j] = VM[i-1][j];
//                System.out.println("大小："+physicalGraph.VMInPM[PM].size());
//                System.out.println("检查："+i+" "+physicalGraph.VMInPM[PM].get(i-1));
                if((int)physicalGraph.VMInPM[PM].get(i-1).load.cpu<j){
                    VM[i][j] = max(VM[i-1][j],VM[i-1][j-(int)physicalGraph.VMInPM[PM].get(i-1).load.cpu]+
                            (int)physicalGraph.VMInPM[PM].get(i-1).load.mem);
                }
            }
        }

        boolean item[] = new boolean[physicalGraph.VMInPM[PM].size()+1];
        findWhat(physicalGraph.VMInPM[PM].size(),a,VM,item,physicalGraph,PM);
        for (int i = 1; i <= physicalGraph.VMInPM[PM].size(); i++) {
            if(!item[i]){
                queue.add(physicalGraph.VMInPM[PM].get(i-1));
                System.out.println("第"+(i-1)+"台虚拟机被选中迁移");
            }


        }
        return  queue;
    }

    private void findWhat(int i,int j, int[][] dp,boolean[] item,PhysicalGraph physicalGraph,int PM) {
        if (i > 0) {
            if (dp[i][j] == dp[i - 1][j]) {
                item[i] = false;
                findWhat(i - 1, j,dp,item,physicalGraph,PM);
            } else if (j - (int)physicalGraph.VMInPM[PM].get(i-1).load.cpu >= 0 && dp[i][j]
                    == dp[i - 1][j - (int)physicalGraph.VMInPM[PM].get(i-1).load.cpu] + (int)physicalGraph.VMInPM[PM].get(i-1).load.mem) {
                item[i] = true;
                findWhat(i - 1, j - (int)physicalGraph.VMInPM[PM].get(i-1).load.cpu,dp,item,physicalGraph,PM);
            }
        }
    }

    private int max(int a,int b){
        if(a>b)
            return a;
        else
            return b;
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
                double average = physicalGraph.NodeCapacity[i].cpu/physicalGraph.VMInPM[i].size();
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
