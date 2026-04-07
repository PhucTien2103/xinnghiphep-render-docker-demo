package nhom13.vn.service.impl;

import java.time.LocalDate;

import nhom13.vn.dao.ILeaveBalanceDao;
import nhom13.vn.dao.ILeaveTypeDao;
import nhom13.vn.dao.impl.LeaveBalanceDaoImpl;
import nhom13.vn.dao.impl.LeaveTypeDaoImpl;
import nhom13.vn.entity.LeaveBalance;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.entity.User;
import nhom13.vn.service.ILeaveBalanceService;

public class LeaveBalanceServiceImpl implements ILeaveBalanceService {

    private static final int FALLBACK_ANNUAL_DAYS = 12;
    private static LeaveBalanceServiceImpl instance;

    private final ILeaveBalanceDao leaveBalanceDao;
    private final ILeaveTypeDao leaveTypeDao;

    private LeaveBalanceServiceImpl() {
        this.leaveBalanceDao = new LeaveBalanceDaoImpl();
        this.leaveTypeDao = new LeaveTypeDaoImpl();
    }

    public static LeaveBalanceServiceImpl getInstance() {
        if (instance == null) {
            instance = new LeaveBalanceServiceImpl();
        }
        return instance;
    }

    @Override
    public LeaveBalance findByUserId(int userId) {
        return leaveBalanceDao.findByUserId(userId);
    }

    @Override
    public LeaveBalance ensureDefaultForUser(User user) {
        if (user == null) {
            return null;
        }

        String role = user.getRole();
        if (!"EMPLOYEE".equals(role) && !"MANAGER".equals(role)) {
            return null;
        }

        LeaveBalance existing = leaveBalanceDao.findByUserId(user.getId());
        if (existing != null) {
            return existing;
        }

        int defaultDays = resolveDefaultAnnualDays();

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUser(user);
        leaveBalance.setTotalDays(defaultDays);
        leaveBalance.setUsedDays(0);
        leaveBalance.setRemainingDays(defaultDays);
        leaveBalance.setLastResetYear(LocalDate.now().getYear());

        leaveBalanceDao.insert(leaveBalance);

        return leaveBalanceDao.findByUserId(user.getId());
    }

    @Override
    public void updateRemainingDays(int userId, int remainingDays) {
        if (remainingDays < 0) {
            throw new IllegalArgumentException("Remaining days must be >= 0");
        }

        LeaveBalance leaveBalance = leaveBalanceDao.findByUserId(userId);
        if (leaveBalance == null) {
            throw new IllegalStateException("Leave balance not found");
        }

        int usedDays = leaveBalance.getUsedDays();
        leaveBalance.setRemainingDays(remainingDays);
        leaveBalance.setTotalDays(usedDays + remainingDays);

        leaveBalanceDao.update(leaveBalance);
    }

    @Override
    public void applyAnnualPolicyDayDelta(int delta) {
        leaveBalanceDao.applyAnnualPolicyDayDelta(delta);
    }

    private int resolveDefaultAnnualDays() {
        LeaveType annual = leaveTypeDao.findByCode(LeaveType.CODE_ANNUAL);
        if (annual != null && annual.getDefaultDaysPerYear() > 0) {
            return annual.getDefaultDaysPerYear();
        }
        return FALLBACK_ANNUAL_DAYS;
    }
}

