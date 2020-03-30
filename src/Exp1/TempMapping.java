package Exp1;

public class TempMapping implements Comparable<TempMapping>{
    int PM;
    double temperature;
    TempMapping(int PM,double temperature){
        this.PM = PM;
        this.temperature = temperature;
    }

    @Override
    public int compareTo(TempMapping o) {
        if(o.temperature>this.temperature){
            return 1;
        }
        else if(o.temperature == this.temperature){
            return 0;
        }
        else {
            return -1;
        }
    }
}
