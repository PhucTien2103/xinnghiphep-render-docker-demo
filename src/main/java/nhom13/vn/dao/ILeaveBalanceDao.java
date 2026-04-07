package nhom13.vn.dao;

import java.util.List;

import nhom13.vn.entity.LeaveBalance;

public interface ILeaveBalanceDao {
    LeaveBalance findByUserId(int userId);

    List<LeaveBalance> findAll();

    void insert(LeaveBalance leaveBalance);

    void update(LeaveBalance leaveBalance);

    /**
     * Adjusts every balance: {@code total += delta} (never below used), then {@code remaining = total - used}.
     */
    void applyAnnualPolicyDayDelta(int delta);
}

