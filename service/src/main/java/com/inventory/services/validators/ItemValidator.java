//package com.inventory.services.validators;
//
//import com.inventory.models.Item;
//
//import java.io.File;
//
//import static com.inventory.services.ValidationConstant.*;
//
//public class ItemValidator implements EntityValidator {
//
//    public boolean validateImageUrlItem(String url){
//        if(url == null)
//            return false;
//        else{
//            File dir = new File(url);
//            if(!dir.exists())
//                return false;
//            return true;
//        }
//    }
//
//    public String validateNullFieldEmployee(Item item){
//        if(item.getName() == null)
//            return ITEM_NAME_EMPTY;
//        if(item.getSku() == null)
//            return ITEM_SKU_EMPTY;
////        if(item.getQty() == null)
////            return ITEM_QTY_EMPTY;
////        if(item.getPrice() == null)
////            return ITEM_PRICE_EMPTY;
//        if(item.getLocation() == null)
//            return ITEM_LOCATION_EMPTY;
//        if(item.getImageUrl() == null)
//            return ITEM_IMAGE_EMPTY;
//        return null;
//    }
//}
