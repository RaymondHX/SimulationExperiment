package Exp2;

public class Bucket {
    //bucket 编码，由两个部分组成
    protected boolean first[];
    protected int second[];
    //虚拟机的数量
    protected int M;
    //物理机的数量
    protected int N;

    Bucket(int M,int N){
        this.M = M;
        this.N = N;
        first = new boolean[M];
        second = new int[M];
    }
}
