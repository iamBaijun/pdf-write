package pdf;

import com.bai.pdf.table.PDFTable;
import com.bai.pdf.write.Margin;
import com.bai.pdf.write.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;

public class TableTest {

    public static void main(String[] args) throws Exception {
        Margin margin = new Margin(20, 20);
        float fontSize = 16;
        PDDocument document = new PDDocument();
        PDFont font = PDType0Font.load(document,new File("D://imgtest/fangsong/simfang.ttf"));
        Page page = new Page(document);
        page.setFont(font,fontSize)
                .setLeading(fontSize)
                .setMargin(margin);

        PDFTable table = page.drawTable();
        table.setStyle(font,fontSize,fontSize,margin.getLeft(),margin.getTop(),0.5f);
        int[] widthArray = new int[]{10,20,20,10,10,30};
        table.setStyle(widthArray);
        table.setHeader("序号","姓名","职业","年龄","性别","住址");

        for(int i=0;i<10;i++){
            String[] array = new String[widthArray.length];
            array[0] = String.valueOf(i+1);
            array[1] = "张三";
            array[2] = "工人";
            array[3] = "88";
            array[4] = "男";
            array[5] = "北京市东城区景山前街4号";
            table.setTableBody(array);
        }
        table.drawTable("员工信息");
        page.close();
        document.save(new File("D://imgtest/table.pdf"));
        document.close();



    }
}
