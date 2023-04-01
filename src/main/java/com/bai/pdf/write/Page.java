package com.bai.pdf.write;

import com.bai.pdf.common.Deriction;
import com.bai.pdf.img.PdfImage;
import com.bai.pdf.table.PDFTable;
import com.bai.pdf.text.PdfText;
import com.bai.pdf.util.Cursor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.List;

@Slf4j
public class Page {
    private PDDocument document;
    private PDPageContentStream contentStream;
    private PDPage page;
    private PDFont font;
    private float fontSize;
    private float leading;
    private Cursor cursor;
    private Margin margin;
    private float width;
    private float height;
    private float fontHeight;
    private float fontWidth;
    private int fontNum;//当前一行最多的文字数量

    public Page(PDDocument document) throws IOException {
        this.document = document;
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);

    }

    public Page setFont(PDFont font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
        //计算字体的高度和宽度
        this.fontWidth = font.getAverageFontWidth() / 1000 * fontSize;
        this.fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        return this;
    }

    public Page setLeading(float leading) {
        this.leading = leading;
        return this;
    }

    public Page setMargin(Margin margin) {
        this.margin = margin;
        this.width = page.getMediaBox().getWidth() - margin.getLeft() - margin.getRight();
        this.height = page.getMediaBox().getHeight() - margin.getTop() - fontHeight;
        //初始化高度
        this.cursor = new Cursor(margin.getLeft(), this.height - margin.getTop());
        //计算一行最多可输入的文字数
        this.fontNum = (int) (this.width / this.fontWidth);
        return this;
    }

    public int getFontNum() {
        return fontNum;
    }


    //写入一行 居中写入 左对齐 开头写入
    public PdfText writeText(PdfText pdfText) throws IOException {
        //返回的数据
        PdfText retText = new PdfText(false);
        //一开始写就到了末尾了
        if (isPageEnd()) {
            retText.setOverstepFlag(true);
            retText.setOverList(retText.getList(this.fontNum));
            return retText;
        }

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setLeading(leading);
        contentStream.newLineAtOffset(this.cursor.getX(), this.cursor.getY());
        List<String> list = pdfText.getList(this.fontNum);
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            //是否到了末尾
            if (isPageEnd()) {
                retText.setOverstepFlag(true);
                retText.setOverList(list.subList(i, list.size()));
                break;
            }
            contentStream.showText(str);
            contentStream.newLine();
            //更新当前高度
            this.cursor.setY(this.cursor.getY() - leading);
        }
        contentStream.endText();
        return retText;
    }


    private boolean isPageEnd() {
        if (this.cursor.getY() <= this.margin.getBottom()) {
            return true;
        }
        return false;
    }

    public void drawImage(PdfImage pdfImage, Deriction deriction) throws IOException {
        pdfImage.setPostion(computeXPostin(pdfImage.getWidth(), deriction), cursor.getY() - pdfImage.getHeight());
        pdfImage.drowImg(document, contentStream);
        cursor.setY(cursor.getY() - pdfImage.getHeight());
    }

    //判断当前是否超出
    public boolean isImgOver(PdfImage pdfImage) {
        if (cursor.getY() - pdfImage.getHeight() <= this.margin.getBottom()) {
            return true;
        }
        return false;
    }

    private float computeXPostin(float imgWidth, Deriction deriction) {
        switch (deriction) {
            case RIGHT:
                return this.margin.getLeft() + this.width - imgWidth;
            case CENTER:
                return this.margin.getLeft() + (this.width - imgWidth) / 2;
            case LEFT:
            default:
                return cursor.getX();
        }

    }

    public PDFTable drawTable() throws Exception {
        PDFTable table = new PDFTable(document,page,contentStream);
        return table;
    }


    public void close() {
        try {
            if (null != this.contentStream) {
                this.contentStream.close();
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
