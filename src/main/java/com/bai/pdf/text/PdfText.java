package com.bai.pdf.text;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PdfText {

    private String text;
    private List<String> listText = new ArrayList<>();;
    private Boolean overstepFlag;
    private List<String> overList = new ArrayList<>();

    public PdfText(Boolean overstepFlag){
        this.overstepFlag = overstepFlag;
    }

    public PdfText(String text){
        this(false);
        this.text = text;
    }

    public List<String> getList(int num){
        //如果超出的时候用这个
        if(overstepFlag){
            return overList;
        }
        //先通过换行分割
        String[] strs = text.split("\n");
        for(String str : strs){
            boolean flag;
            do{
                int len = str.length();
                if(len <= num){
                    listText.add(str);
                    flag = false;
                }else{
                    String _str = str.substring(0,num);
                    str = str.substring(num);
                    listText.add(_str);
                    flag = true;
                }
            }while(flag);
        }
        return listText;
    }

    public Boolean isOverstepFlag(){
        return this.overstepFlag;
    }

    public void setOverstepFlag(Boolean overstepFlag) {
        this.overstepFlag = overstepFlag;
    }

    public void setOverList(List<String> overList) {
        this.overList = overList;
    }
}
