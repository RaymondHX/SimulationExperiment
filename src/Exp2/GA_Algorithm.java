package Exp2;

import java.util.*;

public class GA_Algorithm {
    protected static PhysicalGraph physicalGraph;
    protected static VirtualGraph virtualGraphs[];
    private static final int INFINITE = 0x6fffffff;
    //总的虚拟机数量
    protected static int M;
    //物理机的总数量
    protected static int N;
    //虚拟机在bucket code 里的编号和虚拟网络中编号的映射
    protected static VNode[] VM_index;

    static int index_x[];
    static int index_y[];

    static boolean used_x[];
    static boolean used_y[];

    //第i个bucket匹配到第match[i]个物理机
    static int match[];

    //二分图
    static int bucketToPM[][];

    //记录顶点变化的slack数组
    static int slack[];

    //记录任意两点之间最短路径的数组
    static int map[][];

    GA_Algorithm(PhysicalGraph physicalGraph,VirtualGraph[] virtualGraphs,int M,int N){
        this.physicalGraph = physicalGraph;
        this.virtualGraphs = virtualGraphs;
        this.N = N;
        this.M = M;
        this.index_x = new int[N];
        this.index_y = new int[N];
        this.used_x = new boolean[N];
        this.used_y = new boolean[N];
        this.match = new int[N];
        this.bucketToPM = new int[N][M];
        this.slack = new int[N];
        this.map = new int[N][N];
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
                VM_index[k] = new VNode(j,virtualGraphs[i].NodeCapacity[j],virtualGraphs[i].id);
                k++;
            }

        }
    }

    //随机生成的bucket code
    protected Bucket MakeBucketCode(int M,int N){
        Bucket bucket = new Bucket(M, N);
        Random random = new Random();
        int num = 1;
        bucket.first[0] = true;
        //第一部分编码
        for (int i = 1; i <M ; i++) {
            int prob = random.nextInt(10);
            //每次有50%的可能性选中
            if(prob>=0&&prob<5&&!bucket.first[i]){
                bucket.first[i] = true;
                num++;
                if(num==N)
                    break;
            }
            if(i+1==M&&num<N){
                i = 0;
            }
        }
        //第二部分编码
        List<Integer> bottomElemets = new ArrayList<>();
        for (int i = 0; i <M ; i++) {
            if(bucket.first[i]){
                bottomElemets.add(i);
                bucket.second[i] = -1;
            }
            else {
                int select = random.nextInt(bottomElemets.size());
                bucket.second[i] = bottomElemets.get(select);
            }
        }
        return bucket;
    }


    //利用KM算法，计算得到最佳的匹配方式
    protected int[] KM(Bucket bucket,int N, int M){
        int N_1 = 0;
        //完成二分图的构建
        for (int i = 0; i <M ; i++) {
            double cpu = 0;
            if(bucket.first[i]){
                //选出 bottom元素
                VNode vNode = VM_index[i];
                for (int k = 0; k <N ; k++) {
                    //每一个物理机上拥有的虚拟机
                    List<VNode> vNodes = physicalGraph.VMInPM[k];
                    for (VNode vnode2 : vNodes) {
                        //不需要迁移，边的权重加1
                        if (vNode.id == vnode2.id && vNode.VGnum == vnode2.VGnum) {
                            bucketToPM[N_1][k] += 1;
                        }
                    }
                }
                for (int j = 0; j <M ; j++) {

                    if(bucket.second[j]==i){
                        //这个坐标对应的虚拟机
                        vNode = VM_index[j];
                        for (int k = 0; k <N ; k++) {
                            //每一个物理机上拥有的虚拟机
                            List<VNode> vNodes = physicalGraph.VMInPM[k];
                            for (VNode vnode2: vNodes) {
                                //不需要迁移，边的权重加1
                                if(vNode.id==vnode2.id&&vNode.VGnum==vnode2.VGnum){
                                    bucketToPM[N_1][k]+=1;
                                }
                            }
                        }
                        cpu+= vNode.load.cpu;
                    }
                }
                N_1++;
            }
            for (int j = 0; j <N ; j++) {
                //如果这个bucket的虚拟机总负载大于这台物理机的最大负载，这条边不存在
                if(cpu>physicalGraph.maxLoads[j].cpu){
                    bucketToPM[N_1][j] = -1;
                }
            }
        }
        for (int i = 0; i <N ; i++) {
            for (int j = 0; j <N ; j++) {
                System.out.println("bucket"+i+"与物理机"+j+"的边"+bucketToPM[i][j]);
            }
        }
        //完成顶点标杆的实现
        for (int i = 0; i <N ; i++) {
            int max = 0;
            for (int j = 0; j <N ; j++) {
                if(bucketToPM[i][j]>max){
                    max = bucketToPM[i][j];
                }
            }
            index_x[i] = max;
        }
        for (int i = 0; i <N ; i++) {
            index_y[i] =0;
        }
        for (int i = 0; i <N ; i++) {
            System.out.println("第"+i+"个X顶点顶标"+index_x[i]);
            System.out.println("第"+i+"个y顶点顶标"+index_y[i]);

        }
        //bucket点的匹配情况
        for (int i = 0; i <N ; i++) {
            match[i] = -1;
        }
        //KM算法主体部分
        for (int i = 0; i <N ; i++) {
            for (int j = 0; j < N; j++) {
                slack[j] = INFINITE;
            }
            while (true){ //寻找与X顶点相匹配的Y顶点，如果找不到就降低X的顶标继续
                for (int j = 0; j <N ; j++) {
                    used_x[j] = false;
                    used_y[j] = false;
                }
                if(DFS(i)) break;//找到匹配的Y顶点，退出
                //如果没有找到，降低X的顶标，提升Y的顶标
                int diff = INFINITE;
                for (int j = 0; j <N ; j++) {
                    if(!used_y[j]) {
                        diff = diff<=slack[j] ? diff :slack[j];
                    }
                }
                //更新顶标
                for (int j = 0; j <N ; j++) {
                    if(used_x[j]) index_x[j] -=diff;
                    if(used_y[j]) index_y[j] +=diff;
                    else slack[j] -= diff;
                }

            }
        }
        for (int i = 0; i <N ; i++) {
            System.out.println("第"+i+"个物理机对应"+match[i]);
        }
        return match;
    }

    //KM算法中计算路径
    protected boolean DFS(int i){
        //这个X顶点在循环中被访问
        used_x[i] = true;

        //遍历这个X顶点的所有Y顶点
        for (int j = 0; j <N ; j++) {
            if(bucketToPM[i][j]!=-1){
                if(used_y[j]) continue;
                //KM算法顶点变化公式
                int gap = index_x[i] +index_y[j] - bucketToPM[i][j];
                //只有X顶点的顶标加上Y顶点的顶标等于graph中它们之间的边的权时才能匹配成功
                if(gap==0){
                    used_y[j] = true;
                    if(match[j]==-1||DFS(match[j])){
                        match[j] = i;
                        return true;
                    }
                }else {
                    slack[j] = slack[j] <=gap ? slack[j] :gap;
                }
            }
//            else {
//                return false;
//            }
        }
        return false;
    }


    //计算原部署方案到某一个bucket迁移开销
    protected double CalMigrationCost(Bucket bucket,int[] match,VNode[] index){
        double MigrationCost = 0;
        for (int i = 0; i <match.length ; i++) {
            //第i个物理机对应的bottom element
            int buttomElement = match[i];
            List<Integer> buttoms = new ArrayList<>();
            buttoms.add(buttomElement);
            for (int j = 0; j <N ; j++) {
                if(bucket.second[j]==buttomElement){
                    buttoms.add(j);
                }
            }
            List<VNode> Vnodes = physicalGraph.VMInPM[i];
            for (Integer inte:buttoms) {
                boolean isThere = false;
                for (VNode vnode:Vnodes) {
                    if(index[inte].VGnum==vnode.VGnum&&index[inte].id==vnode.id){
                        isThere = true;
                        break;
                    }
                }
                //bucket中这台虚拟机需要迁移
                if(!isThere){
                    //找到这台虚拟机原来所在的物理机
                    for (int j = 0; j <N ; j++) {
                        for (int k = 0; k <physicalGraph.VMInPM[j].size() ; k++) {
                            if(index[inte].VGnum==physicalGraph.VMInPM[j].get(k).VGnum
                                    &&index[inte].id==physicalGraph.VMInPM[j].get(k).id){
                                //计算迁移这台机器的迁移开销
                                double dis[] = FindMinPath(physicalGraph,j);
                                double distance = dis[k];
                                MigrationCost += index[inte].load.mem*distance;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return MigrationCost;
    }

    //计算通信开销(目前还没定义好，这里bucket的hashcode代替)
    protected double CalCommunicationCost(Bucket bucket){
        return bucket.hashCode();
    }

    //使用优先队列实现dijkstra算法找到两点间的最短路径
    protected   double[] FindMinPath(PhysicalGraph physicalGraph, int from){
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

    //同一个种族，从这个种族中表现最好的bucket code学习
    protected Bucket LearnFromSameSpecie(Bucket best,Bucket learn){
        Random random = new Random();
        for (int i = 0; i <M ; i++) {
            if (best.second[i]!=-1){
                int possibility = random.nextInt(100);
                //每一个第二部分的值以50%的可能性变成best里所对应的值
                if(possibility>0&&possibility<50){
                    int temp = best.second[i];
                    learn.second[i] = temp;
                }
            }
        }
        return learn;
    }


    //对bucket code 进行变异处理
    protected Bucket Mutate(Bucket bucket){
        List<Integer> bottomElements = new ArrayList<>();
        for (int i = 0; i <M ; i++) {
            if(bucket.first[i])
                bottomElements.add(i);
        }
        Random random = new Random();
        for (int i = 0; i <M ; i++) {
            if(bucket.second[i]!=-1){
                int possibility = random.nextInt(100);
                //每一个第二部分的值以20%的可能性变异
                if(possibility>0&&possibility<20){
                    int index = random.nextInt(bottomElements.size());
                    bucket.second[i] = index;
                }
            }
        }
        return bucket;
    }

    //判断bucket code是否有效
    protected boolean WeatherEffectBucketCode(Bucket bucket){
        //首先找出可用资源最小的物理机（这里指CPU的资源）
        double phyicalNodeRes = 0;
        for (int i = 0; i <physicalGraph.Node ; i++) {
            if(physicalGraph.NodeCapacity[i].cpu>phyicalNodeRes)
                phyicalNodeRes = physicalGraph.NodeCapacity[i].cpu;
        }
        //然后找出总消耗资源最大的虚拟机组（bucket）
        double MaxbucketNeed = 0;
        for (int i = 0; i <M ; i++) {
            if(bucket.first[i]){
                double temp = 0;
                for (int j = 0; j <M ; j++) {
                    if(bucket.second[j]==i){
                        temp+=VM_index[j].load.cpu;
                    }
                }
                if(temp>MaxbucketNeed)
                    MaxbucketNeed = temp;
            }
        }
        if(phyicalNodeRes>MaxbucketNeed)
            return true;
        else
            return false;
    }

    //利用遗传算法，找出最佳的bucket code
    protected Bucket FindBestCodeWithGA(){
        return null;
    }


    public static void main(String[] args) {
        Util util = new Util();
        PhysicalGraph physicalGraph = new PhysicalGraph();
        util.ConstructPhysicalGraph(physicalGraph);
        VirtualGraph[] virtualGraphs = new VirtualGraph[4];
        for (int i = 0; i <4 ; i++) {
            virtualGraphs[i] = new VirtualGraph();
        }
        util.ConstructVirtualGraph(virtualGraphs);
        for (int i = 0; i <4 ; i++) {
            System.out.println("第"+i+"个虚拟网络");
            util.Mapping(physicalGraph,virtualGraphs[i]);
        }
        GA_Algorithm ga_algorithm = new GA_Algorithm(physicalGraph,virtualGraphs,8,4);
        ga_algorithm.FillVM_Index();
        Bucket bucket = ga_algorithm.MakeBucketCode(8,4);
        bucket.sout();
        int[] matchs = ga_algorithm.KM(bucket,4,8);
        System.out.println(ga_algorithm.CalMigrationCost(bucket,matchs,VM_index));

   }
}
