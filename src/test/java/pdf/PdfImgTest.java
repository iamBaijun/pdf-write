package pdf;

import com.bai.pdf.common.Deriction;
import com.bai.pdf.img.PdfImage;
import com.bai.pdf.write.Margin;
import com.bai.pdf.write.Page;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfImgTest {

    public static void main(String[] args) throws IOException {
        Margin margin = new Margin(20, 20);
        PDDocument document = new PDDocument();
        List<PdfImage> imgList = new ArrayList<>();
        for(int j=0;j<10;j++){
            for (char i = 'A'; i <= 'F'; i++) {
                imgList.add(new PdfImage(
                        new File("C:\\Users\\baishujun\\Pictures\\Camera Roll\\" + i + ".jpg"))
                        .setWidthAndHeight(200, 200)
                );
            }
        }

        Page page = new Page(document);
        page.setMargin(margin);

        for(int i=0;i<imgList.size();i++){
            if(page.isImgOver(imgList.get(i))){
                page.close();
                page = new Page(document)
                .setMargin(margin);
            }
            if(i<10){
                page.drawImage(imgList.get(i), Deriction.LEFT);
            }else if(i<20){
                page.drawImage(imgList.get(i), Deriction.CENTER);
            }else{
                page.drawImage(imgList.get(i), Deriction.RIGHT);
            }
        }
        page.close();

        document.save(new File("D://imgtest/img.pdf"));
        document.close();

    }
}
