package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;
import com.payment.entities.Employee;
import com.payment.entities.Organization;
import com.payment.entities.SalaryComponent;
import com.payment.entities.SalaryStructure;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.EmployeeRepo;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.SalaryStructureRepo;
import com.payment.security.SecurityUtil;

@Service
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

	@Autowired private SalaryStructureRepo salaryStructureRepository;
	@Autowired private EmployeeRepo employeeRepository;
	@Autowired private OrganizationRepo organizationRepository;
	@Autowired private SecurityUtil securityUtil;

    @Override
    public SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request,Long orgId) {
        // Long organizationId = securityUtil.getCurrentOrganizationId(); // ✅ from JWT
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // ✅ Check duplicate for same period
        Optional<SalaryStructure> existing = salaryStructureRepository
                .findByEmployee_EmployeeIdAndPeriodMonthAndPeriodYear(request.getEmployeeId(),
                        currentMonth, currentYear);

        if (existing.isPresent()) {
            throw new IllegalStateException("Salary Structure already exists for this employee and period");
        }

        // ✅ Calculate salary components
        SalaryComponent component = calculateSalaryComponents(employee.getSalary());

        // ✅ Build SalaryStructure
        SalaryStructure structure = new SalaryStructure();
        structure.setEmployee(employee);
        structure.setOrganization(organization);
        structure.setSalaryComponent(component);
        structure.setStatus("Drafted");
//        structure.setCreatedAt(LocalDate.now());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return mapToResponse(saved);
    }

    @Override
    public SalaryStructureResponse updateSalaryStructure(Long slipId,Long orgId) {

        SalaryStructure structure = salaryStructureRepository.findById(slipId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));

        Employee employee = structure.getEmployee();

        if(employee.getOrganization().getOrganizationId() != orgId){
            throw new RuntimeException("You can only edit salary of your organization");
        }

        // ✅ Recalculate components based on updated employee salary
        SalaryComponent updatedComponent = calculateSalaryComponents(employee.getSalary());

        SalaryComponent existingComponent = structure.getSalaryComponent();
        existingComponent.setBasicSalary(updatedComponent.getBasicSalary());
        existingComponent.setHra(updatedComponent.getHra());
        existingComponent.setDa(updatedComponent.getDa());
        existingComponent.setPf(updatedComponent.getPf());
        existingComponent.setOtherAllowances(updatedComponent.getOtherAllowances());
        existingComponent.setNetSalary(updatedComponent.getNetSalary());

        structure.setCreatedAt(LocalDate.now());
        structure.setStatus("Updated");

        SalaryStructure updated = salaryStructureRepository.save(structure);
        return mapToResponse(updated);
    }

    // ✅ Helper method
    private SalaryComponent calculateSalaryComponents(BigDecimal totalSalary) {
        SalaryComponent sc = new SalaryComponent();

        BigDecimal basic = totalSalary.multiply(BigDecimal.valueOf(0.40));
        BigDecimal hra = totalSalary.multiply(BigDecimal.valueOf(0.20));
        BigDecimal da = totalSalary.multiply(BigDecimal.valueOf(0.15));
        BigDecimal pf = totalSalary.multiply(BigDecimal.valueOf(0.10));
        BigDecimal others = totalSalary.subtract(basic.add(hra).add(da).add(pf));

        sc.setBasicSalary(basic);
        sc.setHra(hra);
        sc.setDa(da);
        sc.setPf(pf);
        sc.setOtherAllowances(others);
        sc.setNetSalary(totalSalary);
        return sc;
    }

    private SalaryStructureResponse mapToResponse(SalaryStructure s) {
        SalaryStructureResponse dto = new SalaryStructureResponse();
        dto.setSlipId(s.getSlipId());
        dto.setEmployeeName(s.getEmployee().getEmployeeName());
        dto.setDepartment(s.getEmployee().getDepartment());
        dto.setPeriodMonth(s.getPeriodMonth());
        dto.setPeriodYear(s.getPeriodYear());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setStatus(s.getStatus());

        SalaryComponent c = s.getSalaryComponent();
        dto.setBasicSalary(c.getBasicSalary());
        dto.setHra(c.getHra());
        dto.setDa(c.getDa());
        dto.setPf(c.getPf());
        dto.setOtherAllowances(c.getOtherAllowances());
        dto.setNetSalary(c.getNetSalary());

        return dto;
    }
}
