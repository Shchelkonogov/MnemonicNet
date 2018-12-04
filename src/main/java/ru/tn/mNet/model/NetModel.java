package ru.tn.mNet.model;

/**
 * Класс описывающий модель графа сети
 */
public class NetModel {

    private String name, svgName;
    private int svgId, objectId;
    private double length;

    /**
     * Конструктор модели сети
     * @param name имя объекта
     * @param svgName имя svg элемента в бд
     * @param svgId id svg элемента в бд
     * @param objectId id объекта
     * @param length длина объекта (используется только для труб)
     */
    public NetModel(String name, String svgName, int svgId, int objectId, double length) {
        this.name = name;
        this.svgName = svgName;
        this.svgId = svgId;
        this.objectId = objectId;
        this.length = length;
    }

    /**
     * Возвращает имя svg элемента в бд
     * @return имя
     */
    public String getSvgName() {
        return svgName;
    }

    /**
     * Возвращает длину объекта (используется только для труб)
     * @return длина
     */
    public double getLength() {
        return length;
    }

    /**
     * Возвращает имя объекта
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает id объекта
     * @return id
     */
    public int getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NetModel{");
        sb.append("name='").append(name).append('\'');
        sb.append(", svgName='").append(svgName).append('\'');
        sb.append(", svgId=").append(svgId);
        sb.append(", objectId=").append(objectId);
        sb.append(", length=").append(length);
        sb.append('}');
        return sb.toString();
    }
}
