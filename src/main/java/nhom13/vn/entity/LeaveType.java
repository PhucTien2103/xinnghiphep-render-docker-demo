package nhom13.vn.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "LeaveTypes", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class LeaveType implements Serializable {

    public static final String CODE_ANNUAL = "ANNUAL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * When true, pending validation and approval deduct from the user's annual leave pool ({@link LeaveBalance}).
     */
    @Column(nullable = false)
    private boolean consumesBalance;

    /**
     * For {@link #CODE_ANNUAL}, defines default days per year for new balances and drives policy-wide adjustments.
     */
    @Column(nullable = false)
    private int defaultDaysPerYear;

    @Column(nullable = false)
    private boolean active = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConsumesBalance() {
        return consumesBalance;
    }

    public void setConsumesBalance(boolean consumesBalance) {
        this.consumesBalance = consumesBalance;
    }

    public int getDefaultDaysPerYear() {
        return defaultDaysPerYear;
    }

    public void setDefaultDaysPerYear(int defaultDaysPerYear) {
        this.defaultDaysPerYear = defaultDaysPerYear;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
