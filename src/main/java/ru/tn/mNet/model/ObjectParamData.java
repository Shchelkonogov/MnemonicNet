package ru.tn.mNet.model;

/**
 * Класс описывающий модель параметров объектов
 */
public class ObjectParamData {

    private String t1, t2;

    /**
     * Конструктор модели параметров
     * @param t1 параметр t1
     * @param t2 параметр t2
     */
    public ObjectParamData(String t1, String t2) {
        this.t1 = t1;
        this.t2 = t2;
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

    @Override
    public String toString() {
        return "ObjectParamData{" + "t1='" + t1 + '\'' +
                ", t2='" + t2 + '\'' +
                '}';
    }
}
