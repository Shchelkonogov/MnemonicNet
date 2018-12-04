package ru.tn.mNet.model.svgCreate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс описывающий теги svg объекта
 */
public class Tag {

    private String name;
    private String value;
    private List<Attr> attrs = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<String> stringTags = new ArrayList<>();
    private boolean close;

    /**
     * Конструктор создания тега
     * @param name имя тега
     * @param close статус загрытия тега<br>
     *              если true то {@code <tag/>}<br>
     *              если false то {@code <tag></tag>}
     */
    public Tag(String name, boolean close) {
        this.name = name;
        this.close = close;
    }

    /**
     * Добавляем один атрибут в тег {@code <тег attr ...>}
     * @param attr атрибут
     */
    public void addAttr(Attr attr) {
        this.attrs.add(attr);
    }

    /**
     * Добавляем массив атрибутов в тег {@code <тег attr ...>}
     * @param items коллекция атрибутов
     */
    public void addAttrs(List<Attr> items) {
        this.attrs.addAll(items);
    }

    /**
     * Добавляем вложенные теги
     * @param tag вложенный тег
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Добавляем элементы ввиде текста
     * @param tag текст вложенных элементов
     */
    public void addStringTags(String tag) {
        stringTags.add(tag);
    }

    /**
     * Добавляем текстовое значение для тега
     * @param value текстовое значение
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\n");
        sb.append('<');
        sb.append(name);
        if (Objects.nonNull(attrs)) {
            for (Attr item: attrs) {
                sb.append(' ').append(item);
            }
        }
        if (close) {
            sb.append("/>");
        } else {
            sb.append('>');
            if (Objects.nonNull(tags)) {
                for (Tag item: tags) {
                    sb.append(item);
                }
            }
            if (Objects.nonNull(stringTags)) {
                for (String item: stringTags) {
                    sb.append("\n");
                    sb.append(item);
                }
            }
            if(Objects.nonNull(value)) {
                sb.append(value);
            }
            sb.append("</").append(name).append('>');
        }
        return sb.toString();
    }
}
