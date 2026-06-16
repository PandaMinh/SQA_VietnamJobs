# VietnamJobs E2E

Bo test nay duoc scaffold de uu tien:
- `integration test` cho cac luong chinh Seeker, Employer, Admin
- `regression test` sau khi tich hop module
- `seeded-data regression`, tan dung du lieu san co trong `Database/vietnamjobs.sql`

## Cau truc

```text
vietnamjobs-e2e/
├── pom.xml
├── README.md
├── test-data/
│   ├── cv_valid.pdf
│   └── avatar.png
└── src/test/java/com/demo/e2e/
    ├── config/
    │   └── TestConfig.java
    ├── pages/
    │   ├── BasePage.java
    │   ├── LoginPage.java
    │   ├── RegisterPage.java
    │   ├── JobPage.java
    │   ├── SeekerPage.java
    │   ├── EmployerPage.java
    │   └── AdminPage.java
    └── tests/
        └── VietnamJobsE2ETest.java
```

## Muc tieu regression

Bo 50 test hien tai duoc chia theo 5 nhom:
- Public/Auth smoke
- Seeker regression
- Employer regression
- Admin regression
- Cross-role integration smoke

Luot uu tien:
1. page load va route protection
2. login theo role
3. thao tac chinh tren man hinh that
4. luong tich hop su dung seeded data co san
5. CRUD category voi du lieu unique de giam va cham khi rerun

## Prerequisites

Can co:
- JDK 17
- `JAVA_HOME` da set
- internet/dependency access de Maven tai dependency lan dau
- app VietnamJobs dang chay tai `http://localhost:8087` hoac URL ban cau hinh

Kiem tra Java:

```powershell
java -version
$env:JAVA_HOME
```

Neu chua co `JAVA_HOME`, can set truoc khi chay Maven Wrapper.

## Cai browser cho Playwright

Trong thu muc `vietnamjobs-e2e`, sau khi Maven resolve xong dependency, can cai browser:

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Neu ban muon cach on dinh hon, co the chay browser install bang IDE sau khi dependency duoc tai xong.

## Cach chay

### Chay toan bo regression suite

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test
```

### Chay rieng smoke subset

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml -Dtest.groups=smoke test
```

### Chay rieng integration subset

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml -Dtest.groups=integration test
```

### Chay rieng class E2E

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml -Dtest=VietnamJobsE2ETest test
```

### Chay headful de debug

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml -De2e.headless=false -De2e.slowMoMs=300 test
```

## Cau hinh tai khoan va seeded data

`TestConfig.java` ho tro doc tu env var hoac system property.

### Tai khoan

Mac dinh:
- Seeker: `autotest_seeker`
- Employer: `autotest_employer`
- Admin: `autotest_admin`
- Password mac dinh cho ca 3: `Autotest@123`

Vi du:

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test `
  -De2e.seeker.username=ungvien1 `
  -De2e.seeker.password=your-seeker-password `
  -De2e.employer.username=nhatuyendung1 `
  -De2e.employer.password=your-employer-password `
  -De2e.admin.username=admin `
  -De2e.admin.password=your-admin-password
```

### URL

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test -De2e.baseUrl=http://localhost:8087
```

### Seeded data dang duoc suite mac dinh su dung

Theo script `Database/autotest_accounts.sql`:
- `publicPostingId=900010`
- `integrationPostingId=900010`
- `integrationEmployerId=900002`
- `integrationEmployerName=Autotest Employer Company`
- `existingCategoryName=IT`

Neu data tren may ban khac, override bang system property:

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test `
  -De2e.publicPostingId=18 `
  -De2e.publicPostingTitle="Tuyển nữ nhân viên tiếp tân" `
  -De2e.integrationPostingId=32 `
  -De2e.integrationPostingTitle="Bai dang cong ty Thanh Tu" `
  -De2e.integrationEmployerId=42 `
  -De2e.integrationEmployerName="Thanh Tu company"
```

## Luong tich hop chinh da duoc bao phu

### Seeker
- login
- xem job list
- search job
- xem detail
- save job
- apply job
- upload CV
- mo lich su da ung tuyen
- mo lich su da luu

### Employer
- login
- dashboard
- company profile
- update company page
- recruitment post list
- add job
- edit job
- mo danh sach ung vien

### Admin
- login
- account list
- company list + detail
- job list + detail
- category list + children
- category CRUD voi du lieu unique

### Cross-role
- Seeker mo seeded posting
- Employer mo applicant list cua seeded posting
- Admin mo company/job detail lien quan seeded posting

## Luu y ve do on dinh

Bo test nay uu tien tinh lap lai:
- category test dung ten unique theo timestamp
- employer create/update job dung title unique theo timestamp
- test route protection va page load khong phu thuoc qua nhieu vao noi dung dong
- cross-role flow dung seeded IDs de giam phu thuoc vao thao tac tao moi

Nhung van co mot so gioi han:
- app hien tai dung du lieu that trong DB, khong co database reset per test
- mot so luong nhu `save/apply` co the tao them record moi moi lan chay
- suite chua co cleanup cho posting moi tao, vi luong delete posting trong app hien tai chua on dinh
- may hien tai cua ban can set `JAVA_HOME` truoc khi build

## Goi y dung cho regression sau tich hop

Sau moi lan merge/tich hop:
1. chay smoke subset truoc
2. neu smoke xanh, chay full `VietnamJobsE2ETest`
3. neu co loi, uu tien xem nhom:
   - auth / route protection
   - seeker core flow
   - employer post/applicant flow
   - admin moderation/category flow

Neu ban muon, buoc tiep theo minh co the:
- tach 50 test nay thanh nhieu class theo role de report de doc hon
- them screenshot/video khi fail
- them GitHub Actions/Jenkins step cho regression E2E
- them seeded SQL rieng cho E2E de suite chay lap lai sach hon

## Minh chung HTML va screenshot tung buoc

Framework hien tai tu dong:
- chup screenshot sau moi thao tac page object quan trong
- tao report HTML cho tung testcase
- tao report tong hop cho ca lan chay
- luu `trace-xx.zip` cua Playwright de debug sau khi fail

Thu muc artifact mac dinh:

```text
vietnamjobs-e2e/target/evidence/run-YYYYMMDD-HHMMSS/
├── index.html
├── 001-tc001_loginPageLoads/
│   ├── index.html
│   ├── step-001.png
│   ├── step-002.png
│   └── trace-01.zip
└── ...
```

Mo report tong hop:
- `target/evidence/<run-folder>/index.html`

Co the doi thu muc artifact:

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test -De2e.evidence.dir=target/custom-evidence
```

Tat minh chung neu can chay nhanh:

```powershell
..\Website\vietnamjobs_42\mvnw.cmd -f pom.xml test -De2e.evidence.enabled=false
```
