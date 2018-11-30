package com.inventory.services.validators;

import com.inventory.models.Assignment;

public class AssignmentValidator extends EntityValidator {

    public boolean validateStatus(String status) {
        if (!status.equals("Pending") && !status.equals("Approved") &&
                !status.equals("Rejected") && !status.equals("Handover"))
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
