package com.bai.pdf.table;

import com.bai.pdf.util.Cursor;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFTable {

    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;
    private PDFont font;
    private float fontSize;
    private int[] arrays;
    private float leading;
    private float marginLeft;
    private float marginTop;
    private float tableWidth;
    private float pageHeight;
    private float fontHeight;
    private float fontWidth;
    private float xEnd;
    private float yStart;
    private List<PDFTableTr> trList;
    private List<PDFTableTr> headerList;
    private Cursor cursor;
    private float lineWidth;
    private final float paddingBottom = 5f;
    private float[] widthArrays;
    private boolean overFlag = false;
    private List<PDFTableTr> overTrList;

    public PDFTable(PDDocument document, PDPage page, PDPageContentStream contentStream) {
        this.document = document;
        this.page = page;
        this.contentStream = contentStream;
        this.trList = new ArrayList<>();
        this.headerList = new ArrayList<>();
    }

    //设置字体样式和宽度间距等
    public PDFTable setStyle(PDFont font, float fontSize, float leading, float marginLeft, float marginTop, float lineWidth) throws IOException {
        this.font = font;
        this.fontSize = fontSize;
        this.leading = leading;
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.tableWidth = page.getMediaBox().getWidth() - marginLeft * 2;
        this.pageHeight = page.getMediaBox().getHeight();
        this.fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        this.fontWidth = font.getAverageFontWidth() / 1000 * fontSize;
        //表格结束和开始的位置
        this.xEnd = page.getMediaBox().getWidth() - marginLeft;
        this.yStart = this.pageHeight - marginTop;
        //设置当前可以输入的位置
        cursor = new Cursor(marginLeft, yStart);
        this.lineWidth = lineWidth;
        contentStream.setLineWidth(0.5f);
        return this;
    }

    //设置整体宽度
    public PDFTable setStyle(int[] arrays) throws Exception {
        this.arrays = arrays;
        //这里要计算出每个td
        if (Arrays.stream(arrays).sum() != 100) {
            throw new Exception("宽度比例不正确");
        }
        widthArrays = new float[arrays.length];
        for (int i = 0; i < this.arrays.length; i++) {
            widthArrays[i] = this.tableWidth * (this.arrays[i] / 100f);
        }

        return this;
    }

    //设置表头
    public PDFTable setHeader(String... header) {
        PDFTableTr htr = new PDFTableTr(header);
        headerList.add(htr);
        // trList.addAll(headerList);
        return this;
    }

    //设置表内容
    public PDFTable setTableBody(String... data) {
        PDFTableTr htr = new PDFTableTr(data);
        trList.add(htr);
        return this;
    }

    public PDFTable setTableBody(List<String> list) {
        String[] data = new String[list.size()];
        PDFTableTr htr = new PDFTableTr(list.toArray(data));
        trList.add(htr);
        return this;
    }

    public PDFTable setOverList(List<PDFTableTr> list) {
        this.trList = list;
        return this;
    }

    //计算当前高度  减去当前字体的高度
    private void compur() {

        cursor.setY(cursor.getY() - fontHeight - paddingBottom);
    }

    private void compur(int row) {

        row = row > 0 ? row - 1 : 0;
        cursor.setY(cursor.getY() - fontHeight * row - paddingBottom);
    }

    //画一条横线
    private void rowLine(float _startX, float _endX, float y) throws IOException {
        contentStream.moveTo(_startX, y);
        contentStream.lineTo(_endX, y);
        contentStream.stroke();
    }

    //画一条竖线
    private void colLine(float x, float _startY, float _endY) throws IOException {
        contentStream.moveTo(x, _startY);
        contentStream.lineTo(x, _endY);
        contentStream.stroke();
    }

    private PDFTable endDrowTable() throws IOException {
        contentStream.stroke();
        return this;
    }

    public PDFTable drawTable(String title) throws IOException {
        //设置初始位置
        text(title, tableWidth, cursor.getX(), cursor.getY(), true);//标题
        compur();
        //设置以下当前 yStart
        this.yStart = cursor.getY();
        rowLine(marginLeft, xEnd, cursor.getY());

        //设置表头
        dataWrite(headerList);
        dataWrite(trList);
        //补充纵向竖线
        drawColLine();

      //  endDrowTable();
        return this;
    }


    private void dataWrite(List<PDFTableTr> list) throws IOException {
        float postion;
        for (int i = 0; i < list.size(); i++) {
            List<PDFTableTd> tds = list.get(i).getTds();
            int maxRow = 0;
            postion = cursor.getX();
            OverObj overObj = overCol(tds);
            if (cursor.getY() - overObj.getMaxHeight()*fontHeight <= marginTop) {
                //这一页满了不写了
                this.overTrList = list.subList(i, list.size());
                this.overFlag = true;
                break;
            }
            compur();
            for (int j = 0; j < tds.size(); j++) {
                PDFTableTd td = tds.get(j);
                if (j != 0) {
                    postion += widthArrays[j - 1];
                }
                float itemHeight = overObj.getHeights()[j];
                float y = cursor.getY() - (overObj.getMaxHeight()*fontHeight - itemHeight * fontHeight)/2;
                int row = text(td.getValue(), widthArrays[j], postion, y ,true);
                if (maxRow < row) {
                    maxRow = row;
                }
            }
            compur(maxRow);
            rowLine(marginLeft, xEnd, cursor.getY());
        }
    }

    private void drawColLine() throws IOException {
        float postion = marginLeft;
        for (int i = 0; i < widthArrays.length; i++) {
            if (i != 0) {
                postion += widthArrays[i - 1];
            }
            colLine(postion, yStart, cursor.getY());
        }
        colLine(xEnd, yStart, cursor.getY());
    }

    public OverObj overCol(List<PDFTableTd> tds) {
        int max = 1;
        OverObj obj = new OverObj(tds.size());
        for (int i = 0; i < widthArrays.length; i++) {
            int num = fontNum(widthArrays[i]);
            String text = tds.get(i).getValue();
            if (text.length() > num) {
                List<String> list = textList(num, text);
                if(list.size() > max){
                    max = list.size();
                }
                obj.setHeight(list.size(),i);
            }else{
                obj.setHeight(1,i);
            }
        }
        obj.setMaxHeight(max);

        return obj;
    }


    private int text(String text, float width, float x, float y, boolean center) throws IOException {
        int num = fontNum(width);
        int maxRow = 1;
        //需要换行
        if (text.length() > num) {
            List<String> list = textList(num, text);
            maxRow = list.size();
            writeTextLine(list, x, y);
        } else {
            //只有不用换行的数据才能居中
            if (center) {
                x = center(text, width, textLength(text), x);
            }
            writeTextLine(text, x, y);
        }
        return maxRow;
    }

    //获取文字的长度
    private float textLength(String text) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    public float center(String text, float width, float textWidth, float x) {
        return x + (width - textWidth) / 2f;
    }

    //写文字
    private void writeTextLine(String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.setLeading(leading);
        contentStream.setFont(font, fontSize);
        contentStream.showText(text);
        contentStream.endText();
    }

    private void writeTextLine(List<String> textList, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.setLeading(leading);
        contentStream.setFont(font, fontSize);
        for (String s : textList) {
            contentStream.showText(s);
            contentStream.newLine();
        }
        contentStream.endText();
    }

    //一行中可以写多少个字
    public int fontNum(float width) {
        return (int) (width / fontWidth);
    }

    //将文字分为多行
    private List<String> textList(int num, String text) {
        List<String> list = new ArrayList<>();
        String _text = "";
        do {
            if (text.length() <= num) {
                num = text.length();
            }
            _text = text.substring(0, num);
            text = text.substring(num);
            list.add(_text);
        } while (text.length() > 0);
        return list;
    }

    public List<PDFTableTr> getOverTrList() {
        return this.overTrList;
    }

    public boolean getOverFlag() {
        return this.overFlag;
    }

    @Data
    class OverObj{
        private float maxHeight;
        private float[] heights;

        public OverObj(int length){
            heights = new float[length];
        }

        public void setMaxHeight(float maxHeight){
            this.maxHeight = maxHeight;
        }

        public void setHeight(float height,int index){
            heights[index] = height;
        }

    }
}
