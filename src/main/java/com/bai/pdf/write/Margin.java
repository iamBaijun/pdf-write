package com.bai.pdf.write;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Margin {
    private float top;
    private float right;
    private float bottom;
    private float left;
    private float firstLine;

    public Margin(){}
    public Margin(float top,float left){
        this.top = top;
        this.bottom = top;
        this.left = left;
        this.right = left;
    }

    
}
