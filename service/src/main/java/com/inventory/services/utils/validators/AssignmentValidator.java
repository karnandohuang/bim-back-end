package com.inventory.services.utils.validators;

import com.inventory.models.entity.Assignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentValidator extends EntityValidator {

    private final static String PENDING = "Pending";
    private final static String APPROVED = "Approved";
    private final static String HANDOVER = "Received";
    private final static String REJECTED = "Rejected";

    public boolean validateChangeStatus(String status, String changedStatus) {
        if (status.equals(PENDING) && changedStatus.equals(HANDOVER))
            return false;
        return true;
    }

    public boolean validateStatus(String status) {
        if (!status.equals(PENDING) && !status.equals(APPROVED) &&
                !status.equals(REJECTED) && !status.equals(HANDOVER))
            return false;
        return true;
    }

    public String validateNullFieldAssignment(Assignment assignment) {
        if (assignment.getEmployee() == null)
            return "Employee id is empty";
        else if (assignment.getItem() == null)
            return "Item id is empty";
        else if (assignment.getQty() == 0)
            return "Qty is empty";
        return null;
    }
}
