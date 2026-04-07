package nhom13.vn.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.service.ILeaveTypeService;

/**
 * Groups leave requests by configured {@link LeaveType} (active types in DB order) plus {@code Khác}
 * for missing or inactive (legacy) types.
 */
public final class LeaveTypeDistributionUtil {

    public static final String OTHER_LABEL = "Khác";

    private static final String OTHER_CODE = "__OTHER__";

    private LeaveTypeDistributionUtil() {
    }

    public static String displayName(LeaveType lt) {
        if (lt == null) {
            return OTHER_LABEL;
        }
        if (lt.getName() != null && !lt.getName().isBlank()) {
            return lt.getName().trim();
        }
        if (lt.getCode() != null && !lt.getCode().isBlank()) {
            return lt.getCode().trim();
        }
        return OTHER_LABEL;
    }

    private static String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            return OTHER_CODE;
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Keys iteration order: active leave types (sorted by code), then {@link #OTHER_LABEL}.
     */
    public static LinkedHashMap<String, Integer> distribution(List<LeaveRequest> requests,
            ILeaveTypeService leaveTypeService) {
        leaveTypeService.ensureSystemDefaults();
        List<LeaveType> active = leaveTypeService.findAllActive();
        if (active == null) {
            active = List.of();
        }
        List<LeaveType> sorted = new ArrayList<>(active);
        sorted.sort(Comparator.comparing(LeaveType::getCode, String.CASE_INSENSITIVE_ORDER));
        Set<Integer> activeIds = sorted.stream().map(LeaveType::getId).collect(Collectors.toSet());

        Map<String, Integer> byCode = new HashMap<>();
        for (LeaveType t : sorted) {
            byCode.put(normalizeCode(t.getCode()), 0);
        }
        byCode.put(OTHER_CODE, 0);

        if (requests != null) {
            for (LeaveRequest lr : requests) {
                LeaveType lt = lr.getLeaveType();
                String ck;
                if (lt == null) {
                    ck = OTHER_CODE;
                } else if (activeIds.contains(lt.getId())) {
                    ck = normalizeCode(lt.getCode());
                } else {
                    ck = OTHER_CODE;
                }
                byCode.merge(ck, 1, Integer::sum);
            }
        }

        LinkedHashMap<String, Integer> out = new LinkedHashMap<>();
        for (LeaveType t : sorted) {
            String label = displayName(t);
            int n = byCode.getOrDefault(normalizeCode(t.getCode()), 0);
            out.merge(label, n, Integer::sum);
        }
        out.merge(OTHER_LABEL, byCode.getOrDefault(OTHER_CODE, 0), Integer::sum);
        return out;
    }
}
