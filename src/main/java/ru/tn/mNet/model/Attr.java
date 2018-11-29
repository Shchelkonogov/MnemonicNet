package ru.tn.mNet.model;

/**
 * Класс описывающий атрибуты svg тегов
 */
public class Attr {

    private String name;
    private String value;

    public Attr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(name);
        sb.append("=\"").append(value).append('"');
        return sb.toString();
    }
}
