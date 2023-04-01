package com.bai.pdf.img;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class PdfImage {

    private byte[] data;
    private float x;
    private float y;
    private float width;
    private float height;

    public PdfImage setPostion(float x,float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public PdfImage setWidthAndHeight(float width,float height){
        this.width = width;
        this.height = height;
        return this;
    }

    public PdfImage(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileChannel channel = fis.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(1024);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((channel.read(buff)) > -1){
            out.write(buff.array(),0,buff.limit());
            buff.rewind();
        }
        this.data = out.toByteArray();
        out.close();
        fis.close();
        channel.close();
    }

    public PdfImage(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = -1;
        while((len = in.read(buff))>-1){
            out.write(buff,0,len);
        }
        this.data = out.toByteArray();
        out.close();
        in.close();

    }

    public PdfImage(byte[] data){
        this.data = data;
    }


    public byte[] getData(){
        return this.data;
    }

    public void drowImg(PDDocument document, PDPageContentStream contentStream) throws IOException {
        PDImageXObject image = PDImageXObject.createFromByteArray(document, data,"插入图片");
        contentStream.drawImage(image, x, y, width,height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
