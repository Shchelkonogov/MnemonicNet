package ru.tn.mNet.model;

public class NetModel {

    String n1, n2, n3;
    double n4;

    public NetModel(String n1, String n2, String n3, double n4) {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
    }

    public String getN1() {
        return n1;
    }

    public void setN1(String n1) {
        this.n1 = n1;
    }

    public String getN2() {
        return n2;
    }

    public void setN2(String n2) {
        this.n2 = n2;
    }

    public String getN3() {
        return n3;
    }

    public void setN3(String n3) {
        this.n3 = n3;
    }

    public double getN4() {
        return n4;
    }

    public void setN4(double n4) {
        this.n4 = n4;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NetModel{");
        sb.append("n1='").append(n1).append('\'');
        sb.append(", n2='").append(n2).append('\'');
        sb.append(", n3='").append(n3).append('\'');
        sb.append(", n4=").append(n4);
        sb.append('}');
        return sb.toString();
    }
}
