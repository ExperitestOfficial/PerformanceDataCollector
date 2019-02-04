# PerformanceDataCollector
Enable you to collect transaction information and export it into a csv file

Following is a usage example:

```java
public class IOSDemoTest extends BaseTest {
	protected IOSDriver<IOSElement> driver = null;
	TransactionCollector tc = null;
	@BeforeMethod
	@Parameters("deviceQuery")
	public void setUp(@Optional("@os='ios'") String deviceQuery) throws Exception {
		init(deviceQuery);
		// Init application / device capabilities
		//dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
		dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
		dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");//dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
		dc.setCapability("testName", "IOSDemoTest");
		driver = new IOSDriver<>(new URL(getProperty("url",cloudProperties) + "/wd/hub"), dc);
		tc = new TransactionCollector(driver, null);
	}
	@Test
	public void test() {
		tc.startTransaction("all", "Network=3G,Other=aa", "user=user1");
		tc.startTransaction("send user and password", "Network=3G", null);
		driver.findElement(in.Repo.obj("login.username")).sendKeys("company");
		driver.findElement(in.Repo.obj("login.password")).sendKeys("company");
		tc.endTransaction("send user and password", Status.FAIL);
		driver.findElement(in.Repo.obj("login.login")).click();
		tc.endTransaction("all", Status.SUCCESS);
	}
	@AfterMethod
	public void tearDown() throws IOException {
		tc.toCsvFile(new File("out.csv"));
		driver.quit();
	}
}

```