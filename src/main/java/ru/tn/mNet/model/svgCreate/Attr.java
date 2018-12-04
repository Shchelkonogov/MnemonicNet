package ru.tn.mNet.model.svgCreate;

/**
 * Класс описывающий атрибуты svg тегов
 */
public class Attr {

    private String name;
    private String value;

    /**
     * Конструктор создания атрибута
     * @param name имя атрибута
     * @param value значение атрибута
     */
    public Attr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + "=\"" + value + '"';
    }
}
