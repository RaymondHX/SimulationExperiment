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


    public void sout() {
        for (int i = 0; i <first.length ; i++) {
            if(first[i]){
                System.out.println("底部元素："+i);
            }
        }
        for (int i = 0; i <second.length ; i++) {
            if (second[i]!=-1){
                System.out.println("第"+i+"台虚拟机"+"对应buttom element"+second[i]);
            }
        }
    }
}
