package pdf;

import com.bai.pdf.text.PdfText;
import com.bai.pdf.write.Margin;
import com.bai.pdf.write.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

@Slf4j
public class PdfTextTest {

    public static void main(String[] args) throws IOException {
        Margin margin = new Margin(20,50);

        PDDocument document = new PDDocument();

        PDFont font = PDType0Font.load(document,new File("D://imgtest/fangsong/simfang.ttf"));

        String str = "在1928年前，有名的密苏里大学哥伦比亚分校毕业的记者在远东工作，其中超过半数以上在中国。在这些人中比较著名的包括密勒（Thomas F.F.Millard）、鲍威尔（J.B.Powell）、美联社的莫里斯（John R.Morris）、哈瑞斯（Morris Harris）、巴布（J.C.Babb）、怀特（James D.White）、合众国际社的克林（Benjamin Kline)、《纽约时报》的米索威滋(Hernry F Misselwitz），《纽约先锋论坛报》的科内（Vitor Keen）、《密勒氏评论报》的克劳（Carl Crow）等，后来又有武道（Maurice Votaw）、斯诺（Edgar Snow），还有虽非密大背景、但出自密苏里州的史沫特莱（Agnes Smedley）、项美丽（Emily Hahn）等。从1900年开始，这些人从美国中西部络绎不绝地开赴中国，形成了一道壮丽的景观。西北大学的汉密尔顿教授（J.M.Hamilton）形容他们为“密苏里新闻团伙”（Missouri Monopoly），阿道夫大学的罗赞斯基博士（Mordechai Rozanski） 更戏称这些人为“密苏里黑手党”（Missouri Mafia）。\n" +
                "从另一方面观察，民国时期中国一大批重要的新闻记者、新闻教育家、新闻官员、政治家、院士多出于密苏里大学哥伦比亚分校。 例如《广州市时报》主笔黄宪昭、主管对外新闻的国民党中宣部副部长董显光,《中央日报》社长马星野，《申报》著名记者、后任复旦大学教授的汪英宾, 路透社记者赵敏恒、国民党新闻官员沈剑虹、著名报人吴嘉棠、新闻教育家蒋荫恩、梁士纯、谢然之等, 显然,中国也有一支“密苏里新闻帮”。\n" +
                "中国新闻的崛起\n" +
                "徐宝璜\n" +
                "徐宝璜，字伯轩，江西九江人。著名新闻教育家。他是最先在国内开设新闻学课程的大学教授，主张报纸应具有独立的社会地位，应代表国民提出建议和要求。同时，他认为报纸的舆论是根据新闻而来，新闻又以正确的事实为基础，因此新闻中的事实正确与否决定舆论的健全与否，报纸在提倡道德，开启民智方面具有重要的职责和作用。徐宝璜在我国新闻教育方面做出了很大的贡献，被誉为“新闻教育界第一位大师”和“新闻学界最初开山祖”。";
        StringBuffer buff = new StringBuffer();
        IntStream.range(0,100).forEach(e->{
            buff.append(str);
        });

        Page page ;
        PdfText text = new PdfText(buff.toString());
        do{

            page = new Page(document);
            page.setFont(font,16)
                    .setLeading(16)
                    .setMargin(margin);
            text = page.writeText(text);
            page.close();
        }while (text.isOverstepFlag());

        document.save(new File("text.pdf"));
        document.close();

    }
}
