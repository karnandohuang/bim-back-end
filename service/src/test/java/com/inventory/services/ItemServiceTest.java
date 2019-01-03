package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Employee;
import com.inventory.models.entity.Item;
import com.inventory.repositories.ItemRepository;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.item.ItemServiceImpl;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.validators.ItemValidator;
import com.itextpdf.text.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemValidator validator;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private GeneralMapper mapper;

    @Mock
    private PagingHelper pagingHelper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Paging paging = new Paging();
    private List<Item> itemList = mock(ArrayList.class);
    private Page<Item> itemPageList = mock(Page.class);

    @Test
    public void getItemIdValidSuccess() {
        String id = "IM005";
        Item item = setItemWithId();
        mockValidateId(true, id);
        mockFindItemById(true, id);
        Item i = itemService.getItem(id);
        assertEquals(item, i);
        verify(validator).validateIdFormatEntity(anyString(), anyString());
        verify(itemRepository).findById(id);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void getItemIdValidFailed() {
        String id = "IM099";
        mockValidateId(true, id);
        mockFindItemById(false, id);
        try {
            itemService.getItem(id);
        } catch (RuntimeException e) {
            verify(itemRepository).findById(id);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void getItemIdNotValidFailed() {
        String id = "99";
        mockValidateId(false, id);
        mockFindItemById(false, id);
        try {
            itemService.getItem(id);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(id, "IM");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(itemRepository);
        }
    }

    @Test
    public void findItemBasedOnNameAndPagingDescSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("desc");
        List<Item> returnItems = itemService.getItemList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(itemRepository).findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(anyString(), anyString(),
                pageArgument.capture());
        verify(itemRepository).countAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(anyString(), anyString());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(itemList.size(), returnItems.size());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void findItemBasedOnNameAndPagingAscSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("asc");
        List<Item> returnItems = itemService.getItemList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(itemRepository).findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(anyString(), anyString(),
                pageArgument.capture());
        verify(itemRepository).countAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(anyString(), anyString());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(itemList.size(), returnItems.size());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void insertItemImageUrlNullStringSuccess() {
        Item item = setItemWithId();
        item.setId(null);
        mockValidateImageUrl(true, item.getImageUrl());
        mockSaveItem(item);
        Item returned = itemService.saveItem(item);
        verify(validator).validateNullFieldItem(item);
        verify(validator).validateImageUrlItem(item.getImageUrl());
        verify(itemRepository).save(item);
        verify(itemRepository).flush();
        item = setItemWithId();

        assertEquals(item, returned);

        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void insertItemImageUrlNullFieldSuccess() {
        Item item = setItemWithId();
        item.setId(null);
        item.setImageUrl(null);
        mockValidateImageUrl(true, anyString());
        mockSaveItem(item);
        Item returned = itemService.saveItem(item);
        verify(validator).validateNullFieldItem(item);
        verify(validator).validateImageUrlItem(anyString());
        verify(itemRepository).save(item);
        verify(itemRepository).flush();
        item = setItemWithId();

        assertEquals(item, returned);

        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void insertItemImageUrlNotValidFailed() {
        Item item = setItemWithId();
        item.setId(null);
        item.setImageUrl("abc");
        mockValidateImageUrl(false, anyString());
        mockSaveItem(item);
        try {
            itemService.saveItem(item);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldItem(item);
            verify(validator).validateImageUrlItem(anyString());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void insertItemNameNullAndImageUrlNullFailed() {
        Item item = setItemWithId();
        item.setId(null);
        mockNullFieldItem(true);
        mockValidateImageUrl(true, item.getImageUrl());
        mockSaveItem(item);
        try {
            itemService.saveItem(item);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldItem(item);
            verify(validator).validateImageUrlItem(item.getImageUrl());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void editItemImageUrlNullStringSuccess() {
        Item item = setItemWithId();
        mockValidateId(true, item.getId());
        mockFindItemById(true, item.getId());
        mockValidateImageUrl(true, item.getImageUrl());
        mockMapItem(false, item);
        mockSaveItem(item);
        Item returned = itemService.saveItem(item);
        assertEquals(item, returned);
        verify(validator).validateNullFieldItem(item);
        verify(validator).validateIdFormatEntity(item.getId(), "IM");
        verify(itemRepository).save(item);
        verify(itemRepository).flush();
    }

    @Test
    public void editItemIdValidNotFoundImageUrlNullStringSuccess() {
        Item item = setItemWithId();
        mockValidateId(true, item.getId());
        mockFindItemById(false, item.getId());
        try {
            itemService.saveItem(item);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(validator).validateNullFieldItem(item);
            verify(validator).validateIdFormatEntity(item.getId(), "IM");
            verify(itemRepository).findById(item.getId());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void editItemIdNotValidImageUrlNullStringSuccess() {
        Item item = setItemWithId();
        mockValidateId(false, item.getId());
        try {
            itemService.saveItem(item);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(validator).validateNullFieldItem(item);
            verify(validator).validateIdFormatEntity(item.getId(), "IM");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(itemRepository);
        }
    }

    @Test
    public void itemChangeQtyBasedOnAssignmentSuccess() {
        Assignment assignment = setAssignment(2);
        mockValidateId(true, assignment.getItem().getId());
        mockFindItemById(true, assignment.getItem().getId());
        itemService.changeItemQty(assignment);
        verify(validator).validateIdFormatEntity(assignment.getItem().getId(), "IM");
        verify(itemRepository).findById(assignment.getItem().getId());
        verify(itemRepository).save(any(Item.class));
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void itemChangeQtyBasedOnAssignmentQtyMoreThanItemFailed() {
        Assignment assignment = setAssignment(10);
        mockValidateId(true, assignment.getItem().getId());
        mockFindItemById(true, assignment.getItem().getId());
        try {
            itemService.changeItemQty(assignment);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(assignment.getItem().getId(), "IM");
            verify(itemRepository).findById(assignment.getItem().getId());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void itemChangeQtyBasedOnAssignmentItemQtyZeroFailed() {
        Assignment assignment = setAssignment(2);
        mockValidateId(true, assignment.getItem().getId());
        mockFindItemQtyZeroById(true, assignment.getItem().getId());
        try {
            itemService.changeItemQty(assignment);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(assignment.getItem().getId(), "IM");
            verify(itemRepository).findById(assignment.getItem().getId());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void recoverItemQtyItemValidFoundSuccess() {
        Map<String, Integer> recoveredItems = setRecoverItemMap();
        for (Map.Entry<String, Integer> entry : recoveredItems.entrySet()) {
            mockValidateId(true, entry.getKey());
            mockFindItemById(true, entry.getKey());
        }
        assertEquals("Recover success", itemService.recoverItemQty(recoveredItems));
        verify(validator).validateIdFormatEntity(anyString(), anyString());
        verify(itemRepository).findById(anyString());
        verify(itemRepository).save(any(Item.class));
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void recoverItemQtyItemValidNotFoundFailed() {
        Map<String, Integer> recoveredItems = setRecoverItemMap();
        for (Map.Entry<String, Integer> entry : recoveredItems.entrySet()) {
            mockValidateId(true, entry.getKey());
            mockFindItemById(false, entry.getKey());
        }
        try {
            itemService.recoverItemQty(recoveredItems);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(anyString(), anyString());
            verify(itemRepository).findById(anyString());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void recoverItemQtyItemNotValidFailed() {
        Map<String, Integer> recoveredItems = setRecoverItemMap();
        for (Map.Entry<String, Integer> entry : recoveredItems.entrySet()) {
            mockValidateId(false, entry.getKey());
        }
        try {
            itemService.recoverItemQty(recoveredItems);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(anyString(), anyString());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void deleteItemValidFoundNoPendingAssignmentSuccess() {
        List<String> ids = new ArrayList<>();
        ids.add("IM001");
        mockItemPendingAssignment(false, ids.get(0));
        mockValidateId(true, ids.get(0));
        mockFindItemById(true, ids.get(0));
        assertEquals("Delete Success", itemService.deleteItem(ids));
        verify(validator).validateIdFormatEntity(ids.get(0), "IM");
        verify(itemRepository).findById(ids.get(0));
        verify(itemRepository).deleteById(ids.get(0));
        verify(assignmentService).getAssignmentCountByItemIdAndStatus(ids.get(0), "Pending");
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(assignmentService);
    }

    @Test
    public void deleteItemValidNotFoundFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("IM001");
        mockValidateId(true, ids.get(0));
        mockFindItemById(false, ids.get(0));
        try {
            itemService.deleteItem(ids);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(ids.get(0), "IM");
            verify(itemRepository).findById(ids.get(0));
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
            verifyZeroInteractions(assignmentService);
        }
    }

    @Test
    public void deleteItemNotValidFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        mockValidateId(false, ids.get(0));
        try {
            itemService.deleteItem(ids);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(ids.get(0), "IM");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(itemRepository);
            verifyZeroInteractions(assignmentService);
        }
    }

    @Test
    public void deleteItemValidFoundPendingAssignmentFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("IM001");
        mockItemPendingAssignment(true, ids.get(0));
        mockValidateId(true, ids.get(0));
        mockFindItemById(true, ids.get(0));
        try {
            itemService.deleteItem(ids);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(validator).validateIdFormatEntity(ids.get(0), "IM");
            verify(itemRepository).findById(ids.get(0));
            verify(assignmentService).getAssignmentCountByItemIdAndStatus(ids.get(0), "Pending");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
            verifyNoMoreInteractions(assignmentService);
        }
    }

    @Test
    public void getImageWithValidUrlSuccess() {
        String url = "something valid";
        mockValidExistImageUrl(true, url);
        itemService.getItemImage(url);
        verify(validator).validateImageUrlExist(url);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void getImageNotValidUrlFailed() {
        String url = "something valid";
        mockValidExistImageUrl(false, url);
        try {
            itemService.getItemImage(url);
        } catch (RuntimeException e) {
            verify(validator).validateImageUrlExist(url);
            verifyNoMoreInteractions(validator);
        }
    }

    @Test
    public void uploadImageWithItemIdValidFoundSuccess() {
        MultipartFile file = null;
        Item item = setItemWithId();
        item.setImageUrl("something valid");
        mockValidateId(true, item.getId());
        mockFindItemById(true, item.getId());
        mockValidateImageUrl(true, item.getImageUrl());
        try {
            itemService.uploadFile(file, item.getId());
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(item.getId(), "IM");
            verify(itemRepository).findById(item.getId());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(itemRepository);
        }
    }

    @Test
    public void getPdf() {
        Item item = setItemWithId();
        try {
            itemService.getPdf(item);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void mockValidExistImageUrl(boolean valid, String url) {
        when(validator.validateImageUrlExist(url))
                .thenReturn(valid ? true : false);
    }

    private void mockValidImageUlr(boolean valid, String url) {
        when(validator.validateImageUrlItem(url))
                .thenReturn(valid);
    }

    private void mockItemPendingAssignment(boolean found, String itemId) {
        when(assignmentService.getAssignmentCountByItemIdAndStatus(itemId, "Pending"))
                .thenReturn(found ? 1.0 : 0.0);
    }

    private Map<String, Integer> setRecoverItemMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("IM001", 2);
        return map;
    }

    private Assignment setAssignment(int qty) {
        Assignment assignment = new Assignment();
        assignment.setId("AM001");
        assignment.setEmployee(new Employee());
        assignment.setItem(setItemWithId());
        assignment.setQty(qty);
        assignment.setStatus("Pending");
        assignment.setNotes("");
        return assignment;
    }

    private void mockSaveItem(Item item) {
        when(itemRepository.save(any(Item.class))).thenReturn(setItemWithId());
    }

    private void mockNullFieldItem(boolean found) {
        when(validator.validateNullFieldItem(any(Item.class)))
                .thenReturn(found ? "something" : null);
    }

    private void mockValidateImageUrl(boolean valid, String url) {
        when(validator.validateImageUrlItem(url))
                .thenReturn(valid ? true : false);
    }

    private void mockMapItem(boolean isNull, Item item) {
        when(mapper.map(item, Item.class))
                .thenReturn(isNull ? null : item);
    }

    private void mockItemListBasedOnNameAndPaging() {
        when(itemRepository.findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(anyString(), anyString()
                , any(Pageable.class)))
                .thenReturn(itemPageList);
    }

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "IM"))
                .thenReturn(valid ? true : false);
    }

    private void setPaging(String sortedType) {
        this.paging.setPageNumber(1);
        this.paging.setPageSize(3);
        this.paging.setSortedBy("updatedDate");
        this.paging.setSortedType(sortedType);
    }

    private void mockFindItemById(boolean found, String id) {
        Item item = setItemWithId();
        if (found)
            when(itemRepository.findById(id))
                    .thenReturn(Optional.ofNullable(item));
        else
            when(itemRepository.findById(id))
                    .thenReturn(null);
    }

    private void mockFindItemQtyZeroById(boolean found, String id) {
        Item item = setItemWithId();
        item.setQty(0);
        if (found)
            when(itemRepository.findById(id))
                    .thenReturn(Optional.ofNullable(item));
        else
            when(itemRepository.findById(id))
                    .thenReturn(null);
    }

    private Item setItemWithId() {
        Item item = new Item();
        item.setId("IM001");
        item.setName("Macbook Pro 15");
        item.setPrice(10000000);
        item.setQty(9);
        item.setLocation("Thamrin Office");
        item.setImageUrl("null");
        return item;
    }
}
