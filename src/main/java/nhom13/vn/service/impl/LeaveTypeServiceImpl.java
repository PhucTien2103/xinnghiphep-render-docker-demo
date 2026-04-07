package nhom13.vn.service.impl;

import java.util.List;

import nhom13.vn.dao.ILeaveTypeDao;
import nhom13.vn.dao.impl.LeaveTypeDaoImpl;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.service.ILeaveBalanceService;
import nhom13.vn.service.ILeaveTypeService;

public class LeaveTypeServiceImpl implements ILeaveTypeService {

    private static final int FALLBACK_ANNUAL_DAYS = 12;

    private static LeaveTypeServiceImpl instance;

    private final ILeaveTypeDao leaveTypeDao;
    private final ILeaveBalanceService leaveBalanceService;

    private LeaveTypeServiceImpl() {
        this.leaveTypeDao = new LeaveTypeDaoImpl();
        this.leaveBalanceService = LeaveBalanceServiceImpl.getInstance();
    }

    public static LeaveTypeServiceImpl getInstance() {
        if (instance == null) {
            instance = new LeaveTypeServiceImpl();
        }
        return instance;
    }

    @Override
    public void ensureSystemDefaults() {
        upsertDefaultIfMissing(LeaveType.CODE_ANNUAL, "Nghỉ phép năm", true, FALLBACK_ANNUAL_DAYS);
        upsertDefaultIfMissing("SICK", "Nghỉ ốm", false, 0);
        upsertDefaultIfMissing("UNPAID", "Nghỉ không lương", false, 0);
    }

    private void upsertDefaultIfMissing(String code, String name, boolean consumesBalance, int defaultDaysPerYear) {
        LeaveType existing = leaveTypeDao.findByCode(code);
        if (existing != null) {
            return;
        }
        LeaveType lt = new LeaveType();
        lt.setCode(code.trim().toUpperCase());
        lt.setName(name);
        lt.setConsumesBalance(consumesBalance);
        lt.setDefaultDaysPerYear(Math.max(0, defaultDaysPerYear));
        lt.setActive(true);
        leaveTypeDao.insert(lt);
    }

    @Override
    public List<LeaveType> findAll() {
        return leaveTypeDao.findAllOrderByCode();
    }

    @Override
    public List<LeaveType> findAllActive() {
        return leaveTypeDao.findAllActiveOrderByCode();
    }

    @Override
    public LeaveType findById(int id) {
        return leaveTypeDao.findById(id);
    }

    @Override
    public LeaveType findByCode(String code) {
        return leaveTypeDao.findByCode(code);
    }

    @Override
    public void create(LeaveType leaveType) {
        if (leaveType == null) {
            throw new IllegalArgumentException("leaveType is required");
        }
        String code = normalizeCode(leaveType.getCode());
        leaveType.setCode(code);
        if (leaveTypeDao.findByCode(code) != null) {
            throw new IllegalStateException("Leave type code already exists: " + code);
        }
        if (leaveType.getName() == null || leaveType.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        leaveType.setName(leaveType.getName().trim());
        if (leaveType.getDefaultDaysPerYear() < 0) {
            leaveType.setDefaultDaysPerYear(0);
        }
        leaveTypeDao.insert(leaveType);
    }

    @Override
    public void update(LeaveType incoming) {
        if (incoming == null || incoming.getId() <= 0) {
            throw new IllegalArgumentException("Invalid leave type");
        }
        LeaveType existing = leaveTypeDao.findById(incoming.getId());
        if (existing == null) {
            throw new IllegalStateException("Leave type not found");
        }

        int previousAnnualDays = existing.getDefaultDaysPerYear();
        boolean isAnnual = LeaveType.CODE_ANNUAL.equalsIgnoreCase(existing.getCode());

        if (incoming.getName() == null || incoming.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        existing.setName(incoming.getName().trim());
        existing.setConsumesBalance(incoming.isConsumesBalance());
        int newDefaultDays = Math.max(0, incoming.getDefaultDaysPerYear());
        existing.setDefaultDaysPerYear(newDefaultDays);
        existing.setActive(incoming.isActive());

        leaveTypeDao.update(existing);

        if (isAnnual && previousAnnualDays != newDefaultDays) {
            leaveBalanceService.applyAnnualPolicyDayDelta(newDefaultDays - previousAnnualDays);
        }
    }

    @Override
    public boolean deleteById(int id) {
        LeaveType existing = leaveTypeDao.findById(id);
        if (existing == null) {
            return false;
        }
        if (existing.isActive()) {
            return false;
        }
        if (leaveTypeDao.countUsage(id) > 0) {
            return false;
        }
        leaveTypeDao.delete(existing);
        return true;
    }

    private static String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code is required");
        }
        return code.trim().toUpperCase();
    }
}
