import com.inventory.controllers.EmployeeController;
import com.inventory.helpers.EmployeeHelper;
import com.inventory.models.entity.Employee;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.utils.exceptions.employee.EmployeeNotFoundException;
import com.inventory.webmodels.responses.BaseResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeHelper helper;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    public void getEmployeeWithIdSuccess() throws Exception{
        mockEmployeeServiceGetEmployeeWithId(true, "EM001");
        mockGetBaseResponse();
        testBaseResponseExpectTrue("/api/employees/EM001");
    }

    private void testBaseResponseExpectTrue(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success", is(true)));
    }

    private void testBaseResponseExpectFalse(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success", is(false)));
    }

    private void mockGetBaseResponse() {
        BaseResponse response = setBaseResponse(true, "");

        when(helper.getBaseResponse(true, ""))
                .thenReturn(response);
    }

    private BaseResponse setBaseResponse(boolean success, String errorMessage) {
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        response.setPaging(null);
        return response;
    }

    private void mockEmployeeServiceGetEmployeeWithId(boolean found, String id) {
        if(found){
            when(employeeService.getEmployee(id))
                    .thenReturn(setEmployee(id));
        } else {
            when(employeeService.getEmployee(id))
                    .thenThrow(new EmployeeNotFoundException(id, "id"));
        }
    }

    private Employee setEmployee(String id){
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName("Oliver");
        employee.setSuperiorId("-");
        employee.setRole("EMPLOYEE");
        employee.setDivision("IT");
        employee.setPosition("IT Intern");
        employee.setDob("01/01/1998");
        return employee;
    }

}
