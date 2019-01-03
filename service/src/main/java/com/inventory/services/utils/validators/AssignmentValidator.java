package com.inventory.services.utils.validators;

import com.inventory.models.entity.Assignment;
import org.springframework.stereotype.Component;

import static com.inventory.services.utils.constants.ValidationConstant.*;

@Component
public class AssignmentValidator extends EntityValidator {

    private final static String PENDING = "Pending";
    private final static String APPROVED = "Approved";
    private final static String HANDOVER = "Received";
    private final static String REJECTED = "Rejected";

    public boolean validateChangeStatus(String status, String changedStatus) {
        if (status == null || changedStatus == null)
            return false;
        else if (status.equals(PENDING) && changedStatus.equals(HANDOVER))
            return false;
        else if ((status.equals(APPROVED) || status.equals(REJECTED)) && changedStatus.equals(PENDING))
            return false;
        else if (status.equals(HANDOVER) && !changedStatus.equals(HANDOVER))
            return false;
        return true;
    }

    public boolean validateStatus(String status) {
        if ((!status.equals(PENDING) && !status.equals(APPROVED) &&
                !status.equals(REJECTED) && !status.equals(HANDOVER)))
            return false;
        return true;
    }

    public String validateNullFieldAssignment(Assignment assignment) {
        if (assignment.getEmployee() == null)
            return ASSIGNMENT_EMPLOYEE_EMPTY;
        else if (assignment.getItem() == null)
            return ASSIGNMENT_ITEM_EMTPTY;
        else if (assignment.getQty() <= 0)
            return ASSIGNMENT_QTY_EMPTY;
        return null;
    }
}
