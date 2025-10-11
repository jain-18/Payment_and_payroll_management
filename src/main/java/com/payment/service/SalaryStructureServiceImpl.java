package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;
import com.payment.entities.Employee;
import com.payment.entities.Organization;
import com.payment.entities.Request;
import com.payment.entities.SalaryComponent;
import com.payment.entities.SalaryStructure;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repo.EmployeeRepo;
import com.payment.repo.OrganizationRepo;
import com.payment.repo.RequestRepo;
import com.payment.repo.SalaryStructureRepo;
import com.payment.security.SecurityUtil;

@Service
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    @Autowired
    private SalaryStructureRepo salaryStructureRepository;
    @Autowired
    private EmployeeRepo employeeRepository;
    @Autowired
    private OrganizationRepo organizationRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private OrganizationRepo organizationRepo;
    @Autowired
    private RequestRepo requestRepo;

    @Override
    public SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request, Long orgId) {
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
        // structure.setCreatedAt(LocalDate.now());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return mapToResponse(saved);
    }

    @Override
    public SalaryStructureResponse updateSalaryStructure(Long slipId, Long orgId) {

        SalaryStructure structure = salaryStructureRepository.findById(slipId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));

        Employee employee = structure.getEmployee();

        if (employee.getOrganization().getOrganizationId() != orgId) {
            throw new IllegalStateException("You can only edit salary of your organization");
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

    @Override
    @Transactional
    public void sendRequestToAdmin(Long orgId) {
        // 1. Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        List<SalaryStructure> salaryStructures = salaryStructureRepository.findAllByOrganizationOrganizationId(orgId)
                .stream()
                .filter(s -> "DRAFTED".equalsIgnoreCase(s.getStatus()) && s.getOrganization().equals(organization))
                .toList();

        if (salaryStructures.isEmpty()) {
            throw new IllegalStateException("No pending salary payments found for this organization");
        }

        // 3. Calculate total salary
        BigDecimal totalSalary = salaryStructures.stream()
                .map(s -> s.getSalaryComponent().getNetSalary())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Check for duplicate request in the current month with the same amount
        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        boolean duplicateExists = requestRepo.existsByOrganizationAndRequestStatusInAndRequestDateBetweenAndTotalAmount(
                organization,
                List.of("PENDING", "APPROVED"),
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth(),
                totalSalary);

        if (duplicateExists) {
            throw new IllegalStateException("A salary request for this month and amount already exists.");
        }

        // 5. Create a new Request entity
        Request request = new Request();
        request.setCreatedBy(organization.getOrganizationName());
        request.setOrganization(organization);
        request.setRequestDate(LocalDate.now());
        request.setRequestStatus("PENDING");
        request.setRequestType("SalaryPayment");
        request.setTotalAmount(totalSalary);

        Request savedRequest = requestRepo.save(request);

        // 6. Assign request to all unpaid salary structures
        salaryStructures.forEach(ss -> {
            ss.setRequest(savedRequest);
            ss.setStatus("PENDING");
        });

        salaryStructureRepository.saveAll(salaryStructures);
    }

    @Override
    @Transactional
    public void sendRequestUpdateToAdmin(Long orgId) {
        // 1. Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        // 2. Fetch all salary structures with status UPDATED
        List<SalaryStructure> updatedStructures = salaryStructureRepository
                .findAllByOrganizationOrganizationId(orgId)
                .stream()
                .filter(s -> "UPDATED".equalsIgnoreCase(s.getStatus()))
                .toList();

        if (updatedStructures.isEmpty()) {
            throw new IllegalStateException("No updated salary structures found for this organization");
        }

        // 3. Calculate total updated salary amount
        BigDecimal totalSalary = updatedStructures.stream()
                .map(s -> s.getSalaryComponent().getNetSalary())
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // 4. Create a new Request for the updated salaries
        Request newRequest = new Request();
        newRequest.setCreatedBy(organization.getOrganizationName());
        newRequest.setOrganization(organization);
        newRequest.setRequestDate(LocalDate.now());
        newRequest.setRequestStatus("PENDING");
        newRequest.setRequestType("SalaryUpdatePayment");
        newRequest.setTotalAmount(totalSalary);

        Request savedRequest = requestRepo.save(newRequest);

        // 5. Assign the new request to all UPDATED salary structures
        for (SalaryStructure ss : updatedStructures) {
            ss.setRequest(savedRequest);
            ss.setStatus("PENDING"); // reset status to PENDING
            salaryStructureRepository.save(ss);
        }
    }

}
