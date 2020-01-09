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
        return (int) (o.temperature-this.temperature);
    }
}
