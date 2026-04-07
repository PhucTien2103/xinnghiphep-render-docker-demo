package nhom13.vn.factory;

import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.entity.User;

import java.sql.Date;

public class LeaveRequestFactory {

    public static LeaveRequest create(User user, String start, String end, String reason, LeaveType leaveType) {

        LeaveRequest lr = new LeaveRequest();

        lr.setUser(user);
        lr.setLeaveType(leaveType);
        lr.setStartDate(Date.valueOf(start));
        lr.setEndDate(Date.valueOf(end));
        lr.setReason(reason);

        lr.setStatus("PENDING");

        return lr;
    }
}