package com.inventory.mappers;

import com.inventory.models.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

@Component
public class PdfMapper {

    public byte[] getPdf(Item item) throws DocumentException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        //insert header
        Calendar cal = Calendar.getInstance();

        //insert text
        Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
        Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        Chunk chunk = new Chunk("This is the title", chapterFont);
        Chapter chapter = new Chapter(new Paragraph(chunk), 1);
        chapter.setNumberDepth(0);
        chapter.add(new Paragraph(item.getId(), paragraphFont));
        chapter.add(new Paragraph(item.getName(), paragraphFont));
        chapter.add(new Paragraph(Integer.toString(item.getPrice()), paragraphFont));
        chapter.add(new Paragraph(Integer.toString(item.getQty()), paragraphFont));
        chapter.add(new Paragraph(item.getLocation(), paragraphFont));


//        //insert image
//        Path path = null;
//        Image img = null;
//        try {
//            path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
//            img = Image.getInstance(path.toAbsolutePath().toString());
//        } catch (IOException | NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        document.add(img);

        document.add(chapter);
        byte[] pdf = byteArrayOutputStream.toByteArray();
        document.close();

        return pdf;
    }

}
