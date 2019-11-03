package com.inventory.services.validator;

import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Employee;
import com.inventory.models.entity.Item;
import com.inventory.services.utils.validators.AssignmentValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.inventory.services.utils.constants.ValidationConstant.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentValidatorTest {
    @InjectMocks
    private AssignmentValidator validator;

    @Test
    public void validateStatusValidSuccess() {
        String status = "Pending";
        assertTrue(validator.validateStatus(status));
    }

    @Test
    public void validateStatusNotValidFailed() {
        String status = "Accepted";
        assertFalse(validator.validateStatus(status));
    }

    @Test
    public void validateChangeStatusValidSuccess() {
        String actualStatus = "Approved";
        String changedStatus = "Received";
        assertTrue(validator.validateChangeStatus(actualStatus, changedStatus));
    }

    @Test
    public void validateChangeStatusNotValidFailed() {
        String actualStatus = "Approved";
        String changedStatus = "Pending";
        assertFalse(validator.validateChangeStatus(actualStatus, changedStatus));
    }

    @Test
    public void validateChangeStatusNullFailed() {
        String actualStatus = null;
        String changedStatus = "Pending";
        assertFalse(validator.validateChangeStatus(actualStatus, changedStatus));
    }

    @Test
    public void validateChangeStatusAllNullFailed() {
        String actualStatus = null;
        String changedStatus = null;
        assertFalse(validator.validateChangeStatus(actualStatus, changedStatus));
    }

    @Test
    public void validateNullFieldAssignmentNotFoundSuccess() {
        Assignment assignment = setAssignment();
        assertNull(validator.validateNullFieldAssignment(assignment));
    }

    @Test
    public void validateNullFieldAssignmentFoundItemFailed() {
        Assignment assignment = setAssignment();
        assignment.setItem(null);
        assertEquals(ASSIGNMENT_ITEM_EMTPTY, validator.validateNullFieldAssignment(assignment));
    }

    @Test
    public void validateNullFieldAssignmentFoundEmployeeFailed() {
        Assignment assignment = setAssignment();
        assignment.setEmployee(null);
        assertEquals(ASSIGNMENT_EMPLOYEE_EMPTY, validator.validateNullFieldAssignment(assignment));
    }

    @Test
    public void validateNullFieldAssignmentFoundQtyZeroFailed() {
        Assignment assignment = setAssignment();
        assignment.setQty(0);
        assertEquals(ASSIGNMENT_QTY_EMPTY, validator.validateNullFieldAssignment(assignment));
    }

    private Assignment setAssignment() {
        Assignment assignment = new Assignment();
        assignment.setEmployee(new Employee());
        assignment.setItem(new Item());
        assignment.setQty(1);
        return assignment;
    }
}
