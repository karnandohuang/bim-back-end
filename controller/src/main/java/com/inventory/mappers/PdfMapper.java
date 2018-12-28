package com.inventory.mappers;

import com.inventory.models.entity.Item;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

@Component
public class PdfMapper {

    public class Header extends PdfPageEventHelper {

        protected Phrase header;

        public void setHeader(Phrase header) {
            this.header = header;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, header, 559, 806, 0);
        }
    }

    public ByteArrayInputStream getPdf(Item item) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER);
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        //insert header
        Calendar cal = Calendar.getInstance();
        Header event = new Header();
        writer.setPageEvent(event);
        event.setHeader(new Phrase(String.format("Blibli Inventory Manager")));
//        event.setHeader(new Phrase(String.format("" + cal.getTime())));

        //insert text
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD);
        Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
        Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
//        Chunk chunk = new Chunk(item.getName(), chapterFont);
        Chapter chapter = new Chapter("", 1);
        chapter.setNumberDepth(0);

        Paragraph bim = new Paragraph("Blibli Inventory Manager", paragraphFont);
        bim.setAlignment(Element.ALIGN_RIGHT);

        Paragraph date = new Paragraph("Generated at : " + cal.getTime(), paragraphFont);
        date.setAlignment(Element.ALIGN_RIGHT);

        Paragraph pageTitle = new Paragraph("Item Information", titleFont);
        pageTitle.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pageTitle);
        chapter.add(new Paragraph(" "));
        chapter.add(bim);
        chapter.add(date);

        chapter.add(new Paragraph(" "));
        chapter.add(new Paragraph("Name : " + item.getName(), chapterFont));
        chapter.add(new Paragraph("ID : " + item.getId(), paragraphFont));
        chapter.add(new Paragraph("Price : " + Integer.toString(item.getPrice()), paragraphFont));
        chapter.add(new Paragraph("Quantity : " + Integer.toString(item.getQty()), paragraphFont));
        chapter.add(new Paragraph("Location : " + item.getLocation(), paragraphFont));


        //insert image
//        Path path = null;
//        Image img = null;
//        try {
//            path = Paths.get(ClassLoader.getSystemResource(item.getImageUrl()).toURI());
//            img = Image.getInstance(item.getImageUrl());
//        } catch (IOException | NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        document.add(img);

        document.add(chapter);
        document.close();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

}
