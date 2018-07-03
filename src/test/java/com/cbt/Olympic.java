package com.cbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Olympic {

	WebDriver driver;
	Map<String, String> goldMedalsNumbersAndCountries;
	Map<String, String> silverMedalsNumbersAndCountries;
	Map<String, String> bronzeMedalsNumbersAndCountries;
	String xpathRanks;
	String xpathCountries;

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().fullscreen();

	}

	@BeforeMethod
	public void setUpMethod() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics#Medal_table.");
		goldMedalsNumbersAndCountries = mapCreator("gold");
		silverMedalsNumbersAndCountries = mapCreator("silver");
		bronzeMedalsNumbersAndCountries = mapCreator("bronze");
		xpathRanks = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tbody/tr/td[1]";
		xpathCountries = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tbody/tr/th/a";

	}

	public Map<String, String> mapCreator(String str) {
		Map<String, String> xMap = new HashMap<>();
		int n = 0;
		for (int i = 1; i <= 10; i++) {
			if (str.equalsIgnoreCase("gold")) {
				n = 2;
			}
			if (str.equalsIgnoreCase("silver")) {
				n = 3;
			}
			if (str.equalsIgnoreCase("bronze")) {
				n = 4;
			}

			xMap.put(
					driver.findElement(By
							.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tbody/tr["
									+ i + "]/th/a"))
							.getText(),

					driver.findElement(By
							.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tbody/tr["
									+ i + "]/td[" + n + "]"))
							.getText());
		}
		return xMap;
	}

	@Test
	public void sortTest() {

		List<String> actualRank = listCreator(xpathRanks);
		List<String> expectedCountry = listCreator(xpathCountries);

		List<String> expectedRank = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
		Assert.assertEquals(actualRank, expectedRank);

		Collections.sort(expectedCountry);

		driver.findElement(
				By.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/thead/tr/th[2]"))
				.click();
		List<String> actualCountry = listCreator(xpathCountries);
		Assert.assertEquals(actualCountry, expectedCountry);

		List<String> actualRankAfterClickNOC = listCreator(xpathRanks);
		Assert.assertNotEquals(actualRankAfterClickNOC, expectedRank);
	}

	public List<String> listCreator(String xpath) {
		List<String> sample = new ArrayList<>();
		List<WebElement> element = driver.findElements(By.xpath(xpath));
		for (int i = 0; i < 10; i++) {
			sample.add(element.get(i).getText());
		}
		return sample;
	}

	@Test
	public void theMostTest() {

		String actualGold = mostNumberOfMedals(goldMedalsNumbersAndCountries);
		String actualSilver = mostNumberOfMedals(silverMedalsNumbersAndCountries);
		String actualBronze = mostNumberOfMedals(bronzeMedalsNumbersAndCountries);

		Assert.assertEquals(actualGold, "United States");
		Assert.assertEquals(actualSilver, "United States");
		Assert.assertEquals(actualBronze, "United States");
	}

	public static String mostNumberOfMedals(Map<String, String> medalNumsAndContry) {
		int maxMedal = 0;
		String mostMedalCountry = "";
		Set<Entry<String, String>> elements = medalNumsAndContry.entrySet();
		for (Entry<String, String> each : elements) {
			if (Integer.parseInt(each.getValue()) > maxMedal) {
				maxMedal = Integer.parseInt(each.getValue());
				mostMedalCountry = each.getKey();
			}
		}
		return mostMedalCountry;
	}

	@Test
	public void countryByMedalTest() {

		List<String> actual = medalCount(silverMedalsNumbersAndCountries, 18);
		List<String> expexted = Arrays.asList("China", "France");
		Assert.assertEquals(actual, expexted);
	}

	public List<String> medalCount(Map<String, String> medalNumsAndContry, int nums) {
		List<String> medalCount = new ArrayList<>();
		Set<Entry<String, String>> elements = medalNumsAndContry.entrySet();
		for (Entry<String, String> each : elements) {
			if (Integer.parseInt(each.getValue()) == nums) {
				medalCount.add(each.getKey());
			}

		}
		return medalCount;
	}

	@Test
	public void getIndexTest() {
		String actual = getRowAndColNum("japan");
		String expected = "[6,2]";
		Assert.assertEquals(actual, expected);
	}

	public String getRowAndColNum(String countryName) {
		String rowNCol = "";
		List<String> countries = listCreator(xpathCountries);
		for (int i = 0; i < 10; i++) {
			if (countries.get(i).equalsIgnoreCase(countryName)) {
				rowNCol = "[" + (i + 1) + ",2]";
			}
		}
		return rowNCol;
	}

	@Test
	public void getSumTest() {
		List<String> actual = sumOfMedal(bronzeMedalsNumbersAndCountries, 18);
		List<String> expected = Arrays.asList("Italy", "Australia");

		Assert.assertEquals(actual, expected);
	}

	public List<String> sumOfMedal(Map<String, String> medalNumsAndContry, int total) {
		List<String> sumOfMedal = new ArrayList<>();
		Set<Entry<String, String>> entrySet = medalNumsAndContry.entrySet();
		List<Entry<String, String>> entryList = new ArrayList<>(entrySet);
		for (int i = 0; i < 10; i++) {
			for (int j = i + 1; j < 10; j++) {
				if (Integer.parseInt(entryList.get(i).getValue())
						+ Integer.parseInt(entryList.get(j).getValue()) == total) {
					sumOfMedal.add(entryList.get(i).getKey());
					sumOfMedal.add(entryList.get(j).getKey());
				}
			}
		}
		return sumOfMedal;
	}
}
