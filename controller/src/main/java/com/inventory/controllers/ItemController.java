package com.inventory.controllers;

import com.inventory.helpers.ItemHelper;
import com.inventory.helpers.PdfMapper;
import com.inventory.models.Paging;
import com.inventory.models.entity.Item;
import com.inventory.services.item.ItemService;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.item.ItemResponse;
import com.inventory.webmodels.responses.item.ListOfItemResponse;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;

import static com.inventory.webmodels.API_PATH.*;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private ItemHelper helper;

    @Autowired
    private PdfMapper pdfMapper;

    @GetMapping(value = API_PATH_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfItemResponse> getListItem(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfItemResponse list = new ListOfItemResponse(itemService.getItemList(name, paging));
        BaseResponse<ListOfItemResponse> response = helper.getListBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> getItem(@PathVariable String id) {
        Item item;
        BaseResponse<ItemResponse> response;
        try {
            item = itemService.getItem(id);
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedItemResponse(item));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional
    public BaseResponse<ItemResponse> saveItem(@RequestBody ItemRequest request) {
        Item item = generalMapper.map(request, Item.class);
        BaseResponse<ItemResponse> response;
        try {
            item = itemService.saveItem(item);
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedItemResponse(item));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @PostMapping(value = API_PATH_UPLOAD_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("itemId") String itemId,
            HttpServletRequest request
    ) {
        BaseResponse<String> response;
        try {
            String success = itemService.uploadFile(file, itemId);
            response = helper.getStandardBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getStandardBaseResponse(false, e.getMessage());
        }
        return response;
    }

    @DeleteMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<String> deleteItem(@RequestBody DeleteRequest request) {
        BaseResponse<String> response;
        try {
            String success = itemService.deleteItem(request.getIds());
            response = helper.getBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
        }
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM_DETAIL_PDF, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String id) throws DocumentException {
        Item item;
        item = itemService.getItem(id);
        ByteArrayInputStream inputStream = pdfMapper.getPdf(item);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.set("Content-Disposition", "inline");
        headers.set("filename", "details.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping(value = API_PATH_ITEMS_GET_IMAGE, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageAsResponseEntity(@RequestParam String imagePath) {
        HttpHeaders headers = new HttpHeaders();
        byte[] media = itemService.getItemImage(imagePath);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }


}
