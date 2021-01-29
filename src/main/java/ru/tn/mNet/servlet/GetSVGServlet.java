package ru.tn.mNet.servlet;

import ru.tn.mNet.bean.LoadNetData;
import ru.tn.mNet.bean.LoadObjectParamData;
import ru.tn.mNet.bean.LoadSvgContent;
import ru.tn.mNet.model.*;
import ru.tn.mNet.model.svgCreate.Attr;
import ru.tn.mNet.model.svgCreate.AttrInter;
import ru.tn.mNet.model.svgCreate.Tag;
import ru.tn.mNet.model.svgCreate.TagInter;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Сервлет формирующий svg сети
 */
@WebServlet(name = "GetSVGServlet", urlPatterns = "/getSvg")
public class GetSVGServlet extends HttpServlet {

    private HashMap<String, SVG> svgItemsMap = new HashMap<>();

    @EJB
    private LoadSvgContent bean;

    @EJB
    private LoadNetData netDataBean;

    @EJB
    private LoadObjectParamData objectParamDataBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Load mnemonic scheme net for: " + req.getParameter("objectId"));

        SVG svg;
        Tag svgElement;
        List<Tag> graphTags = new ArrayList<>();
        int graphObjectsCount = 0;
        final int netLength = 300;
        LocalDateTime time = LocalDateTime.parse(req.getParameter("date"), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        List<NetModel> netData = netDataBean.loadData(req.getParameter("objectId"), req.getParameter("type"));
        System.out.println("GraphData: " + netData);

        if(netData.isEmpty() || netData.get(0).getName().equals("ERROR")) {
            byte[] context = bean.getSvg("NET_error.svg").getBytes(StandardCharsets.UTF_8);

            resp.setContentType("image/svg+xml");
            resp.setContentLength(context.length);
            resp.getOutputStream().write(context);

            return;
        }

        double graphWidthMax = netData.stream().mapToDouble(NetModel::getLength).sum();

        //Высчитываем время на ТЭЦ так как в момент запроса время должно быть на цтп
        LocalTime timeOnTECDif = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(graphWidthMax / 1.6))
                .setScale(0, RoundingMode.HALF_EVEN).longValueExact());
        LocalDateTime timeOnTEC = time
                .minusHours(timeOnTECDif.getHour())
                .minusMinutes(timeOnTECDif.getMinute())
                .minusSeconds(timeOnTECDif.getSecond());

        double xCurrentPosition;
        double xNetLength = 0;
        ObjectParamData paramData;
        int objectIndex = 0;
        List<Integer> objectsList = new ArrayList<>();
        List<BigDecimal> objectsLength = new ArrayList<>();

        for(NetModel netItem: netData) {
            System.out.println("Draw " + netItem.getSvgName());
            if (!netItem.getSvgName().equals("Труба")) {
                objectsList.add(netItem.getObjectId());
                objectsLength.add(new BigDecimal(xNetLength).setScale(2, RoundingMode.HALF_EVEN));

                objectIndex++;
                xCurrentPosition = graphObjectsCount * netLength;

                //Расчет времени на каждом объекте (подача обратка)
                LocalTime startTime = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(xNetLength / 1.6))
                        .setScale(0, RoundingMode.HALF_EVEN).longValueExact());
                LocalTime endTime = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf((2 * graphWidthMax - xNetLength) / 1.6))
                        .setScale(0, RoundingMode.HALF_EVEN).longValueExact());

                paramData = objectParamDataBean.load(netItem.getObjectId(),
                        timeOnTEC.plusHours(startTime.getHour())
                                .plusMinutes(startTime.getMinute())
                                .plusSeconds(startTime.getSecond())
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                        timeOnTEC.plusHours(endTime.getHour())
                                .plusMinutes(endTime.getMinute())
                                .plusSeconds(endTime.getSecond())
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

                //Добавляем объект на граф
                svg = getSVG(netItem.getSvgName());

                String objectName = insertDataToSVG(svg.getValue(), "Name", netItem.getName());
                objectName = insertDataToSVG(objectName, "T1", paramData.getT1(), String.valueOf(objectIndex));
                objectName = insertDataToSVG(objectName, "T2", paramData.getT2(), String.valueOf(objectIndex));
                objectName = insertDataToSVG(objectName, "T1п", paramData.getT1p(), String.valueOf(objectIndex));
                objectName = insertDataToSVG(objectName, "T2п", paramData.getT2p(), String.valueOf(objectIndex));

                svgElement = new Tag(TagInter.GROUP, false);
                svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (xCurrentPosition - (svg.getWidth() / 2))
                        + ", " + (640 - (svg.getHeight() / 2)) + ")"));
                svgElement.addStringTags(objectName);
                graphTags.add(svgElement);

                //Добавляем вертикальные пунктирные линии для размера
                svgElement = new Tag(TagInter.LINE, true);
                svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, "730"),
                        new Attr(AttrInter.X2, String.valueOf(xCurrentPosition)),
                        new Attr(AttrInter.Y1, String.valueOf(800
                                + ((graphObjectsCount - 1) * ((netData.indexOf(netItem) == 0 ? 0 : getSVG("arrow_right").getHeight()) + 5))
                                + (graphObjectsCount == 0 ? 5 : 0))),
                        new Attr(AttrInter.X1, String.valueOf(xCurrentPosition)),
                        new Attr("stroke-linecap", "square"),
                        new Attr("stroke-dasharray", "5,5"),
                        new Attr(AttrInter.STROKE, "#000000"),
                        new Attr(AttrInter.FILL, "none")));
                graphTags.add(svgElement);

                if (netData.indexOf(netItem) != 0) {
                    //Добавляем разрывы на основной прямой графа сети
                    svg = getSVG("Переход");
                    svgElement = new Tag(TagInter.GROUP, false);
                    svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (xCurrentPosition - (netLength / 2) - (svg.getWidth() / 2))
                            + ", " + (640 - (svg.getHeight() / 2)) + ")"));
                    svgElement.addStringTags(svg.getValue());
                    graphTags.add(svgElement);

                    //добавляем левые стрелки для размера
                    svg = getSVG("arrow_left");

                    double yPosition = 800 + (graphObjectsCount * 5) + ((graphObjectsCount - 1) * svg.getHeight());

                    svgElement = new Tag(TagInter.GROUP, false);
                    svgElement.addAttr(new Attr(AttrInter.TRANSFORM,
                            "translate(-1, " + yPosition + ")"));
                    svgElement.addStringTags(svg.getValue());
                    graphTags.add(svgElement);

                    //Добавляем правые стрелки для размера
                    svg = getSVG("arrow_right");
                    svgElement = new Tag(TagInter.GROUP, false);
                    svgElement.addAttr(new Attr(AttrInter.TRANSFORM,
                            "translate(" + (xCurrentPosition - svg.getWidth()) + ", " + yPosition + ")"));
                    svgElement.addStringTags(svg.getValue());
                    graphTags.add(svgElement);

                    double yLineTextPosition = yPosition + (svg.getHeight() / 2);

                    //Добавляем горизонтальную линию размера
                    svgElement = new Tag(TagInter.LINE, true);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, String.valueOf(yLineTextPosition)),
                            new Attr(AttrInter.X2, String.valueOf(xCurrentPosition - svg.getWidth())),
                            new Attr(AttrInter.Y1, String.valueOf(yLineTextPosition)),
                            new Attr(AttrInter.X1, String.valueOf(svg.getWidth() + 1)),
                            new Attr(AttrInter.STROKE, "#000000"),
                            new Attr(AttrInter.FILL, "none")));
                    graphTags.add(svgElement);

                    //Добавляем текст с расстоянием к размерам
                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "start"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yLineTextPosition - 3)),
                            new Attr("x", String.valueOf(svg.getWidth() + 1)),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue("L" + graphObjectsCount + ": " + new BigDecimal(String.valueOf(xNetLength))
                            .setScale(2, RoundingMode.HALF_EVEN) + "м");
                    graphTags.add(svgElement);

                    //Добавляем текст со временем подачи к размерам
                    String timeDirect = "tп: " + startTime.format(DateTimeFormatter.ofPattern("HHч mmм ssс"));

                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "end"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yLineTextPosition - 3)),
                            new Attr("x", String.valueOf(xCurrentPosition - svg.getWidth())),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue(timeDirect);
                    graphTags.add(svgElement);

                    //Добавляем текст со временем обратки к размерам
                    String timeReverse = " tо: " + endTime.format(DateTimeFormatter.ofPattern("HHч mmм ssс"));

                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "end"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yLineTextPosition + 13)),
                            new Attr("x", String.valueOf(xCurrentPosition - svg.getWidth())),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue(timeReverse);
                    graphTags.add(svgElement);
                }

                graphObjectsCount++;
            } else {
                xNetLength += netItem.getLength();
            }
        }

        int windowWidthMax = (graphObjectsCount - 1) * netLength;

        //Добавляем текст описания графа
        svgElement = new Tag(TagInter.TEXT, false);
        svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                new Attr("xml:space", "preserve"),
                new Attr("text-anchor", "middle"),
                new Attr("font-family", "serif"),
                new Attr("font-size", "20"),
                new Attr("font-weight", "bold"),
                new Attr("y", String.valueOf(370)),
                new Attr("x", String.valueOf(windowWidthMax / 2)),
                new Attr(AttrInter.STROKE_WIDTH, "0"),
                new Attr(AttrInter.FILL, "#000000")));
        svgElement.setValue("Мнемосхема сегмента магистральной сети");
        graphTags.add(svgElement);

        svgElement = new Tag(TagInter.TEXT, false);
        svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                new Attr("id", "timeID"),
                new Attr("xml:space", "preserve"),
                new Attr("text-anchor", "middle"),
                new Attr("font-family", "serif"),
                new Attr("font-size", "20"),
                new Attr("font-weight", "bold"),
                new Attr("y", String.valueOf(400)),
                new Attr("x", String.valueOf(windowWidthMax / 2)),
                new Attr(AttrInter.STROKE_WIDTH, "0"),
                new Attr(AttrInter.FILL, "#000000")));
        svgElement.setValue("Время на потребителе " + time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        graphTags.add(svgElement);

        //Вставляем невидимые параметры svg элемента, что бы дальше с ним работать
        svgElement = new Tag(TagInter.TEXT, false);
        svgElement.addAttrs(Arrays.asList(new Attr("id", "graphWidthMax"),
                new Attr("y", String.valueOf(370)),
                new Attr("x", String.valueOf(windowWidthMax / 2)),
                new Attr("opacity", "0")));
        svgElement.setValue(String.valueOf(graphWidthMax));
        graphTags.add(svgElement);

        StringBuilder objects = new StringBuilder();
        for (Integer item: objectsList) {
            objects.append(' ').append(item);
        }

        svgElement = new Tag(TagInter.TEXT, false);
        svgElement.addAttrs(Arrays.asList(new Attr("id", "objects"),
                new Attr("y", String.valueOf(370)),
                new Attr("x", String.valueOf(windowWidthMax / 2)),
                new Attr("opacity", "0")));
        svgElement.setValue(objects.toString().trim());
        graphTags.add(svgElement);

        StringBuilder lengths = new StringBuilder();
        for (BigDecimal item: objectsLength) {
            lengths.append(' ').append(item);
        }

        svgElement = new Tag(TagInter.TEXT, false);
        svgElement.addAttrs(Arrays.asList(new Attr("id", "lengths"),
                new Attr("y", String.valueOf(370)),
                new Attr("x", String.valueOf(windowWidthMax / 2)),
                new Attr("opacity", "0")));
        svgElement.setValue(lengths.toString().trim());
        graphTags.add(svgElement);

        //Создаем корневой тег
        Tag rootTag = new Tag(TagInter.SVG, false);
        rootTag.addAttrs(Arrays.asList(new Attr(AttrInter.WIDTH, String.valueOf(windowWidthMax)),
                new Attr(AttrInter.HEIGHT, "1024"),
                new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                new Attr("xmlns:svg", "http://www.w3.org/2000/svg"),
                new Attr("xmlns:xlink", "http://www.w3.org/1999/xlink"),
                new Attr("id", "mnemonicSVG")));

        //Линия на которой размещаются все объекты графа
        svgElement = new Tag(TagInter.LINE, true);
        svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, "640"),
                new Attr(AttrInter.X2, String.valueOf(windowWidthMax)),
                new Attr(AttrInter.Y1, "640"),
                new Attr(AttrInter.X1, "0"),
                new Attr(AttrInter.STROKE_WIDTH, "5"),
                new Attr(AttrInter.STROKE, "#ff0000"),
                new Attr(AttrInter.FILL, "none")));
        rootTag.addTag(svgElement);

        graphTags.forEach(rootTag::addTag);

        String contentS = "<?xml version=\"1.0\"?>" + rootTag;

        byte[] context = contentS.getBytes(StandardCharsets.UTF_8);

        resp.setContentType("image/svg+xml");
        resp.setContentLength(context.length);
        resp.getOutputStream().write(context);
    }

    /**
     * Загружает svg элементы по имени
     * (по сути метод кеширует svg элементы,
     * что бы их не грузить каждый раз из базы)
     * @param name имя svg элемента
     * @return svg элемент
     */
    private SVG getSVG(String name) {
        if (!svgItemsMap.containsKey(name)) {
            System.out.println("Add to map item: " + name);
            svgItemsMap.put(name, new SVG(bean.getSvg(name)));
        }
        return svgItemsMap.get(name);
        //Отключение кеширования
//        return new SVG(bean.getSvg(name));
    }

    /**
     * Вставляем в тег text новые значения
     * @param svgPart текст svg элемента
     * @param id имя id тега text в который надо внести новое значение
     * @param value новое значение
     * @param index индекс для добавление в id
     * @return полученная строка, после изменения
     */
    private String insertDataToSVG(String svgPart, String id, String value, String index) {
        return svgPart.replaceAll("(<text .*id=\")(" + id + ")(\".*>)(.*)(</text>)", "$1" + id + "_" + index + "$3" + value + "$5");
    }

    /**
     * Вставляем в тег text новые значения
     * @param svgPart текст svg элемента
     * @param id имя id тега text в который надо внести новое значение
     * @param value новое значение
     * @return полученная строка, после изменения
     */
    private String insertDataToSVG(String svgPart, String id, String value) {
        return insertDataToSVG(svgPart, id, value, "");
    }
}
