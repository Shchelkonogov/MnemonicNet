package ru.tn.mNet.model;

/**
 * Класс описывающий модель графа сети
 */
public class NetModel {

    private String name, svgName;
    private int svgId, objectId;
    private double length;

    public NetModel(String name, String svgName, int svgId, int objectId, double length) {
        this.name = name;
        this.svgName = svgName;
        this.svgId = svgId;
        this.objectId = objectId;
        this.length = length;
    }

    public String getSvgName() {
        return svgName;
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NetModel{");
        sb.append("name='").append(name).append('\'');
        sb.append(", svgName='").append(svgName).append('\'');
        sb.append(", svgId=").append(svgId);
        sb.append(", objectId=").append(objectId);
        sb.append(", lenght=").append(length);
        sb.append('}');
        return sb.toString();
    }
}
