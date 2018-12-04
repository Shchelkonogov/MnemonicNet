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
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Load mnemonic scheme net for: " + req.getParameter("objectId"));

        SVG svg;
        Tag svgElement;
        List<Tag> graphTags = new ArrayList<>();
        int graphObjectsCount = 0;
        final int netLength = 300;

        List<NetModel> netData = netDataBean.loadData(req.getParameter("objectId"));
        System.out.println("GraphData: " + netData);

        double graphWidthMax = netData.stream().mapToDouble(NetModel::getLength).sum();

        double xCurrentPosition;
        double xNetLength = 0;
        ObjectParamData paramData;
        for(NetModel netItem: netData) {
            System.out.println("Draw " + netItem.getSvgName());
            if (!netItem.getSvgName().equals("Труба")) {
                xCurrentPosition = graphObjectsCount * netLength;

                paramData = objectParamDataBean.load(netItem.getObjectId());

                //Добавляем объект на граф
                svg = getSVG(netItem.getSvgName());
                svgElement = new Tag(TagInter.GROUP, false);
                svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (xCurrentPosition - (svg.getWidth() / 2))
                        + ", " + (640 - (svg.getHeight() / 2)) + ")"));
//                svgElement.addStringTags(svg.getValue());

                String test = insertDataToSVG(svg.getValue(), "Name", netItem.getName());
                test = insertDataToSVG(test, "T1", paramData.getT1());
                test = insertDataToSVG(test, "T2", paramData.getT2());

//                svgElement.addStringTags(insertDataToSVG(svg.getValue(), "Name", netItem.getName()));
                svgElement.addStringTags(test);
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

                    svg = getSVG("Переход");
                    svgElement = new Tag(TagInter.GROUP, false);
                    svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (xCurrentPosition - (netLength / 2) - (svg.getWidth() / 2))
                            + ", " + (640 - (svg.getHeight() / 2)) + ")"));
                    svgElement.addStringTags(svg.getValue());
                    graphTags.add(svgElement);


                    //добавляем левые стрелочки для размера
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

                    double xTextPosition = ((svg.getWidth() + 1) + xCurrentPosition - svg.getWidth()) / 2;

                    //Добавляем текст с расстоянием к размерам
                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
//                            new Attr("text-anchor", "middle"),
                            new Attr("text-anchor", "start"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yLineTextPosition - 3)),
//                            new Attr("x", String.valueOf(xTextPosition)),
                            new Attr("x", String.valueOf(svg.getWidth() + 1)),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue(String.valueOf("L: " + new BigDecimal(String.valueOf(xNetLength))
                            .setScale(2, RoundingMode.HALF_EVEN)) + "м");
                    graphTags.add(svgElement);

                    //Добавляем текст со временем к размерам
                    String timeDirect = "tп: " + LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(xNetLength / 1.6))
                            .setScale(0, RoundingMode.HALF_EVEN)
                            .longValueExact())
                            .format(DateTimeFormatter.ofPattern("HHч mmм ssс"));
                    String timeReverse = " tо: " + LocalTime.ofSecondOfDay(
                            new BigDecimal(String.valueOf((2 * graphWidthMax - xNetLength) / 1.6))
                            .setScale(0, RoundingMode.HALF_EVEN)
                            .longValueExact())
                            .format(DateTimeFormatter.ofPattern("HHч mmм ssс"));

                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "end"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
//                            new Attr("y", String.valueOf(yLineTextPosition + 13)),
                            new Attr("y", String.valueOf(yLineTextPosition - 3)),
//                            new Attr("x", String.valueOf(xTextPosition)),
                            new Attr("x", String.valueOf(xCurrentPosition - svg.getWidth())),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
//                    svgElement.setValue(timeDirect + timeReverse);
                    svgElement.setValue(timeDirect);
                    graphTags.add(svgElement);

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

        Tag rootTag = new Tag(TagInter.SVG, false);
        rootTag.addAttrs(Arrays.asList(new Attr(AttrInter.WIDTH, String.valueOf(windowWidthMax)),
                new Attr(AttrInter.HEIGHT, "1024"),
                new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                new Attr("xmlns:svg", "http://www.w3.org/2000/svg"),
                new Attr("xmlns:xlink", "http://www.w3.org/1999/xlink")));

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
//        System.out.println(contentS);

        byte[] context = contentS.getBytes("UTF-8");

        resp.setContentType("image/svg+xml");
        resp.setContentLength(context.length);
        resp.getOutputStream().write(context);
    }

    private SVG getSVG(String name) {
        if (!svgItemsMap.containsKey(name)) {
            System.out.println("Add to map item: " + name);
            svgItemsMap.put(name, new SVG(bean.getSvg(name)));
        }
        return svgItemsMap.get(name);
//        return new SVG(bean.getSvg(name));
    }

    private String insertDataToSVG(String svgPart, String id, String value) {
        return svgPart.replaceAll("(<text .*id=\"" + id + "\".*>)(.*)(</text>)", "$1" + value + "$3");
    }
}
