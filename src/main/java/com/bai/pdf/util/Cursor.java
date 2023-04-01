package com.bai.pdf.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cursor {
    private float x;
    private float y;

    public void setPostion(float x,float y){
        this.x = x;
        this.y = y;
    }
}
