// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.chart;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class ChartDecorator {

    private static final int BASE_ITEM_LABEL_SIZE = 10;

    private static final int BASE_LABEL_SIZE = 12;

    private static final int BASE_TICK_LABEL_SIZE = 10;

    private static final int BASE_LEGEND_LABEL_SIZE = 10;

    private static final int BASE_TITLE_LABEL_SIZE = 14;

    /**
     * DOC bZhou ChartDecorator constructor comment.
     */
    private ChartDecorator() {
    }

    /**
     * DOC bZhou Comment method "decorate".
     * 
     * @param chart
     */
    public static void decorate(JFreeChart chart, PlotOrientation orientation) {
        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof CategoryPlot) {
                decorateCategoryPlot(chart, orientation);

                int rowCount = chart.getCategoryPlot().getDataset().getRowCount();

                for (int i = 0; i < rowCount; i++) {
                    // by zshen bug 14173 add the color in the colorList when chart neend more the color than 8.
                    if (i >= colorList.size()) {
                        colorList.add(generalRandomColor());
                    }
                    // ~14173
                    ((CategoryPlot) plot).getRenderer().setSeriesPaint(i, colorList.get(i));
                }

            }

            if (plot instanceof XYPlot) {
                decorateXYPlot(chart);

                int count = chart.getXYPlot().getDataset().getSeriesCount();
                for (int i = 0; i < count; i++) {
                    // by zshen bug 14173 add the color in the colorList when chart neend more the color than 8.
                    if (i >= colorList.size()) {
                        colorList.add(generalRandomColor());
                    }
                    // ~14173
                    ((XYPlot) plot).getRenderer().setSeriesPaint(i, colorList.get(i));
                }
            }

            if (plot instanceof PiePlot) {
                decoratePiePlot(chart);
            }
        }
    }

    /**
     * 
     * DOC zshen Comment method "generalRandomColor".
     * 
     * @return a object of color and don't contain into colorList
     */
    private static Color generalRandomColor() {
        Random rad = new Random();
        Color newColor = null;
        do {
            newColor = new Color(rad.nextInt(255), rad.nextInt(255), rad.nextInt(255));
        } while (colorList.contains(newColor) || Color.white.equals(newColor));
        return new Color(rad.nextInt(255), rad.nextInt(255), rad.nextInt(255));
    }

    /**
     * DOC xqliu Comment method "decorateColumnDependency".
     * 
     * @param chart
     */
    public static void decorateColumnDependency(JFreeChart chart) {
        decorate(chart, PlotOrientation.HORIZONTAL);
        CategoryItemRenderer renderer = ((CategoryPlot) chart.getPlot()).getRenderer();
        renderer.setSeriesPaint(0, colorList.get(1));
        renderer.setSeriesPaint(1, colorList.get(0));

    }

    /**
     * DOC bZhou Comment method "decorateCategoryPlot".
     * 
     * @param chart
     */
    public static void decorateCategoryPlot(JFreeChart chart, PlotOrientation orientation) {

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer render = plot.getRenderer();
        CategoryAxis domainAxis = plot.getDomainAxis();
        // ADD msjian TDQ-5111 2012-4-9: set something look it well
        domainAxis.setCategoryMargin(0.1);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setLowerMargin(0.05);
        // TDQ-5111~

        ValueAxis valueAxis = plot.getRangeAxis();

        Font font = new Font("Tahoma", Font.BOLD, BASE_ITEM_LABEL_SIZE);//$NON-NLS-1$

        render.setBaseItemLabelFont(font);
        // MOD zshen 10998: change the font name 2010-01-16
        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE);//$NON-NLS-1$
        domainAxis.setLabelFont(font);

        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE);//$NON-NLS-1$
        valueAxis.setLabelFont(font);

        font = new Font("sans-serif", Font.PLAIN, BASE_TICK_LABEL_SIZE);//$NON-NLS-1$
        domainAxis.setTickLabelFont(font);
        valueAxis.setTickLabelFont(font);

        font = new Font("Tahoma", Font.PLAIN, BASE_LEGEND_LABEL_SIZE);//$NON-NLS-1$
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }

        font = new Font("sans-serif", Font.BOLD, BASE_TITLE_LABEL_SIZE);//$NON-NLS-1$
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(font);
        }

        font = null;

        if (render instanceof BarRenderer) {

            int rowCount = chart.getCategoryPlot().getDataset().getRowCount();
            if (!isContainsChineseColumn(chart))
                domainAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 10));//$NON-NLS-1$
            domainAxis.setUpperMargin(0.1);
            // MOD klliu bug 14570: Label size too long in Text statistics graph 2010-08-09
            domainAxis.setMaximumCategoryLabelLines(10);
            ((BarRenderer) render).setItemMargin(-0.40 * rowCount);

            // ADD msjian TDQ-5111 2012-4-9: set Bar Width and let it look well
            // not do this when the bar is horizontal Orientation
            if (orientation == null) {
                ((BarRenderer) render).setMaximumBarWidth(0.2);
            }
            // TDQ-5111~
        }
        // ~10998
    }

    /**
     * DOC bZhou Comment method "decorateXYPlot".
     * 
     * @param chart
     */
    private static void decorateXYPlot(JFreeChart chart) {

        Font font = null;
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer render = plot.getRenderer();
        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis valueAxis = plot.getRangeAxis();

        font = new Font("Tahoma", Font.BOLD, BASE_ITEM_LABEL_SIZE);//$NON-NLS-1$

        render.setBaseItemLabelFont(font);

        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE);//$NON-NLS-1$
        domainAxis.setLabelFont(font);

        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE);//$NON-NLS-1$
        valueAxis.setLabelFont(font);

        font = new Font("sans-serif", Font.PLAIN, BASE_TICK_LABEL_SIZE);//$NON-NLS-1$
        domainAxis.setTickLabelFont(font);
        valueAxis.setTickLabelFont(font);

        font = new Font("Tahoma", Font.PLAIN, BASE_LEGEND_LABEL_SIZE);//$NON-NLS-1$
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }

        font = new Font("sans-serif", Font.BOLD, BASE_TITLE_LABEL_SIZE);//$NON-NLS-1$
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(font);
        }

        font = null;
    }

    /**
     * 
     * DOC qiongli Comment method "decoratePiePlot".
     * 
     * @param chart
     */
    private static void decoratePiePlot(JFreeChart chart) {

        Font font = new Font("sans-serif", Font.BOLD, BASE_TITLE_LABEL_SIZE);//$NON-NLS-1$
        TextTitle textTitle = chart.getTitle();
        // MOD msjian TDQ-5213 2012-5-7: fixed NPE
        if (textTitle != null) {
            textTitle.setFont(font);
        }
        font = new Font("Tahoma", Font.PLAIN, BASE_ITEM_LABEL_SIZE);//$NON-NLS-1$
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }
        // TDQ-5213~
        PiePlot plot = (PiePlot) chart.getPlot();
        font = new Font("Monospaced", Font.PLAIN, 10);//$NON-NLS-1$
        plot.setLabelFont(font);
        plot.setNoDataMessage(DefaultMessagesImpl.getString("ChartDecorator.NoDataMessage")); //$NON-NLS-1$
        StandardPieSectionLabelGenerator standardPieSectionLabelGenerator = new StandardPieSectionLabelGenerator(("{0}:{2}"),
                NumberFormat.getNumberInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(standardPieSectionLabelGenerator);
        plot.setLabelLinkPaint(Color.GRAY);
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelGap(0.02D);
        plot.setOutlineVisible(false);
        plot.setMaximumLabelWidth(0.2D);
        plot.setCircular(false);
    }

    private static final Color COLOR_0 = new Color(244, 147, 32);

    private static final Color COLOR_1 = new Color(128, 119, 178);

    private static final Color COLOR_2 = new Color(190, 213, 48);

    private static final Color COLOR_3 = new Color(236, 23, 133);

    private static final Color COLOR_4 = new Color(35, 157, 190);

    private static final Color COLOR_5 = new Color(164, 155, 100);

    private static final Color COLOR_6 = new Color(250, 212, 16);

    private static final Color COLOR_7 = new Color(234, 28, 36);

    private static final Color COLOR_8 = new Color(192, 131, 91);

    private static List<Color> colorList = new ArrayList<Color>();

    /**
     * 
     * DOC mzhao 2009-07-28 Bind the indicator with specific color.
     */
    public static enum IndiBindColor {
        INDICATOR_ROW_COUNT("Row Count", COLOR_7), //$NON-NLS-1$
        INDICATOR_NULL_COUNT("Null Count", COLOR_2), //$NON-NLS-1$
        INDICATOR_DISTINCT_COUNT("Distinct Count", COLOR_0), //$NON-NLS-1$
        INDICATOR_UNIQUE_COUNT("Unique Count", COLOR_1), //$NON-NLS-1$
        INDICATOR_DUPLICATE_COUNT("Duplicate Count", COLOR_3);//$NON-NLS-1$

        String indLabel = null;

        Color color = null;

        public Color getColor() {
            return color;
        }

        IndiBindColor(String indicatorLabel, Color bindColor) {
            indLabel = indicatorLabel;
            color = bindColor;
        }
    }

    static {
        colorList.add(COLOR_7);
        colorList.add(COLOR_2);
        colorList.add(COLOR_0);
        colorList.add(COLOR_1);
        colorList.add(COLOR_3);
        colorList.add(COLOR_4);
        colorList.add(COLOR_5);
        colorList.add(COLOR_6);
        colorList.add(COLOR_8);
    }

    // private static Color randomColorPicker() {
    // int i = RandomUtils.nextInt(colorList.size());
    // return colorList.get(i);
    // }

    /**
     * Returns true if this string contains the chinese char values. DOC yyi Comment method
     * "isContainsChinese".2010-09-26:14692.
     * 
     * @param str
     * @return
     */
    private static boolean isContainsChineseColumn(JFreeChart chart) {
        Object[] columnNames = chart.getCategoryPlot().getDataset().getColumnKeys().toArray();
        String regEx = "[\u4e00-\u9fa5]";//$NON-NLS-1$
        Pattern pat = Pattern.compile(regEx);
        boolean flg = false;
        for (Object str : columnNames) {
            Matcher matcher = pat.matcher(str.toString());
            if (matcher.find()) {
                flg = true;
                break;
            }
        }
        return flg;
    }
}
