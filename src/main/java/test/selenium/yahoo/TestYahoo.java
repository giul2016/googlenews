package test.selenium.yahoo;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import dataObject.article.Article;

public class TestYahoo {
	static final DateFormat df = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss Z");
	static final DateFormat googleDateStyle = new SimpleDateFormat(
			"MMM dd, yyyy");
	static final String titleXpath = ".//div[@class='body classical']/div[@class='body-wrap']/h3/a";
	static final String titleUrlXpath = ".//div[@class='body classical']/div[@class='body-wrap']/h3/a";
	static final String contentXpath = ".//div[@class='body classical']/div[@class='body-wrap']/p[@class='summary mt-xxs']";
	static final String publisherXpath = ".//div[@class='body classical']/div[@class='attribution fz-xxs']/span[@class='source']";
	static final String publishedDateXpath = ".//div[@class='body classical']/div[@class='attribution fz-xxs']/span[@class='time']";

	public static void main(String[] args) {
		ProfilesIni pr = new ProfilesIni();
		FirefoxProfile fp = pr.getProfile("SeleniumUser");
		FirefoxDriver driver = new FirefoxDriver(fp);
		driver.get("https://uk.news.yahoo.com/uk/");
		List<WebElement> tds = driver.findElements(By
				.xpath(".//div[@class='wrapper cf']"));
		for (WebElement td : tds) {
			Article article = getArticle(td);
			System.out.println("Title: " + article.getTitle());
			System.out.println("TitleUrl: " + article.getTitleUrl());
			System.out.println("Content: " + article.getContent());
			System.out.println("Publisher: " + article.getPublisher());
			System.out.println("publishedDate: " + article.getPublishedDate());

		}
	}

	public static Article getArticle(WebElement td) {
		Article article = new Article();
		List<WebElement> title = td.findElements(By.xpath(titleXpath));
		List<WebElement> titleUrl = td.findElements(By.xpath(titleUrlXpath));
		List<WebElement> content = td.findElements(By.xpath(contentXpath));
		List<WebElement> publisher = td.findElements(By.xpath(publisherXpath));
		List<WebElement> publishedDate = td.findElements(By
				.xpath(publishedDateXpath));

		article.setTitle(title.get(0).getText());
		article.setTitleUrl(titleUrl.get(0).getAttribute("href"));
		article.setContent(content.get(0).getText());
		if (!publisher.isEmpty()) {
			article.setPublisher(publisher.get(0).getText());
		} else {
			article.setPublisher(null);
		}

		if (!publishedDate.isEmpty()) {
			article.setPublishedDate(df.format(fromTimeAgoToDate(publishedDate
					.get(0).getText())));
		} else {
			article.setPublishedDate(null);
		}

		return article;
	}

	public static Date fromTimeAgoToDate(String timeAgo) {
		Date convertedDate = null;
		if (timeAgo.contains("ago")) {
			Pattern patter = Pattern.compile("([0-9]+) ([a-zA-Z]+) ago");
			Matcher matcher = patter.matcher(timeAgo);
			String timeNumber = null;
			String timeUnit = null;
			if (matcher.find()) {
				timeNumber = matcher.group(1);
				timeUnit = matcher.group(2);
			}

			if (timeNumber != null && timeUnit != null) {
				Date currentDate = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(currentDate);

				if (timeUnit.contains("second")) {
					cal.add(Calendar.SECOND, Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();
				} else if (timeUnit.contains("min")) {
					cal.add(Calendar.MINUTE, Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				} else if (timeUnit.contains("hour")) {
					cal.add(Calendar.HOUR, Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				} else if (timeUnit.contains("day")) {
					cal.add(Calendar.DAY_OF_MONTH,
							Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				} else if (timeUnit.contains("week")) {
					cal.add(Calendar.WEEK_OF_MONTH,
							Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				} else if (timeUnit.contains("month")) {
					cal.add(Calendar.MONTH, Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				} else if (timeUnit.contains("year")) {
					cal.add(Calendar.YEAR, Integer.valueOf("-" + timeNumber));
					convertedDate = cal.getTime();

				}

			}
		} else {

			ParsePosition p = new ParsePosition(0);
			convertedDate = googleDateStyle.parse(timeAgo, p);

		}
		return convertedDate;

	}

}
