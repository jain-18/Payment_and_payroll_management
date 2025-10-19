package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.dto.SalaryRequestOfMonth;
import com.payment.dto.SalarySlip;
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
        request.setRequestType("SALARYPAYMENT");
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
        newRequest.setRequestType("SALARYPAYMENT");
        newRequest.setTotalAmount(totalSalary);

        Request savedRequest = requestRepo.save(newRequest);

        // 5. Assign the new request to all UPDATED salary structures
        for (SalaryStructure ss : updatedStructures) {
            ss.setRequest(savedRequest);
            ss.setStatus("PENDING"); // reset status to PENDING
            salaryStructureRepository.save(ss);
        }
    }

    @Override
    public Page<SalaryRequestOfMonth> getAllSalarySlip(Long orgId, String status, PageRequest pageable) {
        Page<SalaryStructure> slips;

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        if (status == null || status.isBlank()) {
            // ✅ If no status provided → fetch slips for current month & year only
            slips = salaryStructureRepository.findByOrganizationOrganizationIdAndPeriodMonthAndPeriodYear(
                    orgId, currentMonth, currentYear, pageable);
        } else {
            // ✅ If status provided → fetch slips for that status (irrespective of month)
            slips = salaryStructureRepository.findByOrganizationOrganizationIdAndStatusIgnoreCase(
                    orgId, status, pageable);
        }

        return slips.map(slip -> {
            SalaryRequestOfMonth res = new SalaryRequestOfMonth();
            res.setSlipId(slip.getSlipId());
            res.setEmployeeId(slip.getEmployee().getEmployeeId());
            res.setEmployee(slip.getEmployee().getEmployeeName());
            res.setSalary(slip.getSalaryComponent().getNetSalary());
            res.setStatus(slip.getStatus());
            res.setPeriodMonth(slip.getPeriodMonth());
            res.setPeriodYear(slip.getPeriodYear());
            return res;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalarySlip> getSalarySlip(Long orgId, Long empId, String month, String year) {
        // 1️⃣ Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        List<SalaryStructure> structures;

        // 2️⃣ Determine which query to run
        boolean hasMonth = month != null && !month.trim().isEmpty();
        boolean hasYear = year != null && !year.trim().isEmpty();

        if (hasMonth && hasYear) {
            // Specific month/year → single record
            int periodMonth;
            int periodYear;
            try {
                periodMonth = Integer.parseInt(month);
                periodYear = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Month and Year must be numeric values");
            }

            SalaryStructure structure = salaryStructureRepository
                    .findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodMonthAndPeriodYear(
                            orgId, empId, periodMonth, periodYear)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No salary structure found for employee " + empId + " for " + month + "/" + year));

            structures = List.of(structure);

        } else if (hasYear) {
            // All slips for a specific year
            int periodYear;
            try {
                periodYear = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Year must be numeric");
            }

            structures = salaryStructureRepository
                    .findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodYear(orgId, empId, periodYear);

            if (structures.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No salary slips found for employee " + empId + " for year " + year);
            }

        } else {
            // No month/year → all salary slips
            structures = salaryStructureRepository
                    .findByOrganizationOrganizationIdAndEmployeeEmployeeId(orgId, empId);

            if (structures.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No salary slips found for employee " + empId);
            }
        }

        // 3️⃣ Convert structures to SalarySlip DTOs
        return structures.stream().map(structure -> {
            SalaryComponent component = structure.getSalaryComponent();
            Employee employee = structure.getEmployee();

            SalarySlip slip = new SalarySlip();
            slip.setSlipId(structure.getSlipId());
            slip.setEmployeeName(employee.getEmployeeName());
            slip.setOrganizationName(organization.getOrganizationName());
            slip.setBasicSalary(component.getBasicSalary());
            slip.setHra(component.getHra());
            slip.setDa(component.getDa());
            slip.setPf(component.getPf());
            slip.setOtherAllowances(component.getOtherAllowances());
            slip.setNetSalary(component.getNetSalary());
            slip.setPeriodMonth(structure.getPeriodMonth());
            slip.setPeriodYear(structure.getPeriodYear());

            return slip;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalarySlip> getSalarySlipWithPagination(
            Long orgId,
            Long empId,
            String month,
            String year,
            PageRequest pageable) {
        // 1️⃣ Validate organization
        Organization organization = organizationRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with id " + orgId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization is not active for this operation");
        }

        Page<SalaryStructure> structures;

        // 2️⃣ Determine query conditions
        boolean hasMonth = month != null && !month.trim().isEmpty();
        boolean hasYear = year != null && !year.trim().isEmpty();

        // 3️⃣ Month + Year given → find exact slip
        if (hasMonth && hasYear) {
            int periodMonth;
            int periodYear;
            try {
                periodMonth = Integer.parseInt(month);
                periodYear = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Month and Year must be numeric values");
            }

            Optional<SalaryStructure> optStructure = salaryStructureRepository
                    .findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodMonthAndPeriodYear(
                            orgId, empId, periodMonth, periodYear);

            if (optStructure.isEmpty()) {
                // No salary structure found → return empty page instead of error
                return Page.empty(pageable);
            }

            // Wrap single result into a Page
            List<SalaryStructure> singleResult = List.of(optStructure.get());
            Page<SalaryStructure> singlePage = new org.springframework.data.domain.PageImpl<>(singleResult, pageable,
                    1);

            return convertToSalarySlipPage(singlePage, organization);
        }

        // 4️⃣ Year only → get all salary slips for that year with pagination
        if (hasYear) {
            int periodYear;
            try {
                periodYear = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Year must be numeric");
            }

            structures = salaryStructureRepository
                    .findByOrganizationOrganizationIdAndEmployeeEmployeeIdAndPeriodYear(
                            orgId, empId, periodYear, pageable);

            if (structures.isEmpty()) {
                return Page.empty(pageable);
            }

            return convertToSalarySlipPage(structures, organization);
        }

        // 5️⃣ No month/year → get all salary slips
        structures = salaryStructureRepository
                .findByOrganizationOrganizationIdAndEmployeeEmployeeId(orgId, empId, pageable);

        if (structures.isEmpty()) {
            return Page.empty(pageable);
        }

        return convertToSalarySlipPage(structures, organization);
    }

    private Page<SalarySlip> convertToSalarySlipPage(Page<SalaryStructure> structures, Organization organization) {
        return structures.map(structure -> {
            SalaryComponent component = structure.getSalaryComponent();
            Employee employee = structure.getEmployee();

            SalarySlip slip = new SalarySlip();
            slip.setSlipId(structure.getSlipId());
            slip.setEmployeeName(employee.getEmployeeName());
            slip.setOrganizationName(organization.getOrganizationName());
            slip.setBasicSalary(component.getBasicSalary());
            slip.setHra(component.getHra());
            slip.setDa(component.getDa());
            slip.setPf(component.getPf());
            slip.setOtherAllowances(component.getOtherAllowances());
            slip.setNetSalary(component.getNetSalary());
            slip.setPeriodMonth(structure.getPeriodMonth());
            slip.setPeriodYear(structure.getPeriodYear());

            return slip;
        });
    }

     @Override
    @Transactional
    public void createAllSalaryStructure(Long orgId) {
        // 1️⃣ Fetch the organization once
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id " + orgId));

        // 2️⃣ Get all employees in the organization
        List<Employee> employees = employeeRepository.findByOrganizationOrganizationId(orgId);
        if (employees.isEmpty()) {
            throw new IllegalStateException("No employees found for organization ID: " + orgId);
        }

        // 3️⃣ Determine current month & year
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // 4️⃣ Create salary structure for each employee
        for (Employee employee : employees) {

            // 🔸 Check if structure already exists for this employee & period
            boolean exists = salaryStructureRepository
                    .existsByEmployeeEmployeeIdAndPeriodMonthAndPeriodYear(employee.getEmployeeId(), currentMonth, currentYear);

            if (exists) {
                // Skip to avoid duplicate entries
                continue;
            }

            // 🔸 Calculate components based on employee's base salary
            SalaryComponent component = calculateSalaryComponents(employee.getSalary());

            // 🔸 Build structure entity
            SalaryStructure structure = new SalaryStructure();
            structure.setEmployee(employee);
            structure.setOrganization(organization);
            structure.setSalaryComponent(component);
            structure.setPeriodMonth(currentMonth);
            structure.setPeriodYear(currentYear);
            structure.setStatus("DRAFTED");

            // 🔸 Persist structure
            salaryStructureRepository.save(structure);
        }
    }

}
