package nhom13.vn.dao;

import java.util.List;

import nhom13.vn.entity.LeaveType;

public interface ILeaveTypeDao {

    List<LeaveType> findAllOrderByCode();

    List<LeaveType> findAllActiveOrderByCode();

    LeaveType findById(int id);

    LeaveType findByCode(String code);

    long countUsage(int leaveTypeId);

    void insert(LeaveType leaveType);

    void update(LeaveType leaveType);

    void delete(LeaveType leaveType);
}
