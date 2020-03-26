package Exp1;

import Exp1.Load;

import java.util.List;

public class VirtualGraph {

    public int id;
    public int Node = 0;
    public int Edge;
    public int VN2PN[];
   // public List VE2PE[];

    public List VE2PE[][];
    public int VEindex;
    public double VEBandwidth[];

    public Load NodeCapacity[];
    public int EdgeCapacity[][];

    public VirtualGraph(int id){
        this.id = id;
    }
}
