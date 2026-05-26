package testing;

import java.util.Arrays;

public abstract class TestSuite {

	protected int passed = 0;
	protected int failed = 0;

	protected abstract void runTest(String input, int N);

	public final void executeSuite(String suiteName) {

		this.passed = 0;
		this.failed = 0;
		
		TestUtils.section(suiteName + " Test Suite");

		String[] staticCases = TestUtils.staticCases();
		String[] randomCases = TestUtils.randomCases();
		String[] customCases = TestUtils.customCases();
		
		TestUtils.section("STATIC TESTS (" + staticCases.length + " cases)");
		for (int i = 0; i < staticCases.length; i++) {
			runTest(staticCases[i], 2 * i);
		}
		System.out.print("\n");

		if(randomCases.length > 0) {
			TestUtils.section("RANDOM TESTS (" + randomCases.length + " cases)");
			for (int i = 0; i < randomCases.length; i++) {
				runTest(randomCases[i], 2 * i);
			}
			System.out.print("\n");
		}

		if(customCases.length > 0) {		
			TestUtils.section("CUSTOM TESTS (" + customCases.length + " cases)");
			for (int i = 0; i < customCases.length; i++) {
				runTest(customCases[i], 2 * i);
			}
			System.out.print("\n");
		}

		TestUtils.section("Passed: " + passed + "   Failed: " + failed);
    }

	protected void printResult(String label, boolean ok, String exp, String got, int N) {
		String nu = (N/2) + (N%2==0 ? "a" : "b");
		if (ok) {
			System.out.printf(nu + ": [PASS] %s%n", label);
			passed++;
		}
		else {
			System.out.printf(nu + ": [FAIL] %s%n", label);
			System.out.printf("	  exp: %s%n", TestUtils.truncate(exp, 80));
			System.out.printf("	  got: %s%n", TestUtils.truncate(got, 80));
			failed++;
		}
	}
}
