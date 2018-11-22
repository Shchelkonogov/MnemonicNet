package ru.tn.mNet.servlet;

import ru.tn.mNet.bean.LoadNetData;
import ru.tn.mNet.bean.LoadSvgContent;
import ru.tn.mNet.model.Attr;
import ru.tn.mNet.model.NetModel;
import ru.tn.mNet.model.Tag;
import ru.tn.mNet.model.TagInter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "GetSVGServlet", urlPatterns = "/getSvg")
public class GetSVGServlet extends HttpServlet {

    @Inject
    private LoadSvgContent bean;

    @Inject
    private LoadNetData netDataBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Tag rootTag = new Tag(TagInter.SVG, false);
        rootTag.addAttrs(Arrays.asList(new Attr(TagInter.WIDTH, "1280"),
                new Attr(TagInter.HEIGHT, "1024"),
                new Attr("xmlns", "http://www.w3.org/2000/svg"),
                new Attr("xmlns:svg", "http://www.w3.org/2000/svg"),
                new Attr("xmlns:xlink", "http://www.w3.org/1999/xlink")));

        Tag line = new Tag(TagInter.LINE, true);
        line.addAttrs(Arrays.asList(new Attr("y2", "640"),
                new Attr("x2", "1280"),
                new Attr("y1", "640"),
                new Attr("x1", "0"),
                new Attr("stroke-width", "5"),
                new Attr("stroke", "#ff0000"),
                new Attr("fill", "none")));
        rootTag.addTag(line);

        List<NetModel> netData = netDataBean.loadData("06-03-183");
        System.out.println(netData);

        int xMax = 1280;
        int yMax = 0;

        for (NetModel item: netData) {
            yMax += item.getN4();
        }
        //System.out.println(yMax);

        int xCurent = 0;

        //int xCurrent1 = 0;

        int i = 0;
        for (NetModel item: netData) {
            //if (!item.getN3().equals("Камера теплосети")) {
            if (!item.getN3().equals("Труба")) {
                Pattern pattern = Pattern.compile("<!--(.*)-->");
                Matcher matcher = pattern.matcher(bean.getSvg(item.getN3()));
                if(matcher.find()) {

                    System.out.println(matcher.group() + " " + item.getN3());
                    double width = getDouble(matcher.group(), "width");
                    double height = getDouble(matcher.group(), "height");
                    //System.out.println(width);
                    //System.out.println(height);


                    double height2 = 0;
                    if (netData.indexOf(item) != 0) {
                        Pattern pattern1 = Pattern.compile("<!--(.*)-->");
                        Matcher matcher1 = pattern1.matcher(bean.getSvg("arrow_right"));
                        if(matcher1.find()) {
                            i++;

                            System.out.println(matcher1.group() + " " + "arrow_right");
                            double width1 = getDouble(matcher1.group(), "width");
                            double height1 = getDouble(matcher1.group(), "height");

                            height2 = height1;

                            Tag group = new Tag("g", false);
                            group.addAttr(new Attr("transform", "translate(" + ((xCurent * xMax) / yMax - width1) + ", " + (840 + (5 * i) + (height1 * (i - 1))) + ")"));
                            group.addStringTags(bean.getSvg("arrow_right"));
                            rootTag.addTag(group);

                            Tag line2 = new Tag(TagInter.LINE, true);
                            line2.addAttrs(Arrays.asList(new Attr("y2", String.valueOf(840 + (5 * i) + ((i - 1) * height1) + (height1 / 2))),
                                    new Attr("x2", String.valueOf(((xCurent * xMax) / yMax) - width1)),
                                    new Attr("y1", String.valueOf(840 + (5 * i) + ((i - 1) * height1) + (height1 / 2))),
                                    new Attr("x1", String.valueOf(width1 + 1)),
                                    //new Attr("stroke-width", "5"),
                                    new Attr("stroke", "#000000"),
                                    new Attr("fill", "none")));
                            rootTag.addTag(line2);

                            Tag text = new Tag("text", false);
                            text.addAttrs(Arrays.asList(new Attr("xmlns", "http://www.w3.org/2000/svg"),
                                    new Attr("xml:space", "preserve"),
                                    new Attr("text-anchor", "middle"),
                                    new Attr("font-family", "serif"),
                                    new Attr("font-size", "16"),
                                    new Attr("y", String.valueOf(840 + (5 * i) + ((i - 1) * height1) + (height1 / 2) - 5)),
                                    new Attr("x", String.valueOf((((xCurent * xMax) / yMax) - width1 + (width1 + 1)) / 2)),
                                    //new Attr("x", String.valueOf(width1 + 1)),
                                    new Attr("stroke-linecap", "null"),
                                    new Attr("stroke-linejoin", "null"),
                                    new Attr("stroke-dasharray", "null"),
                                    new Attr("stroke-width", "0"),
                                    new Attr("fill", "#000000")));
                            text.setValue(String.valueOf(xCurent) + "м");
                            rootTag.addTag(text);

                            //xCurrent1 = 0;
                        }
                    }

                    Tag line1 = new Tag(TagInter.LINE, true);
                    line1.addAttrs(Arrays.asList(new Attr("y2", "730"),
                            new Attr("x2", String.valueOf((xCurent * xMax) / yMax)),
                            new Attr("y1", String.valueOf(840 + ((i - 1) * (height2 + 5)) + (i == 0 ? 5 : 0))),
                            //new Attr("y1", "840"),
                            new Attr("x1", String.valueOf((xCurent * xMax) / yMax)),
                            new Attr("stroke-linecap", "square"),
                            new Attr("stroke-dasharray", "5,5"),
                            new Attr("stroke", "#000000"),
                            new Attr("fill", "none")));
                    rootTag.addTag(line1);

                    Tag group = new Tag("g", false);
                    group.addAttr(new Attr("transform", "translate(" + (((xCurent * xMax) / yMax) - (width / 2)) + ", " + (640 - (height / 2)) + ")"));
                    group.addStringTags(bean.getSvg(item.getN3()));
                    rootTag.addTag(group);


                }
            } else {
                xCurent += item.getN4();
                //xCurrent1 += item.getN4();
                System.out.println(xCurent + " " + item.getN3());
            }//}
        }

        for (int j = 0; j < i; j++) {
            Pattern pattern = Pattern.compile("<!--(.*)-->");
            Matcher matcher = pattern.matcher(bean.getSvg("arrow_left"));
            if(matcher.find()) {

                System.out.println(matcher.group() + " " + "arrow_left");
                double width = getDouble(matcher.group(), "width");
                double height = getDouble(matcher.group(), "height");

                Tag group = new Tag("g", false);
                group.addAttr(new Attr("transform", "translate(-1, " + (840 + 5 + (j * 5) + (j * height)) + ")"));
                group.addStringTags(bean.getSvg("arrow_left"));
                rootTag.addTag(group);
            }
        }



//        Tag group1 = new Tag("g", false);
//        group1.addAttr(new Attr("transform", "translate(20, 300)"));
//        group1.addStringTags(bean.getSvg("arrow_left"));
//        rootTag.addTag(group1);
//        Tag group2 = new Tag("g", false);
//        group2.addAttr(new Attr("transform", "translate(0, 300)"));
//        group2.addStringTags(bean.getSvg("arrow_right"));
//        rootTag.addTag(group2);

        String contentS = "<?xml version=\"1.0\"?>" + rootTag;

        //System.out.println(contentS);

        byte[] context = contentS.getBytes("UTF-8");

        resp.setContentType("image/svg+xml");
        resp.setContentLength(context.length);
        resp.getOutputStream().write(context);
    }

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

}
