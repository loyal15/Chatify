package vsppsgv.chatify.im.common.ui;

public enum CIPackageRunningMode {
	PACKAGE_MODE_DEV_DEVELOPMENT(0),
	PACKAGE_MODE_DEV_PRODUCT(1),
	PACKAGE_MODE_DEV_TEST(2),
	PACKAGE_MODE_PRODUCT(3),;
	
	private final int number;

    private CIPackageRunningMode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
