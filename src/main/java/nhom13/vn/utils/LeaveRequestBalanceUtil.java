package nhom13.vn.utils;

import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.LeaveType;

public final class LeaveRequestBalanceUtil {

    private LeaveRequestBalanceUtil() {
    }

    /**
     * Legacy rows without a {@link LeaveType} still consume the annual pool (backward compatible).
     */
    public static boolean consumesAnnualPool(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            return false;
        }
        LeaveType type = leaveRequest.getLeaveType();
        if (type == null) {
            return true;
        }
        return type.isConsumesBalance();
    }
}
