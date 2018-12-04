package ru.tn.mNet.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс описывающий svg объекты с размерами
 */
public class SVG {

    private String value;
    private double width = 0;
    private double height = 0;

    /**
     * Конструктор для модели svg объекта
     * @param value текст svg элемента
     */
    public SVG(String value) {
        this.value = value;
        calculateSize();
    }

    /**
     * Вычисляет размеры svg элемента
     */
    private void calculateSize() {
        String line = getCommentLine(value);
        if (Objects.nonNull(line)) {
            width = getDouble(line, "width");
            height = getDouble(line, "height");
        }
    }

    /**
     * Поиск значений в строке вида patternPath="return"
     * @param line строка в которой ищем
     * @param patternPath объект значение которого ищем
     * @return найденное значение
     */
    private double getDouble(String line, String patternPath) {
        Pattern pattern = Pattern.compile(patternPath + "=\"\\d+[.]?\\d+\"");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()) {
            Pattern patternData = Pattern.compile("\\d+[.]?\\d+");
            Matcher matcherData = patternData.matcher(matcher.group());
            if(matcherData.find()) {
                return Double.valueOf(matcherData.group());
            }
        }
        return 0;
    }

    /**
     * Ищет в line строку с комментариями в которой указан размер
     * @param line строка в которой производится поиск
     * @return возвращает найденную строку
     */
    private String getCommentLine(String line) {
        Pattern pattern = Pattern.compile("<!--(.*)-->");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * Возращает текст svg элемента
     * @return текст svg элемента
     */
    public String getValue() {
        return value;
    }

    /**
     * Возвращает ширину элемента
     * @return ширина
     */
    public double getWidth() {
        return width;
    }

    /**
     * Возвращает длину элемента
     * @return длина
     */
    public double getHeight() {
        return height;
    }
}
