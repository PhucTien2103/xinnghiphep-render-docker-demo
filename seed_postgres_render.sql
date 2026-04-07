BEGIN;

-- 1) Company
INSERT INTO companies (name, address, contactinfo)
SELECT 'Cong ty ABC', '123 Nguyen Hue, Quan 1, TP.HCM', 'contact@abc.local'
WHERE NOT EXISTS (
    SELECT 1 FROM companies WHERE name = 'Cong ty ABC'
);

-- 2) Users (password is plain text to match current login logic)
INSERT INTO users (username, password, email, fullname, role, status, companyid)
SELECT
    'superadmin',
    '123456',
    'superadmin@example.com',
    'Nguyen Quan Tri',
    'SUPER_ADMIN',
    1,
    c.id
FROM companies c
WHERE c.name = 'Cong ty ABC'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'superadmin');

INSERT INTO users (username, password, email, fullname, role, status, companyid)
SELECT
    'manager01',
    '123456',
    'manager01@example.com',
    'Tran Quan Ly',
    'MANAGER',
    1,
    c.id
FROM companies c
WHERE c.name = 'Cong ty ABC'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager01');

INSERT INTO users (username, password, email, fullname, role, status, companyid)
SELECT
    'employee01',
    '123456',
    'employee01@example.com',
    'Le Nhan Vien Mot',
    'EMPLOYEE',
    1,
    c.id
FROM companies c
WHERE c.name = 'Cong ty ABC'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'employee01');

-- 3) Leave types
INSERT INTO leavetypes (code, name, consumesbalance, defaultdaysperyear, active)
SELECT 'ANNUAL', 'Nghi phep nam', true, 12, true
WHERE NOT EXISTS (SELECT 1 FROM leavetypes WHERE code = 'ANNUAL');

INSERT INTO leavetypes (code, name, consumesbalance, defaultdaysperyear, active)
SELECT 'SICK', 'Nghi om', false, 0, true
WHERE NOT EXISTS (SELECT 1 FROM leavetypes WHERE code = 'SICK');

-- 4) Leave balances
INSERT INTO leavebalances (userid, totaldays, useddays, remainingdays, lastresetyear)
SELECT u.id, 12, 0, 12, EXTRACT(YEAR FROM CURRENT_DATE)::int
FROM users u
WHERE u.username = 'superadmin'
  AND NOT EXISTS (SELECT 1 FROM leavebalances lb WHERE lb.userid = u.id);

INSERT INTO leavebalances (userid, totaldays, useddays, remainingdays, lastresetyear)
SELECT u.id, 12, 2, 10, EXTRACT(YEAR FROM CURRENT_DATE)::int
FROM users u
WHERE u.username = 'manager01'
  AND NOT EXISTS (SELECT 1 FROM leavebalances lb WHERE lb.userid = u.id);

INSERT INTO leavebalances (userid, totaldays, useddays, remainingdays, lastresetyear)
SELECT u.id, 12, 1, 11, EXTRACT(YEAR FROM CURRENT_DATE)::int
FROM users u
WHERE u.username = 'employee01'
  AND NOT EXISTS (SELECT 1 FROM leavebalances lb WHERE lb.userid = u.id);

COMMIT;
