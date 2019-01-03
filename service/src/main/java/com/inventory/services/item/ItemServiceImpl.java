package com.inventory.services.item;

import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Item;
import com.inventory.repositories.ItemRepository;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.Header;
import com.inventory.services.utils.exceptions.EntityNullFieldException;
import com.inventory.services.utils.exceptions.item.*;
import com.inventory.services.utils.validators.ItemValidator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.inventory.services.utils.constants.ExceptionConstant.ID_WRONG_FORMAT_ERROR;

@Service
public class ItemServiceImpl implements ItemService {

    private final static String ITEM_ID_PREFIX = "IM";
    private final static Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemValidator validator;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private GeneralMapper mapper;
    @Autowired
    private PagingHelper pagingHelper;

    @Override
    @Transactional
    public Item getItem(String id) throws RuntimeException {
        if (!validator.validateIdFormatEntity(id, ITEM_ID_PREFIX))
            throw new ItemFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        try {
            return itemRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException(id, "Id");
        }
    }

    @Override
    @Transactional
    public List<Item> getItemList(String search, Paging paging) {
        List<Item> listOfItem;
        PageRequest pageRequest;
        if (paging.getSortedType().matches("desc")) {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.DESC,
                    paging.getSortedBy());
        } else {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.ASC,
                    paging.getSortedBy());
        }
        listOfItem = itemRepository.findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(search, search, pageRequest).getContent();
        float totalRecords = itemRepository.countAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(search, search);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfItem;
    }

    @Override
    @Transactional
    public Item saveItem(Item request) throws RuntimeException {

        Item item;

        String nullFieldItem = validator.validateNullFieldItem(request);

        if (request.getId() != null) {
            logger.info("request image url : " + request.getImageUrl());
            logger.info("edit value");
            String url = this.getItem(request.getId()).getImageUrl();
            item = mapper.map(request, Item.class);
            if(request.getImageUrl() == null)
                item.setImageUrl(url);
        } else {
            item = request;
        }

        if (item.getImageUrl() == null) {
            item.setImageUrl("null");
            logger.info("image url is null");
        }

        boolean isImageUrlValid = validator.validateImageUrlItem(item.getImageUrl());

        logger.info("checking all validation!");

        if (nullFieldItem != null)
            throw new EntityNullFieldException(nullFieldItem);

        else if (!isImageUrlValid)
            throw new ImagePathWrongException();

        item = itemRepository.save(item);
        itemRepository.flush();
        return item;
    }

    @Override
    @Transactional
    public Item changeItemQty(Assignment assignment) throws RuntimeException {
        Item item = this.getItem(assignment.getItem().getId());
        int qty = item.getQty() - assignment.getQty();
        if (item.getQty() <= 0)
            throw new ItemOutOfQtyException(item.getName());
        else if (qty < 0)
            throw new ItemQtyLimitReachedException(item.getName());
        item.setQty(qty);
        item = itemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    public String recoverItemQty(Map<String, Integer> listOfRecoveredItems) throws RuntimeException {
        for (Map.Entry<String, Integer> entry : listOfRecoveredItems.entrySet()) {
            Item item;
            item = this.getItem(entry.getKey());
            item.setQty(item.getQty() + entry.getValue());
            itemRepository.save(item);
        }
        return "Recover success";
    }

    @Override
    @Transactional
    public String deleteItem(List<String> ids) throws RuntimeException {
        for (String id : ids) {
            Item item = this.getItem(id);
            if (assignmentService.getAssignmentCountByItemIdAndStatus(item.getId(), "Pending") > 0)
                throw new ItemStillHaveAssignmentException();
            else {
                itemRepository.deleteById(item.getId());
            }
        }
        return "Delete Success";
    }

    @Override
    @Transactional
    public String uploadFile(MultipartFile file, String itemId) throws RuntimeException {
        logger.info("uploading value image!");
        Item item;
        item = this.getItem(itemId);
        logger.info(item.getName());
        Calendar cal = Calendar.getInstance();
        logger.info("month : " + (cal.get(cal.MONTH) + 1));
        File createdDir = new File("/Users/karnandohuang/Documents/Projects/blibli-inventory-system/bim-back-end/resources/" +
                cal.get(cal.YEAR) + "/" + (cal.get(cal.MONTH) + 1) + "/" + itemId);
        File convertFile = new File(createdDir.getAbsolutePath() + "/" +
                file.getOriginalFilename());
        try {
            if (!convertFile.exists())
                createdDir.mkdirs();
            else
                convertFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(convertFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            try {
                fout.write(file.getBytes());
            } catch (NullPointerException e) {
                throw new ImageNotFoundException(file.getOriginalFilename());
            }
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("setting up image url in value! image url : " + convertFile.getAbsolutePath());
        item.setAssignmentList(null);
        item.setImageUrl(convertFile.getAbsolutePath());
        logger.info("saving value!");
        this.saveItem(item);
        return "Upload image success";
    }

    @Override
    public byte[] getItemImage(String path) {
        if (!validator.validateImageUrlExist(path))
            throw new ImagePathWrongException();
        else {
            File file = new File(path);
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                logger.info("error getting image from path : " + path);
                return new byte[0];
            }
        }
    }

    @Override
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
//        Chunk chunk = new Chunk(value.getName(), chapterFont);
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

        String imageFile = item.getImageUrl();

//        insert image
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
