package com.inventory.services.admin;

import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.exceptions.EntityNullFieldException;
import com.inventory.services.utils.exceptions.admin.AdminAlreadyExistException;
import com.inventory.services.utils.exceptions.admin.AdminFieldWrongFormatException;
import com.inventory.services.utils.exceptions.admin.AdminNotFoundException;
import com.inventory.services.utils.validators.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.inventory.services.utils.constants.ExceptionConstant.ID_WRONG_FORMAT_ERROR;
import static com.inventory.services.utils.constants.ExceptionConstant.MEMBER_EMAIL_WRONG_FORMAT_ERROR;

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

    private static final String ADMIN_ID_PREFIX = "AD";

    @Override
    @Transactional
    public Admin getAdminByEmail(String email) {
        if (!validator.validateEmailFormatMember(email))
            throw new AdminFieldWrongFormatException(MEMBER_EMAIL_WRONG_FORMAT_ERROR);
        Admin admin = adminRepository.findByEmail(email);
        if (admin == null)
            throw new AdminNotFoundException(email, "Email");
        return admin;
    }

    @Override
    @Transactional
    public Admin getAdmin(String id) {
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
            admin = adminRepository.findByEmail(
                    email);
        if (admin == null)
            throw new AdminNotFoundException(email, "Email");
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
    public Admin saveAdmin(Admin request) {

        String nullFieldAdmin = validator.validateNullFieldAdmin(request);

        Admin admin;

        if (request.getId() != null) {
            String password = this.getAdmin(request.getId()).getPassword();
            if (request.getPassword() != null)
                password = encoder.encode(request.getPassword());
            admin = mapper.map(request, Admin.class);
            admin.setPassword(password);

        } else {
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

    @Override
    @Transactional
    public String deleteAdmin(List<String> ids) {
        for (String id : ids) {
            this.getAdmin(id);
            adminRepository.deleteById(id);
        }
        return "Delete success!";
    }
}
