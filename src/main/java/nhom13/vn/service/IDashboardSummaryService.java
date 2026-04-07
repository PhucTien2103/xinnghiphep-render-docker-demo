package nhom13.vn.service;

import java.util.Map;

import nhom13.vn.entity.User;

public interface IDashboardSummaryService {

    Map<String, Object> buildAdminManagerSummary(User user);

    Map<String, Object> buildEmployeeSummary(User user);
}
