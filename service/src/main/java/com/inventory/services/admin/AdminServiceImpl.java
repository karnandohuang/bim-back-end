package com.inventory.services.admin;

import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.GeneralMapper;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.admin.AdminAlreadyExistException;
import com.inventory.services.exceptions.admin.AdminFieldWrongFormatException;
import com.inventory.services.exceptions.admin.AdminNotFoundException;
import com.inventory.services.validators.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.inventory.services.constants.ExceptionConstant.ID_WRONG_FORMAT_ERROR;
import static com.inventory.services.constants.ExceptionConstant.MEMBER_EMAIL_WRONG_FORMAT_ERROR;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AdminValidator validator;

    @Autowired
    private GeneralMapper mapper;

    private final static String ADMIN_ID_PREFIX = "AD";

    @Override
    @Transactional
    public Admin getAdminByEmail(String email) {
        try {
            return adminRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new AdminNotFoundException(email, "Email");
        }
    }

    @Override
    @Transactional
    public Admin getAdmin(String id) throws AdminNotFoundException {
        if (!validator.validateIdFormatEntity(id, ADMIN_ID_PREFIX))
            throw new AdminFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        try {
            return adminRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new AdminNotFoundException(id, "Id");
        }
    }

    @Override
    public Boolean login(String email, String password) {
        boolean isEmailValid = validator.validateEmailFormatMember(email);
        if (!isEmailValid)
            return false;
        Admin admin;
        try {
            admin = adminRepository.findByEmail(
                    email);
        } catch (Exception e) {
            return false;
        }
        if (!encoder.matches(password, admin.getPassword()))
            return false;
        return true;
    }

    @Override
    @Transactional
    public List<Admin> getAdminList(Paging paging) {
        List<Admin> listOfAdmin;
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
        listOfAdmin = adminRepository.findAll(pageRequest).getContent();
        float totalRecords = adminRepository.count();
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAdmin;
    }

    private void setPagingTotalRecordsAndTotalPage(Paging paging, float totalRecords) {
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
    }

    @Override
    @Transactional
    public Admin saveAdmin(Admin request) throws RuntimeException {

        String nullFieldAdmin = validator.validateNullFieldAdmin(request);

        Admin admin;

        if (request.getId() != null)

            admin = editAdmin(request);

        else {
            admin = request;

            admin.setPassword(encoder.encode(request.getPassword()));
        }

        Admin isAdminExist = adminRepository.findByEmail(admin.getEmail());

        boolean isEmailValid = validator.validateEmailFormatMember(admin.getEmail());

        if (nullFieldAdmin != null)
            throw new EntityNullFieldException(nullFieldAdmin);

        else if (!isEmailValid)
            throw new AdminFieldWrongFormatException(MEMBER_EMAIL_WRONG_FORMAT_ERROR);

        else if (isAdminExist != null && !isAdminExist.getId().equals(admin.getId()))
            throw new AdminAlreadyExistException(admin.getEmail());

        else
            return adminRepository.save(admin);
    }

    private Admin editAdmin(Admin request) {
        this.getAdmin(request.getId());
        return mapper.map(request, Admin.class);
    }

    @Override
    @Transactional
    public String deleteAdmin(List<String> ids) throws RuntimeException {
        for (String id : ids) {
            this.getAdmin(id);
            adminRepository.deleteById(id);
        }
        return "Delete success!";
    }
}
