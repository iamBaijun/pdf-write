package com.bai.pdf.table;

import lombok.Data;

@Data
public class PDFTableTd {

    private String value;

    public PDFTableTd(String value) {
        this.value = value;
    }
}