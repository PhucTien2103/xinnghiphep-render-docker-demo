package nhom13.vn.service;

import java.util.List;

import nhom13.vn.entity.LeaveType;

public interface ILeaveTypeService {

    void ensureSystemDefaults();

    List<LeaveType> findAll();

    List<LeaveType> findAllActive();

    LeaveType findById(int id);

    LeaveType findByCode(String code);

    void create(LeaveType leaveType);

    /**
     * Updates configuration. When {@link LeaveType#CODE_ANNUAL} {@code defaultDaysPerYear} changes,
     * all {@link nhom13.vn.entity.LeaveBalance} rows are adjusted to keep {@code remaining = total - used}.
     */
    void update(LeaveType leaveType);

    /**
     * @return {@code false} when the type is referenced by any leave request
     */
    boolean deleteById(int id);
}
