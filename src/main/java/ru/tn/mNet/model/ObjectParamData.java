package ru.tn.mNet.model;

/**
 * Класс описывающий модель параметров объектов
 */
public class ObjectParamData {

    private String t1, t2, t1p, t2p;

    /**
     * Конструктор модели параметров
     * @param t1 параметр t1
     * @param t2 параметр t2
     * @param t1p параметр t1p
     * @param t2p параметр t2p
     */
    public ObjectParamData(String t1, String t2, String t1p, String t2p) {
        this.t1 = t1;
        this.t2 = t2;
        this.t1p = t1p;
        this.t2p = t2p;
    }

    /**
     * Возвращает параметр t1
     * @return t1
     */
    public String getT1() {
        return t1;
    }

    /**
     * Возвращает параметр t2
     * @return t2
     */
    public String getT2() {
        return t2;
    }

    public String getT1p() {
        return t1p;
    }

    public String getT2p() {
        return t2p;
    }

    @Override
    public String toString() {
        return "ObjectParamData{" + "t1='" + t1 + '\'' +
                ", t2='" + t2 + '\'' +
                ", t1p='" + t1p + '\'' +
                ", t2p='" + t2p + '\'' +
                '}';
    }
}
