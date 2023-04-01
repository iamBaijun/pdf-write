package com.bai.pdf.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class PDFTableTr {

    private List<PDFTableTd> tds = new ArrayList<>();
    private String[] data;
    private int[] arr;
    private float width;

    public PDFTableTr(String[] data){
        this.data = data;
        setTd();
    }

    private void setTd(){
        for(int i=0;i<data.length;i++){
            String text = data[i].replaceAll("\\t","").replaceAll("\\n","");
            PDFTableTd td = new PDFTableTd(text);
            tds.add(td);
        }
    }

    public List<PDFTableTd> getTds() {
        return tds;
    }
}